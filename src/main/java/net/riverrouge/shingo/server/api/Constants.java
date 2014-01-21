package net.riverrouge.shingo.server.api;

/**
 * Contains the client IDs and scopes for allowed clients consuming the API.
 */
public class Constants {
  public static final String WEB_CLIENT_ID = "replace this with your web client application ID";

  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
  public static final String API_EXPLORER_CLIENT_ID
      = com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;
}