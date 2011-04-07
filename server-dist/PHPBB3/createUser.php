#!/usr/bin/php
<?php
if (!isset($argv[1]) || !isset($argv[2]) || !isset($argv[3])) {
        die ("Usage: createUser.php userName userPassword userEmail\n");
}

$user_name = $argv[1];
$user_pass = md5($argv[2]);
$user_email = $argv[3];

require_once('config.inc.php');

$group_name = "REGISTERED";

$sql = "SELECT group_id FROM " . GROUPS_TABLE . " WHERE group_name = '" . $db->sql_escape($group_name) . "' AND group_type = " . GROUP_SPECIAL;

$result = $db->sql_query($sql) or die(1);

$row = $db->sql_fetchrow($result);

$group_id = $row['group_id'];
$user_actkey = md5(rand(0,100) . time());
$user_actkey = substr($user_actkey, 0, rand(6,10));
$timezone = '0';
$language = 'en';
$user_type = USER_NORMAL;
$is_dst = date('I');
$arr = $db->sql_fetchrow($result);
$user_row = array(
        'username'              => $user_name,
        'user_password'         => $user_pass,
        'user_email'            => $user_email,
        'group_id'              => (int) $group_id,
        'user_timezone'         => (float) $timezone,
        'user_dst'              => $is_dst,
        'user_lang'             => $language,
        'user_type'             => $user_type,
        'user_actkey'           => $user_actkey,
        'user_regdate'          => time()
);
user_add($user_row) or exit(1);
exit(0);
?>
