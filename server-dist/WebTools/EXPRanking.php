<?php

error_reporting(E_ALL);

require('includes/config.php');

// Get the name of the newbie house
$sql = "SELECT fName from factions WHERE fIsNewbieHouse = 1";
$result = mysql_query($sql) or die(mysql_error());
$row = mysql_fetch_array($result);
$newbieHouse = $row['fName'];

$sql = "SELECT playerName, playerExperience, playerHouseName, playerFluff from players WHERE playerHouseName <> '$newbieHouse' AND playerExperience > 0 ORDER BY playerExperience DESC";
$result = mysql_query($sql) or die(mysql_error());
$currentRank = 1;

require('templates/EXPRankHeader.tmp');

while ($row = mysql_fetch_array($result)) {
	$player = array("name"=>$row['playerName'], "exp"=>$row['playerExperience'], "house"=>$row['playerHouseName'], "fluff"=>$row['playerFluff']);
	require('templates/EXPRankRow.tmp');
	$currentRank++;
}

require('templates/EXPRankFooter.tmp');

?>