<?php
//链接数据库
include ("mysqli_connect.php");

/*
返回状态码:
300: 处理成功
301：服务器异常
*/
$status=301;

//获取用户id
$userid = $_POST['userID'];
$type = $_POST['type'];

if($type == 'userName'){
	$userName = $_POST['userName'];
	$sql = "update userInfo set userName = '{$userName}' where userID = {$userid}";
	if(mysqli_query($dbc,$sql)){
		$status=300;
	}else{
		$status=301;
	}
}else if($type == 'userPwd'){
	$userPwd = $_POST['userPwd'];
	$sql = "update userInfo set userPwd = '{$userPwd}' where userID = {$userid}";
	if(mysqli_query($dbc,$sql)){
		$status=300;
	}else{
		$status=301;
	}
}else if($type == 'userTel'){
	$userTel = $_POST['userTel'];
	$sql = "update userInfo set userTel = '{$userTel}' where userID = {$userid}";
	if(mysqli_query($dbc,$sql)){
		$status=300;
	}else{
		$status=301;
	}
}else if($type == 'userImg'){
	
	//处理上传文件
	$base_path = "upload/"; 
	$fileName=$_FILES['file']['name']; 
	$name=explode('.',$fileName);      
	$userpicads = $base_path .  'user_'.$userid. '.' .$name[1]; 
	if (move_uploaded_file ( $_FILES ['file'] ['tmp_name'], $userpicads )) {  
		$status=300; 
	} else {  
		$status=301; 
	}
}

echo mysqli_error($dbc);

$dbc->close();

//如果保存文件成功，更新数据库
/*
if($status==300)
{
	$sql = "update mh_user set userpicads='{$userpicads}' where id={$userid}";
	$pdo->exec($sql);
}*/

//输出返回结果
$ret = array('status'=> $status);
echo json_encode($ret);

?>
