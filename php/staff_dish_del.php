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
var_dump($_SESSION);
include ('mysqli_connect.php');


$delis=$_GET['id'];

	//删除数据的同时删除服务器照片
	$sql = "select dishImage from dishInfo where dishID={$delis} ;";
	$row = (mysqli_query($dbc, $sql))->fetch_assoc();
	if(file_exists($row['dishImage'])){
		unlink($row['dishImage']);
	}
	
    $sql = "delete from dishInfo where dishID={$delis} ;";
    $res = mysqli_query($dbc, $sql);
	
	

    if ($res == 1) {
        echo "<script>alert('删除成功！')</script>";
        echo "<script>window.location.href='staff_dish.php'</script>";
    }
    else {
        echo "删除失败！";
        echo "<script>window.location.href='staff_dish.php'</script>";
    }

?>
