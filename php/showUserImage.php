<?php
include ('mysqli_connect.php');
$rs = mysqli_query($dbc,"select userImage,imageType from userInfo where userID=$_GET[id]");
$data = mysqli_fetch_assoc($rs);
header("Content-type: " . $data["imageType"]);
echo $data["userImage"];
?>