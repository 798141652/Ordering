<?php
DEFINE ('DB_USER','root');
DEFINE ('DB_PASSWORD','19981126');
DEFINE ('DB_HOST','localhost');
DEFINE ('DB_NAME','ordering');

$dbc=@mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) OR die('Could not to connect to Mysql:'.mysqli_connect_error());

mysqli_set_charset($dbc, 'utf8');
?>