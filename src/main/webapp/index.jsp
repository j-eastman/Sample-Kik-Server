<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Global Site Tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-107126519-1"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments)};
  gtag('js', new Date());

  gtag('config', 'UA-107126519-1');
</script>

<title>Chatterer</title>
<link rel="stylesheet" type="text/css"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="newpage.css">
<link rel="stylesheet" type="text/css"
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
<link href="https://fonts.googleapis.com/css?family=Armata"
	rel="stylesheet">
<script async
	src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<script>
  (adsbygoogle = window.adsbygoogle || []).push({
    google_ad_client: "ca-pub-1199096101350382",
    enable_page_level_ads: true
  });
</script>
</head>
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript">
</script>

<body>

	<div class="container">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
						aria-expanded="false">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="/" target="_self">Chatterer Bot</a>
				</div>

				<div class="collapse navbar-collapse"
					id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav nav navbar-left">
						<li><a href="/about.html" target="_self">About</a></li>
						<li><a href="/chat" target="_self">Chatroom</a></li>
						<li><a href="/PrivacyPolicy.html"
							target="_self">Privacy Policy</a></li>
						<li><a href="/TermsOfService.html" target ="_self">Terms of Service</a></li>
						<li><a href="/contact.html" target = "_self">Contact Us</a></li>	
					</ul>
				</div>
				<!-- /.navbar-collapse -->
			</div>
			<!-- /.container-fluid -->
		</nav>
		<div class="jumbotron">
			<h1>
				Chatterer Bot <i class="fa fa-commenting" aria-hidden="true"></i>
			</h1>
		</div>

		<div class="row">
			<div class="col col-sm-12">
				<script async
					src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
				<!-- Chatterer -->
				<ins class="adsbygoogle" style="display: block"
					data-ad-client="ca-pub-1199096101350382" data-ad-slot="4278125476"
					data-ad-format="auto"></ins>
				<script>
				(adsbygoogle = window.adsbygoogle || []).push({});
			</script>
				<form>
					<div class="form-group">
						<label for="textbox"><h4>Talk Below</h4></label>
						<textarea readonly id="myBox" class="form-control" rows="10"
							placeholder="Chatterer responses will appear here"></textarea>
					</div>

					<div id="submit" class="form-group">
						<input class="responseBox" autocomplete="off" id="responseBox" type="text"
							name="userinput" placeholder="Enter text here...">
					</div>
					<div class="form-group text-center">
						<button class="link" type="button" onclick="submitMessage()" id="button">Enter</button>
					</div>
				</form>
				<form>
					<button type="button" onclick="toggleCensor()" class="censor" id="toggle_button" name="toggle">Toggle
						Censor</button>
					<input type="hidden" name="submit_id" value="null" />
				</form>
			</div>
		</div>
	</div>
</body>
<script>
	function toggleCensor(){
		$.ajax({
      		type: "POST",
      		url: "/censor",
     		data: getCookie("username"),
			success: function(response) {
				document.getElementById("toggle_button").innerHTML=response;
      		}
    	});
		return false;
	}
	function submitMessage(){
		if (document.getElementById("responseBox").value != ""){
			document.getElementById("myBox").scrollBot = document.getElementById("myBox").scrollHeight;
			var thing1 =document.getElementById("myBox").value;
			$('.form-control').val(thing1+"You: "+ document.getElementById("responseBox").value);
    		$.ajax({
      			type: "POST",
      			url: "/web",
     			data: getCookie("username") + "<brk>" +document.getElementById("responseBox").value,
				success: function(response) {
					var thing =document.getElementById("myBox").value;
        			$('.form-control').val(thing+"\nChatterer: "+ response + "\n");
					document.getElementById('responseBox').value='';
					document.getElementById("myBox").scrollTop = document.getElementById("myBox").scrollHeight;
      			}
    		});
	
    		return false;
		}
	}
	</script>
	<script>
    	document.getElementById('responseBox').addEventListener('keypress', function(event) {
        	if (event.keyCode == 13) {
            	event.preventDefault();
				submitMessage();
        	}
    	});
</script>
<script>
	var userMessage = "";
	document.getElementsByName("submit_id")[0].setAttribute("value",getCookie("username"));
	var textarea = document.getElementById('myBox');
	$(document).ready(function(){
		var cook = getCookie("username");
		if (cook == ""){
			var temp = Math.round((Math.random(999999)*100000)).toString();
			setCookie("username",temp,100);
		}
	
  	$(".responseBox").css({
    	'width': ($(".form-control").width() + 'px')
  	});
	});
	function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
	function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
	</script>
</html>