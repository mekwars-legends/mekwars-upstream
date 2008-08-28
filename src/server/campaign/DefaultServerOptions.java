/*
 * MekWars - Copyright (C) 2004 
 * 
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

/**
 * @author jtighe
 * 
 * This class loads the default server config options as well as saves
 * the server configs back to the serverconfig  file
 */
package server.campaign;

import java.util.Properties;
import common.CampaignData;

/**
 * @author Torren Oct 22, 2004 Loads the default settings for the server config If any configs are added please add them to this function as well as createConfig().
 */
public class DefaultServerOptions {
    private Properties defaults; // default server config

    protected void createDefaults() {
        defaults = new Properties();

        defaults.setProperty("TickTime", "90000");
        defaults.setProperty("SliceTime", "18000");

        defaults.setProperty("HTMLOUTPUT", "true");

        defaults.setProperty("RankingPath", "./Ranking.htm");
        defaults.setProperty("EXPRankingPath", "./EXPRanking.htm");
        defaults.setProperty("NewsPath", "./News.rdf");
        defaults.setProperty("HouseRankPath", "./HouseRanking.html");
        defaults.setProperty("XMLPlanetPath", "./data/DynPlanets.xml");
        defaults.setProperty("MechstatPath", "./Mechstats.htm");

        /*
         * defaults.setProperty("MaxBVDifference", "150"); defaults.setProperty("MaxBVPercent", "0");
         */

        // Op: Stinger BV mods
        defaults.setProperty("UseOperationsRule", "true");
        defaults.setProperty("InfantryOperationsBVMod", "1.0");
        defaults.setProperty("BAOperationsBVMod", "1.0");
        defaults.setProperty("ProtoOperationsBVMod", "1.0");
        defaults.setProperty("VehicleOperationsBVMod", "1.0");
        defaults.setProperty("MekOperationsBVMod", "1.0");
        defaults.setProperty("AeroOperationsBVMod", "1.0");

        defaults.setProperty("MinimumHouseBays", "20");
        defaults.setProperty("NewbieHouseBays", "30");
        defaults.setProperty("MercHouseBays", "4");
        defaults.setProperty("UseExperience", "true");
        defaults.setProperty("UseTechnicians", "true");
        defaults.setProperty("BaseTechCost", "10");
        defaults.setProperty("DecreasingTechCost", "true");
        defaults.setProperty("XPForDecrease", "500");
        defaults.setProperty("MinimumTechCost", "5");
        defaults.setProperty("AdditivePerTech", ".04");
        defaults.setProperty("AdditiveCostCeiling", "1.2");
        defaults.setProperty("SenderPaysOnTransfer", "true");
        defaults.setProperty("ReceiverPaysOnTransfer", "true");
        defaults.setProperty("TransferPayment", ".10");
        defaults.setProperty("MaintainanceIncrease", "1");
        defaults.setProperty("MaintainanceDecrease", "1");
        defaults.setProperty("BaseUnmaintainedLevel", "75");
        defaults.setProperty("UnmaintainedPenalty", "10");
        defaults.setProperty("TransferScrapLevel", "75");
        defaults.setProperty("TechsToProtoPointRatio", "1");
        defaults.setProperty("FootInfTakeNoBays", "true");
        defaults.setProperty("DisableTechAdvancement", "false");

        defaults.setProperty("MinEXPforMercenaries", "7500");
        defaults.setProperty("HideELO", "true");
        defaults.setProperty("AllowLowerLevelUsersToSeeUpperLevelUsersDoings", "false");
        defaults.setProperty("FreeMinticksOnStartup", "0");
        // TODO: Make this a client option
        defaults.setProperty("ShowVehWeightclassInChallenges", "false");

        defaults.setProperty("VotingEnabled", "true");
        defaults.setProperty("StartingVotes", "2");
        defaults.setProperty("XPForAdditionalVote", "500");
        defaults.setProperty("MaximumVotes", "7");

        defaults.setProperty("PlayerBaseMoney", "2000");
        defaults.setProperty("MaxSOLExp", "150");
        defaults.setProperty("MaxSOLCBills", "300");
        defaults.setProperty("MinEXPforDefecting", "0");
        defaults.setProperty("MinEXPforMedium", "0");
        defaults.setProperty("MinEXPforHeavy", "0");
        defaults.setProperty("MinEXPforAssault", "0");

        defaults.setProperty("ScrapsAllowed", "1");// nee "ScrappingAllowed",
        // 6.12.05 @urgru
        defaults.setProperty("ScrapCostMultiplier", ".40");// nee
        // "ScrappingCostsBills",
        // 11.12.06 @urgru
        defaults.setProperty("DonationsAllowed", "2");// nee
        // "DonatingAllowed",
        // 6.12.05 @urgru
        defaults.setProperty("DonationCostMultiplier", ".20");// nee
        // "DonatingCostsBills",
        // 11.12.06
        // @urgru
        defaults.setProperty("UsedPurchaseCostMulti", ".50");// @urgru
        // 11.12.06
        defaults.setProperty("SelectableSalvage", "true");
        defaults.setProperty("TimeToSelectSalvage", "300");

        defaults.setProperty("BaseUnitLossPayment", "0");// int
        defaults.setProperty("NewCostMultiUnitLossPayment", ".50");// float
        defaults.setProperty("SalvageMultiToUnitLossPayment", ".50");// float
        defaults.setProperty("MekMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("VehMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("ProtoMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("BAMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("InfMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("AeroMultiToUnitLossPayment", "1.0");// float
        defaults.setProperty("NewCostMultiMaxUnitLossPayment", "1.0");// float
        defaults.setProperty("FlatMaxUnitLossPayment", "0");// int. 0 ensures no
        // payment by
        // default.

        defaults.setProperty("MinEXPforBMSelling", "800");
        defaults.setProperty("MinEXPforBMBuying", "300");
        defaults.setProperty("MinBMSalesTicks", "5");// 6.4.05 @urgru
        defaults.setProperty("MinBMSalesPrice", "5");// 6.4.05 @urgru
        defaults.setProperty("MaxBMSalesTicks", "100");// 8.2.06 @urgru
        defaults.setProperty("MaxBMSalesPrice", "999999");// 8.2.06 @urgru
        defaults.setProperty("BMBidFlu", "10");
        defaults.setProperty("BMSellFlu", "20");
        defaults.setProperty("BMFluSizeCost", "5");

        defaults.setProperty("InfantryMayBeSoldOnBM", "false");// 10.23.05
        // @urgru
        defaults.setProperty("BAMayBeSoldOnBM", "true");// 10.23.05 @urgru
        defaults.setProperty("ProtosMayBeSoldOnBM", "true");// 10.23.05 @urgru
        defaults.setProperty("VehsMayBeSoldOnBM", "true");// 10.23.05 @urgru
        defaults.setProperty("MeksMayBeSoldOnBM", "true");// 10.23.05 @urgru
        defaults.setProperty("AerosMayBeSoldOnBM", "true");

        defaults.setProperty("WelfareCeiling", "30");// go above this and you
        // no longer can pull
        // welfare units
        defaults.setProperty("WelfareTotalUnitBVCeiling", "3000");// Go above
        // this and
        // you no
        // longer
        // can pull
        // welfare
        // units

        defaults.setProperty("InfluenceCeiling", "2000000");
        defaults.setProperty("InfluenceTimeMin", "150000");
        defaults.setProperty("BaseInfluence", "6");
        defaults.setProperty("FloorPenalty", ".50");
        defaults.setProperty("CeilingPenalty", ".40");
        defaults.setProperty("OverlapPenalty", ".30");

        // unit prices (MU)
        defaults.setProperty("LightPrice", "10");
        defaults.setProperty("MediumPrice", "30");
        defaults.setProperty("HeavyPrice", "60");
        defaults.setProperty("AssaultPrice", "80");
        defaults.setProperty("LightVehiclePrice", "5");
        defaults.setProperty("MediumVehiclePrice", "15");
        defaults.setProperty("HeavyVehiclePrice", "30");
        defaults.setProperty("AssaultVehiclePrice", "40");
        defaults.setProperty("LightInfantryPrice", "3");
        defaults.setProperty("MediumInfantryPrice", "8");
        defaults.setProperty("HeavyInfantryPrice", "15");
        defaults.setProperty("AssaultInfantryPrice", "20");

        // influence costs
        defaults.setProperty("LightInf", "10");
        defaults.setProperty("MediumInf", "20");
        defaults.setProperty("HeavyInf", "40");
        defaults.setProperty("AssaultInf", "80");
        defaults.setProperty("LightVehicleInf", "1");
        defaults.setProperty("MediumVehicleInf", "2");
        defaults.setProperty("HeavyVehicleInf", "3");
        defaults.setProperty("AssaultVehicleInf", "4");
        defaults.setProperty("LightInfantryInf", "1");
        defaults.setProperty("MediumInfantryInf", "2");
        defaults.setProperty("HeavyInfantryInf", "3");
        defaults.setProperty("AssaultInfantryInf", "4");

        // PP Costs. Untyped is for meks.
        defaults.setProperty("LightPP", "10000");
        defaults.setProperty("MediumPP", "20000");
        defaults.setProperty("HeavyPP", "45000");
        defaults.setProperty("AssaultPP", "95000");
        defaults.setProperty("LightVehiclePP", "10000");
        defaults.setProperty("MediumVehiclePP", "20000");
        defaults.setProperty("HeavyVehiclePP", "45000");
        defaults.setProperty("AssaultVehiclePP", "95000");
        defaults.setProperty("LightInfantryPP", "10000");
        defaults.setProperty("MediumInfantryPP", "20000");
        defaults.setProperty("HeavyInfantryPP", "45000");
        defaults.setProperty("AssaultInfantryPP", "95000");

        defaults.setProperty("LightBattleArmorInf", "1");
        defaults.setProperty("MediumBattleArmorInf", "2");
        defaults.setProperty("HeavyBattleArmorInf", "3");
        defaults.setProperty("AssaultBattleArmorInf", "4");
        defaults.setProperty("LightBattleArmorPP", "10000");
        defaults.setProperty("MediumBattleArmorPP", "20000");
        defaults.setProperty("HeavyBattleArmorPP", "45000");
        defaults.setProperty("AssaultBattleArmorPP", "95000");
        defaults.setProperty("LightBattleArmorPrice", "5");
        defaults.setProperty("MediumBattleArmorPrice", "15");
        defaults.setProperty("HeavyBattleArmorPrice", "30");
        defaults.setProperty("AssaultBattleArmorPrice", "40");

        defaults.setProperty("LightAeroInf", "1");
        defaults.setProperty("MediumAeroInf", "2");
        defaults.setProperty("HeavyAeroInf", "3");
        defaults.setProperty("AssaultAeroInf", "4");
        defaults.setProperty("LightAeroPP", "10000");
        defaults.setProperty("MediumAeroPP", "20000");
        defaults.setProperty("HeavyAeroPP", "45000");
        defaults.setProperty("AssaultAeroPP", "95000");
        defaults.setProperty("LightAeroPrice", "5");
        defaults.setProperty("MediumAeroPrice", "15");
        defaults.setProperty("HeavyAeroPrice", "30");
        defaults.setProperty("AssaultAeroPrice", "40");

        defaults.setProperty("LightProtoMekInf", "1");
        defaults.setProperty("MediumProtoMekInf", "2");
        defaults.setProperty("HeavyProtoMekInf", "3");
        defaults.setProperty("AssaultProtoMekInf", "4");
        defaults.setProperty("LightProtoMekPrice", "5");
        defaults.setProperty("MediumProtoMekPrice", "15");
        defaults.setProperty("HeavyProtoMekPrice", "30");
        defaults.setProperty("AssaultProtoMekPrice", "40");
        defaults.setProperty("LightProtoMekPP", "10000");
        defaults.setProperty("MediumProtoMekPP", "20000");
        defaults.setProperty("HeavyProtoMekPP", "45000");
        defaults.setProperty("AssaultProtoMekPP", "95000");

        defaults.setProperty("ProduceComponentsWithNoFactory", "false");

        defaults.setProperty("UseMek", "true");
        defaults.setProperty("UseVehicle", "true");
        defaults.setProperty("UseBattleArmor", "true");
        defaults.setProperty("UseProtoMek", "true");
        defaults.setProperty("UseInfantry", "true");
        defaults.setProperty("UseAero", "true");
        
        defaults.setProperty("UseOnlyLightInfantry", "true");
        defaults.setProperty("UseOnlyOneVehicleSize", "false");

        // AP settings
        defaults.setProperty("APAtMaxLightUnits", "30");
        defaults.setProperty("APAtMaxMediumUnits", "20");
        defaults.setProperty("APAtMaxHeavyUnits", "20");
        defaults.setProperty("APAtMaxAssaultUnits", "20");
        defaults.setProperty("AutoProductionFailureRate", "70");
        defaults.setProperty("RareChance", "1.0");

        // Factory refresh rates
        defaults.setProperty("LightRefresh", "5");
        defaults.setProperty("MediumRefresh", "10");
        defaults.setProperty("HeavyRefresh", "15");
        defaults.setProperty("AssaultRefresh", "20");

        // House bay caps & sales
        defaults.setProperty("MaxLightUnits", "20");
        defaults.setProperty("MaxOtherUnits", "15");
        defaults.setProperty("LightSaleTicks", "8");
        defaults.setProperty("MediumSaleTicks", "12");
        defaults.setProperty("HeavySaleTicks", "16");
        defaults.setProperty("AssaultSaleTicks", "20");

        // Tick & News Information
        defaults.setProperty("ShowOutputMultiplierOnTick", "false");
        defaults.setProperty("ShowCompleteGameInfoOnTick", "true");
        defaults.setProperty("ShowCompleteGameInfoInNews", "true");

        defaults.setProperty("ImmunityTime", "40");

        defaults.setProperty("ElitePilotsBVMod", "false");
        defaults.setProperty("FastHoverBVMod", "100");

        defaults.setProperty("IPCheck", "false");
        defaults.setProperty("ProbeInReserve", "false");
        defaults.setProperty("HideActiveStatus", "false");
        defaults.setProperty("ForcedDeactivation", "false");

        // map factors
        defaults.setProperty("MekMapSizeFactor", "100");
        defaults.setProperty("VehicleMapSizeFactor", "80");
        defaults.setProperty("InfantryMapSizeFactor", "10");
        defaults.setProperty("BattleArmorMapSizeFactor", "10");
        defaults.setProperty("ProtoMekMapSizeFactor", "10");
        defaults.setProperty("AeroMapSizeFactor", "100");
        
        defaults.setProperty("MinCountForTick", "2000");
        defaults.setProperty("MaxCountForTick", "6000");
        defaults.setProperty("MinActiveTime", "40");
        defaults.setProperty("MaxLancesPerPlayer", "7");
        defaults.setProperty("BaseCountForProduction", "1.0");

        defaults.setProperty("EXPNeededPerHouseRank", "0");
        defaults.setProperty("MinContractEXP", "200");
        defaults.setProperty("PilotSkills", "true");
        defaults.setProperty("BornSkillChance", "10");
        defaults.setProperty("SkillLevelChance", "5");
        defaults.setProperty("ReduceSkillsInQue", "true");
        defaults.setProperty("ClearXPInQue", "50");
        defaults.setProperty("ForceSalvage", "true");

        defaults.setProperty("XPRewardCap", "500");
        defaults.setProperty("XPRollOverCap", "500");
        defaults.setProperty("ShowReward", "true");
        defaults.setProperty("AllowInfluenceForRewards", "true");
        defaults.setProperty("InfluenceForARewardPoint", "100");
        defaults.setProperty("AllowTechsForRewards", "true");
        defaults.setProperty("TechsForARewardPoint", "5");
        defaults.setProperty("AllowUnitsForRewards", "true");
        defaults.setProperty("RewardPointsForAMek", "10");
        defaults.setProperty("RewardPointsForAVeh", "3");
        defaults.setProperty("RewardPointsForInf", "1");
        defaults.setProperty("RewardPointsForProto", "1");
        defaults.setProperty("RewardPointsForBA", "1");
        defaults.setProperty("RewardPointsForAero", "1");
        defaults.setProperty("RewardPointsForALight", "2");
        defaults.setProperty("RewardPointsForAMed", "4");
        defaults.setProperty("RewardPointsForAHeavy", "6");
        defaults.setProperty("RewardPointsForAnAssault", "8");
        defaults.setProperty("AllowRareUnitsForRewards", "false");
        defaults.setProperty("RewardPointMultiplierForRare", "2.5");
        defaults.setProperty("RewardPointNonHouseMultiplier", "5");
        defaults.setProperty("AllowFactoryRefreshForRewards", "false");
        defaults.setProperty("RewardPointToRefreshFactory", "2");
        defaults.setProperty("RewardsRepodFolder", "rewards");
        defaults.setProperty("RewardsRareBuildTable", "rare");

        // Reward points with Advanced Repair
        defaults.setProperty("AllowRepairsForRewards", "false");
        defaults.setProperty("RewardPointsForRepair", "10000");

        // Lets the players repair a single crit.
        defaults.setProperty("AllowCritRepairsForRewards", "false");
        defaults.setProperty("RewardPointsForCritRepair", "10.0");

        // Techs
        defaults.setProperty("RewardPointsForGreen", "1");
        defaults.setProperty("RewardPointsForReg", "2");
        defaults.setProperty("RewardPointsForVet", "3");
        defaults.setProperty("RewardPointsForElite", "4");

        // repod settings
        defaults.setProperty("DoesRepodCost", "true");
        defaults.setProperty("RepodCostLight", "20");
        defaults.setProperty("RepodCostMedium", "50");
        defaults.setProperty("RepodCostHeavy", "100");
        defaults.setProperty("RepodCostAssault", "150 ");
        defaults.setProperty("RepodFluLight", "5");
        defaults.setProperty("RepodFluMedium", "10");
        defaults.setProperty("RepodFluHeavy", "15");
        defaults.setProperty("RepodFluAssault", "20");
        defaults.setProperty("RepodUsesFactory", "true");
        defaults.setProperty("RepodRefreshTimeLight", "2");
        defaults.setProperty("RepodRefreshTimeMedium", "4");
        defaults.setProperty("RepodRefreshTimeHeavy", "6");
        defaults.setProperty("RepodRefreshTimeAssault", "8");
        defaults.setProperty("RepodUsesComp", "true");
        defaults.setProperty("RepodCompLight", "1000");
        defaults.setProperty("RepodCompMedium", "2000");
        defaults.setProperty("RepodCompHeavy", "4500");
        defaults.setProperty("RepodCompAssault", "9500");
        defaults.setProperty("UseCommonTableForRepod", "true");
        defaults.setProperty("RepodRandomMod", "50");
        defaults.setProperty("RandomRepodAllowed", "true");
        defaults.setProperty("RandomRepodOnly", "false");
        defaults.setProperty("GlobalRepodAllowed", "false");
        defaults.setProperty("GlobalRepodWithRPCost", "10");
        defaults.setProperty("NoFactoryRepodFolder", "3025");

        defaults.setProperty("UnitsInMultipleArmiesAmount", "1");

        defaults.setProperty("ShowComponentGainEvery", "4");
        defaults.setProperty("ShowFactionRanks", "true");
        defaults.setProperty("NewbieHouseName", "Solaris Training Company");
        defaults.setProperty("UseStaticMaps", "false");
        defaults.setProperty("SaveEverySlice", "4");
        defaults.setProperty("ExperienceForBay", "500");
        defaults.setProperty("MaxBaysFromEXP", "10");
        defaults.setProperty("AllowedMegaMekVersion", "0.29.81-dev");

        // BLIND DROPS (MM option: real_blind_drop)
        defaults.setProperty("UseBlindDrops", "false");// TODO: move this into
        // ops

        // SOL unit defaults & legal fights
        defaults.setProperty("NumUnitsToQualifyForNew", "5");
        defaults.setProperty("NumResetsWhileImmune", "0");
        defaults.setProperty("SOLLightMeks", "3");
        defaults.setProperty("SOLMediumMeks", "3");
        defaults.setProperty("SOLHeavyMeks", "0");
        defaults.setProperty("SOLAssaultMeks", "0");
        defaults.setProperty("SOLLightVehs", "0");
        defaults.setProperty("SOLMediumVehs", "0");
        defaults.setProperty("SOLHeavyVehs", "0");
        defaults.setProperty("SOLAssaultVehs", "0");
        defaults.setProperty("SOLLightInf", "0");
        defaults.setProperty("SOLMediumInf", "0");
        defaults.setProperty("SOLHeavyInf", "0");
        defaults.setProperty("SOLAssaultInf", "0");
        defaults.setProperty("SOLLightProtoMek", "0");
        defaults.setProperty("SOLMediumProtoMek", "0");
        defaults.setProperty("SOLHeavyProtoMek", "0");
        defaults.setProperty("SOLAssaultProtoMek", "0");
        defaults.setProperty("SOLLightBattleArmor", "0");
        defaults.setProperty("SOLMediumBattleArmor", "0");
        defaults.setProperty("SOLHeavyBattleArmor", "0");
        defaults.setProperty("SOLAssaultBattleArmor", "0");
        defaults.setProperty("SOLLightAero", "0");
        defaults.setProperty("SOLMediumAero", "0");
        defaults.setProperty("SOLHeavyAero", "0");
        defaults.setProperty("SOLAssaultAero", "0");

        // artillery defaults
        defaults.setProperty("AssaultArtilleryFile", "ArrowIV.blk");
        defaults.setProperty("HeavyArtilleryFile", "LongTom.blk");
        defaults.setProperty("MediumArtilleryFile", "Sniper.blk");
        defaults.setProperty("LightArtilleryFile", "Thumper.blk");
        defaults.setProperty("MaxAssaultArtillery", "0");
        defaults.setProperty("MaxHeavyArtillery", "1");
        defaults.setProperty("MaxMediumArtillery", "2");
        defaults.setProperty("MaxLightArtillery", "100");
        defaults.setProperty("BVForAssaultArtillery", "8000");
        defaults.setProperty("BVForHeavyArtillery", "4000");
        defaults.setProperty("BVForMediumArtillery", "2000");
        defaults.setProperty("BVForLightArtillery", "1000");
        defaults.setProperty("DistanceFromMap", "5");
        defaults.setProperty("HeaviestArtilleryFirst", "true");

        // Gun Emplacement defaults
        defaults.setProperty("AssaultGunEmplacementFile", "heavy-ranged-missile-battery.blk");
        defaults.setProperty("HeavyGunEmplacementFile", "heavy-ranged-missile-battery.blk");
        defaults.setProperty("MediumGunEmplacementFile", "light-laser-battery.blk");
        defaults.setProperty("LightGunEmplacementFile", "static-light-laser-battery.blk");
        defaults.setProperty("MaxAssaultGunEmplacement", "0");
        defaults.setProperty("MaxHeavyGunEmplacement", "1");
        defaults.setProperty("MaxMediumGunEmplacement", "2");
        defaults.setProperty("MaxLightGunEmplacement", "100");
        defaults.setProperty("BVForAssaultGunEmplacement", "8000");
        defaults.setProperty("BVForHeavyGunEmplacement", "4000");
        defaults.setProperty("BVForMediumGunEmplacement", "2000");
        defaults.setProperty("BVForLightGunEmplacement", "1000");
        defaults.setProperty("HeaviestGunEmplacementFirst", "true");

        // Offboard units base chance of being over run
        // -1 for each hex off they are.
        defaults.setProperty("ArtilleryOffBoardOverRun", "117");
        defaults.setProperty("OffBoardChanceOfCapture", "50");

        // campaign lock (prevents activation)
        defaults.setProperty("CampaignLock", "false");

        // min exp to set the motd
        defaults.setProperty("MinMOTDExp", "200");
        defaults.setProperty("MaxMOTDLength", "7000");

        // ratios TODO: Remove? Move into ops? Are these still used?
        defaults.setProperty("AllowRatios", "false"); // if turned off you can
        // have as many units as
        // you want and also
        // non-mek armies
        defaults.setProperty("MekToInfantryRatio", "100");// with AllowRatios
        // on this well set
        // your inf to mek
        // Ratio 100% 50% if
        // 50% then 1 inf
        // for every 2 meks
        defaults.setProperty("MekToVehicleRatio", "100");

        // Pilot skill selectable settings
        // Mek Only
        defaults.setProperty("chanceforDMforMek", "20");
        defaults.setProperty("chanceforMSforMek", "20");
        defaults.setProperty("chanceforPRforMek", "20");
        defaults.setProperty("chanceforSVforMek", "20");
        defaults.setProperty("chanceforIMforMek", "20");
        defaults.setProperty("chanceforEDforMek", "20");

        // skills for all units
        defaults.setProperty("chanceforMAforMek", "20");
        defaults.setProperty("chanceforMAforVehicle", "20");
        defaults.setProperty("chanceforMAforInfantry", "20");
        defaults.setProperty("chanceforMAforProtoMek", "20");
        defaults.setProperty("chanceforMAforBattleArmor", "20");
        defaults.setProperty("chanceforMAforAero", "20");
        defaults.setProperty("chanceforNAPforMek", "20");
        defaults.setProperty("chanceforNAPforVehicle", "20");
        defaults.setProperty("chanceforNAPforInfantry", "20");
        defaults.setProperty("chanceforNAPforProtoMek", "20");
        defaults.setProperty("chanceforNAPforBattleArmor", "20");
        defaults.setProperty("chanceforNAPforAero", "20");
        defaults.setProperty("chanceforNAGforMek", "20");
        defaults.setProperty("chanceforNAGforVehicle", "20");
        defaults.setProperty("chanceforNAGforInfantry", "20");
        defaults.setProperty("chanceforNAGforProtoMek", "20");
        defaults.setProperty("chanceforNAGforBattleArmor", "20");
        defaults.setProperty("chanceforNAGforAero", "20");
        defaults.setProperty("chanceforATforMek", "20");
        defaults.setProperty("chanceforATforVehicle", "20");
        defaults.setProperty("chanceforATforInfantry", "20");
        defaults.setProperty("chanceforATforProtoMek", "20");
        defaults.setProperty("chanceforATforBattleArmor", "20");
        defaults.setProperty("chanceforATforAero", "20");
        defaults.setProperty("chanceforWSforMek", "0");
        defaults.setProperty("chanceforWSforVehicle", "0");
        defaults.setProperty("chanceforWSforInfantry", "0");
        defaults.setProperty("chanceforWSforProtoMek", "0");
        defaults.setProperty("chanceforWSforBattleArmor", "0");
        defaults.setProperty("chanceforWSforAero", "0");
        defaults.setProperty("chanceforTGforMek", "0");
        defaults.setProperty("chanceforTGforVehicle", "0");
        defaults.setProperty("chanceforTGforInfantry", "0");
        defaults.setProperty("chanceforTGforProtoMek", "0");
        defaults.setProperty("chanceforTGforBattleArmor", "0");
        defaults.setProperty("chanceforTGforAero", "0");
        defaults.setProperty("chanceforGMforMek", "0");
        defaults.setProperty("chanceforGMforVehicle", "0");
        defaults.setProperty("chanceforGMforInfantry", "0");
        defaults.setProperty("chanceforGMforProtoMek", "0");
        defaults.setProperty("chanceforGMforBattleArmor", "0");
        defaults.setProperty("chanceforGMforAero", "0");
        defaults.setProperty("chanceforGBforMek", "0");
        defaults.setProperty("chanceforGBforVehicle", "0");
        defaults.setProperty("chanceforGBforInfantry", "0");
        defaults.setProperty("chanceforGBforProtoMek", "0");
        defaults.setProperty("chanceforGBforBattleArmor", "0");
        defaults.setProperty("chanceforGBforAero", "0");
        defaults.setProperty("chanceforGLforMek", "0");
        defaults.setProperty("chanceforGLforVehicle", "0");
        defaults.setProperty("chanceforGLforInfantry", "0");
        defaults.setProperty("chanceforGLforProtoMek", "0");
        defaults.setProperty("chanceforGLforBattleArmor", "0");
        defaults.setProperty("chanceforGLforAero", "0");
        defaults.setProperty("chanceforTNforMek", "0");
        defaults.setProperty("chanceforTNforVehicle", "0");
        defaults.setProperty("chanceforTNforInfantry", "0");
        defaults.setProperty("chanceforTNforProtoMek", "0");
        defaults.setProperty("chanceforTNforBattleArmor", "0");
        defaults.setProperty("chanceforTNforAero", "0");
        defaults.setProperty("chanceforEIforMek", "0");
        defaults.setProperty("chanceforEIforVehicle", "0");
        defaults.setProperty("chanceforEIforInfantry", "0");
        defaults.setProperty("chanceforEIforProtoMek", "0");
        defaults.setProperty("chanceforEIforBattleArmor", "0");
        defaults.setProperty("chanceforEIforAero", "0");
        defaults.setProperty("chanceforGTforMek", "0");
        defaults.setProperty("chanceforGTforVehicle", "0");
        defaults.setProperty("chanceforGTforInfantry", "0");
        defaults.setProperty("chanceforGTforProtoMek", "0");
        defaults.setProperty("chanceforGTforBattleArmor", "0");
        defaults.setProperty("chanceforGTforAero", "0");
        defaults.setProperty("chanceforQSforMek", "0");
        defaults.setProperty("chanceforQSforVehicle", "0");
        defaults.setProperty("chanceforQSforInfantry", "0");
        defaults.setProperty("chanceforQSforProtoMek", "0");
        defaults.setProperty("chanceforQSforBattleArmor", "0");
        defaults.setProperty("chanceforQSforAero", "0");
        defaults.setProperty("chanceforMTforMek", "20");
        defaults.setProperty("chanceforMTforProtoMek", "20");
        defaults.setProperty("chanceforVDNIforMek", "0");
        defaults.setProperty("chanceforVDNIforVehicle", "0");
        defaults.setProperty("chanceforVDNIforAero", "0");
        defaults.setProperty("chanceforBVDNIforMek", "0");
        defaults.setProperty("chanceforBVDNIforVehicle", "0");
        defaults.setProperty("chanceforBVDNIforAero", "0");
        defaults.setProperty("chanceforPSforMek", "0");
        defaults.setProperty("chanceforPSforVehicle", "0");
        defaults.setProperty("chanceforPSforInfantry", "0");
        defaults.setProperty("chanceforPSforBattleArmor", "0");
        defaults.setProperty("chanceforPSforAero", "0");

        // limiter settings
        defaults.setProperty("ShowInfInCheckAttack", "true");
        defaults.setProperty("CountInfForLimiters", "true");
        defaults.setProperty("LowerLimitBuffer", "1");
        defaults.setProperty("UpperLimitBuffer", "1");
        defaults.setProperty("DefaultUpperLimit", "4");// make this client
        // configurable
        // eventually
        defaults.setProperty("DefaultLowerLimit", "4");// make this client
        // configurable
        // eventually
        defaults.setProperty("AllowLimiters", "true");

        // Pilots/Crews stay with their units after donation
        defaults.setProperty("CrewsStayWithUnits", "false");

        // Pesonal Pilot Queues
        defaults.setProperty("AllowPersonalPilotQueues", "false");
        defaults.setProperty("CostToBuyNewPilot", "1");
        defaults.setProperty("CostToBuyNewProtoPilot", "1");
        defaults.setProperty("AllowPlayerToBuyPilotsFromHouseWhenPoolIsFull", "false");
        defaults.setProperty("MaxAllowedPilotsInQueueToBuyFromHouse", "2"); // if the player has more then 2 pilots in their queue of size and wieght they cannot buy from the faction.
        defaults.setProperty("BasePilotSurvival", "20"); // if the player has more then 2 pilots in their queue of a size and wieght they cannot buy from the faction.
        defaults.setProperty("TrappedInMechSurvivalMod", "-20"); // If the pilot was still in his mek when it died this mod is applied to the surival rate.
        defaults.setProperty("DownPilotsMustRollForSurvival", "false"); // engined and cored meks whoes pilots are alive may have to check for survival
        defaults.setProperty("ChanceToConvertCapturedPilots", "10");// capture a pilot and you have chance to convert them

        // no-play options
        defaults.setProperty("NoPlayListSize", "1");
        defaults.setProperty("NoPlayMUCost", "25");
        defaults.setProperty("NoPlayRPCost", "1");
        defaults.setProperty("NoPlayInfluenceCost", "100");
        defaults.setProperty("NoPlaysFromAdminsCountForMax", "true");

        // pilot retirement options
        defaults.setProperty("PilotRetirementAllowed", "true");
        defaults.setProperty("EarlyRetirementAllowed", "true");
        defaults.setProperty("TotalSkillForFreeRetirement", "5");
        defaults.setProperty("CostPerLevelToRetireEarly", "25");
        defaults.setProperty("UseCommonPilotNameFileOnly", "true");
        defaults.setProperty("RandomRetirementOfElites", "false");

        // Money and resource names
        defaults.setProperty("MoneyLongName", "CBills");
        defaults.setProperty("MoneyShortName", "CB");
        defaults.setProperty("FluLongName", "Influence");
        defaults.setProperty("FluShortName", "flu");

        // Techs per Units exculding Protos
        defaults.setProperty("TechsForLightMek", "3");
        defaults.setProperty("TechsForMediumMek", "4");
        defaults.setProperty("TechsForHeavyMek", "5");
        defaults.setProperty("TechsForAssaultMek", "6");
        defaults.setProperty("TechsForLightVehicle", "1");
        defaults.setProperty("TechsForMediumVehicle", "2");
        defaults.setProperty("TechsForHeavyVehicle", "3");
        defaults.setProperty("TechsForAssaultVehicle", "4");
        defaults.setProperty("TechsForLightInfantry", "1");
        defaults.setProperty("TechsForMediumInfantry", "1");
        defaults.setProperty("TechsForHeavyInfantry", "1");
        defaults.setProperty("TechsForAssaultInfantry", "1");
        defaults.setProperty("TechsForLightBattleArmor", "1");
        defaults.setProperty("TechsForMediumBattleArmor", "1");
        defaults.setProperty("TechsForHeavyBattleArmor", "1");
        defaults.setProperty("TechsForAssaultBattleArmor", "1");
        defaults.setProperty("TechsForLightAero", "1");
        defaults.setProperty("TechsForMediumAero", "1");
        defaults.setProperty("TechsForHeavyAero", "1");
        defaults.setProperty("TechsForAssaultAero", "1");

        // Defection penalties @urgru 6.13.05
        defaults.setProperty("DefectionUnitLossPercent", "100");
        defaults.setProperty("DefectionInfluenceLossPercent", "100");
        defaults.setProperty("DefectionRewardLossPercent", "0");
        defaults.setProperty("DefectionCBillLossPercent", "25");
        defaults.setProperty("DefectionEXPLossPercent", "25");

        defaults.setProperty("DefectionUnitLossFlat", "0");
        defaults.setProperty("DefectionInfluenceLossFlat", "0");
        defaults.setProperty("DefectionRewardLossFlat", "0");
        defaults.setProperty("DefectionCBillLossFlat", "0");
        defaults.setProperty("DefectionEXPLossFlat", "0");

        defaults.setProperty("ReplaceUnitsLeavingSOL", "false");
        defaults.setProperty("FactionUnitsLeavingSOL", "false");
        defaults.setProperty("PenalizeDefectToMerc", "true");
        defaults.setProperty("PenalizeDefectToNonConq", "true");

        defaults.setProperty("MaxIdleTime", "0");
        // ban weapon specialist weapons
        defaults.setProperty("BannedWSWeapons", "AMS,Leg Attack,Swarm Mek,Stop Swarm Attack");

        // Direct Sell Vars
        defaults.setProperty("UseDirectSell", "false");
        defaults.setProperty("SellDirectLightMekPrice", "0");
        defaults.setProperty("SellDirectMediumMekPrice", "0");
        defaults.setProperty("SellDirectHeavyMekPrice", "0");
        defaults.setProperty("SellDirectAssaultMekPrice", "0");
        defaults.setProperty("SellDirectLightVehiclePrice", "0");
        defaults.setProperty("SellDirectMediumVehiclePrice", "0");
        defaults.setProperty("SellDirectHeavyVehiclePrice", "0");
        defaults.setProperty("SellDirectAssaultVehiclePrice", "0");
        defaults.setProperty("SellDirectLightInfantryPrice", "0");
        defaults.setProperty("SellDirectMediumInfantryPrice", "0");
        defaults.setProperty("SellDirectHeavyInfantryPrice", "0");
        defaults.setProperty("SellDirectAssaultInfantryPrice", "0");
        defaults.setProperty("SellDirectLightBattleArmorPrice", "0");
        defaults.setProperty("SellDirectMediumBattleArmorPrice", "0");
        defaults.setProperty("SellDirectHeavyBattleArmorPrice", "0");
        defaults.setProperty("SellDirectAssaultBattleArmorPrice", "0");
        defaults.setProperty("SellDirectLightProtoMekPrice", "0");
        defaults.setProperty("SellDirectMediumProtoMekPrice", "0");
        defaults.setProperty("SellDirectHeavyProtoMekPrice", "0");
        defaults.setProperty("SellDirectAssaultProtoMekPrice", "0");
        defaults.setProperty("SellDirectLightAeroPrice", "0");
        defaults.setProperty("SellDirectMediumAeroPrice", "0");
        defaults.setProperty("SellDirectHeavyAeroPrice", "0");
        defaults.setProperty("SellDirectAssaultAeroPrice", "0");

        // Level 3 MaxTech Pilot Skill BV Mods.
        defaults.setProperty("GunneryBallisticBaseBVMod", "50");
        defaults.setProperty("GunneryLaserBaseBVMod", "50");
        defaults.setProperty("GunneryMissileBaseBVMod", "50");
        defaults.setProperty("PainResistanceBaseBVMod", "50");
        defaults.setProperty("IronManBaseBVMod", "50");
        defaults.setProperty("DodgeManeuverBaseBVMod", "50");
        defaults.setProperty("ManeuveringAceBaseBVMod", "50");
        defaults.setProperty("ManeuveringAceSpeedRating", "5");
        defaults.setProperty("TacticalGeniusBVMod", "50");
        defaults.setProperty("MeleeSpecialistBaseBVMod", "50");
        defaults.setProperty("HatchetRating", "5");
        defaults.setProperty("WeaponSpecialistBaseBVMod", "100");
        defaults.setProperty("EnhancedInterfaceBaseBVMod", "50");
        defaults.setProperty("EdgeBaseBVMod", "50");
        defaults.setProperty("MaxEdgeChanges", "5");
        defaults.setProperty("VDNIBaseBVMod", "50");
        defaults.setProperty("BufferedVDNIBaseBVMod", "50");
        defaults.setProperty("PainShuntBaseBVMod", "50");

        // Mega Mek Client Settings
        defaults.setProperty("MMTimeStampLogFile", "false");
        defaults.setProperty("MMShowUnitId", "true");
        defaults.setProperty("MMKeepGameLog", "false");
        defaults.setProperty("MMGameLogName", "game.log");

        // Automated backup settings
        defaults.setProperty("AutomaticBackupHours", "12");
        defaults.setProperty("LastAutomatedBackup", "0");

        // Pilot level up settings (need GUI)
        defaults.setProperty("AllowGreenPilots", "false");
        defaults.setProperty("BestGunnerySkill", "1");
        defaults.setProperty("BestPilotingSkill", "2");
        defaults.setProperty("BestTotalPilot", "4");
        defaults.setProperty("BaseRollToLevel", "250");
        defaults.setProperty("MultiplierPerPreviousLevel", "1");// 0 to use only base
        defaults.setProperty("UseRandomPilotLevelups", "true");
        defaults.setProperty("LosingPilotsCheckToLevel", "false");

        // Disconnection auto-report settings
        defaults.setProperty("DisconnectionAddUnitsDestroyed", "0");
        defaults.setProperty("DisconnectionAddUnitsSalvage", "1");
        defaults.setProperty("DisconnectionTimeToReport", "600");// 10
        // minutes
        // to report
        defaults.setProperty("DisconnectionGracePeriod", "120");// 2 minute
        // grace period
        // after crash
        defaults.setProperty("DisconnectionPayPercentage", "55");

        // Prelim Reports
        defaults.setProperty("AllowPreliminaryOperationsReports", "false");
        defaults.setProperty("MinChanceForAccurateOperationsReports", "100");

        // Use Calculated costs instead of static cost for units.
        defaults.setProperty("UseCalculatedCosts", "false");
        defaults.setProperty("CostModifier", "1.0"); // this is set to off
        // lower or raise the
        // calculated cost. i.e.
        // cost is 10 mil for a
        // unit .1(10%) will set
        // it to 1 mil.

        // Disputed plants
        defaults.setProperty("DisputedPlanetColor", "999999");
        defaults.setProperty("MinPlanetOwnerShip", "50");

        // Advanced Repair
        defaults.setProperty("UseAdvanceRepair", "false");
        defaults.setProperty("UseSimpleRepair", "false");
        defaults.setProperty("UseTechRepair", "false");

        // Increase the cost of non-faction units for maintenance
        defaults.setProperty("UseNonFactionUnitsIncreasedTechs", "false");
        defaults.setProperty("NonFactionUnitsIncreasedTechs", "1.0");

        // The base cost to hire a tech.
        defaults.setProperty("GreenTechHireCost", "1");
        defaults.setProperty("RegTechHireCost", "2");
        defaults.setProperty("AllowRegTechsToBeHired", "false");

        // The base cost for each of these techs to do a job.
        defaults.setProperty("GreenTechRepairCost", "1");
        defaults.setProperty("RegTechRepairCost", "2");
        defaults.setProperty("VetTechRepairCost", "3");
        defaults.setProperty("EliteTechRepairCost", "4");

        // chance that a tech dies when they fail a repair on a 2
        defaults.setProperty("ChanceTechDiesOnFailedRepair", "1");

        // Hanger buy and sell back costs.
        defaults.setProperty("CostToBuyNewBay", "10");
        defaults.setProperty("BaySellBackPrice", "2");

        // Max number of bays a player is allowed to buy. -1 to disable.
        defaults.setProperty("MaxBaysToBuy", "-1");

        /*
         * Number of seconds it will take to repair a crit/weapon Based on Maxtech repairs i.e. engines take longer to repair then Medium Lasers or Actuators.
         */
        defaults.setProperty("TimeForEachRepairPoint", "60");

        /*
         * What it costs to repair each crit. Note this will be accumulative as all the crits will be counted i.e. Large Laser with 2 cirts will cost more to repair then a medium Laser. Engines are based on the total number of crits for the engine not just the number of crits damaged. i.e. a STD engine costs less to repair then an XL engine.
         */
        defaults.setProperty("EngineCritRepairCost", "1.0");

        // Break out of equipment types.
        defaults.setProperty("SystemCritRepairCost", "1.0");
        defaults.setProperty("EquipmentCritRepairCost", "1.0");
        defaults.setProperty("EnergyWeaponCritRepairCost", "1.0");
        defaults.setProperty("BallisticCritRepairCost", "1.0");
        defaults.setProperty("MissileCritRepairCost", "1.0");

        // Break out of equipment types for replacement.
        defaults.setProperty("SystemCritReplaceCost", "1.0");
        defaults.setProperty("EquipmentCritReplaceCost", "1.0");
        defaults.setProperty("EnergyWeaponCritReplaceCost", "1.0");
        defaults.setProperty("BallisticCritReplaceCost", "1.0");
        defaults.setProperty("MissileCritReplaceCost", "1.0");

        // cost per point of armor.
        defaults.setProperty("CostPointStandard", "1.0");
        defaults.setProperty("CostPointFF", "1.0");
        defaults.setProperty("CostPointReactive", "1.0");
        defaults.setProperty("CostPointReflective", "1.0");
        defaults.setProperty("CostPointHardened", "1.0");
        defaults.setProperty("CostPointLFF", "1.0");
        defaults.setProperty("CostPointHFF", "1.0");
        defaults.setProperty("CostPointPatchwork", "1.0");
        defaults.setProperty("CostPointStealth", "1.0");
        defaults.setProperty("CostPointFFProto", "1.0");

        // Cost per point of IS
        defaults.setProperty("CostPointStandardIS", "1.0");
        defaults.setProperty("CostPointEndoIS", "1.0");
        defaults.setProperty("CostPointEndoProtoIS", "1.0");
        defaults.setProperty("CostPointReinforcedIS", "1.0");
        defaults.setProperty("CostPointCompositeIS", "1.0");

        /*
         * Use real costs this will override the about calculations.
         */
        defaults.setProperty("UseRealRepairCosts", "false");

        /*
         * This is used to modify the cost of real repairs. i.e. scale them up or down depending on the SO's choice value of .75 would use 75% of the real cost.
         */
        defaults.setProperty("RealRepairCostMod", "1.0");

        // Allow players to donate and sell damaged units.
        defaults.setProperty("AllowSellingOfDamagedUnits", "false");
        defaults.setProperty("AllowDonatingOfDamagedUnits", "false");

        // cost modifier to buy donated units that are damaged
        defaults.setProperty("CostModifierToBuyArmorDamagedUnit", "0.9");
        defaults.setProperty("CostModifierToBuyCritDamagedUnit", "0.8");
        defaults.setProperty("CostModifierToBuyEnginedUnit", "0.5");

        // Attack from reserver options
        defaults.setProperty("AllowAttackFromReserve", "false");
        defaults.setProperty("AttackFromReserveResponseTime", "5");
        defaults.setProperty("AttackFromReserveSleepTime", "60");// time
        // between
        // each
        // attack.

        // Pilot Damage Transfers
        defaults.setProperty("AllowPilotDamageToTransfer", "false");
        defaults.setProperty("PilotAmountHealedPerTick", "1");
        defaults.setProperty("MedTechAmountHealedPerTick", "1");
        defaults.setProperty("AmountOfDamagePerPilotHit", "1");

        // Newish Black Market settings
        defaults.setProperty("UseVickeryAuctionType", "true");
        defaults.setProperty("UseHighestSealedBidAuctionType", "false");
        defaults.setProperty("AuctionFee", "0.15");// double, so .15 = 15% of
        // the price is taken as fee
        defaults.setProperty("RareMinSaleTime", "30");// in ticks
        defaults.setProperty("ChanceToSendUnitToBM", "40");// int % chance to
        // sell instead of
        // autoscrapping

        /*
         * BM blocking. Lists are $ delimited and case sensitive, such that a value of "Liao$Marik$Davion" for BMNoSell would stop all sales attempts by players in factions w/ those exact names. "NoClanTech" is a boolean. If true, no *player* will be able to sell clan units on the BM, but house overflows (unless NoSell is enabled for the house) and rare production can still go on.
         */
        defaults.setProperty("BMNoBuy", "");
        defaults.setProperty("BMNoSell", "");
        defaults.setProperty("BMNoClan", "false");

        /*
         * Price adjustments for purchases from factories not originally owned by the faction. This adjustment stacks with any faction penalty already attached to the factory. Float values, used as multipliers. Examples: 80 CBill Faction Base * 1.15 CBillMultiplier = 92 CBill final cost. 80 CBill Faction Base * 1.00 CBillMultiplier = 80 CBill final cost. 80 CBill Faction Base * 0.75 CBillMultiplier = 60 CBill final cost.
         */
        defaults.setProperty("NonOriginalCBillMultiplier", "1.0");
        defaults.setProperty("NonOriginalInfluenceMultiplier", "1.0");
        defaults.setProperty("NonOriginalComponentMultiplier", "1.0");

        // Special scrapping costs for damaged units when using Advanced Repair
        defaults.setProperty("CostToScrapOnlArmorDamage", ".16");
        defaults.setProperty("CostToScrapCriticallyDamaged", ".8");
        defaults.setProperty("CostToScrapEngined", ".0");

        // If you unenroll where to the units go?
        defaults.setProperty("DonateUnitsUponUnenrollment", "false");

        // Show Op name to the Defender.
        defaults.setProperty("DisplayOperationName", "false");

        // Number of days a player is inactive before they get purged.
        defaults.setProperty("PurgePlayerFilesDays", "0");

        // Key to allow SO's to force their Clients to update
        defaults.setProperty("ForceUpdateKey", "");

        // To allow or not to allow partial bins into combat.
        defaults.setProperty("AllowUnitsToActivateWithPartialBins", "true");

        // Only used when commands are saved to the DB
        defaults.setProperty("ServerOptionsSavedToDB", "false");

        defaults.setProperty("StoreUnitHistoryInDatabase", "false");

        // Mod Mail Message of the Day
        defaults.setProperty("MMOTD", "");

        // REF 1602720 factories fresh at a set rate
        defaults.setProperty("FactoryRefreshPoints", "-1");
        defaults.setProperty("TIMESTAMP", "-1");

        // Black Markt Parts
        defaults.setProperty("UsePartsBlackMarket", "false");
        defaults.setProperty("UsePartsRepair", "false");
        defaults.setProperty("AllowCrossOverTech", "false");

        defaults.setProperty("ForumGroupName", "");
        defaults.setProperty("REQUIREEMAILFORREGISTRATION", "false");

        // Victory Condition Kill Unit Commanders Settings.
        defaults.setProperty("allowUnitCommanderMek", "true");
        defaults.setProperty("allowUnitCommanderVehicle", "false");
        defaults.setProperty("allowUnitCommanderInfantry", "false");
        defaults.setProperty("allowUnitCommanderProtoMek", "false");
        defaults.setProperty("allowUnitCommanderBattleArmor", "false");
        defaults.setProperty("allowUnitCommanderVTOL", "false");
        defaults.setProperty("allowUnitCommanderAero", "false");
        defaults.setProperty("allowGoingActiveWithoutUnitCommanders", "true");

        // SubFactions
        defaults.setProperty("autoPromoteSubFaction", "true");
        defaults.setProperty("factionLeaderLevel", "30");
        defaults.setProperty("daysbetweenpromotions", "7");

        // Factory Type Names
        defaults.setProperty("LightFactoryTypeTitle", "Light");
        defaults.setProperty("MediumFactoryTypeTitle", "Medium");
        defaults.setProperty("HeavyFactoryTypeTitle", "Heavy");
        defaults.setProperty("AssaultFactoryTypeTitle", "Assault");

        // Factory Class Names
        defaults.setProperty("MekFactoryClassTitle", "Mek");
        defaults.setProperty("VehicleFactoryClassTitle", "Vehicle");
        defaults.setProperty("ProtoMekFactoryClassTitle", "ProtoMek");
        defaults.setProperty("BattleArmorFactoryClassTitle", "BattleArmor");
        defaults.setProperty("InfantryFactoryClassTitle", "Infantry");
        defaults.setProperty("AeroFactoryClassTitle", "Aero");

        // Single Player Faction Configs
        defaults.setProperty("AllowSinglePlayerFactions", "false");
        defaults.setProperty("MaxFactionName", "20");
        defaults.setProperty("MaxFactionShortName", "5");
        defaults.setProperty("StartingLightMekFactory", "1");
        defaults.setProperty("StartingMediumMekFactory", "1");
        defaults.setProperty("StartingHeavyMekFactory", "1");
        defaults.setProperty("StartingAssaultMekFactory", "1");
        defaults.setProperty("StartingLightInfantryFactory", "1");
        defaults.setProperty("StartingMediumInfantryFactory", "1");
        defaults.setProperty("StartingHeavyInfantryFactory", "1");
        defaults.setProperty("StartingAssaultInfantryFactory", "1");
        defaults.setProperty("StartingLightVehicleFactory", "1");
        defaults.setProperty("StartingMediumVehicleFactory", "1");
        defaults.setProperty("StartingHeavyVehicleFactory", "1");
        defaults.setProperty("StartingAssaultVehicleFactory", "1");
        defaults.setProperty("StartingLightBattleArmorFactory", "1");
        defaults.setProperty("StartingMediumBattleArmorFactory", "1");
        defaults.setProperty("StartingHeavyBattleArmorFactory", "1");
        defaults.setProperty("StartingAssaultBattleArmorFactory", "1");
        defaults.setProperty("StartingLightProtoMekFactory", "1");
        defaults.setProperty("StartingMediumProtoMekFactory", "1");
        defaults.setProperty("StartingHeavyProtoMekFactory", "1");
        defaults.setProperty("StartingAssaultProtoMekFactory", "1");
        defaults.setProperty("StartingLightAeroFactory", "1");
        defaults.setProperty("StartingMediumAeroFactory", "1");
        defaults.setProperty("StartingHeavyAeroFactory", "1");
        defaults.setProperty("StartingAssaultAeroFactory", "1");
        defaults.setProperty("BaseFactoryRefreshRate", "100");
        defaults.setProperty("BaseCommonBuildTableShares", "100");
        defaults.setProperty("BaseFactoryComponents", "1000");
        defaults.setProperty("StartingPlanetBays", "20");

        // Buying Factory options
        defaults.setProperty("NewFactoryBaseCost", "100");
        defaults.setProperty("NewFactoryCostModifierLight", "1.0");
        defaults.setProperty("NewFactoryCostModifierMedium", "1.0");
        defaults.setProperty("NewFactoryCostModifierHeavy", "1.0");
        defaults.setProperty("NewFactoryCostModifierAssault", "1.0");
        defaults.setProperty("NewFactoryCostModifierMek", "1.0");
        defaults.setProperty("NewFactoryCostModifierVehicle", "1.0");
        defaults.setProperty("NewFactoryCostModifierInfantry", "1.0");
        defaults.setProperty("NewFactoryCostModifierBattleArmor", "1.0");
        defaults.setProperty("NewFactoryCostModifierAero", "1.0");
        defaults.setProperty("NewFactoryCostModifierProtoMek", "1.0");
        defaults.setProperty("NewFactoryBaseFlu", "100");
        defaults.setProperty("NewFactoryFluModifierLight", "1.0");
        defaults.setProperty("NewFactoryFluModifierMedium", "1.0");
        defaults.setProperty("NewFactoryFluModifierHeavy", "1.0");
        defaults.setProperty("NewFactoryFluModifierAssault", "1.0");
        defaults.setProperty("NewFactoryFluModifierMek", "1.0");
        defaults.setProperty("NewFactoryFluModifierVehicle", "1.0");
        defaults.setProperty("NewFactoryFluModifierInfantry", "1.0");
        defaults.setProperty("NewFactoryFluModifierBattleArmor", "1.0");
        defaults.setProperty("NewFactoryFluModifierProtoMek", "1.0");
        defaults.setProperty("NewFactoryFluModifierAero", "1.0");

        // Technology Research Options
        defaults.setProperty("TechPointsNeedToLevel", "100");
        defaults.setProperty("TechPointCost", "100");
        defaults.setProperty("TechPointFlu", "100");
        defaults.setProperty("TechLevelTechPointCostModifier", "1.0");
        defaults.setProperty("TechLevelTechPointFluModifier", "1.0");

        // Unit Research Options
        defaults.setProperty("BaseResearchCost", "1.0");
        defaults.setProperty("BaseResearchFlu", "1.0");
        defaults.setProperty("MaxUnitResearchPoints", "10");
        defaults.setProperty("ResearchTechLevelCostModifer", "1.0");
        defaults.setProperty("ResearchTechLevelFluModifer", "1.0");
        defaults.setProperty("ResearchCostModifierMek", "1.0");
        defaults.setProperty("ResearchCostModifierVehicle", "1.0");
        defaults.setProperty("ResearchCostModifierInfantry", "1.0");
        defaults.setProperty("ResearchCostModifierBattleArmor", "1.0");
        defaults.setProperty("ResearchCostModifierProtoMek", "1.0");
        defaults.setProperty("ResearchCostModifierAero", "1.0");
        defaults.setProperty("ResearchCostModifierLight", "1.0");
        defaults.setProperty("ResearchCostModifierMedium", "1.0");
        defaults.setProperty("ResearchCostModifierHeavy", "1.0");
        defaults.setProperty("ResearchCostModifierAssault", "1.0");
        defaults.setProperty("ResearchFluModifierMek", "1.0");
        defaults.setProperty("ResearchFluModifierVehicle", "1.0");
        defaults.setProperty("ResearchFluModifierInfantry", "1.0");
        defaults.setProperty("ResearchFluModifierBattleArmor", "1.0");
        defaults.setProperty("ResearchFluModifierAero", "1.0");
        defaults.setProperty("ResearchFluModifierProtoMek", "1.0");
        defaults.setProperty("ResearchFluModifierLight", "1.0");
        defaults.setProperty("ResearchFluModifierMedium", "1.0");
        defaults.setProperty("ResearchFluModifierHeavy", "1.0");
        defaults.setProperty("ResearchFluModifierAssault", "1.0");

        // Components to Parts Conversion
        defaults.setProperty("BaseComponentToMoneyRatio", "1.0");
        defaults.setProperty("ComponentToPartsModifierMek", "1.0");
        defaults.setProperty("ComponentToPartsModifierVehicle", "1.0");
        defaults.setProperty("ComponentToPartsModifierInfantry", "1.0");
        defaults.setProperty("ComponentToPartsModifierBattleArmor", "1.0");
        defaults.setProperty("ComponentToPartsModifierAero", "1.0");
        defaults.setProperty("ComponentToPartsModifierProtoMek", "1.0");
        defaults.setProperty("ComponentToPartsModifierLight", "1.0");
        defaults.setProperty("ComponentToPartsModifierMedium", "1.0");
        defaults.setProperty("ComponentToPartsModifierHeavy", "1.0");
        defaults.setProperty("ComponentToPartsModifierAssault", "1.0");

        // Slice Settings
        defaults.setProperty("ProcessHouseTicksAtSlice", "false");
        defaults.setProperty("SendSingleCommandAtATime", "false");
        
        //Pilot Upgrade configs
        defaults.setProperty("PlayersCanBuyPilotUpgrades", "false");
        defaults.setProperty("PilotsMustLevelEvenly", "false");
        defaults.setProperty("GiftedPercent", "5");
        
    }

    /**
     * @author jtighe Saves the current server configs to the configfile.
     */
    public void createConfig() {
        try {
            CampaignMain.cm.saveConfigureFile(CampaignMain.cm.getConfig(), CampaignMain.cm.getServer().getConfigParam("CAMPAIGNCONFIG"));
        } catch (Exception ex) {
            CampaignData.mwlog.errLog("Unable to save config file.");
            CampaignData.mwlog.errLog(ex);
            CampaignData.mwlog.errLog(ex.getMessage());
        }
    }

    public Properties getServerDefaults() {
        return defaults;
    }
}