package ua.samosfator.vstupOCR;

import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entrant {
    private final int[] CELL_BOUND = {221, 221, 221};
    private final int[] RECOMMENDED = {144, 238, 144};
    private final int[] ALSO_RECOMMENDED = {161, 248, 161};
    private final int[] ACCEPTED = {189, 210, 240};
    private final int[] ALSO_ACCEPTED = {180, 202, 235};
    private BufferedImage row;
    private int[][][] px;
    private Tesseract tesseract;
    private static int n;
    private int number;
    private String name;
    private String score;
    private String certificate;
    private String zno;
    private String exam;
    private String olympiad;
    private String extraPoints;
    private String pk;
    private String p4;
    private String target;
    private String originals;
    private boolean isRecommended;
    private boolean isAccepted;

    public Entrant(BufferedImage row, Tesseract tesseract) {
        this.row = row;
        this.tesseract = tesseract;
        n++;
        parse();
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getZno() {
        return zno;
    }

    public String getExam() {
        return exam;
    }

    public String getOlympiad() {
        return olympiad;
    }

    public String getExtraPoints() {
        return extraPoints;
    }

    public String getPk() {
        return pk;
    }

    public String getP4() {
        return p4;
    }

    public String getTarget() {
        return target;
    }

    public String getOriginals() {
        return originals;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    private void parse() {
        px = ImgProc.getPixelsRGB(row);
        setStatus();
        OCRCells(splitToCells(px));
    }

    private void OCRCells(List<BufferedImage> cells) {
        for (int i = 1; i < cells.size(); i++) {
            BufferedImage cell = cells.get(i);
            if (i == 0 || i > 5) {
                cell = ImgProc.scale(cropNumberCell(ImgProc.makeGreyScale(cell)), 2);
                if (i > 7) {
                    tesseract.setTessVariable("tessedit_char_whitelist", "+-.");
                } else if (i == 6 || i == 7) {
                    tesseract.setTessVariable("tessedit_char_whitelist", "-.0123456789");
                } else {
                    tesseract.setTessVariable("tessedit_char_whitelist", ".0123456789");
                }
                tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_CHAR);
                if (i == 0) tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
            } else {
                tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
                tesseract.setTessVariable("tessedit_char_whitelist", "'0123456789.:-ІЇЄАБВГДЕЖЗИЙРСТУФХЦЧШЩЬКЛМНОП" +
                        "ЯЮабвгдежзийклмнопрстуфхцчшщюяєіїь");

                switch (i) {
                    case 1: {
                        cell = ImgProc.scale(cell, 12);
                        ImgProc.writeImg(cell, "cell");
                        try {
                            cell = ImageIO.read(new File("cell.png"));
                            cell = ImgProc.scale(cell, 4);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default: {
                        cell = ImgProc.scale(cell, 12);
                        break;
                    }
                }
            }
            try {
                parseCell(tesseract.doOCR(cell), i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setStatus() {
        if (Arrays.equals(px[5][5], RECOMMENDED) || Arrays.equals(px[5][5], ALSO_RECOMMENDED)) {
            isRecommended = true;
        }
        if (Arrays.equals(px[5][5], ACCEPTED) || Arrays.equals(px[5][5], ALSO_ACCEPTED)) {
            isAccepted = true;
        }
    }

    private Entrant parseCell(String raw, int i) {
        raw = raw.replaceAll("\n", " ").replaceAll("\\s{2,}", " ");
        number = n;
        switch (i) {
            case 1: {
                name = raw.replace("йвна", "іївна").replace(".п", "Л").replaceAll("\\.", "");
                name = checkForApostrophe(name);
                name = checkForDmytrovych(name);
                System.out.println(name);
                break;
            }
            case 2: {
                raw = raw.replaceAll("-", "").replaceAll("б", "6").replaceAll(" ", "");
                score = validateScore(raw);
                break;
            }
            case 3: {
                certificate = raw.replaceAll("-", "").replaceAll(" ", "").replaceAll("б", "6");
                break;
            }
            case 4: {
                zno = validateZNO(raw);
                String[] arr = zno.split(" ");
                zno = "";
                for (int i1 = 0; i1 < arr.length; i1++) {
                    if (arr[i1].contains("б")) arr[i1] = arr[i1].replace("б", "6");
                    zno += arr[i1] + " ";
                }
                break;
            }
            case 5: {
                if (raw.length() < 5) {
                    exam = raw.replace("Г", "-");
                } else {
                    exam = raw;
                }
                break;
            }
            case 6: {
                olympiad = raw;
                break;
            }
            case 7: {
                extraPoints = raw;
                break;
            }
            case 8: {
                pk = raw;
                break;
            }
            case 9: {
                p4 = raw;
                break;
            }
            case 10: {
                target = raw;
                break;
            }
            case 11: {
                originals = raw;
                break;
            }
        }
        return this;
    }

    private List<BufferedImage> splitToCells(int[][][] px) {
        List<BufferedImage> cells = new ArrayList<>();
        List<Integer> pxToCrop = getCellCoords(px);
        int pointX = 0;
        int cellWidth = 0;

        for (int i = 0; i < pxToCrop.size(); i++) {
            int columnBoundPos = pxToCrop.get(i);
            if (i > 0) pointX += cellWidth;
            cellWidth = columnBoundPos - pointX;
            BufferedImage cell = ImgProc.crop(row, new Point(pointX, 0), new Rectangle(cellWidth, ImgProc.ROW_HEIGHT));
            cells.add(cell);
        }

        return cells;
    }

    private List<Integer> getCellCoords(int[][][] px) {
        List<Integer> indexesToCrop = new ArrayList<>();
        for (int x = 0; x < px.length; x++) {
            if (Arrays.equals(px[x][32], CELL_BOUND)) {
                indexesToCrop.add(x);
            }
        }
        return indexesToCrop;
    }

    private BufferedImage cropNumberCell(BufferedImage numberCell) {
        return numberCell.getSubimage(0, 22, numberCell.getWidth(), 15);
    }

    private String insertDot(String str) {
        String wholeNumber = str.substring(0, 3);
        String fraction = str.substring(3, str.length());
        return wholeNumber + "." + fraction;
    }

    private String checkForApostrophe(String raw) {
        int yIndex = raw.indexOf("є") > 1 ? raw.indexOf("є") : (raw.indexOf("ю") > 1 ? raw.indexOf("ю") :
                (raw.indexOf("я") > 1 ? raw.indexOf("я") : (raw.indexOf("ї") > 1 ? raw.indexOf("ї") : -1)));
        if (yIndex > 1) {
            String beforeY = raw.substring(0, yIndex).trim();
            String lastChar = beforeY.substring(beforeY.length() - 1);

            int consonantIndex = lastChar.contains("б") ? lastChar.indexOf("б") : (lastChar.contains("п") ?
                    lastChar.indexOf("п") : (lastChar.contains("в") ? lastChar.indexOf("в") :
                    (lastChar.contains("м") ? lastChar.indexOf("м") :
                            (lastChar.contains("ф") ? lastChar.indexOf("ф") : -1))));
            if (consonantIndex > -1) {
                raw = insertApostrophe(raw, yIndex - 1, yIndex);
            }
        }
        return raw;
    }

    private String checkForDmytrovych(String raw) {
        String[] name = raw.split(" ");
        if (new Character(name[2].charAt(0)).isLetter('Д') && raw.endsWith("трович")) {
            return name[0] + " " + name[1] + "Дмитрович";
        } else return raw;
    }

    private String insertApostrophe(String str, int x, int y) {
        String firstPart = str.substring(0, x);
        String secondPart = str.substring(y, str.length());
        return firstPart + "'" + secondPart;
    }

    private String validateScore(String raw) {
        if (!raw.contains(".")) {
            raw = insertDot(raw);
        } else if (raw.contains(",")) {
            raw = insertDot(raw.replace(",", ""));
        } else return raw;
        return raw;
    }

    private String validateZNO(String raw) {
        return raw.replace("Укрм", "Укр.м.").replace("Укрм.", "Укр.м.").replace("Укр.м..", "Укр.м.")
                .replace("Англм.", "Англ.м.").replace("Англм", "Англ.м.").replace("О", "0")
                .replace("Роєм", "Рос.м").replace("Рослчп", "Рос.м");
    }

    @Override
    public String toString() {
        return number + " " + name + "" + score + " " + certificate + " " + zno + " " +
                exam + " " + olympiad + " " + extraPoints + " " + pk + " " + p4 + " " + target + " " + originals;
    }
}
