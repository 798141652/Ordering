<?php
session_start();
$userid=$_POST['uid'];
include ('mysqli_connect.php');
require_once 'imagecompress.php';

	
	if(empty($_FILES["img"]["tmp_name"])){
		$imagePath = "image/defaultimage.jpeg";
	}
	else{
		//图片压缩
		$percent = 0.2;
		//压缩后的图片存入内部目录
		$imagePath = "image/userPhoto".$userid.".".image_type_to_extension(getimagesize($_FILES["img"]["tmp_name"])[2],false);
		echo $imagePath;
		if(file_exists($imagePath)){
		unlink($imagePath);
	}
		(new imgcompress($_FILES["img"]["tmp_name"],$percent))->compressImg($imagePath);
	}
	$sqla="update userInfo set userImage = '{$imagePath}' where userID = {$userid}";
	$resa=mysqli_query($dbc,$sqla);
	//echo mysqli_error($dbc);
	mysqli_close($dbc);

?>
</body>
</html>
