<%@ page import="com.mrlocalhost.mrspotifyclone.handlers.SpotifyAuthorization"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="Pragma" content="no-cache">
		<meta charset="UTF-8">
		<title>MrSpotify Home</title>
		<link rel="icon" href="/MrSpotifyClone/favicon.ico" type="image/x-icon">
		<style>
			@import url(css/style.css);
		</style>
		<script src="https://sdk.scdn.co/spotify-player.js"></script>
		<%
			String userDisplayName = (String)request.getAttribute("userDisplayName");
			String accessToken = (String)request.getAttribute("accessToken");
			String refreshToken = (String)request.getAttribute("refreshToken");
		%>
	</head>
	<body class="background">
		<div class="horizontal-center">
			<div class="vertical-center">
				<script type="text/javascript">
					history.pushState(null,"","/MrSpotifyClone/");
				</script>
				<input id="userDisplayName" readonly type="hidden" value='<%=userDisplayName%>'/>
				<input id="accessToken" readonly type="hidden" value='<%=accessToken%>'/>
				<input id="refreshToken" readonly type="hidden" value='<%=refreshToken%>'/>
				<div class="user-information" id="userInformation">
					<div>
						<div>
							<span id=usernameTag>Logged in as:</span>
							<span id=usernameValue style="margin-left: 0.5em;"></span>
						</div>
					</div>
					<div>
						<div>
							<span id="deviceTag">Listening on device:</span>
							<span id="deviceValue" style="margin-left: 0.5em;"></span>
						</div>
					</div>
				</div>
				<div id="mediaInformation" class="media-information">
					<img src="" class="now-playing__cover album-cover" alt="" id="coverArt"/>
					<div class="now-playing__name" id="trackName"></div>
					<div class="now-playing__artist" style="margin-left: 1em;font-size: 0.8em;" id="trackArtist"></div>
					
					<div class="media-button-group slide-container">
						<span>
							<button class="media-buttons" id="previousTrack">&#x23EE;</button>
							<button class="media-buttons" id="togglePlay">&#x23F3;</button>
							<button class="media-buttons" id="nextTrack">&#x23ED;</button>
						</span>
						<span class="volumeGroup">
							&#128266;
						</span>
						<span class="volumeGroup">
							<input type="range" min="0" max="100" value="10" class="volume-slider" id="volumeSlider">
						</span>
					</div>
				</div>
				<div class="login-prompt" id="loginPrompt">
					<div>Welcome to MrSpotifyClone</div>
					<div>Please log in</div>
					<div style="margin-top: 1.0em;">
						<a class="spotify-login-button" href="<%=SpotifyAuthorization.authorizationCodeUriSync()%>">
							Login With Spotify
						</a>
					</div>
				</div>
				<script type="text/javascript" src="/MrSpotifyClone/js/spotifyApiRequests.js"></script>
				<script type="text/javascript">
					function togglePlaybackState(state) {
						var togglePlayElement = document.getElementById("togglePlay");
						if (state.paused) {
							togglePlayElement.textContent = "\u23F5"; // play icon
						} else {
							togglePlayElement.textContent = "\u23F8"; // pause icon
					}	}
				
					var loggedIn = false;
					var userDisplayNameElement = document.getElementById("userDisplayName");
					var accessTokenElement = document.getElementById("accessToken");
					var refreshTokenElement = document.getElementById("refreshToken");
					
					var userDisplayName = userDisplayNameElement.value;
					localStorage.setItem("accessToken", accessTokenElement.value);
					localStorage.setItem("refreshToken", refreshTokenElement.value);
					
					userDisplayNameElement.remove();
					accessTokenElement.remove();
					refreshTokenElement.remove();
					
					var userInformationElement = document.getElementById("userInformation");
					var loginPromptElement = document.getElementById("loginPrompt");
					var usernameValueElement = document.getElementById("usernameValue");
					var mediaInformationElement = document.getElementById("mediaInformation");
		
					if (userDisplayName == "null") {
						userInformationElement.remove();
						loginPromptElement.style = "display: block; margin-bottom: 1.0em;";
					} else {
						loggedIn = true;
						loginPromptElement.remove();
						userInformationElement.style.display = 'block';
						mediaInformationElement.style.display = 'block';
						usernameValueElement.innerHTML = userDisplayName;
					}
					const track = {name:"",album:{images:[{url:""}]},artists:[{name:""}]};
					if (loggedIn) {
						window.onSpotifyWebPlaybackSDKReady = () => {
							const player = new Spotify.Player({
								name: 'MrSpotifyClone Player',
								getOAuthToken: cb => { cb(localStorage.getItem("accessToken")); },
								volume: 0.1
							});
		
							player.addListener('ready', ({ device_id }) => {
								console.log('Ready with Device ID', device_id);
								//get id of web client for transfer
								var devices = getRequest("https://api.spotify.com/v1/me/player/devices");
								var webPlayerID = "";
								if (typeof(devices) === "object") {
									for (const device of devices["devices"]) {
										if (device.name === "MrSpotifyClone Player") {
											webPlayerID = device.id;
								}	}	}
								//transfer playback to web client
								if (webPlayerID != "") {
									var putData = { "device_ids": [webPlayerID] };
									putRequest("https://api.spotify.com/v1/me/player", putData);
								}
							});
							player.addListener('not_ready', ({ device_id }) => {
								console.log('Device ID has gone offline', device_id);
							});
							player.addListener('initialization_error', ({ message }) => { 
							    console.error(message);
							});
							player.addListener('authentication_error', ({ message }) => {
							    console.error(message);
							});
							player.addListener('account_error', ({ message }) => {
							    console.error(message);
							});
							document.getElementById('previousTrack').onclick = function() {
								player.previousTrack();
							};
							document.getElementById('togglePlay').onclick = function() {
								player.togglePlay();
							};
							document.getElementById('nextTrack').onclick = function() {
								player.nextTrack();
							};
							document.getElementById("volumeSlider").oninput = function() {
								player.setVolume(this.value/100);
							};
							
							player.addListener('player_state_changed', ( state => {
								if (!state) {
							        return;
							    }
								document.getElementById("coverArt").src = state.track_window.current_track.album.images[0].url;
								document.getElementById("trackName").innerHTML = state.track_window.current_track.name;
								document.getElementById("trackArtist").innerHTML = "by "+state.track_window.current_track.artists[0].name;
								player.getCurrentState().then( state => { 
							    	togglePlaybackState(state);
								});
								var devices = getRequest("https://api.spotify.com/v1/me/player/devices");
								var currentDeviceName = "";
								if (typeof(devices) === "object") {
									for (const device of devices["devices"]) {
										if (device.is_active) {
											currentDeviceName = device.name;
								}	}	}
								document.getElementById("deviceValue").innerHTML = currentDeviceName;
							}));
							player.connect();
							
						};
					} else {
						window.onSpotifyWebPlaybackSDKReady = () => {};
					}
				</script>
			</div>
		</div>
	</body>
</html>