<?php

	class DB_CONNECT {

		function __construct() {
			$this->connect();
		}

		function __destruct() {
			$this->close();
		}

		function connect() {
			require_once __DIR__ . '/db_config.php';
			$link = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_NAME) or die("Error: " . mysqli_error($link));

			return $link;
		}

		function close() {
			mysqli_close();
		}
	}
?>