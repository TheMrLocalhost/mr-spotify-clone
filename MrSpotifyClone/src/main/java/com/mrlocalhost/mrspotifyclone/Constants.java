package com.mrlocalhost.mrspotifyclone;

import java.net.URI;
import java.util.logging.Level;

import se.michaelthelin.spotify.SpotifyHttpManager;

public record Constants() {
	private static final String REDIRECT_URI_STRING = "http://localhost:8080/MrSpotifyClone/Authorization";
	public static final String CLIENT_ID = System.getProperty("client.id");
	public static final String CLIENT_SECRET = System.getProperty("client.secret");
	public static final String SPOTIFY_SCOPE =
			  "streaming "
			+ "user-read-email "
			+ "user-read-private "
			+ "user-library-read "
			+ "user-library-modify "
			+ "user-read-playback-state "
			+ "user-modify-playback-state";
	public static final String SPOTIFY_STATE = System.getProperty("client.state");
	public static final URI REDIRECT_URI = SpotifyHttpManager.makeUri(REDIRECT_URI_STRING);
	public static final Level LOG_LEVEL = Level.INFO;
}
