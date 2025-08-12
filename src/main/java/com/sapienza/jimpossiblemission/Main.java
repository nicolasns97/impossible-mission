package com.sapienza.jimpossiblemission;

import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, JAXBException {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("JImpossibleMission");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();




    }
}
