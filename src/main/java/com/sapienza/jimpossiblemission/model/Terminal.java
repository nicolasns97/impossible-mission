package com.sapienza.jimpossiblemission.model;


import java.awt.*;

public class Terminal extends Entity implements Interactable {

    public Terminal(int x, int y, int width, int height) {
        super(x, y-height, width, height);
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    @Override
    public void interact() {

    }
}
