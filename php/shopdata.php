<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>


</table>

</body>
</html>

<?php
include ('mysqli_connect.php');
$sql1 = "select shopID,shopName,shopLocation,shopBrief from shopInfo";
$result1 = mysqli_query($dbc,$sql1);

$sql2 = "select shopID,shopImage from shopInfo";
$result2 = mysqli_query($dbc,$sql2);


if($result1->num_rows > 0){
	$row1=array();
	while($row = $result1->fetch_assoc()){
		array_push($row1,$row);
	}
	$json_string1 = json_encode($row1);
	echo $json_string1;
}else{
	echo "0";
}

if($result2->num_rows > 0){
	$row1=array();
	while($row = $result2->fetch_assoc()){
		array_push($row1,$row);
	}
	for($i=0;$i<count($row1);$i++){
		$row1[$i]['shopImage'] = base64_encode($row1[$i]['shopImage']);
	}
	$json_string2 = json_encode($row1);
	echo $json_string2;
}else{
	echo "0";
}

//处理数据
$file = fopen("/var/www/html/ordering/json/shopinfo.json","w")or die("error1");
fwrite($file,$json_string1);
fclose($file);

//处理照片
$file = fopen("/var/www/html/ordering/json/shopimageinfo.json","w")or die("error1");
fwrite($file,$json_string2);
fclose($file)

	
?>