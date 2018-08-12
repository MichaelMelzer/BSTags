<?php

	$ver = "";
	if (isset($_GET["printerror"]) && $_GET["printerror"] === "true") {
		$ver = "?ver=".time();
		ini_set('display_errors', 1);
		ini_set('display_startup_errors', 1);
		error_reporting(E_ALL);
	}

?>

<!DOCTYPE HTML>
<html>
<head>
	<title>Beat Saver Tags</title>
	<meta charset="utf-8">

	<link href="https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300" rel="stylesheet">
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js"></script>

	<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>

	<link rel="stylesheet" href="style.css<?php echo($ver); ?>">
	<script type="text/javascript" src="script.js<?php echo($ver); ?>"></script>
</head>
<body>
	<header>
		<section class="width">
			<div id="headerLeft" class="text specialfont">
				<h1 class="bigtext">Beat Saver Tags</h1><br />
				<b id="headerBPMDisplay" class="text">BPM: 000 - 300+</b><br />
				<div id="headerBPMSlider" class="headerSlider"></div><br />
				<br />
				<b id="headerLengthDisplay" class="text">Length: 00:00 - 10:00+</b><br />
				<div id="headerLengthSlider" class="headerSlider"></div><br />
				<br />
				<fieldset name="difficulty">
					<input id="tagToggleable_EASY" class="tagToggleable hidden" type="checkbox" name="difficulty" value="EASY" autocomplete="off" onChange="updateSearch();" /><label for="tagToggleable_EASY" class="tag bigTag tagDIFFICULTY">Easy</label>
					<input id="tagToggleable_NORMAL" class="tagToggleable hidden" type="checkbox" name="difficulty" value="NORMAL" autocomplete="off" onChange="updateSearch();" /><label for="tagToggleable_NORMAL" class="tag bigTag tagDIFFICULTY">Normal</label>
					<input id="tagToggleable_HARD" class="tagToggleable hidden" type="checkbox" name="difficulty" value="HARD" autocomplete="off" onChange="updateSearch();" /><label for="tagToggleable_HARD" class="tag bigTag tagDIFFICULTY">Hard</label>
					<input id="tagToggleable_EXPERT" class="tagToggleable hidden" type="checkbox" name="difficulty" value="EXPERT" autocomplete="off" onChange="updateSearch();" /><label for="tagToggleable_EXPERT" class="tag bigTag tagDIFFICULTY">Expert</label>
					<input id="tagToggleable_EXPERTPLUS" class="tagToggleable hidden" type="checkbox" name="difficulty" value="EXPERTPLUS" autocomplete="off" onChange="updateSearch();" /><label for="tagToggleable_EXPERTPLUS" class="tag bigTag tagDIFFICULTY">Expert+</label>
				</fieldset>
			</div>
			<div id="headerRight">
				<input id="searchTags" placeholder="Search tags..." autocomplete="off" onKeyUp="updateHeaderEnter(event);" />
				<!--<input id="searchSongs" placeholder="Search songs..." autocomplete="off" onKeyUp="updateHeaderEnter(event);" />
				<input id="searchAuthors" placeholder="Search authors..." autocomplete="off" onKeyUp="updateHeaderEnter(event);" />-->
				<fieldset id="headerTags" name="tags">
				</fieldset>
			</div>
		</section>
	</header>
	<main>
		<section id="searchResults" class="width">
		</section>
	</main>
	<section class="loading text">
		Fetching results... (there might be a database bottleneck, please wait)
	</section>
</body>
</html>