package com.yxf.jump;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class ImageViewFrame extends JFrame {

    JLabel label;

    public ImageViewFrame() {
        setTitle("screen shot");
        setSize(360, 640);
        label = new JLabel();
        add(label);
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    Main.stop = !Main.stop;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void setImage(BufferedImage image) {
        label.setIcon(new ImageIcon(image));
    }
}
