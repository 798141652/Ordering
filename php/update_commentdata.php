<?php
//链接数据库
include ("mysqli_connect.php");

error_reporting(E_ALL ^ E_DEPRECATED);
echo ($json = file_get_contents('php://input'));
$obj = json_decode($json,true);//第二个参数为true表示json转为array，而不是object对象

echo $obj;

$num = count($obj);

$sql1 = "delete from userComment;";
mysqli_query($dbc,$sql1);
echo mysqli_error($dbc);

for($i=0;$i<$num;$i++){
	$commentID = $obj[$i]['commentID'];
	$userID = $obj[$i]['userID'];
	$dishID = $obj[$i]['dishID'];
	$orderID = $obj[$i]['orderID'];
	$shopID = $obj[$i]['shopID'];
	$commentType = $obj[$i]['commentType'];
	$comment = $obj[$i]['comment'];
	$commentTime = $obj[$i]['commentTime'];
	

	$sql2 = "insert into userComment values ({$commentID},{$userID},{$shopID},{$dishID},'{$orderID}','{$commentType}','{$comment}','{$commentTime}');";
	echo $sql2;
	mysqli_query($dbc,$sql2);
	echo mysqli_error($dbc);
	}


?>
