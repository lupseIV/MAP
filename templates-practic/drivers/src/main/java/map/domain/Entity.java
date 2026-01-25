package map.domain;

public class Entity<ID> {
    private Long serialVersionUID;
    private ID id;

    public ID getId() {
        return id;
    }
    public Long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public void setSerialVersionUID(Long serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
    }
}
