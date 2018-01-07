package com.yxf.jump;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    public static boolean stop = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        int similar = 10;
        int[] lastJumpPoint = null;
        int[] lastTargetPoint = null;
        ImageViewFrame imageViewFrame = new ImageViewFrame();
        imageViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageViewFrame.setVisible(true);
        while (true) {
            if (!stop) {
                BufferedImage image = Command.getScreenShot();
                ScreenShotHandler handler = new ScreenShotHandler(image);
                int[] jumpPoint = handler.getJumpLocation();
                int[] targetTop = handler.getTargetTop();
                int[] targetLeft = handler.getTargetLeft();
                int[] targetRight = handler.getTargetRight();
                if (targetLeft != null && targetRight != null) {
                    if (targetTop[0] - targetLeft[0] < image.getWidth() / 12 || targetRight[0] - targetTop[0] < image.getWidth() / 12) {
                        targetRight = null;
                        targetLeft = null;
                    } else if (targetRight[0] - targetLeft[0] < image.getWidth() / 6) {
                        targetRight = null;
                        targetLeft = null;
                    }
                }
                if (jumpPoint != null) {
                    System.out.println("jump point : x = " + jumpPoint[0] + " , y = " + jumpPoint[1]);
                } else {
                    System.out.println("the jump point find failed");
                }
                if (targetTop != null) {
                    System.out.println("target point : x = " + targetTop[0] + " , y = " + targetTop[1]);
                } else {
                    System.out.println("the target point find failed");
                }
                if (jumpPoint != null && targetTop != null) {
                    BufferedImage newImage = drawLocation(image, 360 / (float) image.getWidth(), jumpPoint, targetTop, targetLeft, targetRight);
                    if (targetLeft == null) {
                        imageViewFrame.setTitle("can not find targetLeft");
                        //stop = true;
                        //Command.renameScreenShot();
                    }
                    imageViewFrame.setImage(newImage);
                    if (lastJumpPoint == null || lastTargetPoint == null) {
                        lastJumpPoint = jumpPoint;
                        lastTargetPoint = targetTop;
                    } else {
                        if (isLocationSimilar(lastJumpPoint, jumpPoint, 10) && isLocationSimilar(lastTargetPoint, targetTop, similar)) {
                            int length;
                            length = (int) (Math.abs(jumpPoint[0] - targetTop[0]) / Math.sqrt(3) * 2);
                            if (targetLeft != null) {
                                int distance = (int) Math.sqrt(Math.pow(jumpPoint[0] - targetTop[0], 2) + Math.pow(jumpPoint[1] - targetLeft[1], 2));
                                imageViewFrame.setTitle("length : " + length + " , real length : " + distance);
                                length = distance;
                            } else {
                                if (targetRight != null) {
                                    int distance = (int) Math.sqrt(Math.pow(jumpPoint[0] - targetTop[0], 2) + Math.pow(jumpPoint[1] - targetRight[1], 2));
                                    imageViewFrame.setTitle("length : " + length + " , real length : " + distance);
                                    length = distance;
                                } else {
                                    System.out.println("the length is : " + length);
                                }
                            }


                            Command.press(jumpPoint[0], jumpPoint[1], (int) ((length + 15) * 1.32));
                            Thread.sleep(1000);
                        } else {
                            lastJumpPoint = jumpPoint;
                            lastTargetPoint = targetTop;
                        }
                    }
                }
            } else {
                Thread.sleep(1000);
            }

        }


    }

    public static boolean isLocationSimilar(int[] first, int[] second, int similar) {
        if (Math.abs(first[0] - second[0]) <= similar && Math.abs(first[1] - second[1]) <= similar) {
            return true;
        } else {
            return false;
        }
    }


    public static BufferedImage zoomInImage(BufferedImage originalImage, float times) {
        int width = (int) (originalImage.getWidth() * times);
        int height = (int) (originalImage.getHeight() * times);
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return newImage;
    }

    public static BufferedImage drawLocation(BufferedImage originalImage, float times, int[]... locations) {
        int width = (int) (originalImage.getWidth() * times);
        int height = (int) (originalImage.getHeight() * times);
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.setColor(Color.RED);
        if (locations != null) {
            int len = locations.length;
            for (int i = 0; i < len; i++) {
                if (locations[i] != null) {
                    int x = (int) (locations[i][0] * times);
                    int y = (int) (locations[i][1] * times);
                    int radius = newImage.getWidth() / 6;
                    g.drawLine(x - radius, y, x + radius, y);
                    g.drawLine(x, y - radius, x, y + radius);
                }
            }
        }
        g.dispose();
        return newImage;
    }
}
