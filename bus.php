<!DOCTYPE html>
<html>
<?php
if(!empty($_GET['lat']))
{
	$file = fopen("coord.xml", 'w');
	
	$name = $_GET['name'];
	$distance = $_GET['distance'];
	$time = $_GET['time'];
	
	$text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n
	<route>\n
		<name>North Commuter</name>\n
		<lat>".$_GET['lat']."</lat>\n
		<lon>".$_GET['lon']."</lon>\n
		<stop>\n
			<name>".$name[0]."</name>\n 
			<distance>".$distance[0]."</distance>\n
			<time>".$time[0]."</time>\n
		</stop>\n
		<stop>\n
			<name>".$name[1]."</name>\n
			<distance>".$distance[1]."</distance>\n
			<time>".$time[1]."</time>\n
		</stop>\n
		<stop>\n
			<name>".$name[2]."</name>\n
			<distance>".$distance[2]."</distance>\n
			<time>".$time[2]."</time>\n
		</stop>\n
	</route>\n";
	fwrite($file, $text);
}
?>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<style type="text/css">
  html { height: 100% }
  body { height: 100%; margin: 0; padding: 0 }
  #map_canvas { height: 100% }
</style>
<script type="text/javascript"
    src="http://maps.googleapis.com/maps/api/js?sensor=false">
</script>
<script type="text/javascript">
var map, marker;

function initialize()
{
  var myLatLng = new google.maps.LatLng(50,50);
  var myOptions = {
    zoom: 4,
    center: myLatLng,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }

  var image = "bus.gif";
  
  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
  marker = new google.maps.Marker({position: myLatLng, map: map, icon: image});
  marker.setMap(map);
  moveBus();

}

// moveBus() should read Lat and Long from xml file
function moveBus()
{
	// Should the code from here until END only be called once?
	xmlhttp=new XMLHttpRequest();
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.open("GET","coord.xml",false);
	// END
	xmlhttp.send();
	xmlDoc=xmlhttp.responseXML;
	
	lat = xmlDoc.getElementsByTagName("lat")[0].childNodes[0].nodeValue;
	lon = xmlDoc.getElementsByTagName("lon")[0].childNodes[0].nodeValue;
    marker.setPosition(new google.maps.LatLng(lat,lon));
    map.panTo(new google.maps.LatLng(lat,lon));
    setTimeout( moveBus(), 1000 );
};
</script>
</head>

<body onload="initialize()">
<script type="text/javascript">
</script>
<div id="map_canvas" style="height: 500px; width: 500px;"></div>

</body>
</html>
