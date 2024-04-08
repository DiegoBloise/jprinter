package com.dbl.jprinter;

import java.io.File;
import java.io.IOException;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;


public class App extends Application {

    private static Scene scene;
    public static FXTrayIcon trayIcon;

    private static MainScreenController msc;


    @Override
    public void start(Stage primaryStage) throws IOException {
        // Verifica se o arquivo de configuração existe, se não existir, cria-o
        File configFile = new File("config.txt");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erro ao criar arquivo de configuração: " + e.getMessage());
            }
        }

        primaryStage.setTitle("Jprinter");
        primaryStage.setResizable(false);

        scene = new Scene(loadFXML("MainScreen"));
        msc = new MainScreenController();

        trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("/icons/off.png"));

        trayIcon.addExitItem("Sair", e -> msc.exitApplication());
        MenuItem settingsItem = new MenuItem("Configurações", null);

        settingsItem.setOnAction(e -> {
            primaryStage.show();
            primaryStage.toFront();
        });

        trayIcon.addMenuItem(settingsItem);

        trayIcon.show();

        primaryStage.setScene(scene);
        primaryStage.hide();
    }


    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }


    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch();
    }
}