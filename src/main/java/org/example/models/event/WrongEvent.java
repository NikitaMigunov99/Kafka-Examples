package org.example.models.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class WrongEvent {

    private String title;
    private String description;

    public WrongEvent() {
    }

    public WrongEvent(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
