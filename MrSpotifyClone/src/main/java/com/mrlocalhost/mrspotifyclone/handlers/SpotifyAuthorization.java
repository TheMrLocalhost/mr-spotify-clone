package com.mrlocalhost.mrspotifyclone.handlers;

import java.io.IOException;
import java.net.URI;

import org.apache.hc.core5.http.ParseException;

import com.mrlocalhost.mrspotifyclone.Constants;

import se.michaelthelin.spotify.SpotifyApi;

import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

public class SpotifyAuthorization {
	
	private SpotifyAuthorization() {}
	
	public static URI authorizationCodeUriSync() {
		return new SpotifyApi.Builder()
				.setClientId(Constants.CLIENT_ID)
				.setClientSecret(Constants.CLIENT_SECRET)
				.setRedirectUri(Constants.REDIRECT_URI)
				.build()
				.authorizationCodeUri()
				.state(Constants.SPOTIFY_STATE)
				.scope(Constants.SPOTIFY_SCOPE)
				.build()
				.execute();
	}
	
	public static AuthorizationCodeCredentials getTokensSync(String code) throws IOException, SpotifyWebApiException, ParseException {
		return new SpotifyApi.Builder()
				.setClientId(Constants.CLIENT_ID)
				.setClientSecret(Constants.CLIENT_SECRET)
				.setRedirectUri(Constants.REDIRECT_URI)
				.build()
				.authorizationCode(code)
				.build()
				.execute();
	}
}
