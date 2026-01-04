package app.dtos;

import app.entities.User;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO implements Serializable {
    private String username;
    private String name;
    private String password;
    private String email;
    private Integer role;
    private Boolean active;
    private String roleType;

    public UserDTO() {
    }

    public UserDTO( String username, String name, String email, Integer role, Boolean active, String roleType) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
        this.roleType = roleType;
    }

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                getRoleNumber(user),
                user.getActive(),
                Hibernate.getClass(user).getSimpleName()
        );
    }

    private static Integer getRoleNumber(User user) {
        String role = Hibernate.getClass(user).getSimpleName();
        return switch (role) {
            case "Administrator" -> 1;
            case "Responsavel" -> 2;
            case "Colaborador" -> 3;
            default -> 0;
        };
    }

    public static List<UserDTO> from(List<User> users) {
        return users.stream().map(UserDTO::from).collect(Collectors.toList());
    }

    //region setters



    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //endregion
    //region getters


    public String getPassword() {
        return password;
    }

    public Integer getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

    public String getRoleType() {
        return roleType;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    //endregion
}
