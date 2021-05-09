<?php
session_start();
include ('mysqli_connect.php');
require_once 'imagecompress.php';
$shopid=$_GET['id'];
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}
$sqls="select shopName,shopImage,shopLocation,shopBrief from shopInfo where shopID={$shopid}";
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
                <li  class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">用户管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="admin_user.php">所有用户</a></li>
                        <li><a href="admin_user_add.php">增加用户</a></li>

                    </ul>
                </li>
                <li class="active" class="dropdown">
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
<h1 style="text-align: center"><strong>修改档口</strong></h1>
<div style="padding: 10px 500px 10px;">
    <form  action="admin_shop_edit.php?id=<?php echo $shopid; ?>"" method="POST" style="text-align: center" class="bs-example bs-example-form" role="form" enctype="multipart/form-data">
        <div id="login">
            <div class="input-group"><span class="input-group-addon">档口名称</span><input value="<?php echo $results['shopName']; ?>" name="shopName" type="text" placeholder="请修改档口名称" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">档口地址</span><input value="<?php echo $results['shopLocation']; ?>"name="location" type="text" placeholder="请修改档口地址" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">档口简介</span><input value="<?php echo $results['shopBrief']; ?>"name="brief" type="text" placeholder="请修改档口简介" class="form-control"></div><br/>
            <div class="input-group"><span class="input-group-addon">档口照片</span><td><img src=http://49.234.101.49/ordering/<?php echo $results['shopImage']; ?> width='300' /></td>
			<input name="img" type="file" class="form-control"></div><br/>
			
            <label><input type="submit" value="修改" class="btn btn-default"></label>
            <label><input type="reset" value="重置" class="btn btn-default"></label>
        </div>
    </form>
</div>
<?php

if ($_SERVER["REQUEST_METHOD"] == "POST")
{

	$shopID = $_GET["id"];
    $shopName = $_POST["shopName"];
    $location = $_POST["location"];
    $brief = $_POST["brief"];

	if(empty($_FILES["img"]["tmp_name"])){
		$imagePath = "image/defaultimage.jpeg";
	}
	else{
		//图片压缩
		$percent = 0.5;
		//压缩后的图片存入内部目录
		$imagePath = "image/shopPhoto".$shopID.".".image_type_to_extension(getimagesize($_FILES["img"]["tmp_name"])[2],false);
		if(file_exists($imagePath)){
		unlink($imagePath);
	}
		(new imgcompress($_FILES["img"]["tmp_name"],$percent))->compressImg($imagePath);
	}
 

	$sqla="update shopInfo set shopName='{$shopName}',shopImage='{$imagePath}',shopLocation='{$location}',shopBrief='{$brief}' where shopID=$shopID;";
    $resa=mysqli_query($dbc,$sqla);

	if($resa==1)
    {

        echo "<script>alert('修改成功！')</script>";
        echo "<script>window.location.href='admin_shop.php'</script>";

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
