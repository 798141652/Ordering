<?php
session_start();
var_dump($_SESSION);
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
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">菜品管理<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="staff_dish.php">全部菜品</a></li>
                        <li><a href="staff_dish_add.php">增加菜品</a></li>
                    </ul>
                </li>
				<li class="active" class="dropdown">
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
<?php 	
    $sql1="select distinct cartTime,cartUserID,cartStatus from userCart where cartShopID = {$shopid} and cartStatus !='1' and cartStatus!='0' order by cartTime desc";
	$res1=mysqli_query($dbc,$sql1);
	echo mysqli_error($dbc);
	
	foreach($res1 as $row1){
		echo "<table border='4' width='100%' class='table table-hover'>
		<tr>
			<th>菜品ID</th>
			<th>菜品名称</th>
			<th>菜品价格</th>
			<th>菜品数量</th>
		</tr>";
		
		$sql="select cartShopID,orderID,cartDishID,cartDishName,cartDishPrice,cartDishNum from userCart where cartTime ='{$row1['cartTime']}' and cartUserID = {$row1['cartUserID']} and cartShopID = {$shopid};";
		$res=mysqli_query($dbc,$sql);
		foreach ($res as $row){
			echo "<tr>";
			echo "<td>{$row['cartDishID']}</td>";
			echo "<td>{$row['cartDishName']}</td>";	
			echo "<td>{$row['cartDishPrice']}元</td>";
			echo "<td>{$row['cartDishNum']}份</td>";
			echo "</tr>";
			$totalPrice=0;
			$totalPrice += $row['cartDishPrice'];
		};
		echo "<tr>
			<td></td>
			<td></td>
			<td></td>
			<td>订单价格:".$totalPrice."元</td>
		</tr>";
		echo "&nbsp&nbsp订单号:".$row['orderID']."</br>&nbsp&nbsp订单时间:".$row1['cartTime']."</br>&nbsp&nbsp用户ID:".$row1['cartUserID']."";
		echo "</table></br>";
}	
    ?>
</body>
</html>