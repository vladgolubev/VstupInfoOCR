package ua.samosfator.vstupOCR;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws TesseractException, IOException {
        long start = System.currentTimeMillis();

        ImgProc imgProc = new ImgProc(downloadImage(args[0]));
        Tesseract tesseract = Tesseract.getInstance();
        tesseract.setLanguage("ukr");

        HtmlWriter.createHtml(
                imgProc.getRows().stream()
                        .map(row -> new Entrant(row, tesseract))
                        .collect(Collectors.toList()),
                formFilename(args[0]));

        System.out.println("Processed in: " + ((System.currentTimeMillis() - start) / 1000) + " s");
    }

    private static String downloadImage(String link) throws IOException {
        if (link.contains("http:")) {
            URL url = new URL(link);
            ImgProc.writeImg(ImageIO.read(url), "page");
            return "page.png";
        } else return link;
    }

    private static String formFilename(String arg) {
        return arg + ".html";
    }
}
