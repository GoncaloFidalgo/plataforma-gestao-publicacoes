package app.dtos;

import java.io.Serializable;

public class AuthDTO implements Serializable {
    private String username;
    private String password;

    public AuthDTO() {
    }

    public AuthDTO(String password, String username) {
        this.password = password;
        this.username = username;
    }

    //region Getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    //endregion
    //region Setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    //endregion
}
