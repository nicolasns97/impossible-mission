package com.sapienza.jimpossiblemission;

import com.sapienza.jimpossiblemission.controller.GameController;
import com.sapienza.jimpossiblemission.factory.GameFactory;
import com.sapienza.jimpossiblemission.view.GameView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static final boolean DEBUG = true;

    public static void main(String[] args) {
        Debug.getInstance().setDebug(DEBUG);
        GameController gameController = GameFactory.createGame();

        JFrame window = loadWindow(gameController.getGameView());
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.stop();
                System.exit(0);
            }
        });

        gameController.start();

    }

    private static JFrame loadWindow(GameView gameView) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("JImpossibleMission");
        window.add(gameView);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        return window;
    }
}
