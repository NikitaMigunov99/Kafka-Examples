package org.example.models.event;

public class AuditEvent {

    private String id;
    private String type;

    public AuditEvent() {}

    public AuditEvent(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
