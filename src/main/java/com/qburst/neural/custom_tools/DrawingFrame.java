/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qburst.neural.custom_tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DrawingFrame extends JFrame {
    private JComponent canvas;                           // handles graphics display
    private BufferedImage image;                        // what to draw by default

    public DrawingFrame(String title, String filename) {
        super(title);
        try {
            image = ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.err.println("Couldn't load image");
            System.exit(-1);
        }
        createCanvas();
        finishGUI(image.getWidth(), image.getHeight());
    }

    private void createCanvas() {
        canvas = new JComponent() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };
    }

    private void finishGUI(int width, int height) {
        setSize(width, height);
        canvas.setPreferredSize(new Dimension(width, height));
        getContentPane().add(canvas);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }


    private void draw(Graphics g) {
        if (image != null) g.drawImage(image, 0, 0, null);
    }

    public BufferedImage getImage() {
        return image;
    }


    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}
