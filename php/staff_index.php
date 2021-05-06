<?php
session_start();

var_dump($_SESSION);
$userid=$_SESSION['userid'];
if(!isset($userid)){
	 echo "<script>alert('身份信息过期！请重新登录！');</script>";
	 echo "<script>window.location.href='index.php'</script>";
}
include ('mysqli_connect.php');

	//查询所属档口ID
	$sql1="select userBelong from staffInfo where userID = {$userid};";
	$res1=mysqli_query($dbc,$sql1);
	$result1=mysqli_fetch_array($res1);
	$_SESSION['userBelong']=$result1['userBelong'];
	
	
	$shopid=$_SESSION['userBelong'];
	
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ordering || 主页</title>
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
                <li class="active"><a href="staff_index.php">主页</a></li>
                <li><a href="staff_shop.php">档口管理</a></li>
                <li class="dropdown">
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


<h3 style="text-align: center">档口管理员<?php echo $userid;  ?>，您好</h3><br/><br/><br/>
<h4 style="text-align: center"><?php


	//查询所属档口名称
	$sql2="select shopName from shopInfo where shopID = {$shopid};";
	$res2=mysqli_query($dbc,$sql2);
	$result2=mysqli_fetch_array($res2);
	$shopName=$result2['shopName'];
	echo "您工作的档口为{$result1['userBelong']}号档口{$shopName}";;
    ?>
</h4>

<h4 style="text-align: center">
    <?php
    $sqla="select count(*) b from dishInfo where shopID = {$result1['userBelong']};";

    $resa=mysqli_query($dbc,$sqla);
    $resulta=mysqli_fetch_array($resa);
    echo "共有菜品{$resulta['b']}个。";

    ?>
</h4>
<h4 style="text-align: center">
    <?php
    $sql3="select count(distinct cartTime) b from userCart where cartShopID = {$result1['userBelong']} and cartStatus != '0';";
    $res3=mysqli_query($dbc,$sql3);
    $result3=mysqli_fetch_array($res3);
    echo "共有订单{$result3['b']}个。";
	
	$sql4="select count(distinct cartTime) b from userCart where cartShopID = {$result1['userBelong']} and cartStatus = '1';";

    $res4=mysqli_query($dbc,$sql4);
    $result4=mysqli_fetch_array($res4);
    echo "共有未处理订单{$result4['b']}个。";

    ?>
</h4>

</body>
</html>