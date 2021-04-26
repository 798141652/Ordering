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
$sql = "select * from shopInfo";
$result = mysqli_query($dbc,$sql);
if($result->num_rows > 0){
	$row1=array();
	while($row = $result->fetch_assoc()){
		array_push($row1,$row);
	}
	$json_string = json_encode($row1);
	echo $json_string;
}else{
	echo "0";
}

//处理数据
$file = fopen("/var/www/html/ordering/json/shopinfo.json","w")or die("error1");
fwrite($file,$json_string);
fclose($file);


$dbc->close();
?>