<?php
session_start();
include ('mysqli_connect.php');

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 用户管理</title>
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
				<li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">菜品管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="admin_dish.php">全部菜品</a></li>
                        <li><a href="admin_dish_add.php">增加菜品</a></li>
                    </ul>
                </li>
                <li><a href="admin_repass.php">密码修改</a></li>
                <li><a href="index.php">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<h1 style="text-align: center"><strong>档口管理员用户</strong></h1>
<form  id="query" action="admin_staffuser.php" method="POST">
    <div id="query">
        <label ><input  name="userquery" type="text" placeholder="请输入用户名" class="form-control"></label>
        <input type="submit" value="查询" class="btn btn-default">
    </div>
</form>

<table  width='100%' class="table table-hover">
    <tr>
        <th>用户ID号</th>
        <th>用户名</th>
		<th>用户照片</th>
        <th>用户电话</th>
        <th>操作</th>
        <th>操作</th>
    </tr>
    <?php
    $gjc = $_POST["userquery"];
    $sql="select userID,userName,userTel,userImage from userInfo where userType = '2' and userName like '%{$gjc}%';";
	
	
    $res=mysqli_query($dbc,$sql);
	echo("错误描述: " . mysqli_error($dbc)); 
    foreach ($res as $row){
        echo "<tr>";
        echo "<td>{$row['userID']}</td>";
        echo "<td>{$row['userName']}</td>";
        echo "<td><img src=http://49.234.101.49/ordering/showUserImage.php?id={$row['userID']} width='70' /></td>";
        echo "<td>{$row['userTel']}</td>";
        echo "<td><a href='admin_user_edit.php?id={$row['userID']}'>修改</a></td>";
        echo "<td><a href='admin_user_del.php?id={$row['userID']}'>删除</a></td>";
        echo "</tr>";
    };
    ?>
</table>
</body>
</html>