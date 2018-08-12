$(function() {
	$("#headerBPMSlider").slider({
		range: true,
		min: 0,
 		max: 300,
  		values: [0, 300],
  		slide: function(event, ui) {
  			var min = formatNum(ui.values[0], 3);
  			var max = formatNum(ui.values[1], 3);
  			if (max == 300) max = "300+";
  			//max = max == 200 ? "200+" : max;
    		$("#headerBPMDisplay").text("BPM: "+min+" - "+max);
  		},
  		change: updateSearch
	});
	$("#headerLengthSlider").slider({
		range: true,
		min: 0,
 		max: 600,
  		values: [0, 600],
  		slide: function(event, ui) {
  			var min = formatNum(Math.floor(ui.values[0]/60.0), 2) + ":" + formatNum((ui.values[0]%60), 2);
  			var max = formatNum(Math.floor(ui.values[1]/60.0), 2) + ":" + formatNum((ui.values[1]%60), 2);
  			if (max == "10:00") max = "10:00+";
    		$("#headerLengthDisplay").text("Length: "+min+" - "+max);
  		},
  		change: updateSearch
	});
});

$(document).ready(function() {
	updateHeader();
	updateSearch();

	setInterval(onScroll, 100);
	onScroll();
});

function onScroll() {
	$("img").each(function() {
		if (isScrolledIntoView(this) && !$(this).attr("src")) {
			$(this).attr("src", $(this).attr("_src"));
		}
	});
}
function isScrolledIntoView(elem) {
    var docViewTop = $(window).scrollTop();
    var docViewBottom = docViewTop + $(window).height();

    var elemTop = $(elem).offset().top;
    var elemBottom = elemTop + $(elem).height();

    return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
}

function updateHeader() {
	startLoad();
	$.get("db.php?mode=header", { "searchtags": $("#searchTags").val() }, function(data) {
		$("#headerTags").html("");
		$.each(data, function(index, value) {
			$("#headerTags").append(buildTag(value.id, value.name, value.source));
		});
	}).always(function(data) {
		console.log(data);
		endLoad();
	});
}
function updateHeaderEnter(e) {
	if (e.keyCode === 13) {
		updateHeader();
	}
}

function updateSearch() {
	var begin = new Date().getTime();
	startLoad();
	$.get("db.php?mode=search", { 
		"bpm": $("#headerBPMSlider").slider("option", "values"),
		"length": $("#headerLengthSlider").slider("option", "values"),
		"difficulty": $("fieldset[name=difficulty]").serializeArray(),
		"tags": $("fieldset[name=tags]").serializeArray()
	}, function(data) {
		var end = new Date().getTime();
		var delta = end-begin;
		$("#searchResults").html('<span class="song smallSong"><span class="text"><h3 class="smalltext">'+ (data.length >= 100 ? "100 results (max)" : data.length+" results") +' ('+ delta +'ms) - sorted by Playcount</h3></span></span>');

		$(data).each(function(index, song) {
			if (index > 100) return;

			var key = song.songkey;
			var id = key.split("-")[0];

			var min = Math.floor(song.time_s/60);
			var sec = Math.floor(song.time_s)%60;

			$("#searchResults").append(
				'<a href="'+ song.linkUrl +'" target="_blank"> \
					<span class="song"> \
						<img _src="'+ song.coverUrl +'" /> \
						<span class="text"> \
							<h2>'+ song.name +'</h2> \
							<h3 class="smalltext">Length approx. '+ formatNum(min, 2) +':'+ formatNum(sec, 2) +' - BPM: '+ formatNum(song.bpm, 3) +' - Plays: \
							'+ song.playedCount +' - Downloads: '+ song.downloadCount +' - Ups: '+ song.upVotes +' - Downs: '+ song.downVotes +'</h3><br /> \
							<span class="smalltext">'+ song.subName +'</span> \
							<span class="smalltext">'+ song.description +'</span> \
						</span> \
					</span> \
				</a>');
			$(window).scrollTop();
		});
	}).always( function(data) {
		console.log(data);
		endLoad();
	});
}

function buildTag(id, name, source="UNKNOWN") {
	return '<input id="tagToggleable_'+ id +'" class="tagToggleable hidden" type="checkbox" name="tags" value="'+ id +'" autocomplete="off" onChange="updateSearch();" /> \
			<label for="tagToggleable_'+ id +'" class="tag tag'+ source +'">'+ name +'</label>';
}

function formatNum(num, x) {
	for (var i = 0; i < x && num.toString().length < x; i++) {
		num = "0"+num;
	}
	return num;
}

function startLoad() {
	$(".loading").css("display", "block");
}
function endLoad() {
	$(".loading").css("display", "none");
}