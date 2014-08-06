package ua.samosfator.vstupOCR;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws TesseractException, IOException {
        Tesseract tesseract = Tesseract.getInstance();
        ImgProc imgProc = new ImgProc(downloadImage(args[0]));
        String filename = args[0].substring(args[0].length() - 31, args[0].length() - 15) + ".html";
        long start = System.currentTimeMillis();
        tesseract.setLanguage("ukr");

        List<Entrant> entrants = imgProc.getRows().stream().map(e -> new Entrant(e, tesseract)).collect(Collectors.toList());
        HtmlWriter.createHtml(entrants, filename);
        System.out.println("Processed in: " + ((System.currentTimeMillis() - start) / 1000) + " s");
    }

    private static String downloadImage(String link) throws IOException {
        URL url = new URL(link);
        ImgProc.writeImg(ImageIO.read(url), "page");
        return "page.png";
    }
}
