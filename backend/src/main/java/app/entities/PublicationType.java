package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "publication_types")
@NamedQueries({
        @NamedQuery(name = "getAllPublicationTypes", query = "SELECT t FROM PublicationType t ORDER BY t.name")
})
public class PublicationType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    public PublicationType() {}

    public PublicationType(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}