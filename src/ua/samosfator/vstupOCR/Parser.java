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
        raw = Cleanup.basicCleaning(raw);
        entrant.setNumber(Entrant.n);

        switch (i) {
            case 1: {
                entrant.setName(Cleanup.name(raw));
                break;
            }
            case 2: {
                entrant.setScore(Cleanup.score(raw));
                break;
            }
            case 3: {
                entrant.setCertificate(Cleanup.certificate(raw));
                break;
            }
            case 4: {
                entrant.setZno(Cleanup.zno(raw));
                break;
            }
            case 5: {
                entrant.setExam(Cleanup.exam(raw));
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
}
