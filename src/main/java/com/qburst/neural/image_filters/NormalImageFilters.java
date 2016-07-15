package com.qburst.neural.image_filters;

import com.qburst.neural.custom_tools.DrawingFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class NormalImageFilters {

    public static JMenuBar bar;
    private DrawingFrame gui;             // handles the image display
    private BufferedImage original;        // the unprocessed image, as read from a file
    private BufferedImage current;        // the version that's been processed

    public NormalImageFilters(String filename) {
        // Create a GUI element to display the image.
        gui = new DrawingFrame("Image Processing", filename);

        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Hold on to a copy of the image
        current = gui.getImage();
        original = new BufferedImage(gui.getImage().getColorModel(), gui.getImage().copyData(null), gui.getImage().getColorModel().isAlphaPremultiplied(), null);

        // Listen to key presses
        gui.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                process(e.getKeyChar());
            }
        });

        addExtras();
        gui.setJMenuBar(bar);
    }

    private static double constrain(double val, double min, double max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        }
        return val;
    }

    /**
     * Computes the luminosity of an rgb value by one standard formula.
     *
     * @param r red value (0-255)
     * @param g green value (0-255)
     * @param b blue value (0-255)
     * @return luminosity (0-255)
     */
    private static int luminosity(int r, int g, int b) {
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * Main method for the application
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Receive Image as Param
                new NormalImageFilters(args[0]);
            }
        });
    }

    public void process(char op) {
        System.out.println("Processing key '" + op + "'");
        if (op == 'a') { // average
            average(1);
        } else if (op == 'b') { // brighten
            brighten();
        } else if (op == 'c') { // funky color scaling
            scaleColor(1.25, 1.0, 0.75);
        } else if (op == 'd') { // dim
            dim();
        } else if (op == 'f') { // flip
            flip();
        } else if (op == 'g') { // gray
            gray();
        } else if (op == 's') { // sharpen
            sharpen(3);
        } else if (op == 'm') { // scramble
            scramble(5);
        } else if (op == 'n') { // noise
            noise(20);
        } else if (op == 'o') { // revert to original
            current = new BufferedImage(original.getColorModel(), original.copyData(null), original.getColorModel().isAlphaPremultiplied(), null);
        } else if (op == 'q') { // save a snapshot
            try {
                ImageIO.write(current, "jpg", new File("processed_ "+ new Random().nextInt(100)+" .jpg"));
                System.out.println("Saved a snapshot");
            } catch (Exception e) {
                System.err.println("Couldn't save snapshot.");
            }
        } else {
            System.out.println("Unknown operation");
        }
        gui.setImage(current);
    }

    /**
     * Blurs the current image by setting each pixel's values to the average of
     * those in a radius-sized box around it. Use a smallish box (e.g., 1)
     * unless the image is small or you're willing to wait a while.
     */
    private void average(int radius) {
        // Create a new image into which the resulting pixels will be stored.
        BufferedImage result = new BufferedImage(current.getWidth(), current.getHeight(), current.getType());
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                int sumR = 0, sumG = 0, sumB = 0;
                int n = 0;
                // Nested loop over neighbors
                // but be careful not to go outside image (max, min stuff).
                for (int ny = Math.max(0, y - radius);
                     ny < Math.min(current.getHeight(), y + 1 + radius);
                     ny++) {
                    for (int nx = Math.max(0, x - radius);
                         nx < Math.min(current.getWidth(), x + 1 + radius);
                         nx++) {
                        // Add all the neighbors (& self) to the running totals
                        Color c = new Color(current.getRGB(nx, ny));
                        sumR += c.getRed();
                        sumG += c.getGreen();
                        sumB += c.getBlue();
                        n++;
                    }
                }
                Color newColor = new Color(sumR / n, sumG / n, sumB / n);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        // Make the current image be this new image.
        current = result;
    }

    /**
     * Brightens the current image by scaling up pixel values (but not exceeding
     * 255).
     */
    private void brighten() {
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Get current color; scale each channel
                Color color = new Color(current.getRGB(x, y));
                int red = Math.min(255, color.getRed() * 4 / 3);
                int green = Math.min(255, color.getGreen() * 4 / 3);
                int blue = Math.min(255, color.getBlue() * 4 / 3);
                // Put new color
                Color newColor = new Color(red, green, blue);
                current.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    /**
     * Dims the current image by scaling down pixel values.
     */
    private void dim() {
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Get current color; scale each channel
                Color color = new Color(current.getRGB(x, y));
                int red = color.getRed() * 3 / 4;
                int green = color.getGreen() * 3 / 4;
                int blue = color.getBlue() * 3 / 4;
                // Put new color
                Color newColor = new Color(red, green, blue);
                current.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    private void scaleColor(double scaleR, double scaleG, double scaleB) {
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Get current color; scale each channel
                Color color = new Color(current.getRGB(x, y));
                int red = (int) (Math.min(255, color.getRed() * scaleR));
                int green = (int) (Math.min(255, color.getGreen() * scaleG));
                int blue = (int) (Math.min(255, color.getBlue() * scaleB));
                // Put new color
                Color newColor = new Color(red, green, blue);
                current.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    /**
     * Flips the current image upside down.
     */
    private void flip() {
        // Create a new image into which the resulting pixels will be stored.
        BufferedImage result = new BufferedImage(current.getWidth(), current.getHeight(), current.getType());
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                int y2 = current.getHeight() - 1 - y; // note that indices go 0..height-1
                result.setRGB(x, y2, current.getRGB(x, y));
            }
        }
        // Make the current image be this new image.
        current = result;
    }

    /**
     * Makes the current image look grayscale (though still represented as RGB).
     */
    private void gray() {
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Get current color; set each channel to luminosity
                Color color = new Color(current.getRGB(x, y));
                int gray = luminosity(color.getRed(), color.getGreen(), color.getBlue());
                // Put new color
                Color newColor = new Color(gray, gray, gray);
                current.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    /**
     * Sharpens the current image by setting each pixel's values to subtract out
     * those in a radius-sized box around it. Use a smallish box (e.g., 1)
     * unless the image is small or you're willing to wait a while.
     *
     * @param radius size of box; e.g., 1 indicates +-1 around the pixel
     */
    private void sharpen(int radius) {
        // Create a new image into which the resulting pixels will be stored.
        BufferedImage result = new BufferedImage(current.getWidth(), current.getHeight(), current.getType());
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                int sumR = 0, sumG = 0, sumB = 0;
                int n = 0;
                // Nested loop over neighbors
                // but be careful not to go outside image (max, min stuff).
                for (int ny = Math.max(0, y - radius);
                     ny < Math.min(current.getHeight(), y + 1 + radius);
                     ny++) {
                    for (int nx = Math.max(0, x - radius);
                         nx < Math.min(current.getWidth(), x + 1 + radius);
                         nx++) {
                        // Add all the neighbors (but not self) to the running totals
                        if (nx != x || ny != y) {
                            Color c = new Color(current.getRGB(nx, ny));
                            sumR += c.getRed();
                            sumG += c.getGreen();
                            sumB += c.getBlue();
                            n++;
                        }
                    }
                }
                // Weighted center pixel minus sum of neighbors
                Color c = new Color(current.getRGB(x, y));
                int red = (int) constrain(c.getRed() * (n + 1) - sumR, 0, 255);
                int green = (int) constrain(c.getGreen() * (n + 1) - sumG, 0, 255);
                int blue = (int) constrain(c.getBlue() * (n + 1) - sumB, 0, 255);
                Color newColor = new Color(red, green, blue);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        // Make the current image be this new image.
        current = result;
    }

    /**
     * Adds random noise to each pixel.
     *
     * @param scale maximum value of the noise to be added
     */
    private void noise(double scale) {
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Get current color; add noise to each channel
                Color color = new Color(current.getRGB(x, y));
                int red = (int) (constrain(color.getRed() + scale * (2 * Math.random() - 1), 0, 255));
                int green = (int) (constrain(color.getGreen() + scale * (2 * Math.random() - 1), 0, 255));
                int blue = (int) (constrain(color.getBlue() + scale * (2 * Math.random() - 1), 0, 255));
                // Put new color
                Color newColor = new Color(red, green, blue);
                current.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    /**
     * Scrambles the current image by setting each pixel from some nearby pixel.
     *
     * @param radius maximum distance (+- that amount in x and y) of "nearby"
     */
    private void scramble(int radius) {
        // Create a new image into which the resulting pixels will be stored.
        BufferedImage result = new BufferedImage(current.getWidth(), current.getHeight(), current.getType());
        // Nested loop over every pixel
        for (int y = 0; y < current.getHeight(); y++) {
            for (int x = 0; x < current.getWidth(); x++) {
                // Random neighbors in x and y; constrain to image size
                int nx = (int) constrain(x + radius * (2 * Math.random() - 1), 0, current.getWidth() - 1);
                int ny = (int) constrain(y + radius * (2 * Math.random() - 1), 0, current.getHeight() - 1);
                result.setRGB(x, y, current.getRGB(nx, ny));
            }
        }
        // Make the current image be this new image.
        current = result;
    }

    //Add menubar with image filter options
    private void addExtras() {
        bar = new JMenuBar();
        JMenu menu = new JMenu("Operations");
        JMenuItem item1 = new JMenuItem("Average - A");
        JMenuItem item2 = new JMenuItem("Brigthen - B");
        JMenuItem item3 = new JMenuItem("Scale Color - C");
        JMenuItem item4 = new JMenuItem("Dim - D");
        JMenuItem item5 = new JMenuItem("Flip - F");
        JMenuItem item6 = new JMenuItem("Grey - G");
        JMenuItem item7 = new JMenuItem("Sharpen - S");
        JMenuItem item8 = new JMenuItem("Scramble");
        JMenuItem item9 = new JMenuItem("Noise - N");
        JMenuItem item10 = new JMenuItem("Revert- O");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                average(1);
                gui.setImage(current);
            }
        });
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                brighten();
                gui.setImage(current);
            }
        });
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaleColor(1.25, 1.0, 0.75);
                gui.setImage(current);
            }
        });
        item4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dim();
                gui.setImage(current);
            }
        });
        item5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flip();
                gui.setImage(current);
            }
        });
        item6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gray();
                gui.setImage(current);
            }
        });
        item7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sharpen(3);
                gui.setImage(current);
            }
        });
        item8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scramble(5);
                gui.setImage(current);
            }
        });
        item9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                noise(20);
                gui.setImage(current);
            }
        });
        item10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                current = new BufferedImage(original.getColorModel(), original.copyData(null), original.getColorModel().isAlphaPremultiplied(), null);
                gui.setImage(current);
            }
        });
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(item4);
        menu.add(item5);
        menu.add(item6);
        menu.add(item7);
        menu.add(item8);
        menu.add(item9);
        menu.add(item10);
        bar.add(menu);
    }
}
