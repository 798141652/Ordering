<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

</body>
</html>
<?php
session_start();
include ('mysqli_connect.php');

$delis=$_GET['id'];

//删除数据的同时删除服务器照片
	$sql = "select userImage from shopInfo where shopID={$delis} ;";
	$row = (mysqli_query($dbc, $sql))->fetch_assoc();
	if($row['userImage'] != "image/defaultimage.jpeg"){
		if(file_exists($row['userImage'])){
		unlink($row['userImage']);
	}

    $sql = "delete  from userInfo where userID={$delis} ;";
    $res = mysqli_query($dbc, $sql);

    if ($res == 1) {
        echo "<script>alert('删除成功！')</script>";
        echo "<script>window.location.href='admin_user.php'</script>";
    }
    else {
        echo "删除失败！";
        echo "<script>window.location.href='admin_user.php'</script>";
    }

?>
