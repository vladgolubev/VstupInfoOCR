package ua.samosfator.vstupOCR;

import hqx.Hqx_4x;
import hqx.RgbYuv;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImgProc {
    public static final int ROW_HEIGHT = 58;
    private int width;
    private int height;
    private int rowNumber;

    private BufferedImage image;

    public ImgProc(String filename) throws IOException {
        image = ImageIO.read(new File(filename));
        width = image.getWidth();
        height = image.getHeight();
        rowNumber = (int) (height / ROW_HEIGHT) - 2;
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<BufferedImage> getRows() {
        List<BufferedImage> rows = new ArrayList<>();
        for (int i = 1; i < rowNumber + 1; i++) {
            BufferedImage singleRow = crop(image, new Point(1, ROW_HEIGHT * i), new Rectangle(width, ROW_HEIGHT));
            rows.add(singleRow);
        }
        return rows;
    }

    public static void writeImg(BufferedImage img, String filename) {
        try {
            ImageIO.write(img, "png", new BufferedOutputStream(new FileOutputStream(new File(filename + ".png")), 256 * 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage crop(BufferedImage image, Point position, Rectangle rect) {
        return image.getSubimage(
                (int) position.getX(), (int) position.getY(),
                rect.width - 1, rect.height - 1);
    }

    public static BufferedImage scale(BufferedImage before, int scale) {
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(scale * w, scale * h, BufferedImage.TYPE_INT_ARGB);

        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp;

        scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        return scaleOp.filter(before, after);
    }

    public static BufferedImage hqxScale(BufferedImage bi) {
        if (bi.getType() != BufferedImage.TYPE_INT_ARGB && bi.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
            final BufferedImage temp = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
            temp.getGraphics().drawImage(bi, 0, 0, null);
            bi = temp;
        }
        final int[] data = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

        RgbYuv.hqxInit();

        final BufferedImage biDest2 = new BufferedImage(bi.getWidth() * 4, bi.getHeight() * 4, BufferedImage.TYPE_INT_ARGB);
        final int[] dataDest2 = ((DataBufferInt) biDest2.getRaster().getDataBuffer()).getData();
        Hqx_4x.hq4x_32_rb(data, dataDest2, bi.getWidth(), bi.getHeight());

        return bi;
    }


    public static int[][][] getPixelsRGB(BufferedImage img) {
        int[][][] pixelData = new int[img.getWidth()][img.getHeight()][3];
        int[] rgb;
        int width = img.getWidth();
        int height = img.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rgb = getPixelData(img, x, y);
                pixelData[x][y] = rgb;
            }
        }
        return pixelData;
    }

    public static BufferedImage convertToRGB(BufferedImage img) {
        BufferedImage rgbImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgbImg.getGraphics().drawImage(img, 0, 0, null);
        rgbImg.flush();
        return rgbImg;
    }

    public static BufferedImage makeGreyScale(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return image;
    }

    private static int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);

        int rgb[] = new int[]{
                (argb >> 16) & 0xff, //red
                (argb >> 8) & 0xff, //green
                (argb) & 0xff  //blue
        };

        return rgb;
    }
}
