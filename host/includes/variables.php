<?php

	//database configuration
	
	$host       = "localhost";
	$user       = "root";
	$pass       = "";
	$database   = "your_radio_app";
	
	$connect    = new mysqli($host, $user, $pass, $database) or die("Error : ".mysql_error());
	
?>