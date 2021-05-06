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

$sql1 = "delete from userCart;";
mysqli_query($dbc,$sql1);
echo mysqli_error($dbc);

for($i=0;$i<$num;$i++){
	$cartID = $obj[$i]['cartID'];
	$orderID = $obj[$i]['orderID'];
	$userID = $obj[$i]['userID'];
	$dishID = $obj[$i]['dishID'];
	$dishShop = $obj[$i]['dishShop'];
	$dishName = $obj[$i]['dishName'];
	$dishNum = $obj[$i]['dishNum'];
	$dishPrice = $obj[$i]['dishPrice'];
	$cartPrice = $obj[$i]['cartPrice'];
	$cartStatus = $obj[$i]['cartStatus'];
	$cartTime = $obj[$i]['cartTime'];
	

	$sql2 = "insert into userCart values ({$cartID},'{$orderID}',{$userID},{$dishShop},{$dishID},'{$dishName}',{$dishNum},{$dishPrice},{$cartPrice},'{$cartStatus}','{$cartTime}');";
	echo $sql2;
	mysqli_query($dbc,$sql2);
	echo mysqli_error($dbc);
	}


/* grab the posts from the db */
//$query = "SELECT post_title, guid FROM wp_posts WHERE post_author     = $user_id AND post_status = 'publish' ORDER BY ID DESC LIMIT     $number_of_posts";
/*
$u=$obj->{'username'};
$p=$obj->{'password'}; 
mysqli_query("INSERT INTO userCart VALUES ('$u','$p')");

echo mysqli_error($dbc);

$dbc->close();
*/

?>
