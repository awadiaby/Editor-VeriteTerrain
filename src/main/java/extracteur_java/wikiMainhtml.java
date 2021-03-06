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

public class wikiMainhtml {


    /**
     * mise en place des loggers
     *
     * @param args
     * @throws IOException
     */

    private static Logger logger = Logger.getLogger("wikiMainhtml.class");

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
        WikipediaMatrix wiki = new WikipediaMatrix(ExtractType.HTML);

        Set<UrlMatrix> urlMatrixSet;

        File directory = new File("src/main/output");
        if (!directory.exists() || !directory.isDirectory()) {
            logger.log(Level.INFO, "bad destination path");
            System.exit(0);
        }
        File htmlDir = new File(directory.getAbsoluteFile() + "" + File.separator + "html");
        String url;
        String csvFileName;
        htmlDir.mkdir();


        //stat before extraction
        logger.log(Level.INFO, "entering of the function which find tables by criteria");
        logger.log(Level.INFO, "Loading..........");
        logger.log(Level.INFO, "end of searching table by criteria");

        // Html extractionnow it's our turn to improve their work
        wiki.setUrlsMatrix(getUrl(csvUrl));
        wiki.setExtractType(ExtractType.HTML);
        logger.log(Level.INFO, "Extracting via html...");
        //System.out.println("Extracting via html...");

        urlMatrixSet = wiki.getConvertResult();


        //save files
        long execHtml = System.currentTimeMillis();//to measure time of execution
        ArrayList<Integer> extractedHTML = new ArrayList<Integer>();
        ArrayList<String> urls = new ArrayList<String>();

        int numberFileHtml = 0; //Creation of the variable which contains the number of files
        for (UrlMatrix urlMatrix : urlMatrixSet) {
            int i = 0;
            url = urlMatrix.getLink();
            urls.add(url);
            System.out.println("Url :" + url);

            Set<FileMatrix> fileMatrices = urlMatrix.getFileMatrix();
            for (FileMatrix f : fileMatrices) {
                //extraction des tableaux de type html au format csv
                csvFileName = mkCSVFileName(url.substring(url.lastIndexOf("/") + 1, url.length()), i);
                f.saveCsv(htmlDir.getAbsolutePath() + File.separator + csvFileName);
                i++;
            }
            extractedHTML.add(i);
            numberFileHtml += i; //Number of files = current value of i
        }

        // Save information in files
        BufferedWriter writerFile = new BufferedWriter(new FileWriter("numberFilehtml.txt"));
        writerFile.write("" + numberFileHtml);

        BufferedWriter writerTime = new BufferedWriter(new FileWriter("temps-execution-html.txt"));
        writerTime.write("" + (System.currentTimeMillis() - execHtml));
        writerFile.close();
        writerTime.close();

        System.out.println("Extractor HTML created " + numberFileHtml + " files.");//affichage du nombre de tableaux extraits
        System.out.println("Temps d'exécution = " + (System.currentTimeMillis() - execHtml) + " ms");


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
//        wikiMainhtml wikimain = new wikiMainhtml();
//        wikimain.extracteurenmarche();


    }
}

