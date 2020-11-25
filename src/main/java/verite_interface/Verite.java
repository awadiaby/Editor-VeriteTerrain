package verite_interface;

import extracteur_java.wikiMainhtml;
import extracteur_java.wikiMainwikitext;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class Verite extends Application {
    TableView table = new TableView();
    private final ObservableList<Object> dataList
            = FXCollections.observableArrayList();
    int numeroTableau = 0;


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
        Label label2 = new Label("Entrer un numéro");
        TextField textField1 = new TextField();
        Button button1 = new Button("Valider");
        HBox urlBox = new HBox();
        urlBox.getChildren().addAll(label1, textField);
        HBox hBox = new HBox(comboBox);
        HBox hBox1 = new HBox(textField1);
        HBox hBox2 = new HBox(button1);

        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(""))
                numeroTableau = 0;
            else
                numeroTableau = Integer.parseInt(newValue);
        });

//        button1.setOnAction(e ->
//                {
//
//                    label2.setText("The name you entered is " + button1.getText());
//                }
//        );
        AtomicReference<String> url = new AtomicReference<>(textField.getText());
        comboBox.setOnAction(e ->
                {
                    switch (comboBox.getValue()) {
                        case "Extracteur Python":
                            url.set(textField.getText());
                            //showCSVTable(url.get());
                            //todo
                            break;
                        case "Extracteur Java Html":
                            url.set(textField.getText());
                            //verifier que contents contient un fichier qui commence par l'url
                            File directoryPath = new File("src/main/output/html");
                            //List of all files and directories
                            String[] contents = directoryPath.list();

                            if (textField1.getText().equals(""))
                                numeroTableau = 0;
                            else
                                numeroTableau = Integer.parseInt(textField1.getText());
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
                            url.set(textField.getText());
                            //verifier que contents contient un fichier qui commence par l'url
                            File directoryPath1 = new File("src/main/output/html");
                            //List of all files and directories
                            String[] contents1 = directoryPath1.list();

                            if (textField1.getText().equals(""))
                                numeroTableau = 0;
                            else
                                numeroTableau = Integer.parseInt(textField1.getText());
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

        VBox root = new VBox();
        root.getChildren().addAll(urlBox, hBox, table, hBox1, hBox2);
        root.setSpacing(20);
        Scene scene = new Scene(root, 450, 250);
        stage.setTitle("Verite Terrains");
        stage.setScene(scene);

        stage.show();

    }

    private void showCSVTable(String folder, String url) {
        ArrayList<String[]> csvLines = readCSV(folder, url);
        String[] columnsName = csvLines.get(0);

        //  table.getColumns().add(new TableColumn<>("Numero"));
        for (int i = 0; i < columnsName.length; i++) {
            TableColumn col = new TableColumn<>(columnsName[i]);
            int finalI = i;
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>,
                    ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(finalI)));
            table.getColumns().add(col);
        }


        for (int i = 1; i < csvLines.size(); i++) {
            //Iterate Row
            String[] rows = csvLines.get(i);
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int j = 0; j < columnsName.length; j++) {
                //Iterate Column
                row.add(rows[j]);
            }
            dataList.add(row);
        }


        table.setItems(dataList);
    }

    private ArrayList<String[]> readCSV(String folder, String csvFileName) {
        ArrayList<String[]> lines = new ArrayList<>();
        String CsvFile = "src/main/output/" + folder + "/" + csvFileName;
        String FieldDelimiter = ",";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(CsvFile));
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
