package org.utils.images;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.*;

public class ImageUtil {
    public static byte[] imageFileToByteArray(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
    public static boolean isValidImageFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }
}