<?php
/*
* You must configure the following :
* $dbhost
* $dbuser
* $dbpass
* $forumDBname
* $phpbb_root_path
* 
* Then, modify the two require_once lines to point to the
* proper folders
*/
$dbhost = "localhost";
$dbuser = "sa";
$dbpass = "password"
$forumDBname = "forum"
$db = mysql_connect($dbhost, $dbuser, $dbpass);
mysql_select_db($forumDBname, $db);
define('FORUM_ADD', TRUE);
define('phpBBBASE', '/var/www/forum/');
$phpEx = substr(strrchr(__FILE__, '.'), 1);
global $config;
define('IN_PHPBB', TRUE);
define('IN_PORTAL', TRUE);
$phpbb_root_path = '/var/www/forum/';
require_once('/var/www/forum/includes/functions_user.php');
require_once('/var/www/forum/common.php');
?>