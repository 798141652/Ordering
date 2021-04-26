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
	$sql = "select shopImage from shopInfo where shopID={$delis} ;";
	$row = (mysqli_query($dbc, $sql))->fetch_assoc();
	if(file_exists($row['shopImage'])){
		unlink($row['shopImage']);
	}
	
    $sql = "delete from shopInfo where shopID={$delis} ;";
    $res = mysqli_query($dbc, $sql);
	
	

    if ($res == 1) {
        echo "<script>alert('删除成功！')</script>";
        echo "<script>window.location.href='admin_shop.php'</script>";
    }
    else {
        echo "删除失败！";
        echo "<script>window.location.href='admin_shop.php'</script>";
    }

?>
