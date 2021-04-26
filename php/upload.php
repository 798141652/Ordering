<?php
include ('mysqli_connect.php');

$data = $_FILES["img"]["tmp_name"];


$fp    = fopen($_FILES['img']['tmp_name'], 'rb');
$image = addslashes(fread($fp, filesize($data)));

$imageProperties = getimageSize($_FILES['img']['tmp_name']);

$imageType = $imageProperties['mime'];
 
$sqlstr = "update shopInfo set shopImage ='".$image."' , imageType ='".$imageType."'";
if (!mysqli_query($dbc,$sqlstr)) 
{ 
    echo("错误描述: " . mysqli_error($dbc)); 
} 

$result = mysqli_query($dbc,$sqlstr);
var_dump($result);
mysqli_close($dbc);


/*
$imgname = $_FILES['img']['name'];
$filepath = "/var/www/html/ordering/image/";
if (!is_dir($filepath)) { //判断目录是否存在 不存在就创建
    mkdir($filepath, 7777, true);
    }
if (move_uploaded_file($data, $filepath . $imgname)) {
        echo "上传成功";
    } else {
        echo "上传失败";
    }*/

?>