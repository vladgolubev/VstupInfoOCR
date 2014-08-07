package ua.samosfator.vstupOCR;

import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;

public class Entrant {
    public static int n;
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
        n++;
        new Parser(this, row, tesseract).getEntrant();
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

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setZno(String zno) {
        this.zno = zno;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public void setOlympiad(String olympiad) {
        this.olympiad = olympiad;
    }

    public void setExtraPoints(String extraPoints) {
        this.extraPoints = extraPoints;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setP4(String p4) {
        this.p4 = p4;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setOriginals(String originals) {
        this.originals = originals;
    }

    public void setRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    @Override
    public String toString() {
        return number + " " + name + "" + score + " " + certificate + " " + zno + " " +
                exam + " " + olympiad + " " + extraPoints + " " + pk + " " + p4 + " " + target + " " + originals;
    }
}
