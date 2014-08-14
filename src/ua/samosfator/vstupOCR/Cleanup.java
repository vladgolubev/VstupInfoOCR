package ua.samosfator.vstupOCR;

public class Cleanup {
    public static String basicCleaning(String raw) {
        return raw.replaceAll("\n", " ").replaceAll("\\s{2,}", " ");
    }

    public static String name(String raw) {
        String name = raw.replace("йвна", "іївна").replace(".п", "Л").replaceAll("\\.", "");
        name = checkForApostrophe(name);
        name = checkForDmytrovych(name);
        return name;
    }

    public static String score(String raw) {
        raw = raw.replaceAll("-", "").replaceAll("б", "6").replaceAll(" ", "");
        return validateScore(raw);
    }

    public static String certificate(String raw) {
        return raw.replaceAll("-", "").replaceAll(" ", "").replaceAll("б", "6");
    }

    public static String zno(String raw) {
        String zno = validateZNO(raw);
        String[] arr = zno.split(" ");
        zno = "";
        for (int i1 = 0; i1 < arr.length; i1++) {
            if (arr[i1].contains("б")) arr[i1] = arr[i1].replace("б", "6");
            zno += arr[i1] + " ";
        }
        return zno;
    }

    public static String exam(String raw) {
        String exam;
        if (raw.length() < 5) {
            exam = raw.replace("Г", "-");
        } else {
            exam = raw;
        }
        return exam;
    }

    public static String insertDot(String str) {
        String wholeNumber = str.substring(0, 3);
        String fraction = str.substring(3, str.length());
        return wholeNumber + "." + fraction;
    }

    public static String checkForApostrophe(String raw) {
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

    public static String checkForDmytrovych(String raw) {
        String[] name = raw.split(" ");
        if (new Character(name[2].charAt(0)).isLetter('Д') && raw.endsWith("трович")) {
            return name[0] + " " + name[1] + "Дмитрович";
        } else return raw;
    }

    public static String insertApostrophe(String str, int x, int y) {
        String firstPart = str.substring(0, x);
        String secondPart = str.substring(y, str.length());
        return firstPart + "'" + secondPart;
    }

    public static String validateScore(String raw) {
        if (!raw.contains(".")) {
            raw = insertDot(raw);
        } else if (raw.contains(",")) {
            raw = insertDot(raw.replace(",", ""));
        } else return raw;
        return raw;
    }

    public static String validateZNO(String raw) {
        return raw.replace("Укрм", "Укр.м.").replace("Укрм.", "Укр.м.").replace("Укр.м..", "Укр.м.")
                .replace("Англм.", "Англ.м.").replace("Англм", "Англ.м.").replace("О", "0")
                .replace("Роєм", "Рос.м").replace("Рослчп", "Рос.м");
    }
}
