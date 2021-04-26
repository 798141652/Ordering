<?php
include ('mysqli_connect.php');
$rs = mysqli_query($dbc,"select dishImage,imageType from dishInfo where dishID=$_GET[id]");
$data = mysqli_fetch_assoc($rs);
header("Content-type: " . $data["imageType"]);
echo $data["dishImage"];
?>