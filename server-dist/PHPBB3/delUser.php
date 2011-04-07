#!/usr/bin/php
<?php

if (!isset($argv[1])) {
        die ("Usage: delUser.php userName\n");
}

$userName = $argv[1];

require_once('config.inc.php');

$sql = "SELECT user_id from " . USERS_TABLE . " WHERE username_clean = '" . $db->sql_escape($userName) . "' AND user_type=0";
$result = $db->sql_query($sql) or die(mysql_error());
if (mysql_num_rows($result) < 1) {
        echo "User $userName not found\n";
        exit(1);
}

$row = $db->sql_fetchrow($result);
$userID = (int)$row['user_id'];

//user_add($user_row) or exit(-1);

user_delete('retain', $userID, $userName);
exit(0);
?>
