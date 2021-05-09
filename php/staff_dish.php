<?php
session_start();
$shopid=$_SESSION['userBelong'];
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}
include ('mysqli_connect.php');

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 店铺管理</title>
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
<h1 style="text-align: center"><strong>全部菜品</strong></h1>
<form  id="query" action="staff_dish.php" method="POST">
    <div id="query">
        <label ><input  name="dishquery" type="text" placeholder="请输入菜名" class="form-control"></label>
        <input type="submit" value="查询" class="btn btn-default">
    </div>
</form>

<table  width='100%' class="table table-hover">
    <tr>
        <th>菜品ID号</th>
        <th>菜品名</th>
        <th>菜品照片</th>
        <th>菜品价格</th>
        <th>菜品种类</th>
        <th>操作</th>
        <th>操作</th>
    </tr>
    <?php
    $gjc = $_POST["dishquery"];
    $sql="select dishID,dishName,dishImage,dishPrice,dishType from dishInfo where dishName like '%{$gjc}%' and shopID = {$shopid}";

	
    $res=mysqli_query($dbc,$sql);
    foreach ($res as $row){
        echo "<tr>";
        echo "<td>{$row['dishID']}</td>";
        echo "<td>{$row['dishName']}</td>";
		
        echo "<td><img src=http://49.234.101.49/ordering/{$row['dishImage']} width='70' /></td>";
		
        echo "<td>{$row['dishPrice']}</td>";
        echo "<td>{$row['dishType']}</td>";
        echo "<td><a href='staff_dish_edit.php?id={$row['dishID']}'>修改</a></td>";
        echo "<td><a href='staff_dish_del.php?id={$row['dishID']}'>删除</a></td>";
        echo "</tr>";
    };
    ?>
</table>
</body>
</html>