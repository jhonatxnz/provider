package br.com.jhonatan.provider.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Actions {

    INACTIVATE("INACTIVATE"),
    ACTIVATE("ACTIVATE"),
    REACTIVATE("REACTIVATE");

    private final String action;

    Actions(String action) {
        this.action = action;
    }

    public String value() {
        return action;
    }
}