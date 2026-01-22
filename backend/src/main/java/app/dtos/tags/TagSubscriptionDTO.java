package app.dtos.tags;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public class TagSubscriptionDTO implements Serializable {

    @NotEmpty
    private List<String> tag_id;

    public TagSubscriptionDTO() {}

    public List<String> getTag_id() {
        return tag_id;
    }

    public void setTag_id(List<String> tag_id) {
        this.tag_id = tag_id;
    }
}
