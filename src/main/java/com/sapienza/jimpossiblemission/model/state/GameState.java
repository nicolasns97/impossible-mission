package com.sapienza.jimpossiblemission.model.state;

public interface GameState {
    void update();
    void up();
    void down();
    void left();
    void right();
    void space();
}
