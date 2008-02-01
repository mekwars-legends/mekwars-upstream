<?php

	/*
	 * MekWars - Copyright (C) 2007 
	 * 
	 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
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

	error_reporting(E_ALL);
	
	require('includes/config.php');

	$sizeNames=array("Light", "Medium", "Heavy", "Assault");
	
	// For each weight class, send out a class header, then pull the mechs.
	for ($size = 0; $size < 4; $size++) {
		require('templates/MechStatsUnitHeader.php');
		$sql = "SELECT * from mechstats WHERE mechSize = $size ORDER BY mechFileName ASC";
		$result = mysql_query($sql) or die(mysql_error());
		while($row = mysql_fetch_array($result)) {
			$unit = array("name"=>"", "gamesPlayed"=>0, "gamesWon"=>0, "destroyed"=>0, "scrapped"=>0, "BV"=>0, "lastUsed"=>"unknown");
			$unit['name']=$row["mechFileName"];
			$unit['gamesPlayed']=$row['gamesPlayed'];
			$unit['gamesWon']=$row['gamesWon'];
			$unit['destroyed']=$row['timesDestroyed'];
			$unit['scrapped']=$row['timesScrapped'];
			$unit['BV']=$row['originalBV'];
			$unit['lastUsed']=($row['lastTimeUpdated']==0 ? "unknown" : $row['lastTimeUpdated']);
			require('templates/MechStatsRow.php');
		}
		require('templates/MechStatsTableFooter.php');
	}
?>