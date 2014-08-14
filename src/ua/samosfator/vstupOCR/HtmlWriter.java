package ua.samosfator.vstupOCR;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HtmlWriter {
    private static String html = "";
    private static final String HEAD = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; " +
            "charset=windows-1251\"><title></title><meta name=\"copyright\" content=\"© vstup.info 2014\">" +
            "<link rel=\"stylesheet\" href=\"http://vstup.info/i/s.css\" type=\"text/css\"><link rel=\"style" +
            "sheet\" href=\"http://vstup.info/i/t.css\" type=\"text/css\" media=\"screen\"><link rel=\"icon\"" +
            " href=\"/i/i.png\" type=\"image/x-icon\"></head>";
    private static final String BEFORE_TR = "<body class=\"menu_curr_year\"><div id=\"pa\"><div id=\"h\"><center>" +
            "</center><div id=\"tb\" class=\"cb\"><div class=\"bt\"><div></div></div><div class=\"i1\"><div class=" +
            "\"i2\"><div class=\"i3\"><div id=\"tl\"><h1><a href=\"/\">Інформаційна система \"Конкурс\"</a></h1><s" +
            "pan id=\"d\">Вступ до вищих навчальних закладів України I-IV рівнів акредитації</span></div></div></d" +
            "iv></div><div class=\"bb\"><div></div></div></div></div><div id=\"pm\"><div id=\"mc\"><div class=\"mo" +
            "dule\"><div class=\"cb\"><div class=\"bt mt\"><div></div></div><div class=\"i1 ml\"><div class=\"i2 m" +
            "r\"><div class=\"i3 mm\"><h2>ІС \"Конкурс\"</h2><ul><li><a id=\"menu_curr_year\" href=\"/2014/\">2014" +
            " рік</a></li><li><a id=\"menu_2013\" href=\"/2013/\">2013 рік</a></li><li><a id=\"menu_2012\" href=\"" +
            "/2012/\">2012 рік</a></li><li><a id=\"menu_2011\" href=\"/2011/\">2011 рік</a></li><li><a id=\"menu_2" +
            "010\" href=\"/2010/\">2010 рік</a></li><li><a id=\"menu_2009\" href=\"/2009/\">2009 рік</a></li><li><" +
            "a id=\"menu_2008\" href=\"/i2008.html\">2008 рік</a></li></ul></div></div></div></div><div class=\"b" +
            "b mb\"><div></div></div></div><div class=\"module\"><div class=\"cb\"><div class=\"bt mt\"><div></di" +
            "v></div><div class=\"i1 ml\"><div class=\"i2 mr\"><div class=\"i3 mm\"><h2>Партнери</h2><ul><li><a i" +
            "d=\"link\" title=\"МОН України\" target=\"_blank\" href=\"/out/MON\">МОН України</a></li><li><a id=\"" +
            "link\" title=\"УЦОЯО\" target=\"_blank\" href=\"/out/TST\">УЦОЯО</a></li><li><a id=\"link\" title=\"USE" +
            "TI Alliance\" target=\"_blank\" href=\"/out/USETI\">USETI Alliance</a></li><li><a id=\"link\" title=\"" +
            "Право та освіта\" target=\"_blank\" href=\"/out/LAW\">Право та освіта</a></li></ul></div></div></div><" +
            "/div><div class=\"bb mb\"><div></div></div></div><div class=\"module\"><div class=\"cb\"><div class=\"" +
            "bt mt\"><div></div></div><div class=\"i1 ml\"><div class=\"i2 mr\"><div class=\"i3 mm\"><h2>Посилання<" +
            "/h2><ul><li><p>Тут може бути ваше посилання</p></li></ul></div></div></div></div><div class=\"bb mb\">" +
            "<div></div></div></div></div><div id=\"ca\"><div class=\"cb\"><div class=\"bt\"><div></div></div><div " +
            "class=\"i1\"><div class=\"i2\"><div class=\"i3\"><div id=\"c\"><div class=\"row\"><div class=\"col\"><c" +
            "enter><table class=\"striped\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><thead><tr" +
            "><td><div><b>#</b></div></td><td title=\"Прізвище, ім'я, по-батькові абітурієнта\"><div><b>ПІБ</b></div>" +
            "</td><td title=\"Сума всіх балів\"><div><b>Σ</b></div></td><td title=\"Середній бал документа про освіту" +
            "\"><div><b>С</b></div></td><td title=\"Бали сертифікатів ЗНО\"><div><b>ЗНО</b></div></td><td title=\"Ба" +
            "ли екзаменів\"><div><b>Е</b></div></td><td title=\"Додаткові бали за диплом Всеукраїнської олімпіади аб" +
            "о за диплом конкурсу МАН\"><div><b>О</b></div></td><td itle=\"Додаткові бали для випускника підготовчог" +
            "о відділення\"><div><b>П</b></div></td><td title=\"Право на позаконкурсний вступ\"><div><b>ПК</b></div" +
            "></td><td title=\"Право на першочерговий вступ\"><div><b>ПЧ</b></div></td><td title=\"Цільове направлен" +
            "ня\"><div><b>Ц</b></div></td><td title=\"Оригінали до" +
            "кументів\"><div><b>Д</b></div></td></tr></thead><tbody>";
    private static final String AFTER_TR = "</tbody><thead><tr><td><div><b>#</b></div></td><td title=\"Прізвище, ім'я," +
            " по-батькові абітурієнта\"><div><b>ПІБ</b></div></td><td title=\"Сума всіх балів\"><div><b>Σ</b></div>" +
            "</td><td title=\"Середній бал документа про освіту\"><div><b>С</b></div></td><td title=\"Бали сертифікат" +
            "ів ЗНО\"><div><b>ЗНО</b></div></td><td title=\"Бали екзаменів\"><div><b>Е</b></div></td><td title=\"Дода" +
            "ткові бали за диплом Всеукраїнської олімпіади або за диплом конкурсу МАН\"><div><b>О</b></div></td><td t" +
            "itle=\"Додаткові бали для випускника підготовчого відділення\"><div><b>П</b></div></td><td title=\"Право" +
            " на позаконкурсний вступ\"><div><b>ПК</b></div></td><td title=\"Право на першочерговий вступ\"><div><b>П" +
            "Ч</b></div></td><td title=\"Цільове направлення\"><div><b>Ц</b></div></td><td title=\"Оригінали документ" +
            "ів\"><div><b>Д</b></div></td></tr></thead><tbody><tr><td colspan=\"12\"><div><b>Умовні позначення:</b></" +
            "div></td></tr><tr><td colspan=\"2\"><div><b>#</b></div></td><td colspan=\"10\">Порядковий номер у рейтин" +
            "говому списку</td></tr><tr><td colspan=\"2\"><div><b>ПІБ</b></div></td><td colspan=\"10\">Прізвище, ім'я," +
            " по-батькові абітурієнта</td></tr><tr><td colspan=\"2\"><div><b>Σ</b></div></td><td colspan=\"10\">Сума " +
            "всіх балів</td></tr><tr><td colspan=\"2\"><div><b>С</b></div></td><td colspan=\"10\">Середній бал докуме" +
            "нта про освіту</td></tr><tr><td colspan=\"2\"><div><b>ЗНО</b></div></td><td colspan=\"10\">Бали сертифік" +
            "атів ЗНО</td></tr><tr><td colspan=\"2\"><div><b>Е</b></div></td><td colspan=\"10\">Бали екзаменів</td></t" +
            "r><tr><td colspan=\"2\"><div><b>О</b></div></td><td colspan=\"10\">Додаткові бали за диплом Всеукраїнсько" +
            "ї олімпіади або за диплом конкурсу МАН</td></tr><tr><td colspan=\"2\"><div><b>П</b></div></td><td colspan" +
            "=\"10\">Додаткові бали для випускника факультету довузівської підготовки</td></tr><tr><td colspan=\"2\"><" +
            "div><b>ПК</b></div></td><td colspan=\"10\">Право на позаконкурсний вступ</td></tr><tr><td colspan=\"2\"><d" +
            "iv><b>ПЧ</b></div></td><td colspan=\"10\">Право на першочерговий вступ</td></tr><tr><td colspan=\"2\"><div" +
            "><b>Ц</b></div></td><td colspan=\"10\">Цільове направлення</td></tr><tr><td colspan=\"2\"><div><b>Д</b></d" +
            "iv></td><td colspan=\"10\">Оригінали документів</td></tr><tr><td colspan=\"2\"><div><b style=\"background:" +
            "lightgreen\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</b></div>" +
            "</td><td colspan=\"10\">Рекомендовано до зарахування</td></tr><tr><td colspan=\"2\"><div><b style=\"backgro" +
            "und:#b4caeb\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</b></div>" +
            "</td><td colspan=\"10\">Зараховано</td></tr></tbody></table></center></div></div></div></div></div></div><d" +
            "iv class=\"bb\"><div></div></div></div></div></div></div><div id=\"f\">© vstup.info, Міністерство освіти і " +
            "науки України, ГО \"Центр освітньої політики\", 2014<br>Дозволено копіювання та розміщення лише на сайтах В" +
            "НЗ матеріалів сайту vstup.info без отримання згоди ГО \"Центр освітньої політики\"<br>Будь-які інші копіюва" +
            "ння та розміщення на інших сайтах матеріалів сайту vstup.info без отримання згоди ГО \"Центр освітньої полі" +
            "тики\" заборонені<br>ІС \"Конкурс\" адмініструється ГО \"Центр освітньої політики\" в рамках діяльності про" +
            "екту міжнародної технічної допомоги<br>\"Альянс програми сприяння зовнішньому тестуванню в Україні (USETI L" +
            "egacy Alliance) за підтримки Агентства з міжнародного розвитку США (USAID)<br>ІС \"Конкурс\" функціонує від" +
            "повідно до Наказу МОН України № 514 від 11 червня 2008 року<br>Ідея розробки належить Ярославу БОЛЮБАШУ</di" +
            "v></body>";

    private static final String ACCEPTED_COLOR = "#b4caeb";
    private static final String RECOMMENDED_COLOR = "lightgreen";
    private static final String WHITE_COLOR = "#fff";

    private static boolean ENTRANT_RECOMMENDED;
    private static boolean ENTRANT_ACCEPTED;

    public static void createHtml(List<Entrant> entrants, String filename) {
        html += HEAD + BEFORE_TR;
        for (Entrant entrant : entrants) {
            ENTRANT_RECOMMENDED = entrant.isRecommended();
            ENTRANT_ACCEPTED = entrant.isAccepted();
            html += "<tr>";
            insertTD(String.valueOf(entrant.getNumber()));
            insertTD(entrant.getName());
            insertTD(entrant.getScore());
            insertTD(entrant.getCertificate());
            insertTD(entrant.getZno());
            insertTD(entrant.getExam());
            insertTD(entrant.getOlympiad());
            insertTD(entrant.getExtraPoints());
            insertTD(entrant.getPk());
            insertTD(entrant.getP4());
            insertTD(entrant.getTarget());
            insertTD(entrant.getOriginals());
            html += "</tr>";
        }
        html += AFTER_TR;
        try {
            writeHtml(html, filename);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void insertTD(String text) {
        html += "<td style=\"background:";
        if (ENTRANT_RECOMMENDED) {
            html += RECOMMENDED_COLOR;
        } else if (ENTRANT_ACCEPTED) {
            html += ACCEPTED_COLOR;
        } else html += WHITE_COLOR;
        html += "\">" + text + "</td>";
    }

    private static void writeHtml(String html, String name) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw = new PrintWriter(name, "CP1251");
        pw.println(html);
        pw.flush();
        pw.close();
    }
}
