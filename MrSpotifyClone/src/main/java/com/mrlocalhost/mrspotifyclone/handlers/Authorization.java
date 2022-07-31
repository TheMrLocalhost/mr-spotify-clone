package com.mrlocalhost.mrspotifyclone.handlers;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import org.apache.hc.core5.http.ParseException;

import com.mrlocalhost.mrspotifyclone.Constants;

public class Authorization extends HttpServlet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final transient LogWriter logger = new LogWriter(this.getClass().getName());
	
    public Authorization() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		Map<String,String[]> authCodeParams = request.getParameterMap();
		if (!authCodeParams.containsKey("code") || authCodeParams.containsKey("error")) {
			response.getWriter().append("Auth Code not retrieved!");
			response.getWriter().append("\n\n");
			response.getWriter().append("Error: "+request.getParameter("error"));
			return;
		}
		if (!request.getParameter("state").equals(Constants.SPOTIFY_STATE)) {
			response.getWriter().append("State not symetrical!");
			response.getWriter().append("\n\n");
			response.getWriter().append("Constants.SPOTIFY_STATE: "+Constants.SPOTIFY_STATE);
			response.getWriter().append("\n\n");
			response.getWriter().append("request.getParameter(\"code\"): "+request.getParameter("state"));
			return;
		}
		String authCode = request.getParameter("code");
		AuthorizationCodeCredentials tokens = null;
		try {
			tokens = SpotifyAuthorization.getTokensSync(authCode);
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			logger.error("Error retrieving tokens!");
			logger.errorTrace(e);

		}
		if (tokens == null) {
			response.getWriter().append("\n\n");
			response.getWriter().append("Tokens not retrieved!");
			return;
		}
		SpotifyApiHandler spotifyApiHandler = new SpotifyApiHandler(tokens);
		if ( Arrays.asList(spotifyApiHandler.getAccessToken(), spotifyApiHandler.getRefreshToken()).contains(null) ) {
			response.getWriter().append("<br>").append("<br>");
			response.getWriter().append("Tokens are null!");
		}
		
		SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setAccessToken(spotifyApiHandler.getAccessToken())
			.setRefreshToken(spotifyApiHandler.getRefreshToken())
			.build();
		User user = null;
		String userName = "";
		try {
			user = spotifyApi.getCurrentUsersProfile().build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			logger.error("Error retrieving user profile!");
			e.printStackTrace();
		}
		if (user != null) {
			userName = user.getDisplayName();
		}
		request.setAttribute("userDisplayName", userName);
		request.setAttribute("accessToken", tokens.getAccessToken());
		request.setAttribute("refreshToken", tokens.getRefreshToken());
		request.getRequestDispatcher("/Home").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
