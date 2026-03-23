package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.Room.RoomCode;

public class RoomTrigger {
    private final int x;
    private final int y;
    private final RoomCode code;

    public RoomTrigger(int x, int y, RoomCode code) {
        this.x = x;
        this.y = y;
        this.code = code;
    }

    public RoomTrigger(int x, int y, String code) {
        this(x, y, RoomCode.fromPath(code));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public RoomCode getCode() {
        return code;
    }
}
