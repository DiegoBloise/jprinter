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

    private Scene scene;
    private Parent root;
    private FXMLLoader loader;

    private MainScreenController controller;

    public static FXTrayIcon trayIcon;

    @Override
    public void start(Stage stage) throws IOException {
        loader = loadFXML("MainScreen");
        root = loader.load();
        scene = new Scene(root);
        controller = loader.getController();

        userSettingsSetup();
        trayIconSetup(stage);

        stage.setTitle("Jprinter");
        stage.setResizable(false);

        stage.setScene(scene);
        stage.hide();

        if (controller.isDoidera()) {
            controller.changeMonitoringState(null);
        }
    }


    public void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml).load());
    }


    private FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader;
    }


    public void trayIconSetup(Stage stage) {

        trayIcon = new FXTrayIcon(stage, getClass().getResource("/icons/off.png"));

        trayIcon.addExitItem("Sair", e -> controller.exitApplication());
        MenuItem settingsItem = new MenuItem("Configurações", null);

        settingsItem.setOnAction(e -> {
            stage.show();
            stage.toFront();
        });

        trayIcon.addMenuItem(settingsItem);

        trayIcon.show();
    }


    public void userSettingsSetup() {
        File configFile = new File("config.txt");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erro ao criar arquivo de configuração: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        launch();
    }
}