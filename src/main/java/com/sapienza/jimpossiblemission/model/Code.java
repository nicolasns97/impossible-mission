package com.sapienza.jimpossiblemission.model;

public class Code implements Collectable {
    CodeType type;

    public enum CodeType {
        PLATFORM_RESET,
        SNOOZE
    }
}
