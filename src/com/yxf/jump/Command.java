package com.yxf.jump;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class Command {
    public static String executeCmd(String cmd) {
        System.out.println(cmd);
        BufferedReader bf = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String result = "";
            String line;
            while ((line = bf.readLine()) != null) {
                result = result + line + "\n";
            }
            System.out.println(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static BufferedImage getScreenShot() throws IOException {
        URL url = new Command().getClass().getProtectionDomain().getCodeSource().getLocation();
        String path = url.getPath();
        path = path.substring(1, path.length());
        System.out.println(path);
        executeCmd("adb shell mkdir /sdcard/temp/");
        executeCmd("adb shell screencap -p /sdcard/temp/screenshot.png");
        executeCmd("adb pull /sdcard/temp/screenshot.png " + path);
        executeCmd("adb shell rm /sdcard/temp/screenshot.png");
        BufferedImage image = ImageIO.read(new File(path + "screenshot.png"));
        return image;
    }

    public static void renameScreenShot() {
        URL url = new Command().getClass().getProtectionDomain().getCodeSource().getLocation();
        String path = url.getPath();
        path = path.substring(1, path.length());
        File file = new File(path + "screenshot.png");
        File dest = new File(path + System.currentTimeMillis() + ".png");
        file.renameTo(dest);
    }


    public static void press(int x, int y, int ms) {
        executeCmd("adb shell input swipe " + x + " " + y + " " + x + " " + y + " " + ms);
    }
}
