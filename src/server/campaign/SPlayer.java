/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package server.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import megamek.common.AmmoType;
import megamek.common.Entity;
import megamek.common.Mounted;
import megamek.common.Protomech;

import server.MWServ;

import server.campaign.market2.IBuyer;
import server.campaign.market2.ISeller;
import server.campaign.mercenaries.ContractInfo;
import server.campaign.mercenaries.MercHouse;
import server.campaign.pilot.SPilot;
import server.campaign.util.OpponentListHelper;
import server.campaign.CampaignMain;
import server.campaign.util.ExclusionList;
import server.campaign.SUnit;
import server.campaign.SArmy;
import server.campaign.SHouse;
import server.MWChatServer.auth.IAuthenticator;
import server.util.MWPasswdRecord;
import server.util.TokenReader;

import common.Player;
import common.SubFaction;
import common.Unit;
import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitComponents;
import common.util.UnitUtils;

/**
 * A class representing a Player DOCU is not finished
 * 
 * @author Helge Richter (McWizard)
 */
@SuppressWarnings( { "unchecked", "serial", "unused" })
public final class SPlayer extends Player implements Serializable, Comparable,
        IBuyer, ISeller {

    // STATIC VARIABLES
    // STATUS_DISCONNECTED, which is used by the client, is 0
    public static final int STATUS_LOGGEDOUT = 1;
    public static final int STATUS_RESERVE = 2;
    public static final int STATUS_ACTIVE = 3;
    public static final int STATUS_FIGHTING = 4;

    // DATA VARIABLES (SAVED. Most have gets and sets.)
    private String name;
    private String fluffText = "";
    private String myLogo = "";
    private String lastISP = "";

    private int money = 0;
    private int experience = 0;
    private int influence = 50;
    private int currentReward = 0; // number of rewards a player has.
    private int xpToReward = 0; // How much exp the have until the next reward
    private int groupAllowance = 0;

    private int technicians = 0;// @urgru 7/17/04
    private int baysOwned = 0;
    private int currentTechPayment = -1;// num Cbills owed to techs after game

    private double rating = 1600;

    private long lastOnline = 0;

    private Vector<SUnit> units = new Vector<SUnit>(1, 1);
    private Vector<SArmy> armies = new Vector<SArmy>(1, 1);

    private Vector<Integer> totalTechs = new Vector<Integer>(4, 1);
    private Vector<Integer> availableTechs = new Vector<Integer>(4, 1);

    private SPersonalPilotQueues personalPilotQueue = new SPersonalPilotQueues();
    private ExclusionList exclusionList = new ExclusionList();

    // SEMI-PERMANENT VARIABLES. Not saved to String.
    private int scrapsThisTick = 0;
    private int donationsThisTick = 0;

    private double weightedArmyNumber = -1;

    private long lastTimeCommandSent = 0;
    private long lastAttackFromReserve = 0;
    private long activeSince = 0;
    private long attackRestrictionUntil = 0;

    private String sellingto = "";
    private String lastSentStatus = "";
    private String clientVersion = "";// version gets sent by the player and
                                        // set

    private SHouse myHouse;
    private MWPasswdRecord password = null;

    private UnitComponents unitParts = new UnitComponents();

    private int DBId = 0;
    private int forumID = 0;

    boolean isLoading = false; // Player was getting saved multiple times
                                // during loading. Just seemed silly.

    private String subFaction = "";

    private long lastPromoted = 0;

    // CONSTRUCTORS
    /**
     * Stock constructor. Note that an SPlayer is data-less unless/until
     * fromString() or some sets are called.
     * 
     * SPlayers are created in only two places - CampaignMain's load method and
     * the EnrollCommand.
     */
    public SPlayer() {

        // if using advanced repair, populate tech vectors and generate info
        if (CampaignMain.cm.isUsingAdvanceRepair()) {
            for (int x = 0; x < 4; x++) {
                getAvailableTechs().add(0);
                getTotalTechs().add(0);
            }
        }
        this.myHouse = CampaignMain.cm
                .getHouseFromPartialString(CampaignMain.cm
                        .getConfig("NewbieHouseName"));
    }

    /**
     * Save player file immediatly.
     */
    public void setSave() {
        if (!this.isLoading)
            CampaignMain.cm.savePlayerFile(this);
    }

    /**
     * Return a desctiptive string. TODO: Factor this out? Is only used by
     * cyclops writer.
     */
    public static String playerLevelDescription(int level) {

        if (level >= 200)
            return "Admin";

        if (level >= 100)
            return "Moderator";

        if (level >= 30)
            return "Enhanced Player";

        return "Player";

    }

    // PUBLIC METHODS
    /**
     * Override the standard Object.equals(), compare two instances of a player
     * by name only.
     */
    @Override
    public boolean equals(Object o) {

        SPlayer p = null;
        try {
            p = (SPlayer) o;
        } catch (ClassCastException e) {
            return false;
        }

        if (p == null)
            return false;

        if (p.getName().equals(name))
            return true;

        // else
        return false;
    }

    /**
     * A Method that returns a rounded ELO rating for this player. Used to send
     * truncated doubles to the userlist.
     * 
     * @return the rounded rating
     */
    public double getRatingRounded() {
        BigDecimal bd = new BigDecimal(rating);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Determine whether or not a player can use a unit of a given weight class.
     * This is used to prevent new players from buying heavier/larger units and
     * sucking a house dry.
     * 
     * @param -
     *            weight class to check.
     */
    public boolean mayUse(int weightClass) {
        if (weightClass == Unit.MEDIUM)
            if (Integer
                    .parseInt(this.getMyHouse().getConfig("MinEXPforMedium")) > experience)
                return false;
        if (weightClass == Unit.HEAVY)
            if (Integer.parseInt(this.getMyHouse().getConfig("MinEXPforHeavy")) > experience)
                return false;
        if (weightClass == Unit.ASSAULT)
            if (Integer.parseInt(this.getMyHouse()
                    .getConfig("MinEXPforAssault")) > experience)
                return false;
        return true;// LIGHT is always usable.
    }

    /**
     * Add a unit to the player.
     * 
     * Pass-though to addUnit(SUnit,boolean,boolean). This version should be
     * called in almost all situations.
     */
    public void addUnit(SUnit m, boolean isNew) {
        this.addUnit(m, isNew, true);
    }

    /**
     * Add a unit to the player. If the unit is new, make it immune to
     * maintenance scraps.
     * 
     * Nearly all calls should send updates to a client; however, in some
     * instances (ex: when giving units to a SOL player), bandwidth is saved by
     * doing a single PS| at the end of a series of adds.
     */
    public String addUnit(SUnit m, boolean isNew, boolean sendUpdates) {

        if (isNew) {
            long immunityTime = Long.parseLong(this.getMyHouse().getConfig(
                    "ImmunityTime")) * 1000;
            m.setPassesMaintainanceUntil(System.currentTimeMillis()
                    + immunityTime * 2);
        }

        // clear any scrap allowance
        m.setScrappableFor(-1);

        /*
         * OK if there's room, unmaintained if not. This also strips any
         * FOR_SALE from units purchased via the market.
         */
        if (this.getFreeBays() < (CampaignMain.cm.isUsingIncreasedTechs() ? SUnit
                .getHangarSpaceRequired(m, getMyHouse().houseSupportsUnit(
                        m.getUnitFilename()), getMyHouse())
                : SUnit.getHangarSpaceRequired(m, getMyHouse())))
            m.setUnmaintainedStatus();
        else
            m.setStatus(Unit.STATUS_OK);

        // strip illegal ammos
        SUnit.checkAmmoForUnit(m, myHouse);

        m.setPosId(getFreeID());
        units.add(m);

        /*
         * Send PL|HD. Client-side reading of HD adds units to the hangar
         * instead of clearing/replacing the hangar, so we can send just this
         * one, if we like.
         * 
         * Send status update to the client (status determined above), along
         * with total and free bay/tech info.
         */
        if (sendUpdates) {
            CampaignMain.cm.toUser("PL|HD|" + m.toString(true), name, false);
            CampaignMain.cm.toUser("PL|SUS|" + m.getId() + "#" + m.getStatus(),
                    name, false);
            CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name,
                    false);
            CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name, false);
        }
        if (CampaignMain.cm.isUsingMySQL()) {
            m.toDB();
            CampaignMain.cm.MySQL.linkUnitToPlayer(m.getDBId(), getDBId());
        }
        // make sure to save the player, with his fancy new unit ...
        setSave();
        return "";// dummy string returned to comply with IBuyer
    }

    /**
     * Return an SUnit with a given unique ID. If the player doesn't own the
     * unit, return a null.
     * 
     * @param int -
     *            id the the unit to return
     * @return the desired unit, or null.
     */
    public SUnit getUnit(int id) {

        for (SUnit currU : units) {
            if (currU.getId() == id)
                return currU;
        }

        return null;
    }

    /**
     * ISeller-compliant .removeUnit(). Simply get the unit ID and pass to
     * normal SPlayer.removeUnit(int,bool).
     * 
     * Use the (int,boolean) version of remove unit whenever possible in order
     * to intelligently pass select the army update option. ISeller assumes true
     * and sends updates to all armies.
     * 
     * @urgru 1.2.06
     */
    public String removeUnit(SUnit unitToRemove, boolean sendHouseStatusUpdate) {
        this.removeUnit(unitToRemove.getId(), true);
        return "";// dummy stirng returned for IBuyer
    }

    /**
     * Remove the Unit with ID unitid from the player. Ops are checked by
     * discrete commands (ie - SellUnit), unchecked by large blocks of code
     * which force a check on their own (ie - ShortResolver).
     * 
     * @param unitid
     *            the ID of the unit to remove
     */
    public void removeUnit(int unitid, boolean sendArmyUpdate) {
        if (CampaignMain.cm.isUsingMySQL())
            CampaignMain.cm.MySQL.unlinkUnit(unitid);

        SUnit Mech = null;
        for (int i = 0; i < units.size(); i++) {
            Mech = units.elementAt(i);
            if (Mech.getId() == unitid)
                units.removeElementAt(i);
        }

        for (SArmy currA : this.getArmies()) {
            if (currA.getUnitPosition(unitid) > -1) {
                currA.removeUnit(unitid);
                if (sendArmyUpdate) {
                    CampaignMain.cm.toUser("PL|SAD|"
                            + currA.toString(true, "%"), name, false);
                    CampaignMain.cm.getOpsManager()
                            .checkOperations(currA, true);// update legal ops
                }
            }
        }// end for(all armies)

        CampaignMain.cm.toUser("PL|RU|" + unitid, name, false);
        CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name, false);
        CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name, false);
        setSave();// save on remove (adminstrip, etc)
    }

    /**
     * Method which determines the number ot free bays/techs a player has.
     * Simple loop through the hangar.
     * 
     * @return number of free bays/techs
     */
    public int getFreeBays() {

        int free = getTotalMekBays();
        int totalProtos = 0;
        boolean advanceRep = CampaignMain.cm.isUsingAdvanceRepair();

        /*
         * Loop through all units. Those with STATUS_OK and STATUS_FORSALE take
         * up space. Units with STATUS_UNMAINTAINED and STATUS_DESTROYED don't
         * require techs.
         * 
         * Protos get special point-based handling. They're counted and passed
         * off to this.getTechRequiredForProtos(), which determines exactly how
         * many techs are needed for any ProtoMek grouping.
         */
        for (SUnit currU : units) {

            if (currU.getStatus() == Unit.STATUS_OK
                    || currU.getStatus() == Unit.STATUS_FORSALE) {
                if (CampaignMain.cm.isUsingIncreasedTechs())
                    free -= SUnit.getHangarSpaceRequired(currU, getMyHouse()
                            .houseSupportsUnit(currU.getUnitFilename()),
                            getMyHouse());
                else
                    free -= SUnit.getHangarSpaceRequired(currU, getMyHouse());

                // proto counting
                if (currU.getEntity() instanceof Protomech && !advanceRep) {
                    if (!currU.getPilot().getSkills().has(
                            PilotSkill.AstechSkillID))
                        totalProtos++;
                } else if (currU.getEntity() instanceof Protomech)
                    totalProtos++;

            }
        }// end while(more unit data)

        /*
         * Adjust for proto points.
         */
        if (totalProtos > 0) {
            int techRatio = Integer.parseInt(CampaignMain.cm
                    .getConfig("TechsToProtoPointRatio"));
            double ppoints = totalProtos / 5.0;// 5 protos in a point
            int ptechs = (int) (ppoints * techRatio);

            if (ptechs < 1) {
                ptechs = 1;
            }
            free -= ptechs;
        }

        return free;
    }

    /**
     * This can be calcualted in one of three "standard" ways: 1) House bays +
     * techs 2) House bays + experience 3) House bays + techs + experience
     * 
     * Or, two additional ways if using Advanced Repair: 4) House Bays + bays
     * owned by player 5) House bays + bays owned by player + experience
     * 
     * @return the total amount of bays this player has
     */
    public int getTotalMekBays() {// return bay/support number
        int numBays = 0;// amount to return

        boolean usesXP = Boolean.parseBoolean(this.getMyHouse().getConfig(
                "UseExperience"));
        boolean usesTechs = Boolean.parseBoolean(this.getMyHouse().getConfig(
                "UseTechnicians"));
        boolean usesAdvanceRepairs = CampaignMain.cm.isUsingAdvanceRepair();

        if (usesAdvanceRepairs)
            usesTechs = false;

        // include the basic bays. flat amount for mercs/SOL, warehouse # for
        // GreatHouses
        int BASE_BAYS = myHouse.getBaysProvided();
        numBays += BASE_BAYS;

        /*
         * Make sure all non-merc players meet a minimum free bay standard.
         * Useful for small factions on large servers (Marians, etc) and
         * factions which lose a large number of their warehouse worlds,
         * dropping fresh-from-SOL players to an unacceptably low # of bays.
         * Don't give these to mercenaries.
         */
        if (!myHouse.isMercHouse()) {
            int minBays = Integer.parseInt(this.getMyHouse().getConfig(
                    "MinimumHouseBays"));
            if (numBays < minBays)
                numBays = minBays;
        }// end if(non-merc)

        // then add the bays from XP, if the config says to...
        if (usesXP) {
            int experienceForBay = Integer.parseInt(this.getMyHouse()
                    .getConfig("ExperienceForBay"));
            int maxBaysFromXP = Integer.parseInt(this.getMyHouse().getConfig(
                    "MaxBaysFromEXP"));
            int expBays = (experience / experienceForBay);
            if (expBays > maxBaysFromXP)
                expBays = maxBaysFromXP;
            numBays += expBays;
        }

        // and now add the bays from techs if config'ed...
        if (usesTechs)
            numBays += this.getTechnicians();

        // now add bays if you are using advanced repairs
        if (usesAdvanceRepairs)
            numBays += baysOwned;

        return numBays;
    }// end TotalMechBays()

    /**
     * This method does all the math to figure out how much the retainer fee,
     * maintenance cost, whathaveyou is for the current number of technicians.
     * 
     * The number itself is useful in some cases (let people know what they will
     * have to pay after hiring a new tech, for example), and thus separated
     * from the actual payment.
     * 
     * For now, we have only one payment calculation mechanism -- additive
     * costing, whereby each tech costs as much as the last, plus a constant
     * kicker. A cap to this cost can be configured; however, it must be a
     * multiple of the per-tech additive (eg, if the additive is .04, 1.20 would
     * be a valid cap, but 1.30 wouldn't).
     * 
     * @urgru 7/26/04
     */
    private void doPayTechniciansMath() {

        int techs = this.getTechnicians();

        // don't even waste time on 0 cases. Just return.
        if (techs <= 0) {
            this.setCurrentTechPayment(0);
            return;
        }

        // starts as a double, gets cast back to an int for return.
        float amountToPay = 0;

        // load config variables needed to do the math ...
        float additive = Float.parseFloat(this.getMyHouse().getConfig(
                "AdditivePerTech"));
        float ceiling = Float.parseFloat(this.getMyHouse().getConfig(
                "AdditiveCostCeiling"));

        /*
         * divide the ceiling by the addiive. techs past this number are all
         * charged at the ceiling rate. Example: (With 1.20 and .04, the result
         * is 30. Every additional tech (31, 32, etc.) is paid at the ceiling
         * wage.
         */
        int techCeiling = (int) (ceiling / additive);
        if (techs > techCeiling) {
            int techsPastCeiling = techs - techCeiling;
            amountToPay += ceiling * techsPastCeiling;
        }// end if(some techs are paid @ ceiling price)

        /*
         * Add up the number of times the non-ceiling techs were incremented,
         * then figure out their total cost. In cases where the ceiling is
         * passed, the flat fee techs are handled above, so only techs up to
         * that ceiling need to have the additive math done. If the ceiling isnt
         * reached, just use the number of techToPay from the param.
         */
        int techsUsingAdditive = 0;
        if (techs > techCeiling)
            techsUsingAdditive = techCeiling;
        else
            techsUsingAdditive = techs;

        /*
         * Faster to just to a for loop to determine the number of times the
         * additive was made (1 + 2 + 3 + 4, and so on) with ints, and THEN
         * multiply by the double additive than do alot of floating point math
         * by for-in through and multiplying by the additive each time.
         */
        int totalAdditions = 0;
        for (int i = 1; i <= techsUsingAdditive; i++)
            totalAdditions += i;

        // now figure out the final amount to pay ...
        amountToPay += totalAdditions * additive;

        /*
         * now return the amount in INT form since we don't support fractional
         * money. also, set the currentTechPayment, to avoid doing this math
         * again if possible.
         */
        int toSet = Math.round(amountToPay);
        if (toSet < 0) {
            toSet = 0;
        }// don't pay players to add techs.

        this.setCurrentTechPayment(toSet);

    }// end doPayTechnicians(arbitrary number)

    /**
     * Should be called only after an attempt to pay techs comes up short. At
     * present, only used by ShortResolver. Other times techs are paid (eg -
     * TransferCommand) shortfalls stop the player from acting.
     * 
     * Does all the dirty work of lowering the number of technicians and setting
     * units as unmaintained.
     * 
     * @param amountofShortFall -
     *            the amount owed to techs which can't be paid. used to
     *            determine how many walk off / quit.
     * @return numLost - the number of techs or bays lost.
     */
    public int doFireUnpaidTechnicians(float amountOfShortFall) {

        String toReturn = "";

        // layoffs all around! well, at least some. so reset the
        // currentTechPayment
        this.setCurrentTechPayment(-1);

        // load config variables needed to do the calculations
        float additive = Float.parseFloat(this.getMyHouse().getConfig(
                "AdditivePerTech"));
        float ceiling = Float.parseFloat(this.getMyHouse().getConfig(
                "AdditiveCostCeiling"));

        int currentTechs = this.getTechnicians();// current number of techs
        int techCeiling = (int) (ceiling / additive);// the ceiling

        /*
         * Start by getting rid of the most expensive techs (those at the
         * ceiling). Loop until the player is able to afford the bill, or all
         * techs above the ceiling have been dismissed.
         */
        while (amountOfShortFall > 0 && currentTechs > techCeiling) {
            currentTechs = currentTechs - 1;
            amountOfShortFall -= ceiling;
        }

        /*
         * Now start getting rid of the less expensive techs. Each tech costs
         * his # times his additive amount. Loop until theyre all gone, or the
         * bill can be paid.
         */
        while (amountOfShortFall > 0) {

            // fire a tech and reduce shortfall by his cost
            float costOfCurrentTech = currentTechs * additive;
            currentTechs = currentTechs - 1;
            amountOfShortFall -= costOfCurrentTech;

            // catch zero techs, just in case there IS rounding funkiness
            if (currentTechs == 0)
                amountOfShortFall = 0;

        }

        int numberOfTechsFired = this.getTechnicians() - currentTechs;
        this.addTechnicians(-numberOfTechsFired);

        return numberOfTechsFired;
    }

    /**
     * Method that returns the current cost of hiring a new technician, after
     * adjustment for XP, etc. Used by HireTechsCommand, Requests and
     * SetMaintainedCommand.
     */
    public int getTechHiringFee() {
        // get the starting tech cost
        int techCost = Integer.parseInt(CampaignMain.cm
                .getConfig("BaseTechCost"));

        /*
         * Check to see if tech hiring costs should be decreased with
         * experience. If they should be, load the amount of XP for each
         * reduction, and the pricing floor. Loop through the XP amount reducing
         * cost until the floor is reached, or there isnt enough XP to reduce
         * price further.
         */
        boolean decreaseWithXP = Boolean.parseBoolean(this.getMyHouse()
                .getConfig("DecreasingTechCost"));
        if (decreaseWithXP) {
            // if it decreases, see how much
            int xpToDecrease = Integer.parseInt(this.getMyHouse().getConfig(
                    "XPForDecrease"));
            int minTechCost = Integer.parseInt(this.getMyHouse().getConfig(
                    "MinimumTechCost"));

            int numDecreases = (int) Math.floor(experience / xpToDecrease);
            techCost -= numDecreases;

            if (techCost < minTechCost)
                techCost = minTechCost;

            // catch error, in case server is misconfigured
            if (techCost < 0)
                techCost = 0;

        }
        return techCost;
    }

    /**
     * A method which is called to randomly set some units as unmaintained when
     * support levels go negative. Continues until support number is positive
     * again, or all units are unsupported (catches odd problems with units on
     * the black market -- not an expecially graceful solution; however, the
     * alternative is allowing units on the BM to be scrapped mid-auction).
     * 
     * @urgru 8/2/04
     */
    public int setRandomUnmaintained() {

        // holder.
        int numUnmaintained = 0;

        // filter out units which are already unmaintained, for_sale or
        // destroyed
        Vector<SUnit> okUnitsData = new Vector<SUnit>(1, 1);
        for (SUnit currU : units) {
            if (currU.getStatus() == Unit.STATUS_OK)
                okUnitsData.add(currU);
        }

        while (getFreeBays() < 0) {

            if (okUnitsData.size() == 0)// catch no units, just in case ...
                return numUnmaintained;

            // passed the catch. unmaintain some units.
            int rnd = CampaignMain.cm.getRandomNumber(okUnitsData.size());// generate
            // a
            // RND
            SUnit unit = okUnitsData.elementAt(rnd);// get unit @ rnd location
            unit.setUnmaintainedStatus();// make it unmaintained
            numUnmaintained++;
            CampaignMain.cm.toUser("PL|UU|" + unit.getId() + "|"
                    + unit.toString(true), name, false);
            okUnitsData.remove(rnd);// and remove it from the vector

        }// end while(no free bays)

        setSave();
        return numUnmaintained;

    }// end setRandomUnmaintained

    /**
     * Loop through the units and perform maintainance. Check status and adjust
     * maintainance level accordingly. This is called during slices.
     * 
     * Check to ses if units are maintained -- if so, improve maintainance
     * levels. If not, roll a random. If its greater than the maintainance
     * level, scrap the unit. If unit should be scrapped, or just have its
     * mainainance level reduced.
     * 
     * Note that units on the BM arent included in the maintainance loop. It
     * should be impossible to add an unmaintained unit to the BM, but just in
     * case, they're excluded (STATUS_FORSALE is ignored). This prevents off BM
     * nulls.
     */
    public void doMaintainance() {

        if (CampaignMain.cm.isUsingAdvanceRepair())
            return;
        int increase = Integer.parseInt(this.getMyHouse().getConfig(
                "MaintainanceIncrease"));
        int decrease = Integer.parseInt(this.getMyHouse().getConfig(
                "MaintainanceDecrease"));

        ArrayList<SUnit> unitsToDestroy = new ArrayList<SUnit>();
        for (SUnit currUnit : units) {// loops through all units

            // if the unit is maintained, boost its level
            if (currUnit.getStatus() == Unit.STATUS_OK)
                currUnit.addToMaintainanceLevel(increase);

            // unit is unmaintained
            else if (currUnit.getStatus() == Unit.STATUS_UNMAINTAINED) {
                int rnd = CampaignMain.cm.getRandomNumber(100) + 1;

                // immediately after a game, only decrement. don't scrap.
                long currTime = System.currentTimeMillis();
                if (CampaignMain.cm.getIThread().isImmune(this)
                        || currUnit.getPassesMaintainanceUntil() > currTime)
                    currUnit.addToMaintainanceLevel(-decrease);

                // unmaintained, not immune, but luckily passed scrap check
                else if (rnd <= currUnit.getMaintainanceLevel()) {
                    currUnit.addToMaintainanceLevel(-decrease);
                }

                // unmaintained and failed scrap check. blow 'er up.
                else {

                    if (myHouse.isNewbieHouse()) {
                        CampaignMain.cm
                                .toUser(
                                        "Your "
                                                + currUnit.getModelName()
                                                + " is badly maintained and failed a survival roll. In a normal faction, "
                                                + "failing these rolls <b>destroys</b> the unit. In the training faction you simply get this warning. Take heed.",
                                        name, true);
                        return;
                    }// break out if trying to scrap a SOL mech

                    // if scrapping costs bills, subtract the appropriate
                    // amount.
                    int mechscrapprice = Math.round(myHouse.getPriceForUnit(
                            currUnit.getWeightclass(), currUnit.getType())
                            * Float.parseFloat(this.getMyHouse().getConfig(
                                    "ScrapCostMultiplier")));
                    if (this.getMoney() < mechscrapprice)
                        mechscrapprice = this.getMoney();
                    if (mechscrapprice > 0)
                        this.addMoney(-mechscrapprice);

                    // remove all flu, even if scrapping is free
                    int flutolose = this.getInfluence();
                    this.addInfluence(-flutolose);

                    String toSend = "Lack of maintainance has forced your techs to scrap "
                            + currUnit.getPilot().getName()
                            + "'s "
                            + currUnit.getModelName()
                            + " for parts. HQ is displeased (";
                    if (mechscrapprice > 0)
                        toSend += CampaignMain.cm.moneyOrFluMessage(true,
                                false, -mechscrapprice, true)
                                + ", ";
                    toSend += CampaignMain.cm.moneyOrFluMessage(false, false,
                            -flutolose, true)
                            + ").";
                    CampaignMain.cm.toUser(toSend, name, true);

                    myHouse.addDispossessedPilot(currUnit, false);
                    unitsToDestroy.add(currUnit);// actually removing now
                                                    // would cause conc mod
                                                    // error
                }// end else(failed scrap check)

            }// end else if(isnt maintained)
        }// end for(all elements)

        /*
         * remove those units which were destroyed. no need to send updates b/c
         * unmaintained units can't be in armies.
         */
        for (SUnit destroyedU : unitsToDestroy) {
            this.removeUnit(destroyedU.getId(), false);
            if (CampaignMain.cm.isUsingMySQL()) {
                CampaignMain.cm.MySQL.deleteUnit(destroyedU.getDBId());
            }
        }

    }// end doMaintainance()

    /**
     * Method which checks to see if a player owns an unmaintained unit. Called
     * from Request, RequestDonated, Transfer and other commands.
     * 
     * Hacky direct access of SUnitData, but constructing an SUnit when we have
     * direct access to the status and no intent to change it is a bit wasteful.
     * 
     * @return boolean indicating owndership of an unmaintained unit.
     */
    public boolean hasUnmaintainedUnit() {

        for (SUnit currU : units) {
            if (currU.getStatus() == Unit.STATUS_UNMAINTAINED)
                return true;
        }

        // no unmaintained unit found.
        return false;
    }

    /**
     * Transition a player from reserve to active, or vice versa. See in-line
     * comments for more detail.
     * 
     * @param newStatus -
     *            true to activate, false to deac.
     */
    public void setActive(boolean newStatus) {

        // lower case the name only once
        String lowerName = name.toLowerCase();

        // de-activating
        if (!newStatus) {

            activeSince = 0;// deactivating. make a 0.
            setLastOnline(System.currentTimeMillis());

            /*
             * Player is being moved to ianctive status. This means he is no
             * longer an eligible attack target. Need to remove his oplists and
             * clear his entries on other players oplists.
             */
            OpponentListHelper olh = new OpponentListHelper(this,
                    OpponentListHelper.MODE_REMOVE);
            olh
                    .sendInfoToOpponents("left the front lines and may no longer be attacked");

            /*
             * The player also needs to be removed as a possible defender from
             * all outstanding operations. Loop through the ops, removing him
             * from their defender/chicken trees.
             * 
             * It's safe to assume that any deactivation in the face of attack
             * deserves a penalty, so call the punishing shutdown.
             * 
             * NOTE: The chicken threads call setActive(false) in order to turn
             * off someone who has been leeched. This means that the thread is
             * calling its own doPenalty() methods indirectly here ... but also
             * lets the first thread to hit the leach ceiling turn off any other
             * attacks against the player.
             */
            CampaignMain.cm.getOpsManager()
                    .removePlayerFromAllPossibleDefenderLists(this.name, true);

            /*
             * Remove the player from all attacker lists. It is presumed that a
             * player who is fighting could never finish the Deactivate command.
             * If a player has gotten this far, his attacks must be in WAITING
             * status, so we remove the player from the games and cancel if they
             * hit 0 attackers.
             */
            CampaignMain.cm.getOpsManager().removePlayerFromAllAttackerLists(
                    this, null, true);

            // all done. remove the player from the active hash and put him in
            // reserve
            myHouse.getActivePlayers().remove(lowerName);
            myHouse.getReservePlayers().put(lowerName, this);

            // NOTE: Deactivation does NOT call IThread.removeImmunity(). This
            // lets SOL reset units.
            // We remove immunity when someone re-activates instead.
        }

        // activating
        else {

            // activating. set current timestamp and clear immunity.
            activeSince = System.currentTimeMillis();
            CampaignMain.cm.getIThread().removeImmunity(this);

            /*
             * Player is activating. His armies are all acceptable, and his
             * status has changed. Broadcast his army values to other players
             * and construct opponent vectors for the newly activated armies.
             * [NOTE: actual checks moved into a helper class so they can be run
             * as a player logs in w/ a running game and after games as well].
             */
            OpponentListHelper olh = new OpponentListHelper(this,
                    OpponentListHelper.MODE_ADD);
            olh
                    .sendInfoToOpponents("is headed to the front lines. You may attack it with ");

            // make the hash switch
            myHouse.getReservePlayers().remove(lowerName);
            myHouse.getActivePlayers().put(lowerName, this);
        }
    }

    /**
     * Standard active/fighting rotation. Use setFighting(bool,bool) to move a
     * player to reserve from fighting after an AFR game, and this method for
     * everything else.
     */
    public void setFighting(boolean newStatus) {
        this.setFighting(newStatus, false);
    }

    /**
     * Transition a player between fighting and active status.
     * 
     * @param name
     */
    public void setFighting(boolean newStatus, boolean toReserve) {

        // lower case the name only once
        String lowerName = name.toLowerCase();

        // switch to fighting
        if (newStatus) {

            /*
             * remove the player (if present) from the active list, then add him
             * to the fighting hashtable.
             */
            myHouse.getActivePlayers().remove(lowerName);
            myHouse.getFightingPlayers().put(lowerName, this);

            // send status update to the user
            CampaignMain.cm.toUser("CS|" + +SPlayer.STATUS_FIGHTING, name,
                    false);

            /*
             * Player is being moved to busy status. This means he is no longer
             * an eligible attack target. Need to remove his oplists and clear
             * his entries on other players oplists.
             * 
             * Note that this has no effect on players who are being set as Busy
             * immediately after logging in because they disconnected mid-game
             * since they have empty op lists.
             */
            OpponentListHelper olh = new OpponentListHelper(this,
                    OpponentListHelper.MODE_REMOVE);
            olh
                    .sendInfoToOpponents(" entered combat and may no longer be attacked");
        }

        // de-fight from AFR. Move to reserve.
        else if (toReserve) {
            activeSince = 0;
            myHouse.getFightingPlayers().remove(lowerName);
            myHouse.getReservePlayers().put(lowerName, this);
        }

        else {

            /*
             * remove the player (if present) from the active list, then add him
             * to the fighting hashtable.
             */
            myHouse.getFightingPlayers().remove(lowerName);
            myHouse.getActivePlayers().put(lowerName, this);

            /*
             * If player was STATUS_FIGHTING and is being moved back into
             * STATUS_ACTIVE, either - a game was cancelled; or - a game was
             * finished.
             * 
             * If we're dealing with a finished game, let the ImmunityThread
             * handle OpponentList issues. If a cancel, there will not be any
             * immunity and updates should be sent to all players immediately.
             */
            if (!CampaignMain.cm.getIThread().isImmune(this)) {
                OpponentListHelper olh = new OpponentListHelper(this,
                        OpponentListHelper.MODE_ADD);
                olh
                        .sendInfoToOpponents(" halted combat operations and returned to its post. You may attack it with ");
            }
        }

    }// end setFighting(boolean b)

    /**
     * Method which sets a player to fighting without triggering Oplist
     * construction. DO NOT USE THIS METHOD. It is a special activation/business
     * sequence that is used only when a player is returning to the server and
     * already involved in a game and should only be called from ShortOperation.
     * 
     * All standard activations and ALL deactivations should be dealt with via
     * SPlayer.setActive(boolean), which sets up opponent lists, informs
     * potential attackers, etc.
     */
    public void setFightingNoOppList() {

        // no immunity from immediate activation
        CampaignMain.cm.getIThread().removeImmunity(this);

        // mark this as the time-of-activation
        activeSince = System.currentTimeMillis();

        // attempt to remove from both reserve AND active, just in case
        String lowerName = name.toLowerCase();
        myHouse.getReservePlayers().remove(lowerName);
        myHouse.getActivePlayers().remove(lowerName);

        // put the player in the fighting list and update status
        myHouse.getFightingPlayers().put(lowerName, this);
        CampaignMain.cm.toUser("CS|" + +SPlayer.STATUS_FIGHTING, name, false);
    }

    /**
     * A method which grants influence to players. Called from CampaignMain on
     * slices if the player meets various and sundry activity requirements.
     * 
     * This should only be called from CampaignMain, and only on a slice. Any
     * other grant of influence should use addInfluence(int). Post-game adds are
     * now handled by the ShortResolver (whereas the old Tasks code granted flu
     * for as many slices as the player was busy, to a max).
     * 
     * Alot of ugly casting to reduce rounding errors when multiplying/dividing
     * with ints imported from config, and because getting an evenly
     * distributed, declared range double from java.util.Random isnt possible.
     * 
     * @param p -
     *            player to have influence granted
     * @return String - random message and influence information
     */
    protected String addInfluenceAtSlice() {

        MWServ.mwlog.debugLog("Starting addInfluenceAtSlice for "
                + this.getName());

        // cant get any inf beyond ceiling, so no reason to do the math
        int fluCeiling = Integer.parseInt(this.getMyHouse().getConfig(
                "InfluenceCeiling"));
        MWServ.mwlog.debugLog("getting max flu");
        if (influence >= fluCeiling) {
            MWServ.mwlog.debugLog("returning");
            return "";
        }

        MWServ.mwlog.debugLog("checking for merc house");
        // mercs who are active but w/o contract get no flu
        if (this.getHouseFightingFor().isMercHouse())
            return "";
        MWServ.mwlog.debugLog("not in merc house");
        /*
         * Passed simple returns. Now check the player's activity time and make
         * sure he has at least 1 (after weighting) eligible army.
         */
        double weightedNumArmies = this.getWeightedArmyNumber();
        boolean activeLongEnough = (System.currentTimeMillis() - activeSince) > Integer
                .parseInt(this.getMyHouse().getConfig("InfluenceTimeMin"));

        MWServ.mwlog
                .debugLog("check active status, army number/weight, activelong enough");
        if (this.getDutyStatus() == STATUS_ACTIVE && weightedNumArmies > 0
                && activeLongEnough) {

            MWServ.mwlog.debugLog(this.getName() + " is active!");
            // if player has been on long enough to get influece,
            // do all of the math to determine influence grant amount
            double totalInfluenceGrant = 0;
            double baseFlu = Double.parseDouble(this.getMyHouse().getConfig(
                    "BaseInfluence"));
            totalInfluenceGrant = (baseFlu * weightedNumArmies);

            /*
             * reduce flu gain for folks who have alot of flu already. 80% yeild
             * above 100, 60% above 150.
             */
            if (influence > (fluCeiling * .5) && influence < (fluCeiling * .75))
                totalInfluenceGrant = totalInfluenceGrant * .80;
            else if (influence >= fluCeiling * .75)
                totalInfluenceGrant = totalInfluenceGrant * .60;

            // cast totalflu back as an int
            int intFluToAdd = (int) totalInfluenceGrant;

            // Check the flu cap and adjust the grant downwards if necessary.
            int newFlu = influence + intFluToAdd;
            if (newFlu > fluCeiling)
                intFluToAdd -= (newFlu - fluCeiling);

            MWServ.mwlog.debugLog("Adding Flue");
            // then give him the flu
            this.addInfluence(intFluToAdd);

            // flu added. send the player a nice fluffy message about it.
            try {
                MWServ.mwlog.debugLog("staring up flu message");
                String fileName = "";
                MWServ.mwlog.debugLog("getting house");
                SHouse faction = this.getHouseFightingFor();

                MWServ.mwlog.debugLog("getting flu message file");
                if (faction == null)
                    fileName = "./data/influencemessages/CommonInfluenceMessages.txt";
                else
                    fileName = "./data/influencemessages/"
                            + faction.getHouseFluFile()
                            + "InfluenceMessages.txt";

                File messageFile = new File(fileName);
                if (!messageFile.exists()) {
                    fileName = "./data/influencemessages/CommonInfluenceMessages.txt";
                    messageFile = new File(fileName);
                    if (!messageFile.exists()) {
                        MWServ.mwlog
                                .errLog("A problem occured with your CommonInfluenceMessages File!");
                        return "";
                    }
                }

                FileInputStream fis = new FileInputStream(messageFile);
                BufferedReader dis = new BufferedReader(new InputStreamReader(
                        fis));

                MWServ.mwlog.debugLog("getting random flu message");
                int messages = Integer.parseInt(dis.readLine());
                Random rand = new Random();
                int messageLine = rand.nextInt(messages);
                String fluMessage = "";
                while (dis.ready()) {
                    fluMessage = dis.readLine();
                    if (messageLine <= 0)
                        break;
                    messageLine--;
                }

                dis.close();
                fis.close();

                int unitId = rand.nextInt(units.size());// random
                SUnit unitForMessages = units.elementAt(unitId);
                MWServ.mwlog.debugLog("Adding Subs for unitid: " + unitId);
                String fluMessageWithPilotName = fluMessage.replaceAll("PILOT",
                        unitForMessages.getPilot().getName());
                String fluMessageWithModelName = fluMessageWithPilotName
                        .replaceAll("UNIT", unitForMessages.getModelName());
                String fluMessageWithPlayerName = fluMessageWithModelName
                        .replaceAll("PLAYER", name);

                fluMessageWithPlayerName += " ("
                        + CampaignMain.cm.moneyOrFluMessage(false, false,
                                intFluToAdd, true) + ")";
                MWServ.mwlog.debugLog("returning [" + fluMessageWithPlayerName
                        + "] for " + this.getName());
                return fluMessageWithPlayerName;

            } catch (Exception e) {
                MWServ.mwlog
                        .errLog("A problem occured with your CommonInfluenceMessages File!");
                return "";
            }
        }
        return "";
    }// end payInfluence(Player p)

    /**
     * Method that determines the weighted number or armies a player has active.
     * Each army gives an initial weight of 1. Weight for an army is reduced if
     * its BV +/- MaxBVDifference (from campaign configuration) overlaps another
     * armies BV, falls below MinCount or rises above MaxCount.
     * 
     * In short, only the portions of an army which may be *uniquely* targetted
     * by opposing forces with the Min/Max range count fully.
     * 
     * The weight is automatically reduced by the level of overlap, and server
     * operators may declare additional overlap penalties.
     * 
     * Example: Player A has Armies of 3000 and 3050 BV. MaxBVDifference is 150,
     * and an OverlapPenalty of .20 is set in campaignconfig.txt - Starting
     * weight is 2 for two armies, - Raw amount of overlap is (150-(3050-3000 =
     * 50))/150 = .67 - Weight after raw overlap adjustment is 2.0 - 0.67 = 1.33 -
     * OverlapPenalty is applied (1.33 - .20 = 1.13)
     * 
     * In this case, the final weighted number of armies is 1.37.
     * 
     * @return int the weighted army number
     * @author urgru 10/27/04
     */
    public double getWeightedArmyNumber() {

        // only get the weight if it hasnt been calculated already.
        if (weightedArmyNumber <= 0) {

            Vector<SArmy> orderedArmies = new Vector<SArmy>(1, 1);

            int MinCount = this.getMyHouse()
                    .getIntegerConfig("MinCountForTick");
            int MaxCount = this.getMyHouse()
                    .getIntegerConfig("MaxCountForTick");
            int MaxFlatDiff = this.getMyHouse().getIntegerConfig(
                    "MaxBVDifference");
            double MaxPercentDiff = this.getMyHouse().getDoubleConfig(
                    "MaxBVPercent");

            for (SArmy currentArmy : this.getArmies()) {

                // only count armies within the defined Min/Max range
                int forceBV = currentArmy.getOperationsBV(null);
                if (forceBV <= MinCount)
                    continue;

                if (forceBV >= MaxCount)
                    continue;

                // Don't count the army if it's disabled
                if (currentArmy.isDisabled())
                    continue;

                // if they army is only set up for ops that SO's do not deem
                // legal for component production then the player doesnt get
                // anything.
                boolean fLegalOp = false;
                for (String Opname : currentArmy.getLegalOperations().keySet()) {
                    if (!CampaignMain.cm.getOpsManager().getOperation(Opname)
                            .getBooleanValue("DoesNotCountForPP")) {
                        fLegalOp = true;
                        break;
                    }
                }

                // Army does is not used in a PP legal op.
                if (!fLegalOp)
                    continue;

                /*
                 * Sort the armies into BV order, least to greatest. Take an
                 * enumeration of all armies. 1st is added to orderedArmies by
                 * default. Additional armies are compared to previously sorted
                 * BVs and inserted in front of the first element which has a
                 * higher value. If currentForce is larger than previously
                 * sorted armies it is appended to end of the vector
                 */

                // if empty, add the first force by default
                if (orderedArmies.size() == 0)
                    orderedArmies.add(currentArmy);

                else {// size > 0
                    Enumeration f = orderedArmies.elements();
                    int forceNumber = 0;// number of current army
                    boolean forceSorted = false;
                    while (f.hasMoreElements() && !forceSorted) {
                        if (currentArmy.getOperationsBV(null) < ((SArmy) f
                                .nextElement()).getOperationsBV(null)) {
                            orderedArmies.add(forceNumber, currentArmy);
                            forceSorted = true;
                        } else
                            forceNumber++;
                    }// end while(more elements to compare to)

                    if (!forceSorted)
                        orderedArmies.add(currentArmy);

                }// end else (not first)

            }// end for(each army)

            /*
             * Determine overlap of lances, now that they have been ordered.
             * Reduce payout modifier if forces cover similar value ranges. Only
             * do this if there are actually ordered armies!
             */

            weightedArmyNumber = orderedArmies.size();

            double weightMod = Math.max(0, this.getMyHouse().getDoubleConfig(
                    "BaseCountForProduction"));
            weightedArmyNumber *= weightMod;

            if (weightedArmyNumber > 0) {

                Enumeration e = orderedArmies.elements();
                SArmy currentArmy = (SArmy) e.nextElement();// get first army
                int currentBV = currentArmy.getOperationsBV(null);

                // holder for whichever is greater - flat diff or percent
                double currentMaxDiff = 0;

                /*
                 * compare first force to floor. get first army, determine
                 * percent and flat difference, then test against the BV-edge.
                 */
                double caPercentDiff = currentBV * MaxPercentDiff;
                if (MaxFlatDiff >= caPercentDiff)
                    currentMaxDiff = MaxFlatDiff;
                else
                    currentMaxDiff = caPercentDiff;

                if (currentBV - MinCount < currentMaxDiff) {
                    weightedArmyNumber -= this.getMyHouse().getDoubleConfig(
                            "FloorPenalty");
                    int overlap = currentBV - MinCount;
                    weightedArmyNumber -= (currentMaxDiff - overlap)
                            / currentMaxDiff;
                }

                /*
                 * compare intermediate forces to each other...
                 */
                SArmy nextArmy = null;// for use in loop
                int nextBV = 0;// for use in loop
                while (e.hasMoreElements()) {// loop through remaining forces

                    // get the next army, and its BV
                    nextArmy = (SArmy) e.nextElement();
                    nextBV = nextArmy.getOperationsBV(null);

                    /*
                     * test whether flat or percent BV difference is larger for
                     * these two armies. compare based on larger window.
                     */
                    if (MaxPercentDiff <= 0) {
                        currentMaxDiff = MaxFlatDiff;
                    } else {
                        if (currentBV > nextBV)
                            caPercentDiff = currentBV * MaxPercentDiff;
                        else
                            caPercentDiff = nextBV * MaxPercentDiff;

                        if (MaxFlatDiff >= caPercentDiff)
                            currentMaxDiff = MaxFlatDiff;
                        else
                            currentMaxDiff = caPercentDiff;
                    }

                    if (nextBV - currentBV < currentMaxDiff) {
                        weightedArmyNumber -= this.getMyHouse()
                                .getDoubleConfig("OverlapPenalty");
                        int overlap = nextBV - currentBV;
                        weightedArmyNumber -= (currentMaxDiff - overlap)
                                / currentMaxDiff;
                    }
                    currentArmy = nextArmy;// set up for the next iteration
                    currentBV = nextBV;// set up for the next iteration
                }// end while(more elements)

                /*
                 * compare last force to ceiling
                 */
                caPercentDiff = currentBV * MaxPercentDiff;
                if (MaxFlatDiff >= caPercentDiff)
                    currentMaxDiff = MaxFlatDiff;
                else
                    currentMaxDiff = caPercentDiff;
                if (MaxCount - currentBV < currentMaxDiff) {
                    weightedArmyNumber -= this.getMyHouse().getDoubleConfig(
                            "CeilingPenalty");
                    int overlap = MaxCount - currentBV;
                    weightedArmyNumber -= (currentMaxDiff - overlap)
                            / currentMaxDiff;
                }

                /*
                 * Remove armies which cannot attack from the weighting AFTER
                 * overlap checks in order to discourage any abusive stacking.
                 */
                for (SArmy currA : orderedArmies) {
                    if (currA.getLegalOperations().size() <= 0)
                        weightedArmyNumber--;
                }

                // make sure at least 1 is returned, in case penalties create <1
                // cases.
                if (weightedArmyNumber < 1)
                    weightedArmyNumber = 1;

            }// end if(armies were ordered)
        }// end if (weighted <= 0)

        return weightedArmyNumber;
    }

    /**
     * A method which resets the weightedArmyNumber to -1, forcing a
     * recalculation next time the above method (getWeightedArmyNumber) is
     * called. Should be triggered by anything which changes army BV or army
     * numbers - game resolution and EXM, etc.
     * 
     * @urgru 11/12/04
     */
    public void resetWeightedArmyNumber() {
        weightedArmyNumber = -1;
    }

    public void reset(String confirm) {

        if (!confirm.equals("CONFIRM"))
            return;

        this.armies.clear();
        this.units.clear();
        this.money = 0;
        this.exclusionList.getAdminExcludes().clear();
        this.exclusionList.getPlayerExcludes().clear();
        this.experience = 0;
        this.baysOwned = 0;
        this.availableTechs.clear();
        this.totalTechs.clear();
        this.technicians = 0;
        this.fluffText = " ";
        this.currentReward = 0;
        this.groupAllowance = 0;
        this.influence = 0;
        this.myHouse = CampaignMain.cm.getHouseFromPartialString(this
                .getMyHouse().getConfig("NewbieHouseName"), null);
        this.myLogo = " ";
        this.personalPilotQueue.flushQueue();
        this.rating = 1600;
        this.xpToReward = 0;
        this.sellingto = " ";
        this.weightedArmyNumber = 0;
        this.setSave();
    }

    /**
     * Add money to a player. Money is always modified relative to a previous
     * amount (this.fromString is an expetion, but sets the value directly), so
     * there is no need for a public SPlayer.setMoney() method.
     */
    public void addMoney(int i) {

        // holder, amount to store.
        int moneyToSet = money + i;

        // don't let SOL exceed cap, or anyone have negative cash
        int maxNewbieCbills = Integer.parseInt(this.getMyHouse().getConfig(
                "MaxSOLCBills"));
        if (myHouse.isNewbieHouse() && moneyToSet > maxNewbieCbills)
            moneyToSet = maxNewbieCbills;
        if (moneyToSet < 0)
            moneyToSet = 0;

        // change the value and send an update
        this.money = moneyToSet;
        CampaignMain.cm.toUser("PL|SM|" + money, name, false);
        setSave();
    }

    /**
     * Get the amount of money the player currently has on hand. Required for
     * IBuyer.
     */
    public int getMoney() {
        return money;
    }

    public void setPassword(MWPasswdRecord pass) {

        if (pass == null) {
            try {
                throw new Exception();
            } catch (Exception ex) {
                MWServ.mwlog.errLog(ex);
            }
        }
        this.password = pass;
        this.setSave();
    }

    public MWPasswdRecord getPassword() {
        return this.password;
    }

    /**
     * Method required for ISeller compliance. Used to distinguish between human
     * controlled actors (this class) and factions/automated actors (SHouse).
     */
    public boolean isHuman() {
        return true;
    }

    /**
     * Simple method that returns a player's faction.
     */
    public SHouse getMyHouse() {
        return myHouse;
    }

    /**
     * Method which determines which house a player is actually fighting for.
     * Used to display contracting house, instead of real faction, for
     * mercenaries.
     */
    public SHouse getHouseFightingFor() {
        return getMyHouse().getHouseFightingFor(this);
    }

    /**
     * Set the player's faction. Should only be used by Defect, ForcedDefect and
     * Enroll commands.
     */
    public void setMyHouse(SHouse h) {
        myHouse = h;
        setSave();
    }

    /**
     * A Method to get the current duty status of a player. Options are, from
     * lowest to hightest, STATUS_LOGGEDOUT, STATUS_RESERVE, STATUS_ACTIVE, and
     * STATUS_FIGHTING.
     */
    public int getDutyStatus() {

        String lowerName = name.toLowerCase();

        // Fighting
        if (myHouse.getFightingPlayers().containsKey(lowerName))
            return SPlayer.STATUS_FIGHTING;

        // Active
        if (myHouse.getActivePlayers().containsKey(lowerName))
            return STATUS_ACTIVE;

        // Logged into house
        if (myHouse.getReservePlayers().containsKey(lowerName))
            return SPlayer.STATUS_RESERVE;

        // Not in any faction hash. he's logged out.
        return SPlayer.STATUS_LOGGEDOUT;
    }

    /**
     * Determines the weighted number of votes a player can cast. Draws a flat
     * config out of campaignconfig.txt to use as a base number. Additonal votes
     * may be assigned as a player gains XP, up to a configurable ceiling. Used
     * by the various vote cmds to block overvoting, etc.
     * 
     * @return int representing total # of votes player is allowed to cast.
     */
    public int getNumberOfVotesAllowed() {

        int voteTotal = Integer.parseInt(this.getMyHouse().getConfig(
                "StartingVotes"));
        int xpForVote = Integer.parseInt(this.getMyHouse().getConfig(
                "XPForAdditionalVote"));
        int maxVotes = Integer.parseInt(this.getMyHouse().getConfig(
                "MaximumVotes"));

        voteTotal += (int) Math.floor(experience / xpForVote);
        if (voteTotal > maxVotes)
            voteTotal = maxVotes;

        return voteTotal;
    }

    /**
     * Strip the player's units. They disappear forever and are NOT given to the
     * player's house.
     * 
     * @param sendStatus -
     *            boolean. if true, send the player's status downstream. should
     *            usually be true. false when called from NewbieHouse, which
     *            send status on its own after granting new units.
     */
    public void stripOfAllUnits(boolean sendStatus) {
        if (CampaignMain.cm.isUsingMySQL()) {
            // We have to remove them from the database, or we'll have stale
            // data
            // We also have to remove the armies.
            CampaignMain.cm.MySQL.clearArmies(getDBId());
            for (SUnit currU : units) {
                SPilot p = (SPilot) currU.getPilot();
                CampaignMain.cm.MySQL.deletePilot(p.getDBId());
                CampaignMain.cm.MySQL.deleteUnit(currU.getDBId());
            }
        }

        units = new Vector<SUnit>(1, 1);
        armies = new Vector<SArmy>(1, 1);

        if (sendStatus)
            CampaignMain.cm.toUser("PS|" + this.toString(true), name, false);

        setSave();
    }

    // EXPERIENCE SET/ADD/GET Methods
    /**
     * 
     * Add experience to the player. Boolean param is used to prevent RP gain
     * from mod/admin XP additions.
     * 
     * @param i -
     *            amount of RP to add
     * @param modAdded -
     *            true if added from a mod/admin command
     */
    public void addExperience(int i, boolean modAdded) {

        // change xp
        experience += i;

        // check floor
        if (experience < 0)
            experience = 0;

        // check SOL cap
        if (myHouse.isNewbieHouse()
                && experience > Integer.parseInt(this.getMyHouse().getConfig(
                        "MaxSOLExp")))
            experience = Integer.parseInt(this.getMyHouse().getConfig(
                    "MaxSOLExp"));

        // update client & all userlists
        CampaignMain.cm.toUser("PL|SE|" + experience, name, false);
        CampaignMain.cm.doSendToAllOnlinePlayers("PI|EX|" + name + "|"
                + experience, false);

        // update corresponding small player.
        SmallPlayer smallp = myHouse.getSmallPlayers().get(name.toLowerCase());
        if (smallp != null)
            smallp.setExperience(experience);

        // check and send mek bay numbers
        CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name, false);
        CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name, false);

        // check reward, if not mod added. never reduce rollover counter.
        if (!modAdded && i > 0) {

            int currentXP = xpToReward + i;
            int rollOver = (Integer.parseInt(this.getMyHouse().getConfig(
                    "XPRollOverCap")));

            // if XP is over rollover point, reduce until below again
            if (currentXP >= rollOver) {

                int rpToAdd = 0;
                while (currentXP >= rollOver) {
                    currentXP -= rollOver;
                    rpToAdd++;
                }

                addReward(rpToAdd);

                // reset the counter
                setXPToReward(currentXP);

                // set up and send upe rp link
                String toSend = "You earned " + rpToAdd + " experience reward ";
                if (rpToAdd > 1)
                    toSend += " points ";
                else
                    toSend += " point ";
                toSend += "[<a href=\"MWUSERP\">Use RP</a>]";
                CampaignMain.cm.toUser(toSend, name, true);

            } else {
                setXPToReward(currentXP);
            }
        }

        setSave();
    }

    public int getExperience() {
        return experience;
    }

    // SPECIAL USE METHODS (PRIVATE OR PUBLIC&STATIC)
    /**
     * Determine the total BV of all units owned by the player. This is used by
     * the welfare checks to see whether a players units can form an army of
     * sufficient BV.
     * 
     * Note that for_sale units are included in the BV total, because skipping
     * them would allow players to list a unit, get welfare units, and then
     * delist the sales unit in order. Freebies is something we want to avoid,
     * because people are evil and cheat.
     * 
     * @author Jason Tighe.
     * @return the total bv of the player's units.
     */
    private int getHangarBV() {
        int bv = 0;
        for (SUnit currU : units)
            bv += currU.getBV();
        return bv;
    }

    /**
     * Simple private method which returns the next available free position ID
     * (hangar location). While this seems pointless, and probably is, the
     * hangar ID is used by the client for all kinds of things and we're stuck
     * with it until someone takes the time to weed it out completely.
     */
    private int getFreeID() {
        int id = 0;
        boolean found = false;
        while (!found) {
            found = true;
            for (int i = 0; i < units.size(); i++) {
                if (units.get(i).getPosId() == id) {
                    found = false;
                    id++;
                }
            }
        }
        return id;
    }

    public int getFreeArmyId() {
        int i = 0;
        boolean free = false;
        while (!free) {
            free = true;
            for (int j = 0; j < getArmies().size(); j++) {
                if (getArmies().elementAt(j).getID() == i) {
                    free = false;
                    i++;
                }
            }
        }

        return i;
    }

    // METHODS TO CHECK/COMMENT
    /**
     * @author Jason Tighe aka Torren
     * @return if the player is eligible for welfare light meks from faction
     *         bays. due to lack of mechs in bay and they are all light
     */
    public boolean mayAcquireWelfareUnits() {

        if (this.getHangarBV() < Integer.parseInt(this.getMyHouse().getConfig(
                "WelfareTotalUnitBVCeiling"))
                && this.getMoney() < Integer.parseInt(this.getMyHouse()
                        .getConfig("WelfareCeiling")))
            return true;

        // else
        return false;
    }

    /**
     * A method to add a specified amount of influence
     * 
     * @param i -
     *            amount of influence to add
     */
    public void addInfluence(int i) {
        setInfluence(this.getInfluence() + i);
    }

    /**
     * @return current post-game payment to technicians, in Cbills
     */
    @Override
    public int getCurrentTechPayment() {

        // recalculate if -1
        if (currentTechPayment < 0)
            this.doPayTechniciansMath();

        return currentTechPayment;
    }

    /**
     * @param i -
     *            post-game payment to set, in Cbills
     */
    @Override
    public void setCurrentTechPayment(int i) {
        currentTechPayment = i;
        setSave();
    }

    /**
     * @return the number of technicians the player has
     */
    @Override
    public int getTechnicians() {
        if (CampaignMain.cm.isUsingAdvanceRepair())
            return getBaysOwned();
        // else
        return technicians;
    }

    public Vector<Integer> getTotalTechs() {
        return totalTechs;
    }

    public Vector<Integer> getAvailableTechs() {
        return availableTechs;
    }

    public String totalTechsToString() {
        StringBuilder result = new StringBuilder();

        // Make sure that we keep it as size 4. Had some early issues with rouge
        // vectors.
        getTotalTechs().setSize(4);
        for (Integer tech : getTotalTechs())
            result.append(tech + "%");

        return result.toString();
    }

    public String availableTechsToString() {
        StringBuilder result = new StringBuilder();

        // Make sure that we keep it as size 4. Had some early issues with rouge
        // vectors.
        getAvailableTechs().setSize(4);
        for (Integer tech : getAvailableTechs())
            result.append(tech + "%");

        return result.toString();
    }

    public void addAvailableTechs(int type, int number) {

        if (type > UnitUtils.TECH_ELITE)
            return;

        int techs = this.getAvailableTechs().elementAt(type);

        techs += number;

        this.getAvailableTechs().set(type, techs);

        CampaignMain.cm.toUser("PL|UAT|" + this.availableTechsToString(), name,
                false);

    }

    public void setAvailableTechs(int type, int number) {

        if (type > UnitUtils.TECH_ELITE)
            return;

        this.getAvailableTechs().set(type, number);

        CampaignMain.cm.toUser("PL|UAT|" + this.availableTechsToString(), name,
                false);

    }

    public void addTotalTechs(int type, int number) {

        if (type > UnitUtils.TECH_ELITE)
            return;

        int techs = this.getTotalTechs().elementAt(type);
        techs += number;
        this.getTotalTechs().set(type, techs);

        CampaignMain.cm.toUser("PL|UTT|" + this.totalTechsToString(), name,
                false);
    }

    public void setTotalTechs(int type, int number) {
        if (type > UnitUtils.TECH_ELITE)
            return;

        this.getTotalTechs().set(type, number);
        CampaignMain.cm.toUser("PL|UTT|" + this.totalTechsToString(), name,
                false);
    }

    public void updateAvailableTechs(String data) {
        try {
            StringTokenizer techs = new StringTokenizer(data, "%");
            int techType = UnitUtils.TECH_GREEN;
            while (techs.hasMoreTokens()) {
                this.setAvailableTechs(techType, Integer.parseInt(techs
                        .nextToken()));
                techType++;
            }
        } catch (Exception ex) {
        }

    }

    public void updateTotalTechs(String data) {
        try {
            StringTokenizer techs = new StringTokenizer(data, "%");
            int techType = UnitUtils.TECH_GREEN;

            while (techs.hasMoreTokens()) {
                this.setTotalTechs(techType, TokenReader.readInt(techs));
                techType++;
            }
        } catch (Exception ex) {
        }

    }

    public int getBaysOwned() {
        return baysOwned;
    }

    public void setBaysOwned(int bays) {

        int maxBays = 0;

        if (this.getMyHouse() != null)
            maxBays = Integer.parseInt(this.getMyHouse().getConfig(
                    "MaxBaysToBuy"));
        else
            maxBays = CampaignMain.cm.getIntegerConfig("MaxBaysToBuy");

        if (maxBays != -1)
            baysOwned = Math.min(maxBays, bays);
        else
            baysOwned = bays;
    }

    public void addBays(int bays) {
        setBaysOwned(baysOwned + bays);
    }

    public String getLastISP() {
        return lastISP;
    }

    public void setLastISP(String isp) {
        lastISP = isp;
    }

    /**
     * @param t -
     *            int to set technicians to.
     */
    @Override
    public void setTechnicians(int t) {

        // dont allow negative techs. always set negatives back to 0.
        if (t < 0)
            t = 0;

        technicians = t;

        // clear the tech payment any time a new number of techs is set
        this.setCurrentTechPayment(-1);
        CampaignMain.cm.toUser("PL|ST|" + t, name, false);
        CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name, false);
        CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name, false);
        setSave();
    }

    /**
     * @param t -
     *            the number of technicians to add (subtract) from the player's
     *            total sub-zero cases are checked in setTechs(). no check here.
     */
    @Override
    public void addTechnicians(int t) {
        if (CampaignMain.cm.isUsingAdvanceRepair())
            this.addBays(t);
        else
            this.setTechnicians(this.getTechnicians() + t);
    }

    // NAME GET/SET METHODS
    public String getName() {
        return name;
    }

    public String getColoredName() {
        return "<font color=\"" + getHouseFightingFor().getHouseColor() + "\">"
                + name + "</font>";
    }

    public void setName(String s) {
        name = s;
        setSave();
    }

    public SArmy getArmy(int id) {

        for (SArmy currA : armies) {
            if (currA.getID() == id)
                return currA;
        }

        return null;
    }

    public Vector<SArmy> getArmies() {
        return armies;
    }

    public void removeArmy(int armyID) {

        Iterator<SArmy> i = armies.iterator();
        while (i.hasNext()) {
            SArmy currA = i.next();
            if (currA.getID() == armyID) {
                i.remove();
                break;
            }
        }
        if (CampaignMain.cm.isUsingMySQL()) {
            CampaignMain.cm.MySQL.deleteArmy(getDBId(), armyID);
        }
        CampaignMain.cm.toUser("PL|RA|" + armyID, name, false);
    }

    public void setArmies(Vector<SArmy> v) {
        armies = v;
        setSave();
    }

    public Vector<SUnit> getUnits() {
        return units;
    }

    // Comparable
    public int compareTo(Object o) {
        SPlayer p = (SPlayer) o;
        if (this.getRating() > p.getRating())
            return 1;
        else if (this.getRating() < p.getRating())
            return -1;
        return p.getName().compareTo(name);
    }

    public int getScrapsThisTick() {
        return scrapsThisTick;
    }

    public void addScrapThisTick() {
        scrapsThisTick += 1;
    }

    public void setScrapsThisTick(int scraps) {
        scrapsThisTick = scraps;
    }

    public int getDonationsThisTick() {
        return donationsThisTick;
    }

    public void addDonationThisTick() {
        donationsThisTick += 1;
    }

    public void setDonatonsThisTick(int donations) {
        donationsThisTick = donations;
    }

    public Date getLastOnlineDate() {
        return new Date(lastOnline);
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long l) {
        lastOnline = l;
        SmallPlayer smallp = null;
        if (myHouse.getSmallPlayers().containsKey(name.toLowerCase()))
            // update the corresponding small player.
            smallp = myHouse.getSmallPlayers().get(name.toLowerCase());
        else {
            smallp = new SmallPlayer(this.getExperience(), lastOnline, this
                    .getRating(), this.getName(), this.getFluffText(), this
                    .getMyHouse());
            myHouse.getSmallPlayers().put(name.toLowerCase(), smallp);
        }

        smallp.setLastOnline(lastOnline);
    }

    public long getAttackRestrictionUntil() {
        return attackRestrictionUntil;
    }

    public void setAttackRestrictionUntil(long l) {
        attackRestrictionUntil = l;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double d) {
        rating = d;

        // update the corresponding small player.
        SmallPlayer smallp = myHouse.getSmallPlayers().get(name.toLowerCase());
        smallp.setRating(rating);

        // if sharing ratings, send to clients
        if (!Boolean.parseBoolean(this.getMyHouse().getConfig("HideELO"))) {
            Double rounded = this.getRatingRounded();
            CampaignMain.cm.toUser("PL|SR|" + rounded, name, false);
            CampaignMain.cm.doSendToAllOnlinePlayers("PI|RA|" + name + "|"
                    + rounded, false);
        }

        setSave();
    }

    public String getFluffText() {
        if (fluffText.length() > 0 && !fluffText.equals(" ")
                && !fluffText.equals("0"))
            return fluffText;
        return "";
    }

    public void setFluffText(String s) {
        fluffText = s;

        // update the corresponding small player.
        SmallPlayer smallp = myHouse.getSmallPlayers().get(name.toLowerCase());
        smallp.setFluffText(fluffText);

        setSave();
    }

    public String getLastSentStatus() {
        return lastSentStatus;
    }

    public void setLastSentStatus(String s) {
        lastSentStatus = s;
    }

    /**
     * @return Returns the activeSince.
     */
    public long getActiveSince() {
        return activeSince;
    }

    public int getAmountOfTimesUnitExistsInArmies(int unitID) {
        int result = 0;
        Vector v = getArmies();
        for (int i = 0; i < v.size(); i++) {
            SArmy a = (SArmy) v.elementAt(i);
            if (a.getUnit(unitID) != null)
                result++;
        }
        return result;
    }

    public void checkAndUpdateArmies(SUnit unit) {

        for (SArmy army : armies) {
            if (army.isUnitInArmy(unit)) {
                army.setBV(0);
                CampaignMain.cm.toUser("PL|SABV|" + army.getID() + "#"
                        + army.getBV(), name, false);
            }
        }
    }// end checkAndUpdateArmies

    // INFLUENCE SET/ADD/GET METHODS @urgru 1/30/03
    /**
     * A method which returns a players influence
     * 
     * @return int - influence amount
     */
    public int getInfluence() {
        return influence;
    }

    /**
     * A method which directly sets the amount of influence a player has
     * 
     * @param i -
     *            value to give influence
     */
    public void setInfluence(int i) {
        influence = i;
        if (influence > Integer.parseInt(this.getMyHouse().getConfig(
                "InfluenceCeiling")))
            influence = (Integer.parseInt(this.getMyHouse().getConfig(
                    "InfluenceCeiling")));// set to ceiling if above

        if (influence < 0)
            influence = 0; // Set to 0 if below

        CampaignMain.cm.toUser("PL|SI|" + influence, name, false);
        setSave();
    }

    public int getGroupAllowance() {
        return groupAllowance;
    }

    public void setGroupAllowance(int i) {
        groupAllowance = i;
    }

    // get current amount of reward points a player has
    public int getReward() {
        return currentReward;
    }

    // set the current amount of reward points a player has.
    public void setReward(int i) {
        currentReward = i;
        if (currentReward > (Integer.parseInt(this.getMyHouse().getConfig(
                "XPRewardCap"))))
            currentReward = (Integer.parseInt(this.getMyHouse().getConfig(
                    "XPRewardCap")));

        CampaignMain.cm.toUser("PL|SRP|" + currentReward, name, false);
        setSave();
    }

    public void addReward(int toAdd) {
        this.setReward(this.getReward() + toAdd);
    }

    // sets how much exp the player has towards an award point.
    public void setXPToReward(int xp) {
        xpToReward = xp;
        setSave();
    }

    public int getXPToReward() {
        return xpToReward;
    }

    public void setMyLogo(String s) {
        myLogo = s;
    }

    public String getMyLogo() {
        return myLogo;
    }

    public void setPlayerSellingto(String selling) {
        sellingto = selling;
    }

    public String getPlayerSellingto() {
        return sellingto;
    }

    public void setPlayerClientVersion(String version) {
        clientVersion = version;
    }

    public String getPlayerClientVersion() {
        return clientVersion;
    }

    public SPersonalPilotQueues getPersonalPilotQueue() {
        return personalPilotQueue;
    }

    public ExclusionList getExclusionList() {
        return exclusionList;
    }

    public void setLastTimeCommandSent(long l) {
        lastTimeCommandSent = l;
    }

    public long getLastTimeCommandSent() {
        return lastTimeCommandSent;
    }

    public void setLastAttackFromReserve(long time) {
        lastAttackFromReserve = time;
    }

    public long getLastAttackFromReserve() {
        return lastAttackFromReserve;
    }

    public boolean hasRepairingUnits() {
        return hasRepairingUnits(true);
    }

    /**
     * Method which returns a boolean indicating whether any units in all the
     * armies or any units period are being repaired.
     * 
     * @param inArmy -
     *            if false, check all units. true, check units in armies.
     */
    public boolean hasRepairingUnits(boolean inArmy) {

        // if not using advanced repair don't spend the time checking.
        if (!CampaignMain.cm.isUsingAdvanceRepair())
            return false;

        // only check for a repairing unit that is currently in an army
        if (inArmy) {

            for (SArmy army : armies) {
                for (Unit currU : army.getUnits()) {
                    // Needs to be done units are stripped of entity in the army
                    // Might be best to add that to Unit as well have to think
                    // about that --Torren.
                    // TODO: See comment above.
                    SUnit unit = this.getUnit(currU.getId());
                    if (UnitUtils.isRepairing(unit.getEntity()))
                        return true;
                }
            }// end For
        } else {// check for any repairing units the player owns

            for (SUnit currU : getUnits()) {
                if (UnitUtils.isRepairing(currU.getEntity()))
                    return true;
            }

        }// end else

        return false;
    }// end hasRepairingUnits

    /**
     * Used for Advanced Repair cannot repair a unit that is in combat.
     * 
     * @param unitID
     * @return
     */
    public boolean isUnitInLockedArmy(int unitID) {

        if (this.getUnit(unitID) == null)
            return false;

        // check all armies
        for (SArmy army : getArmies()) {
            if (!army.isLocked())
                continue;
            if (army.getUnit(unitID) != null)
                return true;
        }

        return false;
    }// end isUnitInLockedArmy

    /**
     * if damage transfers is allowed then pilots to heal while off line.
     */
    public void healAllPilots() {
        try {
            if (!Boolean.parseBoolean(this.getMyHouse().getConfig(
                    "AllowPilotDamageToTransfer")))
                return;
            Long timeGone = System.currentTimeMillis() - lastOnline;// timeGone
                                                                    // /=60000;
            int tickTime = CampaignMain.cm.getIntegerConfig("TickTime");
            if (timeGone > tickTime)
                healAllPilots((int) (timeGone / tickTime));
        } catch (Exception ex) {
            MWServ.mwlog.errLog(ex);
        }
    }

    public void healAllPilots(int numberOfHeals) {
        if (!Boolean.parseBoolean(this.getMyHouse().getConfig(
                "AllowPilotDamageToTransfer")))
            return;
        int health = Integer.parseInt(this.getMyHouse().getConfig(
                "PilotAmountHealedPerTick"))
                * numberOfHeals;
        int medtechHeal = Integer.parseInt(this.getMyHouse().getConfig(
                "MedTechAmountHealedPerTick"))
                * numberOfHeals;

        if (Boolean.parseBoolean(this.getMyHouse().getConfig(
                "AllowPersonalPilotQueues"))) {
            for (int type = 0; type < 2; type++) {
                for (int weight = 0; weight <= Unit.ASSAULT; weight++) {
                    List<Pilot> list = personalPilotQueue.getPilotQueue(type,
                            weight);
                    for (Pilot pilot : list) {
                        if (pilot.getHits() <= 0)
                            continue;
                        int hits = pilot.getHits();

                        hits -= health;
                        if (pilot.getSkills().has(PilotSkill.MedTechID))
                            hits -= medtechHeal;

                        pilot.setHits(Math.max(0, hits));
                    }// end For each
                }// end for weight
            }// end for type
        }

        for (SUnit unit : units) {
            Pilot pilot = unit.getPilot();

            if (pilot.getHits() <= 0)
                continue;
            int hits = pilot.getHits();

            hits -= health;
            if (pilot.getSkills().has(PilotSkill.MedTechID))
                hits -= medtechHeal;

            pilot.setHits(Math.max(0, hits));
        }// end for each
    }// end healAllPilots

    public void healPilots() {
        if (!Boolean.parseBoolean(this.getMyHouse().getConfig(
                "AllowPilotDamageToTransfer")))
            return;
        int health = Integer.parseInt(this.getMyHouse().getConfig(
                "PilotAmountHealedPerTick"));
        int medtechHeal = Integer.parseInt(this.getMyHouse().getConfig(
                "MedTechAmountHealedPerTick"));

        if (Boolean.parseBoolean(this.getMyHouse().getConfig(
                "AllowPersonalPilotQueues"))) {
            for (int type = 0; type < 2; type++) {
                for (int weight = 0; weight <= Unit.ASSAULT; weight++) {
                    List<Pilot> list = personalPilotQueue.getPilotQueue(type,
                            weight);
                    for (Pilot pilot : list) {
                        if (pilot.getHits() <= 0)
                            continue;
                        int hits = pilot.getHits();

                        hits -= health;
                        if (pilot.getSkills().has(PilotSkill.MedTechID))
                            hits -= medtechHeal;

                        pilot.setHits(Math.max(0, hits));
                    }// end For each
                }// end for weight
            }// end for type
        } else {
            for (SUnit unit : units) {
                Pilot pilot = unit.getPilot();

                if (pilot.getHits() <= 0)
                    continue;
                int hits = pilot.getHits();

                hits -= health;
                if (pilot.getSkills().has(PilotSkill.MedTechID))
                    hits -= medtechHeal;

                pilot.setHits(Math.max(0, hits));
            }// end for each
        }// end else
    }// end healPilots

    // STATUS DISPLAY METHODS
    /*
     * These would normally be under the PUBLIC METHODS heading; however,
     * they're important (and long) enough to justify their own heading.
     */
    /**
     * Complete human readable status of a player. Absolutely must be maintained
     * and properly updated at all times. /c mystatus is the best/only way to
     * accurately confirm a client's data representation vs. the player's state
     * according to the server.
     */
    public String getReadableStatus(boolean adminStatus) {
        DecimalFormat myFormatter = new DecimalFormat("####.##");
        StringBuilder s = new StringBuilder("<br><b>Status for: "
                + getColoredName() + " (" + myHouse.getColoredName());

        if (getSubFactionName().trim().length() > 0) {
            s.append("::");
            s.append(getSubFactionName());
        }

        s.append(")</b><br>");

        // if being checked by an admin, show his activity status.
        if (adminStatus) {
            s.append("Activity Status: ");
            if (this.getDutyStatus() == STATUS_FIGHTING)
                s.append("fighting<br>");
            else if (this.getDutyStatus() == STATUS_ACTIVE)
                s.append("active<br>");
            else
                s.append("inactive<br>");

            if (this.getGroupAllowance() > 0)
                s.append("IP Group Allowance: " + this.getGroupAllowance()
                        + "<br>");
        }

        s.append("  "
                + CampaignMain.cm.moneyOrFluMessage(true, false, getMoney())
                + " //  "
                + CampaignMain.cm.moneyOrFluMessage(false, false, influence)
                + " // " + experience + " Experience<br>");

        // advanced repair
        if (CampaignMain.cm.isUsingAdvanceRepair()) {
            s.append("Technicians (Green/Reg/Vet/Elite): "
                    + this.getTotalTechs().elementAt(UnitUtils.TECH_GREEN)
                    + "/" + this.getTotalTechs().elementAt(UnitUtils.TECH_REG)
                    + "/" + this.getTotalTechs().elementAt(UnitUtils.TECH_VET)
                    + "/"
                    + this.getTotalTechs().elementAt(UnitUtils.TECH_ELITE)
                    + "<br>");
            s.append("Idle Techs (Green/Reg/Vet/Elite):  "
                    + this.getAvailableTechs().elementAt(UnitUtils.TECH_GREEN)
                    + "/"
                    + this.getAvailableTechs().elementAt(UnitUtils.TECH_REG)
                    + "/"
                    + this.getAvailableTechs().elementAt(UnitUtils.TECH_VET)
                    + "/"
                    + this.getAvailableTechs().elementAt(UnitUtils.TECH_ELITE)
                    + "<br>");
            s.append("Bays: " + this.getFreeBays() + "/"
                    + this.getTotalMekBays() + "<br>");
            s.append("Leased Bays: "
                    + getBaysOwned()
                    + " (Cost: "
                    + CampaignMain.cm.moneyOrFluMessage(true, false,
                            getCurrentTechPayment()) + "/Game)<br>");
        }

        // normal techs
        else {
            s.append("Technicians (Idle/Total): " + this.getFreeBays() + "/"
                    + getTotalMekBays() + "<br>");
            s.append("Paid Technicians: "
                    + getTechnicians()
                    + " (Cost: "
                    + CampaignMain.cm.moneyOrFluMessage(true, false,
                            getCurrentTechPayment()) + "/Game)<br>");
        }

        // give the players some basic vote info. should use /c myvotes to get
        // full vote info
        if (Boolean.parseBoolean(this.getMyHouse().getConfig("VotingEnabled"))) {
            int votesCast = CampaignMain.cm.getVoteManager()
                    .getAllVotesBy(this).size();
            int votesAllowed = this.getNumberOfVotesAllowed();
            if (votesAllowed == votesCast)
                s.append("Votes: All votes cast (" + votesCast + "/"
                        + votesAllowed + ").<br>");
            else
                s.append("Votes: " + votesCast + " votes cast. " + votesAllowed
                        + " votes allowed. (" + votesCast + "/" + votesAllowed
                        + ").<br>");
        }// end if(voting is allowed)

        if (!Boolean.parseBoolean(this.getMyHouse().getConfig("HideELO"))
                && !adminStatus)
            s
                    .append("Rating: " + myFormatter.format(this.getRating())
                            + "<br>");
        if (Boolean.parseBoolean(this.getMyHouse().getConfig("ShowReward")))
            s.append("Current Reward Points: "
                    + getReward()
                    + " (Maximum  of "
                    + Integer.parseInt(this.getMyHouse().getConfig(
                            "XPRewardCap")) + ")<br>");

        // if merc show their status.
        if (myHouse.isMercHouse())
            s.append("<br>" + getReadableMercStatus());

        s.append("<br>");

        if (Integer.parseInt(this.getMyHouse().getConfig("NoPlayListSize")) > 0
                || exclusionList.getAdminExcludes().size() > 0) {

            // player no-play
            s.append("<b>No-Play List:</b> ");
            Enumeration en = exclusionList.getPlayerExcludes().elements();
            if (en.hasMoreElements())
                s.append((String) en.nextElement());
            else
                s.append("empty");

            while (en.hasMoreElements())
                s.append(", " + (String) en.nextElement());
            s.append("<br>");

            // admin no-plays
            s.append("<b>No-Play (Admin):</b> ");
            en = exclusionList.getAdminExcludes().elements();
            if (en.hasMoreElements())
                s.append((String) en.nextElement());
            else
                s.append("empty");
            while (en.hasMoreElements())
                s.append(", " + (String) en.nextElement());

            s.append("<br><br>");
        }

        s.append("<b>Current Armies:<br></b>");

        if (armies.size() == 0)
            s.append("(No armies constructed)<br>");
        else {
            // proceed to list lances and hangar contents
            for (SArmy currA : armies) {
                if (adminStatus)
                    s.append(currA.getDescription(true, true, false) + "<br>");
                else
                    s.append(currA.getDescription(true, true, true) + "<br>");
            }
        }

        s.append("<br><b>Contents of Hangar:</b><br>");
        for (SUnit currU : units) {

            if (currU.getStatus() == Unit.STATUS_FORSALE)
                continue;

            if (adminStatus)
                s.append(currU.getDescription(false) + "<br>");
            else
                s.append(currU.getDescription(true) + "<br>");
        }

        // Get info for units the player is selling on Market2
        StringBuilder saleUnits = new StringBuilder();
        for (SUnit currU : units) {

            if (currU.getStatus() != Unit.STATUS_FORSALE)
                continue;

            if (adminStatus)
                saleUnits.append(currU.getDescription(false) + "<br>");
            else
                saleUnits.append(currU.getDescription(true) + "<br>");
        }

        // only include sale heading if units are actually on market
        if (saleUnits.length() > 0) {
            s.append("<br><b>Units on Market:</b><br>");
            s.append(saleUnits);
        }

        s.append("<br>");
        return s.toString();
    }

    /**
     * Method that returns a human readable string containing special info
     * pertinent to mercenaries, such an employer and contract terms.
     */
    public String getReadableMercStatus() {
        String s = "";
        if (myHouse.isMercHouse()) {// if a merc
            s = "Mercenary information for " + getName() + ": <br>";// list name
            s += "Currently fighting for: "
                    + (((MercHouse) myHouse).getHouseFightingFor(this))
                            .getName() + "<br>";// list employing faction
            ContractInfo contract = (((MercHouse) myHouse)
                    .getContractInfo(this));
            if (contract != null)
                s += contract.getInfo(this);
            else
                s += "Contract Status: Currently avaliable for hire <br>";
            s += "<br>";
        }
        return s;
    }

    // TOSTRING AND FROMSTRING METHODS
    /*
     * These would normally be under the "methods" heading; however, they're so
     * huge (and important) that they get a separate block.
     */
    public String toString(boolean toClient) {

        StringBuilder result = new StringBuilder();
        result.append("CP~");
        result.append(name);
        result.append("~");
        result.append(money);
        result.append("~");
        result.append(experience);
        result.append("~");
        result.append(units.size() + "~");
        if (units.size() > 0) {
            synchronized (units) {
                for (SUnit currU : units) {
                    currU.getPilot().setCurrentFaction(myHouse.getName());
                    result.append(currU.toString(toClient));
                    result.append("~");
                }
            }
        }
        result.append(armies.size());
        result.append("~");
        for (int i = 0; i < armies.size(); i++) {
            result.append(armies.elementAt(i).toString(toClient, "%"));
            result.append("~");
        }
        if (!toClient) {
            if (this.getMyHouse() != null)
                result.append(this.getMyHouse().getName());
            else
                result.append(CampaignMain.cm.getConfig("NewbieHouseName"));
            result.append("~");
            result.append(this.lastOnline);
            result.append("~");
        }
        result.append(this.getTotalMekBays());
        result.append("~");
        result.append(this.getFreeBays());
        result.append("~");
        if (toClient)
            if (Boolean.parseBoolean(this.getMyHouse().getConfig("HideELO")))
                result.append("0");
            else
                result.append(this.getRatingRounded());
        else
            result.append(this.rating);
        result.append("~");
        result.append(this.influence);
        result.append("~");
        if (!toClient) {
            result.append(this.fluffText);
            result.append(" ~");

            /*
             * In older code, player-prefered game options were saved here. This
             * feature has been eliminated. Because of terrible coding (using
             * the standard ~ delimiter instead of an inner delimiter like $),
             * we can't eliminate the read in without endangering older saves.
             * We'll just save a 0 for now.
             * 
             * Sometime in the future, this space can be reclaimed. @urgru
             * 12.28.05
             */
            result.append(0);
            result.append("~");
        }

        if (CampaignMain.cm.isUsingAdvanceRepair())
            result.append(getBaysOwned());
        else
            result.append(this.technicians);// used when saving to houses.dat

        // above is used when sending to client bad hack but needed for now
        result.append("~");
        result.append(this.currentReward); // saving current reward points
        result.append("~");

        /*
         * In older code, player's price modifier (mezzo) was saved here. This
         * feature has been eliminated, and the spaces can be reclaimed. @urgru
         * 9.30.06
         */
        if (!toClient) {
            if (CampaignMain.cm.isUsingMySQL())
                result.append(getDBId());
            else
                result.append("0");
            result.append("~");
            result.append(0);
            result.append("~");
        }

        result.append(myHouse.getName());
        result.append("~");
        if (toClient) {
            result.append(getHouseFightingFor().getName());
            result.append("~");
            if (getMyLogo().length() == 0)
                result.append(myHouse.getLogo());
            else
                result.append(getMyLogo());
            result.append("~");
        }
        if (!toClient) {
            result.append(xpToReward);
            result.append("~");
            result.append("0");
            result.append("~");
            result.append(this.getPersonalPilotQueue().toString(toClient));
            result.append("~");
            result.append(this.getExclusionList().adminExcludeToString("$"));
            result.append("~");
            result.append(this.getExclusionList().playerExcludeToString("$"));
            result.append("~");
            if (CampaignMain.cm.isUsingAdvanceRepair()) {
                result.append(this.totalTechsToString());
                result.append("~");
                result.append(this.availableTechsToString());
                result.append("~");
                result.append(baysOwned);
                result.append("~");
            } else {
                result.append(" ");
                result.append("~");
                result.append(" ");
                result.append("~");
                result.append(this.technicians);
                result.append("~");
            }
            if (getMyLogo().trim().length() == 0) {
                if (myHouse.getLogo().trim().length() < 1)
                    result.append(" ");
                else
                    result.append(myHouse.getLogo());
            } else
                result.append(getMyLogo());
            result.append("~");
            result.append(getLastAttackFromReserve());
            result.append("~");
            result.append(getGroupAllowance());
            result.append("~");
            if (lastISP.length() < 1)
                result.append(" ");
            else
                result.append(lastISP);
            result.append("~");
        }
        result.append(isInvisible());
        result.append("~");

        if (!toClient) {
            result.append(groupAllowance);
            result.append("~");
            if (this.password != null) {
                result.append(this.password.getAccess());
                result.append("~");
                result.append(this.password.getPasswd());
                result.append("~");
                result.append(this.password.getTime());
                result.append("~");
            } else {
                result.append("0~ ~0~");
            }
        }

        result.append(unitParts.toString());
        result.append("~");

        result.append(this.getAutoReorder());
        result.append("~");

        if (!toClient) {
            result.append(this.getTeamNumber());
            result.append("~");

            if (this.getSubFactionName().trim().length() < 1)
                result.append(" ");
            else
                result.append(this.getSubFactionName());
            result.append("~");
            result.append(this.getLastPromoted());
            result.append("~");
        }

        return result.toString();
    }

    public void toDB() {
        if (this.isLoading)
            return;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer();
        try {
            MWServ.mwlog.dbLog("Saving player " + getName() + " (DBID: "
                    + getDBId() + ")");
            if (getDBId() == 0) {
                // Not in the database - INSERT it
                sql.setLength(0);
                sql.append("INSERT into players set ");
                sql.append("playerName = ?, ");
                sql.append("playerMoney = ?, ");
                sql.append("playerExperience = ?, ");
                sql.append("playerHouseName = ?, ");
                sql.append("playerLastOnline = ?, ");
                sql.append("playerTotalBays = ?, ");
                sql.append("playerFreeBays = ?, ");
                sql.append("playerRating = ?, ");
                sql.append("playerInfluence = ?, ");
                sql.append("playerFluff = ?, ");
                if (CampaignMain.cm.isUsingAdvanceRepair())
                    sql
                            .append("playerBaysOwned = ?, playerTechnicians = NULL, ");
                else
                    sql
                            .append("playerBaysOwned = NULL, playerTechnicians = ?, ");
                sql.append("playerRP = ?, ");
                sql.append("playerXPToReward = ?, ");
                sql.append("playerTotalTechsString = ?, ");
                sql.append("playerAvailableTechsString = ?, ");
                sql.append("playerLogo = ?, ");
                sql.append("playerLastAFR = ?, ");
                sql.append("playerGroupAllowance = ?, ");
                sql.append("playerLastISP = ?, ");
                sql.append("playerIsInvisible = ?, ");

                sql.append("playerUnitParts = ?, ");
                sql.append("playerAccess = ?, ");
                sql.append("playerAutoReorder = ?, ");
                sql.append("playerTeamNumber = ?, ");

                sql.append("playerSubFactionName = ?, ");
                sql.append("playerForumID = ?");
                ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString(),
                        PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, getName());
                ps.setInt(2, getMoney());
                ps.setInt(3, getExperience());
                ps.setString(4, getMyHouse().getName());
                ps.setLong(5, getLastOnline());
                ps.setInt(6, getTotalMekBays());
                ps.setInt(7, getFreeBays());
                ps.setDouble(8, getRating());
                ps.setInt(9, getInfluence());
                ps.setString(10, getFluffText());
                if (CampaignMain.cm.isUsingAdvanceRepair())
                    ps.setInt(11, getBaysOwned());
                else
                    ps.setInt(11, getTechnicians());
                ps.setInt(12, getReward());
                ps.setInt(13, getXPToReward());
                if (CampaignMain.cm.isUsingAdvanceRepair()) {
                    ps.setString(14, totalTechsToString());
                    ps.setString(15, availableTechsToString());
                } else {
                    ps.setString(14, "");
                    ps.setString(15, "");
                }
                if (getMyLogo().length() > 0)
                    ps.setString(16, getMyLogo());
                else
                    ps.setString(16, "");
                ps.setDouble(17, getLastAttackFromReserve());
                ps.setInt(18, getGroupAllowance());
                ps.setString(19, getLastISP());
                ps.setString(20, Boolean.toString(isInvisible()));
                ps.setString(21, getUnitParts().toString());
                if (getPassword() != null)
                    ps.setInt(22, getPassword().getAccess());
                else
                    ps.setInt(22, 1);
                ps.setString(23, Boolean.toString(getAutoReorder()));
                ps.setInt(24, getTeamNumber());

                ps.setString(25,
                        this.getSubFactionName().trim().length() < 1 ? " "
                                : getSubFactionName());
                ps.setInt(26, getForumID());

                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                setDBId(rs.getInt(1));
                rs.close();

            } else {
                // Already in the database - UPDATE it
                sql.setLength(0);
                sql.append("UPDATE players set ");
                sql.append("playerName = ?, ");
                sql.append("playerMoney = ?, ");
                sql.append("playerExperience = ?, ");
                sql.append("playerHouseName = ?, ");
                sql.append("playerLastOnline = ?, ");
                sql.append("playerTotalBays = ?, ");
                sql.append("playerFreeBays = ?, ");
                sql.append("playerRating = ?, ");
                sql.append("playerInfluence = ?, ");
                sql.append("playerFluff = ?, ");
                if (CampaignMain.cm.isUsingAdvanceRepair())
                    sql
                            .append("playerBaysOwned = ?, playerTechnicians = NULL, ");
                else
                    sql
                            .append("playerTechnicians = ?, playerBaysOwned = NULL, ");
                sql.append("playerRP = ?, ");
                sql.append("playerXPToReward = ?, ");
                sql.append("playerTotalTechsString = ?, ");
                sql.append("playerAvailableTechsString = ?, ");
                sql.append("playerLogo = ?, ");
                sql.append("playerLastAFR = ?, ");
                sql.append("playerGroupAllowance = ?, ");
                sql.append("playerLastISP = ?, ");
                sql.append("playerIsInvisible = ?, ");
                sql.append("playerAccess = ?, ");
                sql.append("playerUnitParts = ?, ");
                sql.append("playerAutoReorder = ?, ");
                sql.append("playerPassword= ?, ");
                sql.append("playerPassTime= ?, ");
                sql.append("playerTeamNumber= ?, ");

                sql.append("playerSubFactionName = ?, ");
                sql.append("playerForumID = ? ");
                sql.append("WHERE playerID = ?");

                ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());
                ps.setString(1, getName());
                ps.setInt(2, getMoney());
                ps.setInt(3, getExperience());
                ps.setString(4, getMyHouse().getName());
                ps.setLong(5, getLastOnline());
                ps.setInt(6, getTotalMekBays());
                ps.setInt(7, getFreeBays());
                ps.setDouble(8, getRating());
                ps.setInt(9, getInfluence());
                ps.setString(10, getFluffText());
                if (CampaignMain.cm.isUsingAdvanceRepair())
                    ps.setInt(11, getBaysOwned());
                else
                    ps.setInt(11, getTechnicians());
                ps.setInt(12, getReward());
                ps.setInt(13, getXPToReward());
                if (CampaignMain.cm.isUsingAdvanceRepair()) {
                    ps.setString(14, totalTechsToString());
                    ps.setString(15, availableTechsToString());

                } else {
                    ps.setString(14, "");
                    ps.setString(15, "");

                }
                if (getMyLogo().length() > 0)
                    ps.setString(16, getMyLogo());
                else
                    ps.setString(16, "");
                ps.setDouble(17, getLastAttackFromReserve());
                ps.setInt(18, getGroupAllowance());
                ps.setString(19, getLastISP());
                ps.setString(20, Boolean.toString(isInvisible()));
                if (getPassword() != null)
                    ps.setInt(21, getPassword().getAccess());
                else
                    ps.setInt(21, 1);
                ps.setString(22, getUnitParts().toString());
                ps.setString(23, Boolean.toString(getAutoReorder()));
                if (getPassword() != null)
                    ps.setString(24, this.password.getPasswd());
                else
                    ps.setString(24, "");
                if (getPassword() != null)
                    ps.setLong(25, this.password.getTime());
                else
                    ps.setLong(25, 0);
                ps.setInt(26, getTeamNumber());

                ps.setString(27,
                        (this.getSubFactionName().trim().length() < 1) ? " "
                                : getSubFactionName());
                ps.setInt(28, getForumID());
                ps.setInt(29, getDBId());
                ps.executeUpdate();

            }
            ps.close();

            // Save Exclude Lists
            this.getExclusionList().toDB(getDBId());

            // Save Units
            if (getUnits().size() > 0) {
                for (SUnit currU : getUnits()) {
                    SPilot pilot = (SPilot) currU.getPilot();
                    pilot.setCurrentFaction(getMyHouse().getName());
                    pilot.toDB(currU.getType(), currU.getWeightclass());
                    currU.toDB();
                    CampaignMain.cm.MySQL.linkUnitToPlayer(currU.getDBId(),
                            getDBId());
                }
            }
            if (getArmies().size() > 0) {
                ps.close();
                ps = CampaignMain.cm.MySQL
                        .getPreparedStatement("DELETE from playerarmies WHERE playerID = "
                                + getDBId());
                ps.executeUpdate();
                for (int i = 0; i < getArmies().size(); i++) {
                    ps.close();
                    sql.setLength(0);
                    sql.append("INSERT into playerarmies set playerID = "
                            + getDBId() + ", armyID = "
                            + getArmies().elementAt(i).getID()
                            + ", armyString = ?");
                    ps = CampaignMain.cm.MySQL.getPreparedStatement(sql
                            .toString());
                    ps.setString(1, getArmies().elementAt(i).toString(false,
                            "%"));
                    ps.executeUpdate();
                }
            }
            // Save Personal Pilots Queues
            for (int weightClass = Unit.LIGHT; weightClass < Unit.ASSAULT; weightClass++) {
                LinkedList<Pilot> currList = getPersonalPilotQueue()
                        .getPilotQueue(Unit.MEK, weightClass);
                int numPilots = currList.size();
                for (int position = 0; position < numPilots; position++) {
                    ((SPilot) currList.get(position)).toDB(Unit.MEK,
                            weightClass);
                    CampaignMain.cm.MySQL.linkPilotToPlayer(((SPilot) currList
                            .get(position)).getDBId(), getDBId());
                }
            }
            for (int weightClass = Unit.LIGHT; weightClass < Unit.ASSAULT; weightClass++) {
                LinkedList<Pilot> currList = getPersonalPilotQueue()
                        .getPilotQueue(Unit.PROTOMEK, weightClass);
                int numPilots = currList.size();
                for (int position = 0; position < numPilots; position++) {
                    ((SPilot) currList.get(position)).toDB(Unit.PROTOMEK,
                            weightClass);
                    CampaignMain.cm.MySQL.linkPilotToPlayer(((SPilot) currList
                            .get(position)).getDBId(), getDBId());
                }
            }
            ps.close();
            MWServ.mwlog.dbLog("Finished saving player");
        } catch (SQLException e) {
            MWServ.mwlog.dbLog("SQL error in SPlayer.toDB: " + e.getMessage());
        }
    }

    /**
     * @author jtighe
     * @param s -
     *            string from a pfile
     * 
     * Used for sperate pfiles with faction name stuck on the end.
     */
    public void fromString(String s) {

        // print the player into the info log. only for Debug
        // MWServ.mwlog.infoLog("CSPlayer: " + s);
        this.isLoading = true;

        try {
            this.armies.clear();

            s = s.substring(3);
            StringTokenizer ST = new StringTokenizer(s, "~");
            name = TokenReader.readString(ST);

            /*
             * name is set before the exclusion list is un-strung in
             * SPlayer.fromString(). Use this opportunity to set it in the
             * ExclusionList so strip/error messages can be sent back to the
             * player properly. Uber-Hacky, but functional.
             * 
             * @urgru 4.2.05
             */
            exclusionList.setOwnerName(name);
            if (CampaignMain.cm.isUsingMySQL()) {
                int dbId = CampaignMain.cm.MySQL.getPlayerIDByName(name);
                setDBId(dbId);
            }

            money = TokenReader.readInt(ST);
            experience = TokenReader.readInt(ST);

            int numofarmies = 0;
            int numofUnits = TokenReader.readInt(ST);
            units = new Vector<SUnit>(1, 1);

            for (int i = 0; i < numofUnits; i++) {
                SUnit m = new SUnit();
                m.fromString((String) ST.nextElement());
                units.add(m);
                CampaignMain.cm
                        .toUser("PL|HD|" + m.toString(true), name, false);
            }

            numofarmies = (Integer.parseInt((String) ST.nextElement()));
            for (int i = 0; i < numofarmies; i++) {
                SArmy a = new SArmy(name);
                a.fromString((String) ST.nextElement(), "%", this);
                if (armies.size() < a.getID())
                    armies.add(a);
                else
                    armies.add(a.getID(), a);
                CampaignMain.cm.toUser("PL|SAD|" + a.toString(true, "%"), name,
                        false);
            }

            this.setMyHouse(CampaignMain.cm.getHouseFromPartialString(
                    TokenReader.readString(ST), null));

            lastOnline = TokenReader.readLong(ST);
            // Just read it. It's not necessary to use it on the server..
            // It's useful for the client
            TokenReader.readString(ST);// Number of Bays
            TokenReader.readString(ST);// Number of Free Bays

            rating = TokenReader.readDouble(ST);
            influence = TokenReader.readInt(ST);
            
            fluffText = TokenReader.readString(ST).trim();

            /*
             * This space used to be occupied by player-set game options. Do not
             * use the node until we're sure all servers have reset and removed
             * any residual data. For now, we'll READ the data correctly, but
             * write out 0's during all saves. At some point in the future,
             * we'll be able to fully reclaim this space.
             */

            int numberOfOptions = TokenReader.readInt(ST);
            for (int i = 0; i < numberOfOptions; i++) {
                TokenReader.readString(ST);// eat the option's Description
                                            // token
                TokenReader.readString(ST);// eat the options Setting token
            }
            if (CampaignMain.cm.isUsingAdvanceRepair()) {
                int greenTechs = TokenReader.readInt(ST);
                int regTechs = greenTechs / 5;
                greenTechs -= regTechs;
                this.updateAvailableTechs(greenTechs + "%" + regTechs
                        + "%0%0%");
                this.getTotalTechs().addAll(getAvailableTechs());
                // give them some bays
                this.setBaysOwned(greenTechs + regTechs);
            } else
                technicians = TokenReader.readInt(ST);
           
            currentReward = TokenReader.readInt(ST);

            /*
             * Eat the next two tokens. Formerly used to save mezzo data. Can be
             * reclaimed soon-ish, as no server used the feature. @urgru 9/30/06
             */
            
                if (CampaignMain.cm.isUsingMySQL())
                    setDBId(TokenReader.readInt(ST));
                else
                    TokenReader.readString(ST);
            
                TokenReader.readString(ST);

            // TODO: Remove this after the next few updates from 0.1.51.2
            
                myHouse = CampaignMain.cm.getHouseFromPartialString(TokenReader
                        .readString(ST), getName());
            
                this.setXPToReward(TokenReader.readInt(ST));
            
                TokenReader.readString(ST);// faction logo not saved.
            
                this.getPersonalPilotQueue().fromString(
                        TokenReader.readString(ST), "$");
            
                this.getExclusionList().adminExcludeFromString(
                        TokenReader.readString(ST), "$");
            
                this.getExclusionList().playerExcludeFromString(
                        TokenReader.readString(ST), "$");

             {
                try {
                    if (CampaignMain.cm.isUsingAdvanceRepair()) {
                        updateTotalTechs(TokenReader.readString(ST));
                        
                            updateAvailableTechs(TokenReader.readString(ST));
                        
                            setBaysOwned(TokenReader.readInt(ST));
                    }// get rid of the 3 blanks
                    else {
                        TokenReader.readString(ST);
                        
                            TokenReader.readString(ST);
                        // allow servers to go back and forth using Bays as
                        // techs since bays are what techs are.
                         {
                            if (technicians <= 0)
                                technicians = TokenReader.readInt(ST);
                            else
                                TokenReader.readString(ST);
                        }
                    }
                }// Had alot of problems with advanced repair so lets just
                    // use this.
                catch (Exception ex) {
                }
            }// get rid of the 2 blanks

            myLogo = TokenReader.readString(ST);

            // Stupid error with player logo if its blank it doesn't save
            // anything
            // and gets skipped.
            // Thats been fixed but for all the PFiles out there with the defect
            // this will allow them to
            // Still Load.
            try {

                setLastAttackFromReserve(TokenReader.readLong(ST));
                setGroupAllowance(TokenReader.readInt(ST));
                setLastISP(TokenReader.readString(ST));
                this.setInvisible(TokenReader.readBoolean(ST));
                this.setGroupAllowance(TokenReader.readInt(ST));
            } catch (Exception ex) {
            }

            try {
                int access = TokenReader.readInt(ST);
                String passwd = TokenReader.readString(ST);
                long time = TokenReader.readLong(ST);

                if (passwd.trim().length() > 2)
                    this.setPassword(new MWPasswdRecord(this.name, access,
                            passwd, time, ""));
            } catch (Exception ex) {
                // Issue with password loading just stop now.
                this.isLoading = false;
                return;
            }
            if (CampaignMain.cm.getBooleanConfig("UsePartsRepair"))
                unitParts.fromString(TokenReader.readString(ST));
            else
                TokenReader.readString(ST);

            this.setAutoReorder(TokenReader.readBoolean(ST));
            this.setTeamNumber(TokenReader.readInt(ST));
            this.subFaction = TokenReader.readString(ST);
            this.lastPromoted = TokenReader.readLong(ST);

            if (this.password != null
                    && this.password.getPasswd().trim().length() <= 2) {
                this.password.setAccess(IAuthenticator.GUEST);
            }

            if (CampaignMain.cm.isUsingCyclops()) {
                CampaignMain.cm.getMWCC().playerWrite(this);
                // CampaignMain.cm.getMWCC().unitWrite(this.getUnitsData().firstElement(),name,this.getHouseName());
                CampaignMain.cm.getMWCC().unitWriteFromList(this.getUnits(),
                        name, myHouse.getName());
                CampaignMain.cm.getMWCC().pilotWriteFromList(
                        this.getPersonalPilotQueue(), name);
                // CampaignMain.cm.getMWCC().pilotWrite((SPilot)this.getUnitsData().firstElement().getPilot(),name);
            }

            CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name,
                    false);
            CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name, false);
            if (CampaignMain.cm.isUsingAdvanceRepair()) {

                if (!this.hasRepairingUnits()) {
                    CampaignMain.cm.toUser("PL|UTT|"
                            + this.totalTechsToString(), name, false);
                    CampaignMain.cm.toUser("PL|UAT|"
                            + this.totalTechsToString(), name, false);
                    this.updateAvailableTechs(this.totalTechsToString());// make
                                                                            // sure
                                                                            // techs
                                                                            // are
                                                                            // in
                                                                            // synch
                } else {
                    CampaignMain.cm.toUser("PL|UTT|"
                            + this.totalTechsToString(), name, false);
                    CampaignMain.cm.toUser("PL|UAT|"
                            + this.availableTechsToString(), name, false);
                }
            }

            healAllPilots();

            /*
             * Check all units for bad ammo or illegal/mis-set vacant pilots.
             * This was being done at the same time as the units are unstrung,
             * but caused a null b/c fixAmmo() uses .myHouse(), which is null at
             * that point in the unstring.
             * 
             * If the units are changed as a result of the checks, a PL|UU is
             * sent, as well as a PL|SAD for each army that includes the unit.
             */
            for (SUnit currU : units) {
                this.fixPilot(currU);
            }
        } catch (Exception ex) {
            MWServ.mwlog.errLog(ex);
        } finally {
            this.isLoading = false;
        }
    }

    public void fromDB(int playerID) {
        if (this.isLoading)
            return;
        this.isLoading = true;
        try {
            ResultSet rs = null, rs1 = null;
            Statement stmt = CampaignMain.cm.MySQL.getStatement();
            Statement stmt1 = CampaignMain.cm.MySQL.getStatement();
            rs = stmt.executeQuery("SELECT * from players WHERE playerID = "
                    + playerID);
            if (rs.next()) {
                this.armies.clear();

                name = rs.getString("playerName");

                exclusionList.setOwnerName(name);
                setDBId(playerID);
                money = rs.getInt("playerMoney");
                experience = rs.getInt("playerExperience");

                units = new Vector<SUnit>(1, 1);
                forumID = rs.getInt("playerForumID");

                rs1 = stmt1
                        .executeQuery("SELECT MWID from units WHERE uplayerID = "
                                + playerID);
                while (rs1.next()) {
                    SUnit m = new SUnit();
                    m.fromDB(rs1.getInt("MWID"));
                    units.add(m);
                    CampaignMain.cm.toUser("PL|HD|" + m.toString(true), name,
                            false);
                }
                rs1.close();
                rs1 = stmt1
                        .executeQuery("SELECT * from playerarmies WHERE playerID = "
                                + playerID);
                while (rs1.next()) {
                    SArmy a = new SArmy(name);
                    a.fromString(rs1.getString("armyString"), "%", this);
                    if (armies.size() < a.getID())
                        armies.add(a);
                    else
                        armies.add(a.getID(), a);

                    CampaignMain.cm.toUser("PL|SAD|" + a.toString(true, "%"),
                            name, false);
                }

                this.setMyHouse(CampaignMain.cm.getHouseFromPartialString(rs
                        .getString("playerHouseName"), null));

                lastOnline = rs.getLong("playerLastOnline");

                rating = rs.getDouble("playerRating");
                influence = rs.getInt("playerInfluence");
                fluffText = rs.getString("playerFluff").trim();
                setTeamNumber(rs.getInt("playerTeamNumber"));

                if (CampaignMain.cm.isUsingAdvanceRepair())
                    this.addBays(rs.getInt("playerBaysOwned"));
                else
                    technicians = rs.getInt("playerTechnicians");

                currentReward = rs.getInt("playerRP");

                myHouse = CampaignMain.cm.getHouseFromPartialString(rs
                        .getString("playerHouseName"), getName());
                this.setXPToReward(rs.getInt("playerXPToReward"));

                this.getPersonalPilotQueue().fromDB(getDBId());

                this.getExclusionList().fromDB(getDBId());

                try {
                    if (CampaignMain.cm.isUsingAdvanceRepair()) {
                        updateTotalTechs(rs.getString("playerTotalTechsString"));
                        updateAvailableTechs(rs
                                .getString("playerAvailableTechsString"));
                    } else {
                        // allow servers to go back and forth using Bays as
                        // techs since bays are what techs are.
                        if (technicians <= 0)
                            technicians = rs.getInt("playerTechnicians");
                    }
                }
                // Had alot of problems with advanced repair so lets just use
                // this.
                catch (Exception ex) {
                }

                myLogo = rs.getString("playerLogo");

                try {
                    setLastAttackFromReserve(rs.getLong("playerLastAFR"));

                    setGroupAllowance(rs.getInt("playerGroupAllowance"));

                    setLastISP(rs.getString("playerLastISP"));

                    this.setInvisible(Boolean.parseBoolean(rs
                            .getString("playerIsInvisible")));

                    this.setGroupAllowance(rs.getInt("playerGroupAllowance"));
                } catch (Exception ex) {
                }

                try {
                    int access = rs.getInt("playerAccess");
                    String passwd = rs.getString("playerPassword");
                    long time = System.currentTimeMillis();
                    if (access >= 2)
                        this.password = new MWPasswdRecord(this.name, access,
                                passwd, time, "");
                } catch (Exception ex) {
                }

                if (CampaignMain.cm.getBooleanConfig("UsePartsRepair"))
                    unitParts.fromString(rs.getString("playerUnitParts"));

                this.subFaction = rs.getString("playerSubfactionName");

                this.setAutoReorder(Boolean.parseBoolean(rs
                        .getString("playerAutoReorder")));

                if (this.password != null
                        && this.password.getPasswd().trim().length() <= 2) {
                    this.password.setAccess(IAuthenticator.GUEST);
                }

                if (CampaignMain.cm.isUsingCyclops()) {
                    CampaignMain.cm.getMWCC().playerWrite(this);
                    CampaignMain.cm.getMWCC().unitWriteFromList(
                            this.getUnits(), name, myHouse.getName());
                    CampaignMain.cm.getMWCC().pilotWriteFromList(
                            this.getPersonalPilotQueue(), name);
                }

                CampaignMain.cm.toUser("PL|SB|" + this.getTotalMekBays(), name,
                        false);
                CampaignMain.cm.toUser("PL|SF|" + this.getFreeBays(), name,
                        false);
                if (CampaignMain.cm.isUsingAdvanceRepair()) {

                    if (!this.hasRepairingUnits()) {
                        CampaignMain.cm.toUser("PL|UTT|"
                                + this.totalTechsToString(), name, false);
                        CampaignMain.cm.toUser("PL|UAT|"
                                + this.totalTechsToString(), name, false);
                        this.updateAvailableTechs(this.totalTechsToString());// make
                                                                                // sure
                                                                                // techs
                                                                                // are
                                                                                // in
                                                                                // synch
                    } else {
                        CampaignMain.cm.toUser("PL|UTT|"
                                + this.totalTechsToString(), name, false);
                        CampaignMain.cm.toUser("PL|UAT|"
                                + this.availableTechsToString(), name, false);
                    }
                }

                healAllPilots();

                /*
                 * Check all units for bad ammo or illegal/mis-set vacant
                 * pilots. This was being done at the same time as the units are
                 * unstrung, but caused a null b/c fixAmmo() uses .myHouse(),
                 * which is null at that point in the unstring.
                 * 
                 * If the units are changed as a result of the checks, a PL|UU
                 * is sent, as well as a PL|SAD for each army that includes the
                 * unit.
                 */
                for (SUnit currU : units) {
                    this.fixPilot(currU);
                }
            }
            rs.close();
            if (rs1 != null)
                rs1.close();
            stmt.close();
            stmt1.close();
        } catch (SQLException e) {
            MWServ.mwlog
                    .dbLog("SQL Error in SPlayer.fromDB: " + e.getMessage());
        } finally {
            this.isLoading = false;
        }
    }

    /**
     * Issue with vacant pilots getting placed in !Mek and !Proto Units This
     * fixes it. Will also be helpful if future bugs cause vacant pilots.
     * 
     * @param unit
     */
    private void fixPilot(SUnit unit) {

        if (!unit.hasVacantPilot())
            return;

        if (Boolean.parseBoolean(this.getMyHouse().getConfig(
                "AllowPersonalPilotQueues"))
                && (unit.getType() == Unit.MEK || unit.getType() == Unit.PROTOMEK))
            return;

        // set a new pilot
        SPilot pilot = this.getMyHouse().getNewPilot(unit.getType());
        unit.setPilot(pilot);

        // send an update to the player
        CampaignMain.cm.toUser("PL|UU|" + unit.getId() + " |"
                + unit.toString(true), name, false);

        // correct the BV of any army which contains the unit
        for (SArmy currA : armies) {
            if (currA.getUnit(unit.getId()) != null) {
                currA.setBV(0);
                CampaignMain.cm.toUser("PL|SAD|" + currA.toString(true, "%"),
                        name, false);
                CampaignMain.cm.getOpsManager().checkOperations(currA, true);// update
                                                                                // legal
                                                                                // operations
            }
        }

    }

    public UnitComponents getUnitParts() {
        return this.unitParts;
    }

    public void updatePartsCache(String part, int amount) {
        if (amount < 0)
            this.getUnitParts().remove(part, amount);
        else
            this.getUnitParts().add(part, amount);
        CampaignMain.cm.toUser("PL|UPPC|" + part + "#" + amount,
                this.getName(), false);

    }

    public SArmy getLockedArmy() {
        for (SArmy army : getArmies()) {
            if (!army.isLocked())
                continue;
            return army;
        }

        return null;
    }

    public int getDBId() {
        return this.DBId;
    }

    public void setDBId(int id) {
        this.DBId = id;
        this.personalPilotQueue.setOwnerID(id);
    }

    public void setForumID(int id) {
        this.forumID = id;
    }

    public int getForumID() {
        return forumID;
    }

    public void setTeamNumber(int team) {
        super.setTeamNumber(team);
        this.setSave();
    }

    public void setSubFaction(String subFaction) {
        this.subFaction = subFaction;
        this.setLastPromoted(System.currentTimeMillis());
        this.setSave();
    }

    public SubFaction getSubFaction() {

        SubFaction sub = this.getMyHouse().getSubFactionList().get(
                this.subFaction);

        if (sub == null)
            return new SubFaction();

        return sub;
    }

    public int getSubFactionAccess() {
        SubFaction sub = this.getMyHouse().getSubFactionList().get(
                this.subFaction);

        if (sub == null)
            return 0;

        return Integer.parseInt(sub.getConfig("AccessLevel"));

    }

    public String getSubFactionName() {
        return this.subFaction;
    }

    public boolean playerIsLoading() {
        return this.isLoading;
    }

    public boolean canBePromoted() {

        if (getMyHouse().getSubFactionList().size() < 1)
            return false;

        int days = getMyHouse().getIntegerConfig("daysbetweenpromotions");

        long day = 1000 * 60 * 60 * 24;

        try {
            long daysSinceLastPromoted = (System.currentTimeMillis() - this
                    .getLastPromoted())
                    / day;

            // They've been promoted in the last number of days so they are not
            // eligible for a check.
            if (daysSinceLastPromoted < days)
                return false;
        } catch (Exception ex) {
            MWServ.mwlog.errLog(ex);
            return false;
        }

        return true;

    }

    public void checkForPromotion() {

        if (!canBePromoted())
            return;

        int currentAccessLevel = getSubFactionAccess();

        for (SubFaction subFaction : getMyHouse().getSubFactionList().values()) {

            if (currentAccessLevel < Integer.parseInt(subFaction
                    .getConfig("AccessLevel"))
                    && getRating() >= Integer.parseInt(subFaction
                            .getConfig("MinELO"))
                    && getExperience() >= Integer.parseInt(subFaction
                            .getConfig("MinExp"))) {
                CampaignMain.cm
                        .toUser(
                                "You are eligible for a promotion to subFaction "
                                        + subFaction.getConfig("Name")
                                        + ". <a href=\"MEKWARS/c RequestSubFactionPromotion#"
                                        + subFaction.getConfig("Name")
                                        + "\">Click here to request promotion.</a>",
                                getName());
            }

        }
    }

    public void checkForDemotion() {

        SubFaction subfaction = getSubFaction();

        int access = Integer.parseInt(subfaction.getConfig("AccessLevel"));
        int elo = Integer.parseInt(subfaction.getConfig("MinELO"));
        int exp = Integer.parseInt(subfaction.getConfig("MinExp"));

        // can go any lower
        if (access < 1)
            return;

        if (elo > getRating() || exp > getExperience()) {
            StringBuilder message = new StringBuilder(this.name);
            message
                    .append(" no longer meets the eligbility requirements for subfaction ");
            message.append(subfaction.getConfig("Name"));
            message.append(". He is eligible for the following:<br>");
            for (SubFaction subFaction : getMyHouse().getSubFactionList()
                    .values()) {

                if (access > Integer.parseInt(subFaction
                        .getConfig("AccessLevel"))
                        && getRating() >= Integer.parseInt(subFaction
                                .getConfig("MinELO"))
                        && getExperience() >= Integer.parseInt(subFaction
                                .getConfig("MinExp"))) {
                    message.append(subFaction.getConfig("Name"));
                    message.append(". <a href=\"MEKWARS/c demoteplayer#");
                    message.append(getName());
                    message.append("#");
                    message.append(subFaction.getConfig("Name"));
                    message.append("\">Click here to demote.</a><br>");
                }

            }
            message.append("None");
            message.append(". <a href=\"MEKWARS/c demoteplayer#");
            message.append(getName());
            message.append("#");
            message.append("None");
            message.append("\">Click here to demote.</a><br>");

            this.getMyHouse().sendMessageToHouseLeaders(message.toString());
        }
    }

    public long getLastPromoted() {
        return lastPromoted;
    }

    public void setLastPromoted(long promotedTime) {
        this.lastPromoted = promotedTime;
    }

}// end SPlayer()
