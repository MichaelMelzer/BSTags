<?php 

	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);

	if (isset($_GET["mode"])) {
		include("../pw.php");
		$mysql = mysqli_connect($login["host"], $login["user"], $login["pass"], $login["db"]);
		switch ($_GET["mode"]) {
			case "header":
				header('Content-Type: application/json');
				$searchingTags = isset($_GET["searchtags"]) && trim($_GET["searchtags"]) !== "";
				$search = $searchingTags ? " AND tags.name LIKE ? " : "";
				$prep = $mysql->prepare("SELECT *,COUNT(songs_tags.tag) AS count FROM songs_tags, tags WHERE songs_tags.tag=tags.id $search GROUP BY tag ORDER BY count DESC LIMIT 0, 100");
				$x = "%".$_GET["searchtags"]."%";
				if ($searchingTags) $prep->bind_param("s", $x);
				$prep->execute();
				$res = $prep->get_result();
				$prep->close();
				echo(json_encode($res->fetch_all(MYSQLI_ASSOC)));
			break;
			case "test":
				header('Content-Type: application/json');
				echo(json_encode($_GET));
			break;
			case "search":
				header('Content-Type: application/json');

				$query = "";
				$i = "";
				$arr = array();

				$query .= " AND songs.bpm >= ? ";
				$i .= "i";
				array_push($arr, $_GET["bpm"][0]);
				$query .= " AND songs.bpm <= ? ";
				$i .= "i";
				array_push($arr, $_GET["bpm"][1] >= 300 ? 999999 : $_GET["bpm"][1]);

				$query .= " AND songs.time_s >= ? ";
				$i .= "d";
				array_push($arr, $_GET["length"][0]);
				$query .= " AND songs.time_s <= ? ";
				$i .= "d";
				array_push($arr, $_GET["length"][1] >= 600 ? 99999 : $_GET["length"][1]);

				if (isset($_GET["difficulty"])) {
					$query .= " AND (";
					foreach ($_GET["difficulty"] as $key => $tag) {
						$x = strtolower((string)($tag["value"]));
						if ($x !== "easy" && $x !== "normal" && $x !== "hard" && $x !== "expert" && $x !== "expertplus") {
							exit("INVALID DIFFICULTY");
						}
						$x = "songs.".$x;
						$query .= "$x = 1 AND ";
					}
					$query = substr($query, 0, -4);
					$query .= ")";
				}

				if (isset($_GET["tags"])) {
					$query .= " AND (";
					foreach ($_GET["tags"] as $key => $tag) {
						$query .= "tag = ? OR ";
						$i .= "i";
						array_push($arr, $tag["value"]);
					}
					$query = substr($query, 0, -3);
					$query .= ")";
				}

				$sql = "SELECT * FROM songs, songs_tags WHERE songs.songkey=songs_tags.song $query GROUP BY songkey ORDER BY songs.playedCount DESC LIMIT 100";
				$prep = $mysql->prepare($sql);

				$prep->bind_param($i, ...$arr);

				$prep->execute();
				$res = $prep->get_result();
				$prep->close();
				echo(json_encode($res->fetch_all(MYSQLI_ASSOC)));
			break;
		}
	}

?>
