package dinnertime.model;

import java.time.ZonedDateTime;

/**
 *
 * @author aaron.mitchell
 */
public class User {
    private String id;
    private String username;
    private String password;    
    private String profileId;
    private String accessToken;
    private ZonedDateTime accessTokenExpiration;

    private boolean loggedIn;
    private String loginError;

    public static User getLoginFailure(String error){
        User user = new User();
        user.setLoggedIn(false);
        user.setLoginError(error);
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getLoginError() {
        return loginError;
    }

    public void setLoginError(String loginError) {
        this.loginError = loginError;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(ZonedDateTime accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }
}
