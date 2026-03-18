package com.sapienza.jimpossiblemission.model;


import java.awt.*;

public class Terminal extends Entity implements Interactable {

    public Terminal(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }

    @Override
    public void interact() {

    }
}
