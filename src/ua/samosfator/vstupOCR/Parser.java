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

public class Parser {
    private final String FULL_CHAR_WHITELIST = "`'0123456789.:-ІЇЄАБВГДЕЖЗИЙРСТУФХЦЧШЩЬКЛМНОПЯЮабвгдежзийклмнопрстуфхцчшщюяєіїь";
    private final int[] CELL_BOUND = {221, 221, 221};
    private final int[] RECOMMENDED = {144, 238, 144};
    private final int[] ALSO_RECOMMENDED = {161, 248, 161};
    private final int[] ACCEPTED = {189, 210, 240};
    private final int[] ALSO_ACCEPTED = {180, 202, 235};
    private Tesseract tesseract;
    private BufferedImage row;
    private int[][][] px;
    private Entrant entrant;

    public Parser(Entrant entrant, BufferedImage row, Tesseract tesseract) {
        this.entrant = entrant;
        this.row = row;
        this.px = ImgProc.getPixelsRGB(row);
        this.tesseract = tesseract;

        setEntrantStatus();
        parseCells(splitToCells(px));
    }

    public Entrant getEntrant() {
        return entrant;
    }

    private void parseCells(List<BufferedImage> cells) {
        for (int i = 1; i < cells.size(); i++) {
            BufferedImage cell = processNumberCell(cells.get(i), i);
            setTesseractSettings(i);
            try {
                parseCell(tesseract.doOCR(scaleCell(cell, i)), i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setEntrantStatus() {
        if (Arrays.equals(px[5][5], RECOMMENDED) || Arrays.equals(px[5][5], ALSO_RECOMMENDED)) {
            entrant.setRecommended(true);
        }
        if (Arrays.equals(px[5][5], ACCEPTED) || Arrays.equals(px[5][5], ALSO_ACCEPTED)) {
            entrant.setAccepted(true);
        }
    }

    private BufferedImage processNumberCell(BufferedImage cell, int cellIndex) {
        if (cellIndex == 0 || cellIndex > 5) {
            return ImgProc.scale(cropNumberCell(ImgProc.makeGreyScale(cell)), 2);
        } else return cell;
    }

    private void setTesseractSettings(int cellIndex) {
        if (cellIndex == 0 || cellIndex > 5) {
            if (cellIndex > 7) {
                tesseract.setTessVariable("tessedit_char_whitelist", "+-.");
            } else if (cellIndex == 6 || cellIndex == 7) {
                tesseract.setTessVariable("tessedit_char_whitelist", "-.0123456789");
            } else {
                tesseract.setTessVariable("tessedit_char_whitelist", ".0123456789");
            }
            tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_CHAR);
            if (cellIndex == 0) tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
        } else {
            tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
            tesseract.setTessVariable("tessedit_char_whitelist", FULL_CHAR_WHITELIST);
        }
    }

    private BufferedImage scaleCell(BufferedImage cell, int cellIndex) {
        switch (cellIndex) {
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
        return cell;
    }

    private void parseCell(String raw, int i) {
        raw = cleanUpRaw(raw);
        entrant.setNumber(Entrant.n);

        switch (i) {
            case 1: {
                entrant.setName(cleanUpName(raw));
                break;
            }
            case 2: {
                entrant.setScore(cleanUpScore(raw));
                break;
            }
            case 3: {
                entrant.setCertificate(cleanUpCertificate(raw));
                break;
            }
            case 4: {
                entrant.setZno(cleanUpZno(raw));
                break;
            }
            case 5: {
                entrant.setExam(cleanUpExam(raw));
                break;
            }
            case 6: {
                entrant.setOlympiad(raw);
                break;
            }
            case 7: {
                entrant.setExtraPoints(raw);
                break;
            }
            case 8: {
                entrant.setPk(raw);
                break;
            }
            case 9: {
                entrant.setP4(raw);
                break;
            }
            case 10: {
                entrant.setTarget(raw);
                break;
            }
            case 11: {
                entrant.setOriginals(raw);
                break;
            }
        }
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

    private String cleanUpRaw(String raw) {
        return raw.replaceAll("\n", " ").replaceAll("\\s{2,}", " ");
    }

    private String cleanUpName(String raw) {
        String name = raw.replace("йвна", "іївна").replace(".п", "Л").replaceAll("\\.", "");
        name = checkForApostrophe(name);
        name = checkForDmytrovych(name);
        return name;
    }

    private String cleanUpScore(String raw) {
        raw = raw.replaceAll("-", "").replaceAll("б", "6").replaceAll(" ", "");
        return validateScore(raw);
    }

    private String cleanUpCertificate(String raw) {
        return raw.replaceAll("-", "").replaceAll(" ", "").replaceAll("б", "6");
    }

    private String cleanUpZno(String raw) {
        String zno = validateZNO(raw);
        String[] arr = zno.split(" ");
        zno = "";
        for (int i1 = 0; i1 < arr.length; i1++) {
            if (arr[i1].contains("б")) arr[i1] = arr[i1].replace("б", "6");
            zno += arr[i1] + " ";
        }
        return zno;
    }

    private String cleanUpExam(String raw) {
        String exam;
        if (raw.length() < 5) {
            exam = raw.replace("Г", "-");
        } else {
            exam = raw;
        }
        return exam;
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
}
