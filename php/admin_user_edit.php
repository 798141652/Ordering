<?php
session_start();
require_once 'imagecompress.php';
include ('mysqli_connect.php');


$userid=$_GET['id'];

$sqls="select userName,userImage,userTel from userInfo where userID={$userid}";
$ress=mysqli_query($dbc,$sqls);
$results=mysqli_fetch_array($ress);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 修改档口</title>
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
                        <li><a href="admin_user.php">全部档口</a></li>
                        <li><a href="admin_user_add.php">增加档口</a></li>
                    </ul>
                </li>
                <li><a href="admin_repass.php">密码修改</a></li>
                <li><a href="index.php">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<h1 style="text-align: center"><strong>修改用户<?php echo $userid?></strong></h1>
<div style="padding: 10px 500px 10px;">
    <form  action="admin_user_edit.php?id=<?php echo $userid; ?>"" method="POST" style="text-align: center" class="bs-example bs-example-form" role="form" enctype="multipart/form-data">
        <div id="login">
            <div class="input-group"><span class="input-group-addon">用户名称</span><input value="<?php echo $results['userName']; ?>"name="userName" type="text" placeholder="请修改用户名称" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">用户电话</span><input value="<?php echo $results['userTel']; ?>"name="userTel" type="text" placeholder="请修改用户电话" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">用户照片</span><td><img src=http://49.234.101.49/ordering/<?php echo $results['userImage']; ?> width='300' /></td>
			<input name="img" type="file" class="form-control"></div><br/>
			
            <label><input type="submit" value="修改" class="btn btn-default"></label>
            <label><input type="reset" value="重置" class="btn btn-default"></label>
        </div>
    </form>
</div>
<?php

if ($_SERVER["REQUEST_METHOD"] == "POST")
{

	$userID = $_GET["id"];
    $userName = $_POST["userName"];
    $userTel = $_POST["userTel"];
	
	//图片压缩
	$percent = 0.5;
	//压缩后的图片存入内部目录
	$imagePath = "image/userPhoto".$userID.".".image_type_to_extension(getimagesize($_FILES["img"]["tmp_name"])[2],false);
	echo $imagePath;

	(new imgcompress($_FILES["img"]["tmp_name"],$percent))->compressImg($imagePath);
 
	$sqla="update userInfo set userName='{$userName}',userImage='{$imagePath}',userTel='{$userTel}' where userID=$userid;";
    $resa=mysqli_query($dbc,$sqla);

if($resa==1)
    {

        echo "<script>alert('修改成功！')</script>";
        echo "<script>window.location.href='admin_user.php'</script>";

    }
    else
    {
        echo "<script>alert('修改失败！请重新输入！');</script>";

    }
mysqli_close($dbc);

}


?>
</body>
</html>
