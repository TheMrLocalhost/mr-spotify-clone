package com.mrlocalhost.mrspotifyclone.handlers;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

import com.mrlocalhost.mrspotifyclone.Constants;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

public class SpotifyApiHandler {

	private String accessToken = null;
	private String refreshToken = null;
	private int expiresIn = -1;
	
	private void setAccessToken(String newAccessToken) { this.accessToken = newAccessToken; }
	private void setRefreshToken(String newRefreshToken) { this.refreshToken = newRefreshToken; }
	private void setExpiresIn(int newExpiresIn) { this.expiresIn = newExpiresIn; }
	
	public SpotifyApiHandler (AuthorizationCodeCredentials credentials) {
		this.accessToken = credentials.getAccessToken();
		this.refreshToken = credentials.getRefreshToken();
		this.expiresIn = credentials.getExpiresIn();
	}
	
	public void updateCredentials() throws IOException, SpotifyWebApiException, ParseException {
		AuthorizationCodeCredentials newCredentials =
				new SpotifyApi.Builder()
			    .setClientId(Constants.CLIENT_ID)
			    .setClientSecret(Constants.CLIENT_SECRET)
			    .setRefreshToken(this.refreshToken)
			    .build()
				.authorizationCodeRefresh()
			    .build()
			    .execute();
		setAccessToken(newCredentials.getAccessToken());
		setRefreshToken(newCredentials.getRefreshToken());
		setExpiresIn(newCredentials.getExpiresIn());
	}
	
	public String getAccessToken() { return this.accessToken; }
	public String getRefreshToken() { return this.refreshToken; }
	public int getExpiresIn() { return this.expiresIn; }
	
}
