<?php
session_start();
$shopid=$_SESSION['userBelong'];
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}

include ('mysqli_connect.php');
require_once 'imagecompress.php';

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 增加菜品</title>
    <link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style>
        body{
            width: 100%;
            height:auto;

        }
        #query{
            text-align: center;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-default navbar-static-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Ordering点餐后台管理系统</a>
        </div>
        <div>
            <ul class="nav navbar-nav">
                <li ><a href="staff_index.php">主页</a></li>
                <li><a href="staff_shop.php">档口管理</a></li>
                <li class="active" class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">菜品管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="staff_dish.php">全部菜品</a></li>
                        <li><a href="staff_dish_add.php">增加菜品</a></li>
                    </ul>
                </li>
				<li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">订单管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="order_manage.php">未处理订单</a></li>
                        <li><a href="order_completed_manage.php">全部订单</a></li>
                    </ul>
                </li>
				<li><a href="show_comment.php">查看评论</a></li>
                <li><a href="staff_repass.php">密码修改</a></li>
                <li><a href="index.php">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<h1 style="text-align: center"><strong>增加菜品</strong></h1>
<div style="padding: 10px 500px 10px;">
    <form  action="staff_dish_add.php" method="POST" style="text-align: center" class="bs-example bs-example-form" role="form" enctype="multipart/form-data">
        <div id="login">
            <div class="input-group"><span class="input-group-addon">菜品名称</span><input name="dishName" type="text" placeholder="请输入菜品名称" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">菜品价格</span><input name="dishPrice" type="text" placeholder="请输入菜品价格" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">菜品类型</span><input name="dishType" type="text" placeholder="请输入菜品类型" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">菜品照片</span></div><br/>
			<input name="img" type="file"><br/>
			<input type="hidden" class="imageurl" />
            <label><input type="submit" value="添加" class="btn btn-default"></label>
            <label><input type="reset" value="重置" class="btn btn-default"></label>
        </div>
    </form>
</div>
<?php

if ($_SERVER["REQUEST_METHOD"] == "POST")
{
    $dishName = $_POST["dishName"];
    $dishPrice = $_POST["dishPrice"];
    $dishType = $_POST["dishType"];
	
	//由于id自增，所以先插入除了id以及image外的数据
	$sqla="insert into dishInfo VALUES (NULL ,{$shopid},'{$dishName}',NULL,'{$dishPrice}','{$dishType}')";
	$resa=mysqli_query($dbc,$sqla);
	$dishID=mysqli_insert_id($dbc);
		
	
	if(empty($_FILES["img"]["tmp_name"])){
		$imagePath = "image/defaultimage.jpeg";
	}
	else{
		//图片压缩
		$percent = 0.3;
		//压缩后的图片存入内部目录
		$imagePath = "image/dishPhoto".$dishID.".".image_type_to_extension(getimagesize($_FILES["img"]["tmp_name"])[2],false);
		(new imgcompress($_FILES["img"]["tmp_name"],$percent))->compressImg($imagePath);
	}
	$sqla="update dishInfo set dishImage = '{$imagePath}' where dishID = {$dishID}";
	$resa=mysqli_query($dbc,$sqla);
    


    if($resa==1)
    {

        echo "<script>alert('添加成功！')</script>";
        echo "<script>window.location.href='staff_dish.php'</script>";

    }
    else
    {
		echo("错误描述: " . mysqli_error($dbc)); 
        echo "<script>alert('添加失败！请重新输入！');</script>";

    }
	mysqli_close($dbc);

}

?>
</body>
</html>
