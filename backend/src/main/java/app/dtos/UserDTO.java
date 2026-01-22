package app.dtos;

import app.dtos.publication.PublicacaoDTO;
import app.entities.User;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDTO implements Serializable {
    private String username;
    private String name;
    private String password;
    private String email;
    private Integer role;
    private Boolean active;
    private String roleType;

    private List<Object> publicacoes = new java.util.ArrayList<>();
    private List<Object> comentarios = new java.util.ArrayList<>();
    private List<Object> ratings = new java.util.ArrayList<>();
    private List<Object> tags_subscritas = new java.util.ArrayList<>();

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
      UserDTO dto = new UserDTO(
          user.getUsername(),
          user.getName(),
          user.getEmail(),
          getRoleNumber(user),
          user.getActive(),
          Hibernate.getClass(user).getSimpleName()
      );

      // Popular as listas se existirem no User
      if (Hibernate.isInitialized(user.getPublicacoes())) {
          dto.setPublicacoes(user.getPublicacoes().stream()
              .map(pub -> pub.getTitulo())
              .collect(Collectors.toList()));
      }

      if (Hibernate.isInitialized(user.getComentarios())) {
          dto.setComentarios(user.getComentarios().stream()
              .map(com -> com.getText())
              .collect(Collectors.toList()));
      }

      if (Hibernate.isInitialized(user.getRatings())) {
          dto.setRatings(user.getRatings().stream()
              .map(rat -> Map.of("value ", rat.getValue(),"publicacao", rat.getPublicacao().getTitulo()))
              .collect(Collectors.toList()));
      }

     /*if (Hibernate.isInitialized(user.getTags_subscritas())) {
          dto.setTags_subscritas(user.getTags_subscritas().stream()
              .map(tag -> )
             .collect(Collectors.toList()));
      }*/

      return dto;
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

    public void setPublicacoes(List<Object> publicacoes) { this.publicacoes = publicacoes; }

    public void setComentarios(List<Object> comentarios) { this.comentarios = comentarios; }

    public void setRatings(List<Object> ratings) { this.ratings = ratings; }

    public void setTags_subscritas(List<Object> tags_subscritas) { this.tags_subscritas = tags_subscritas; }

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

    public List<Object> getPublicacoes() { return publicacoes; }

    public List<Object> getComentarios() { return comentarios; }

    public List<Object> getRatings() { return ratings; }

    public List<Object> getTags_subscritas() { return tags_subscritas; }

    //endregion
}
