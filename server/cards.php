<?php
	$version = $_REQUEST['version'];
	
	$fileName = 'cards_' . $version . '.json';
	error_log('cards.php  version: ' . $version . ' filename: ' . $fileName);

	if (file_exists($fileName)) {
		header("Location: " . $fileName);
	} else {
		//error_log('cards.php - Bad URL: ' . $fileName);
		http_response_code(200);
	} 
?>
