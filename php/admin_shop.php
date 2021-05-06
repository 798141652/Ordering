<?php
session_start();
var_dump($_SESSION);
include ('mysqli_connect.php');
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 档口管理</title>
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
                <li   class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">用户管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="admin_staffuser.php">档口管理员用户</a></li>
						<li><a href="admin_user.php">普通用户</a></li>
                        <li><a href="admin_user_add.php">增加用户</a></li>

                    </ul>
                </li>
                <li  class="active" class="dropdown">
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
<h1 style="text-align: center"><strong>所有档口</strong></h1>
<form  id="query" action="admin_shop.php" method="POST">
    <div id="query">
        <label ><input  name="userquery" type="text" placeholder="请输入档口名" class="form-control"></label>
        <input type="submit" value="查询" class="btn btn-default">
    </div>
</form>

<table  width='100%' class="table table-hover">
    <tr>
        <th>档口ID号</th>
        <th>档口名</th>
		<th>档口照片</th>
        <th>档口地址</th>
		<th>档口简介</th>
        <th>操作</th>
        <th>操作</th>
    </tr>
    <?php
    $gjc = $_POST["userquery"];
    $sql="select shopID,shopName,shopImage,shopLocation,shopBrief from shopInfo where shopName like '%{$gjc}%';";
	
	
    $res=mysqli_query($dbc,$sql);
	echo("错误描述: " . mysqli_error($dbc)); 
    foreach ($res as $row){
        echo "<tr>";
        echo "<td>{$row['shopID']}</td>";
        echo "<td>{$row['shopName']}</td>";
        echo "<td><img src=http://49.234.101.49/ordering/{$row['shopImage']} width='70' /></td>";
        echo "<td>{$row['shopLocation']}</td>";
		echo "<td>{$row['shopBrief']}</td>";
        echo "<td><a href='admin_shop_edit.php?id={$row['shopID']}'>修改</a></td>";
        echo "<td><a href='admin_shop_del.php?id={$row['shopID']}'>删除</a></td>";
        echo "</tr>";
    };
    ?>
</table>
</body>
</html>
