package com.sapienza.jimpossiblemission.model;

public class Code implements Collectable {
    private final CodeType type;

    public CodeType getType() {
        return type;
    }

    public Code(CodeType type) {
        this.type = type;
    }


    public enum CodeType {
        PLATFORM_RESET,
        SNOOZE,
        DOOR_KEY
    }
}
