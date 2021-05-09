<?php
session_start();
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
    <title>Ordering || 增加店铺</title>
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
                <li ><a href="admin_index.php">主页</a></li>
                <li  class="active" class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">用户管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="admin_staffuser.php">档口管理员用户</a></li>
						<li><a href="admin_user.php">普通用户</a></li>
                        <li><a href="admin_user_add.php">增加用户</a></li>

                    </ul>
                </li>
                <li  class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">档口管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="admin_shop.php">全部档口</a></li>
                        <li><a href="admin_shop_add.php">增加档口</a></li>
                    </ul>
                </li>
                <li><a href="admin_repass.php">密码修改</a></li>
                <li><a href="index.php">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<h1 style="text-align: center"><strong>增加用户</strong></h1>
<div style="padding: 10px 500px 10px;">
    <form  action="admin_user_add.php" method="POST" style="text-align: center" class="bs-example bs-example-form" role="form" enctype="multipart/form-data">
        <div id="login">
            <div class="input-group"><span class="input-group-addon">用户ID</span><input name="userID" type="text" placeholder="请输入用户ID" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">用户姓名</span><input name="userName" type="text" placeholder="请输入用户姓名" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">用户电话</span><input name="userTel" type="text" placeholder="请输入用户电话" class="form-control"></div><br/>
			<div class="input-group"><span class="input-group-addon">用户类型</span>
				<input name="userType" type="radio" class="form-control" value="2">档口管理员用户<br/>
				<input name="userType" type="radio" class="form-control" value="3">普通用户<br/>
			</div><br/>
            <div class="input-group"><span class="input-group-addon">用户照片</span></div><br/>
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
	$userID = $_POST["userID"];
    $userName = $_POST["userName"];
    $userTel = $_POST["userTel"];
    $userType = $_POST["userType"];
	if(!isset($userID)||$userID == ""){
		echo "<script>alert('用户ID不能为空！请重新输入！');</script>";
	}else{
				if(empty($_FILES["img"]["tmp_name"])){
					$imagePath = "image/defaultimage.jpeg";
				}
				else{
					//图片压缩
					$percent = 0.5;
					//压缩后的图片存入内部目录
					$imagePath = "image/userPhoto".$userID.".".image_type_to_extension(getimagesize($_FILES["img"]["tmp_name"])[2],false);

					(new imgcompress($_FILES["img"]["tmp_name"],$percent))->compressImg($imagePath);
				}
				
				if($userType == "2"){
					$sql="select * from staffInfo where userID = '{$userID}'";
					if(mysqli_query($dbc,$sql)->num_rows == 0){
						$sqla="insert into staffInfo(userID,userName,userImage,userTel,userType) VALUES ({$userID} ,'{$userName}','{$imagePath}','{$userTel}','2')";
						$resa=mysqli_query($dbc,$sqla);
						if($resa==1)
						{
							echo "<script>alert('添加成功！')</script>";
							echo "<script>window.location.href='admin_user.php'</script>";
						}
						else
						{
							echo("错误描述: " . mysqli_error($dbc)); 
							echo "<script>alert('添加失败！请重新输入！');</script>";
						}
					}else{
						//var_dump($imageProperties);
							echo "<script>alert('添加失败！该ID已经存在！请重新输入！');</script>";
					}
				}else{
					$sql="select * from userInfo where userID = '{$userID}'";
					if(mysqli_query($dbc,$sql)->num_rows == 0){
						$sqla="insert into userInfo(userID,userName,userImage,userTel) VALUES ({$userID} ,'{$userName}','{$imagePath}','{$userTel}')";
						$resa=mysqli_query($dbc,$sqla);
						if($resa==1)
						{
							echo "<script>alert('添加成功！')</script>";
							echo "<script>window.location.href='admin_user.php'</script>";

						}
						else
						{
							echo("错误描述: " . mysqli_error($dbc)); 
							echo "<script>alert('添加失败！请重新输入！');</script>";

						}
					}else{
						//var_dump($imageProperties);
							echo "<script>alert('添加失败！该ID已经存在！请重新输入！');</script>";
					}
				}
				mysqli_close($dbc);			
	
	}
}

?>

</body>
</html>
