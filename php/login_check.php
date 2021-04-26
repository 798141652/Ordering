<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>

</body>
</html>
<?php
include ('mysqli_connect.php');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $acco = $_POST["account"];
    $pw = $_POST["pass"];
}
$adsql="select * from staffInfo where userID={$acco} and userPwd='{$pw}' and userType=1";
$adres=mysqli_query($dbc,$adsql);
echo "hhhh";
var_dump($adres);

$resql="select * from staffInfo where userID={$acco} and userPwd='{$pw}' and userType=2";
$reres=mysqli_query($dbc,$resql);
var_dump($reres);

if(mysqli_num_rows($adres)==1 ){
    session_start();
    $_SESSION['userid']=$acco;
    echo "<script>window.location='admin_index.php'</script>";

}
else if(mysqli_num_rows($reres)==1){

    session_start();
    $_SESSION['userid']=$acco;

    echo "<script>window.location='staff_index.php'</script>";
}
else
{
    echo "<script>alert('用户名或密码错误，请重新输入!');window.location='index.php'
   ;</script>";

}


?>