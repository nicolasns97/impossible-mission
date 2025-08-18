package com.sapienza.jimpossiblemission;

public class Debug {
    private static final Debug instance = new Debug();
    private boolean debug = false;

    public static Debug getInstance() {
        return instance;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
