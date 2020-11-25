package extracteur_java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class wikiMainwikitext {

    /**
     * mise en place des loggers
     *
     * @param args
     * @throws IOException
     */

    private static Logger logger = Logger.getLogger("wikiMainwikitext.class");

    public static void loggerstart() {
        try {
            logger.setLevel(Level.ALL);
            logger.log(Level.INFO, "EXTRACTIONG OF TABLES FROM WIKIPEDIA PAGES");
            FileHandler fichier = new FileHandler("LoggerFile.xml");
            logger.addHandler(fichier);
        } catch (IOException e) {
            logger.log(Level.INFO, "logger did not load");
        }

    }

    public void extracteurenmarche(String csvUrl) throws IOException {

        loggerstart();
        WikipediaMatrix wiki = new WikipediaMatrix(ExtractType.WIKITEXT);
        Set<UrlMatrix> urlMatrixSet;
        File urlsFile = new File("src/main/output");
        if (!urlsFile.exists() && !urlsFile.isDirectory()) {
            logger.log(Level.INFO, "input file note found");
            System.exit(0);
        }

        File directory = new File("src/main/output");
        File wikitextDir = new File(directory.getAbsoluteFile() + "" + File.separator + "wikitext");
        String url;
        String csvFileName;

        wikitextDir.mkdir();

        // stat before extraction
        logger.log(Level.INFO, "entering of the function which find tables by criteria");
        logger.log(Level.INFO, "Loading..........");
        logger.log(Level.INFO, "end of searching table by criteria");

        // Html extractionnow it's our turn to improve their work
        wiki.setUrlsMatrix(getUrl(csvUrl));

        // save files
        // long execHtml = System.currentTimeMillis();//to measure time of execution
        // ArrayList<Integer> extractedHTML = new ArrayList<Integer>();
        ArrayList<String> urls = new ArrayList<String>();

        // int numberFileHtml = 0; //Creation of the variable which contains the number
        // of file

        // Wikitext extraction
        ArrayList<String> urlsWikitext = new ArrayList<String>();
        ArrayList<Integer> extractedWikitext = new ArrayList<Integer>();
        wiki.setUrlsMatrix(getUrl(csvUrl));
        wiki.setExtractType(ExtractType.WIKITEXT);
        logger.log(Level.INFO, "Extracting via wikitext...");
        // System.out.println("Extracting via wikitext...");
        urlMatrixSet = wiki.getConvertResult();
        // save files
        long execWiki = System.currentTimeMillis();// to measure time of execution
        int numberFileWiki = 0; // Creation of the variable which contains the number of files
        for (UrlMatrix urlMatrix : urlMatrixSet) {
            Set<FileMatrix> fileMatrices = urlMatrix.getFileMatrix();
            int i = 0;
            url = urlMatrix.getLink();
            // System.out.println(url);
            urlsWikitext.add(url);

            for (FileMatrix f : fileMatrices) {
                // extraction des tableaux de type wikitext en format csv
                csvFileName = mkCSVFileName(url.substring(url.lastIndexOf("/") + 1, url.length()), i);
                f.saveCsv(wikitextDir.getAbsolutePath() + File.separator + csvFileName);
                i++;
            }
            extractedWikitext.add(i);
            numberFileWiki += i; // Number of files = current value of i

        }

        // Save information in files
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("numberFileWiki.txt"));
        writerFile.write("" + numberFileWiki);

        BufferedWriter writerTime = new BufferedWriter(new FileWriter("temps-execution-wikitext.txt"));
        writerTime.write("" + (System.currentTimeMillis() - execWiki));
        writerFile.close();
        writerTime.close();

        System.out.println("Extractor Wikitext created " + numberFileWiki + " files.");// affichage du nombre de
        // tableaux extraits
        System.out.println("Temps d'exécution = " + (System.currentTimeMillis() - execWiki) + " ms");

    }

    public static Set<UrlMatrix> getUrl(String url) {
        try {
            Set<UrlMatrix> urlsMatrix = new HashSet<UrlMatrix>();
            String BASE_WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/";
            URL uneURL = null;
            String wurl;
            wurl = BASE_WIKIPEDIA_URL + url;
            uneURL = new URL(wurl);
            System.out.println(uneURL);
            HttpURLConnection connexion = (HttpURLConnection) uneURL.openConnection();
            if (connexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                urlsMatrix.add(new UrlMatrix(wurl));
            }

            logger.exiting(wikiMainhtml.class.getName(), "getListofUrls", urlsMatrix);
            return urlsMatrix;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERR_INTERNET_DISCONNECTED");
            return null;
        }
    }

    private static String mkCSVFileName(String url, int n) {
        return url.trim() + "-" + n + ".csv";
    }

    public static void main(String[] args) throws IOException {
//		wikiMainwikitext wikimainwikitext = new wikiMainwikitext();
//		wikimainwikitext.extracteurenmarche();

    }

}
