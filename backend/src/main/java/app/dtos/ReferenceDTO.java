package app.dtos;

import app.entities.PublicationType;
import app.entities.ScientificArea;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ReferenceDTO implements Serializable {
    private Long id; // Added ID
    private String name;

    public ReferenceDTO() {}
    public ReferenceDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Converters
    public static ReferenceDTO from(PublicationType t) {
        return new ReferenceDTO(t.getId(), t.getName());
    }
    public static List<ReferenceDTO> fromTypes(List<PublicationType> list) {
        return list.stream().map(ReferenceDTO::from).collect(Collectors.toList());
    }

    public static ReferenceDTO from(ScientificArea a) {
        return new ReferenceDTO(a.getId(), a.getName());
    }
    public static List<ReferenceDTO> fromAreas(List<ScientificArea> list) {
        return list.stream().map(ReferenceDTO::from).collect(Collectors.toList());
    }
}