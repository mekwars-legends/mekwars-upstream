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

	/*
	 * Configuration variables and database connection information required
	 * by the majority of the PHPTools scripts
	 */
	
	$DBServer = "localhost";
	$DBUser = "sa";
	$DBPassword = "mypassword";
	
	$DBName = "mekwars";
	
	$db = mysql_connect($DBServer, $DBUser, $DBPassword);
	mysql_select_db($DBName, $db);
	
?>