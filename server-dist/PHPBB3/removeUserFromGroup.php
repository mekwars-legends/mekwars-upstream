#!/usr/bin/php
<?php
if (!isset($argv[1]) || !isset($argv[2]) ) {
        die ("Usage: removeUserFromGroup.php userName|userID groupName|groupID\n");
}

$user_name_id = $argv[1];
$group_name_id = $argv[2];
$userID = 0;
$groupID = 0;

if (is_numeric($user_name_id)) {
        $userID = (int)$user_name_id;
} else {
        $sql = "SELECT user_id from " . USERS_TABLE . " WHERE username_clean = '" . $db->sql_escape($user_name_id) . "'";
        $result = $db->sql_query($sql) or die(mysql_error());
        $row = $db->sql_fetchrow($result);
        $userID = (int)$row['user_id'];
}

if (is_numeric($group_name_id)) {
        $groupID = (int)$group_name_id;
} else {
        $sql = "SELECT group_id FROM " . GROUPS_TABLE . " WHERE group_name = '" . $db->sql_escape($group_name_id) . "'";
        $result = $db->sql_query($sql) or die(mysql_error());
        $row = $db->sql_fetchrow($result);
        $groupID = (int)$row['group_id'];
}

$result = group_user_del($groupID, $userID, false, false, false);
if ($result === false) {
        echo "Group membership update completed.\n";
} else {
        echo "Group membership update failed: " . $result . "\n";
        exit(100);
}
exit(0);
?>