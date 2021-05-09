<?php
session_start();
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}
$shopid=$_SESSION['userBelong'];
include ('mysqli_connect.php');


?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 查看评论</title>
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
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">菜品管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="staff_dish.php">全部菜品</a></li>
                        <li><a href="staff_dish_add">增加菜品</a></li>
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
				<li class="active"><a href="show_comment.php">查看评论</a></li>
                <li><a href="staff_repass.php">密码修改</a></li>
                <li><a href="index.php">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<?php 	
    $sql1="select commentID,orderID,dishID,userID,commentType,comment,shopID from userComment where shopID = ".$shopid." order by commentID desc;";
	//var_dump($sql1);
	$res1=mysqli_query($dbc,$sql1);
	//echo mysqli_error($dbc);
	
	foreach($res1 as $row1){
	$res2 = mysqli_query($dbc,"select dishName from dishInfo where dishID = {$row1['dishID']}");
	$result = mysqli_fetch_array($res2);
	echo "<table border='2' width='100%' class='table table-hover' style='table-layout:fixed'>
    <tr>
		<th>评论ID</th>
        <th>订单号</th>
		<th>评论用户ID</th>
		<th>对应菜品ID</th>
		<th>对应菜品名称</th>
        <th>评论类型</th>
        <th>评论内容</th>
    </tr>";
	echo "<tr>
		<td>".$row1['commentID']."</td>
		<td>".$row1['orderID']."</td>
		<td>".$row1['userID']."</td>
		<td>".$row1['dishID']."</td>
		<td>".$result['dishName']."</td>
		<td>".$row1['commentType']."</td>
		<td>".$row1['comment']."</td>
	</tr>";
	echo "</table></br>";
}	
    ?>
</body>
</html>