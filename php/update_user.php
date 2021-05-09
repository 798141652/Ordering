<?php
//链接数据库
include ("mysqli_connect.php");

//$json=$_POST ['json'];
error_reporting(E_ALL ^ E_DEPRECATED);
//$json = '{"username": "dsvcjf","password":"ddfshfd"}';
//echo $json;
echo ($json = file_get_contents('php://input'));
$obj = json_decode($json,true);//第二个参数为true表示json转为array，而不是object对象

echo $obj;

$num = count($obj);

$sql1 = "delete from userInfo;";
mysqli_query($dbc,$sql1);
echo mysqli_error($dbc);

for($i=0;$i<$num;$i++){
	$userID = $obj[$i]['userID'];
	$userName = $obj[$i]['userName'];
	$userPwd = $obj[$i]['userPwd'];
	$userTel = $obj[$i]['userTel'];
	$userImage = $obj[$i]['userImage'];

	

	$sql2 = "insert into userInfo values ({$userID},'{$userName}','{$userPwd}','{$userTel}','{$userImage}');";
	echo $sql2;
	mysqli_query($dbc,$sql2);
	echo mysqli_error($dbc);
	}



?>
