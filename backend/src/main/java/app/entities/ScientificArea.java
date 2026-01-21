package app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "scientific_areas")
@NamedQueries({
        @NamedQuery(name = "getAllScientificAreas", query = "SELECT s FROM ScientificArea s ORDER BY s.name")
})
public class ScientificArea implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    public ScientificArea() {}

    public ScientificArea(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}