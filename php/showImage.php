<?php
include ('mysqli_connect.php');
$rs = mysqli_query($dbc,"select shopImage,imageType from shopInfo where shopID=$_GET[id]");
$data = mysqli_fetch_assoc($rs);
header("Content-type: " . $data["imageType"]);
echo $data["shopImage"];
?>