package verite_interface;

import extracteur_java.wikiMainhtml;
import extracteur_java.wikiMainwikitext;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


public class Verite extends Application {
    private TableView<Object> table = new TableView();
    private final ObservableList<Object> dataList = FXCollections.observableArrayList();
    int numeroTableau = 0;
    Scene scene;
    Stage stage;
    JTable table_;
    JPanel conteneurTableau;
    ScrollPane scrollPane;
    String[][] contentTable;
    String[] columnNames;
    SwingNode swingNode;
    String savedFileName = "";
    String typeExtractor = "";

    int nombreMaxTableau = 0;
    int oldNumber = 0;




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label label1 = new Label("Enter_URL");
        TextField textField = new TextField();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add("Extracteur Python");
        comboBox.getItems().add("Extracteur Java Html");
        comboBox.getItems().add("Extracteur Java Wikitext");
        TextField textNumeroTableau = new TextField();
        Button valider = new Button("Valider");
        HBox urlBox = new HBox();
        urlBox.getChildren().addAll(label1, textField);
        HBox hBox = new HBox(comboBox);
        HBox hBox1 = new HBox(textNumeroTableau);
        HBox boxBoutonValid = new HBox(valider);


        textNumeroTableau.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.equals("")){
                numeroTableau = 0;
            }
            else {
                try {
                    numeroTableau = Integer.parseInt(newValue);
                } catch(Exception e) {
                    numeroTableau = 0;
                }
            }
            savedFileName = savedFileName.replace(oldNumber+"",numeroTableau+"");


            ArrayList<String[]> csvLines = readCSV(typeExtractor, savedFileName);
            columnNames = csvLines.get(0);
            contentTable = new String[csvLines.size()-1][];
            for (int i = 1; i < csvLines.size(); i++) {
                contentTable[i-1] = csvLines.get(i);
            }

            table_ = new JTable(contentTable,columnNames);
            conteneurTableau.paint(null);
            conteneurTableau.add(new JScrollPane(table_), BorderLayout.CENTER);
            createSwingContent(swingNode);

            System.out.println(numeroTableau+" : ("+columnNames.length+","+contentTable.length+")");

            oldNumber = numeroTableau;


        });


        AtomicReference<String> url = new AtomicReference<>(textField.getText());
        comboBox.setOnAction(e ->
                {
                    switch (comboBox.getValue()) {
                        case "Extracteur Python":
                            typeExtractor = " ";
                            url.set(textField.getText());
                            break;
                        case "Extracteur Java Html":
                            typeExtractor = "html";
                            url.set(textField.getText());
                            //verifier que contents contient un fichier qui commence par l'url
                            File directoryPath = new File("src/main/output/html");
                            //List of all files and directories
                            String[] contents = directoryPath.list();

                            if (textNumeroTableau.getText().equals(""))
                                numeroTableau = 0;
                            else
                                numeroTableau = Integer.parseInt(textNumeroTableau.getText());
                            boolean fileInFolder = false;
                            if (contents != null) {
                                for (String filename : contents) {
                                    if (filename.startsWith(textField.getText())) {
                                        fileInFolder = true;
                                        break;
                                    }
                                }
                            }
                            if (fileInFolder) {
                                showCSVTable("html", textField.getText() + "-" + numeroTableau + ".csv");
                            } else {
                                wikiMainhtml wikimain = new wikiMainhtml();
                                try {
                                    wikimain.extracteurenmarche(url.get());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                showCSVTable("html", textField.getText() + "-" + numeroTableau + ".csv");
                            }

                            break;
                        case "Extracteur Java Wikitext":
                            typeExtractor = "wikitext";
                            url.set(textField.getText());
                            File directoryPath1 = new File("src/main/output/html");
                            //List of all files and directories
                            String[] contents1 = directoryPath1.list();

                            if (textNumeroTableau.getText().equals(""))
                                numeroTableau = 0;
                            else
                                numeroTableau = Integer.parseInt(textNumeroTableau.getText());
                            boolean fileInFolder1 = false;
                            if (contents1 != null) {
                                for (String filename : contents1) {
                                    if (filename.startsWith(textField.getText())) {
                                        fileInFolder1 = true;
                                        break;
                                    }
                                }
                            }
                            if (fileInFolder1) {
                                showCSVTable("wikitext", textField.getText() + "-" + numeroTableau + ".csv");
                            } else {
                                wikiMainwikitext wikimain = new wikiMainwikitext();
                                try {
                                    wikimain.extracteurenmarche(url.get());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                showCSVTable("wikitext", textField.getText() + "-" + numeroTableau + ".csv");
                            }

                            break;
                    }
                }
        );


        //avant d'appeler il faut appeler les extracteurs d'abord
        valider.setOnAction(e->{
//            System.out.println(contentTable);
            for(String[] ligne:contentTable){
                for(String cell:ligne)
                    System.out.print(cell +" ");
            System.out.println();}

            save(savedFileName);
        });

        swingNode = new SwingNode();
        VBox root = new VBox();
        root.getChildren().addAll(urlBox, hBox, boxBoutonValid, swingNode, hBox1);

        scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(root);

        root.setSpacing(20);
        Scene scene = new Scene(scrollPane, 500, 400);
        this.scene = scene;
        this.stage = stage;
        stage.setTitle("Verite Terrains");
        stage.setScene(scene);
        stage.show();
    }


    private void showCSVTable(String folder, String url) {
        savedFileName = url;
        ArrayList<String[]> csvLines = readCSV(folder, url);
        columnNames = csvLines.get(0);

        contentTable = new String[csvLines.size()-1][];

        for (int i = 1; i < csvLines.size(); i++) {
            contentTable[i-1] = csvLines.get(i);

        }

        table_ = new JTable(contentTable,columnNames);
        conteneurTableau = new JPanel();
        conteneurTableau.setLayout(new BorderLayout());
        conteneurTableau.add(new JScrollPane(table_), BorderLayout.CENTER);
        createSwingContent(swingNode);
    }

    private void createSwingContent(SwingNode swingNode){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel panel = new JPanel();
                panel.add(table_);
                swingNode.setContent(panel);
            }
        });
    }

    private void save(String fileName){
        System.out.println("file contents are saved in "+fileName);


        String outputFileName = "src/verites/"+fileName;


        try {
            String contenu = String.join(",", Arrays.<String>asList(columnNames));
            for(String[] content: contentTable){
                contenu += "\r\n"+String.join(",", Arrays.<String>asList(content));
            }
            System.out.println("columnsName "+columnNames.length);
            System.out.println("content "+contentTable.length);
            System.out.println("DELIMITER "+contenu);

            FileWriter writer = new FileWriter(outputFileName, false);
            writer.write(contenu);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private ArrayList<String[]> readCSV(String folder, String csvFileName) {
        ArrayList<String[]> lines = new ArrayList<>();
        String csvFile = "src/main/output/" + folder + "/" + csvFileName;
        System.out.println(csvFile);

        String FieldDelimiter = ",";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(FieldDelimiter, -1);
                lines.add(fields);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }
}
