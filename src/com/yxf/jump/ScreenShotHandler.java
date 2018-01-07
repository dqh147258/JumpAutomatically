package com.yxf.jump;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScreenShotHandler {
    BufferedImage image;
    int[] imageColors;


    Color mostColor;

    Color jumpColor = new Color(54, 54, 94);

    Color shadow = new Color(138, 140, 147);

    int[] jumpLocation = null;
    int[] targetTop = null;
    int[] targetLeft = null;
    int[] targetRight = null;

    int startY;
    int endY;
    int startX;
    int endX;


    public ScreenShotHandler(BufferedImage image) {
        this.image = image;
        imageColors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        mostColor = getMostSimilarityColor();
        shadow = new Color((int) (mostColor.getRed() / 1.43), (int) (mostColor.getGreen() / 1.43), (int) (mostColor.getBlue() / 1.43));
        if (mostColor != null) {
            System.out.println("the most color is : " + mostColor);
            jumpLocation = findJumpPoint();
            targetTop = findTargetTop();
            if (targetTop != null) {
                targetLeft = findTargetLeft();
                if (targetLeft == null) {
                    System.out.println("the target left is null");
                }
                targetRight = findTargetRight();
                if (targetRight == null) {
                    System.out.println("the target right is null");
                }
            } else {
                System.out.println("the target top location is null");
            }
        } else {
            System.out.println("the most color is null");
        }
    }

    private void areaReset() {
        startY = image.getHeight() / 4;
        endY = image.getHeight() / 4 * 3;
        startX = image.getWidth() / 8;
        endX = image.getWidth() / 8 * 7;
    }

    private Color getMostSimilarityColor() {
        areaReset();
        ArrayList<ArrayList<Color>> colorsList = new ArrayList<ArrayList<Color>>();

        for (int i = 0; i < image.getWidth(); i += 10) {
            for (int j = 0; j < image.getHeight(); j += 10) {
                Color color = new Color(imageColors[j * image.getWidth() + i]);
                boolean similar = false;
                for (int k = 0; k < colorsList.size(); k++) {
                    if (isSimilar(colorsList.get(k).get(0), color)) {
                        similar = true;
                        colorsList.get(k).add(color);
                        break;
                    }
                }
                if (!similar) {
                    ArrayList<Color> colors = new ArrayList<Color>();
                    colors.add(color);
                    colorsList.add(colors);
                }
            }
        }
        if (colorsList.size() > 0) {
            Collections.sort(colorsList, new Comparator<ArrayList<Color>>() {
                @Override
                public int compare(ArrayList<Color> o1, ArrayList<Color> o2) {
                    return o2.size() - o1.size();
                }
            });
            ArrayList<Color> colors = colorsList.get(0);
            int size = colors.size();
            int red = 0, green = 0, blue = 0;
            for (int i = 0; i < size; i++) {
                red = red + colors.get(i).getRed();
                green = green + colors.get(i).getGreen();
                blue = blue + colors.get(i).getBlue();
            }
            return new Color(red / size, green / size, blue / size);
        }
        return null;
    }

    private Color getMostColor() {
        return mostColor;
    }

    private int[] findJumpPoint() {
        areaReset();
        int[] location = new int[2];
        for (int j = endY - 1; j >= startY; j -= 3) {
            for (int i = startX; i < endX; i += 3) {
                Color color = new Color(imageColors[j * image.getWidth() + i]);
                if (isSimilar(color, jumpColor)) {
                    location[0] = i + image.getWidth() / 60;
                    location[1] = j - image.getWidth() / 80;
                    return location;
                }
            }
        }
        return null;
    }

    public int[] getJumpLocation() {
        return jumpLocation;
    }

    private int[] findTargetTop() {
        areaReset();
        if (jumpLocation == null) {
            return null;
        }

        int[] location = new int[2];

        if (jumpLocation[0] < image.getWidth() / 2) {
            startX = jumpLocation[0] + image.getWidth() / 12;
        } else {
            endX = jumpLocation[0] - image.getWidth() / 12;
        }
        Color color = null;
        mark:
        for (int j = startY; j < endY; j += 3) {
            for (int i = startX; i < endX; i += 3) {
                color = new Color(imageColors[j * image.getWidth() + i]);
                if (!isSimilar(color, mostColor, 20)) {
                    location[0] = i;
                    location[1] = j;
                    break mark;
                } else if (isSimilar(color, Color.WHITE, 18)) {
                    location[0] = i;
                    location[1] = j;
                    break mark;
                }
            }
        }

        if (location[0] != 0 && location[1] != 0 && color != null) {
            int left = location[0];
            for (int i = left - 1; i > startX; i--) {
                if (!isSimilar(mostColor, new Color(imageColors[location[1] * image.getWidth() + i]), 18)) {
                    left = i;
                } else {
                    break;
                }
            }
            int right = location[0];
            for (int i = left + 1; i < endX; i++) {
                if (!isSimilar(mostColor, new Color(imageColors[location[1] * image.getWidth() + i]), 18)) {
                    right = i;
                } else {
                    break;
                }
            }
            location[0] = (left + right) / 2;
            return location;
        }
        return null;
    }

    public int[] getTargetTop() {
        return targetTop;
    }


    private int[] findTargetLeft() {
        areaReset();
        if (jumpLocation[0] < image.getWidth() / 2) {
            //startX = jumpLocation[0] + image.getWidth() / 12;
        } else {
            startX = 10;
            endX = jumpLocation[0] - image.getWidth() / 12;
        }
        endX = targetTop[0];
        startY = targetTop[1];
        int location[] = new int[2];
        for (int j = targetTop[1]; j < endY; j++) {
            for (int i = endX; i > startX; i--) {
                if (!isSimilar(18, new Color(imageColors[j * image.getWidth() + i]), mostColor)) {
                    if (location[0] != 0) {
                        if (location[0] > i) {
                            location[0] = i;
                            location[1] = j;
                        }
                    } else {
                        location[0] = i;
                        location[1] = j;
                    }


                } else {
                    if (i > location[0] - 5) {
                        boolean isSimilar = false;
                        for (int k = location[1]; k < location[1] + 30; k++) {
                            if (!isSimilar(18, new Color(imageColors[k * image.getWidth() + i]), Color.WHITE)) {
                                if (isSimilar(18, new Color(imageColors[k * image.getWidth() + i]), mostColor, shadow)) {
                                    isSimilar = true;
                                } else {
                                    isSimilar = false;
                                    break;
                                }
                            } else {
                                isSimilar = false;
                                break;
                            }
                        }
                        if (isSimilar) {
                            if (!isSimilar(shadow, new Color(imageColors[(location[1] + 10) * image.getWidth() + location[0] + 10]), 20)) {
                                return location;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    public int[] getTargetLeft() {
        return targetLeft;
    }

    private int[] findTargetRight() {
        areaReset();
        int[] location = new int[2];
        if (jumpLocation[0] < image.getWidth() / 2) {
            startX = targetTop[0];
            endX = image.getWidth();
        } else {
            endX = jumpLocation[0];
        }
        startY = targetTop[1] + 1;
        endY = jumpLocation[1];
        for (int j = startY; j < endY; j++) {
            for (int i = startX; i < endX; i++) {
                if (!isSimilar(mostColor, new Color(imageColors[j * image.getWidth() + i]), 18)) {
                    if (location[0] != 0) {
                        if (location[0] < i) {
                            location[0] = i;
                            location[1] = j;
                        }
                    } else {
                        location[0] = i;
                        location[1] = j;
                    }
                } else {
                    if (i < location[0] + 5) {
                        boolean isSimilar = false;
                        for (int k = location[1]; k < location[1] + 30; k++) {
                            if (!isSimilar(18, new Color(imageColors[k * image.getWidth() + i]), Color.WHITE)) {
                                if (isSimilar(mostColor, new Color(imageColors[k * image.getWidth() + i]), 18)) {
                                    isSimilar = true;
                                } else {
                                    isSimilar = false;
                                    break;
                                }
                            } else {
                                isSimilar = false;
                                break;
                            }
                        }
                        if (isSimilar) {
                            return location;
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    public int[] getTargetRight() {
        return targetRight;
    }

    private boolean isSimilar(double first, double second, float similar) {
        if (Math.abs(first - second) < similar) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSimilar(Color first, Color second) {
        return isSimilar(first, second, 10);
    }

    private boolean isSimilar(Color first, Color second, int similar) {
        if (Math.abs(first.getRed() - second.getRed()) +
                Math.abs(first.getGreen() - second.getGreen()) + Math.abs(first.getBlue() - second.getBlue()) < similar * 3) {
            return true;
        }
        return false;
    }

    private boolean isSimilar(int similar, Color src, Color... compare) {
        if (compare != null) {
            int len = compare.length;
            for (int i = 0; i < len; i++) {
                if (isSimilar(src, compare[i], similar)) {
                    return true;
                }
            }
        }
        return false;
    }

}
