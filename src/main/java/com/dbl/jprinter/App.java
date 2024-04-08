package com.dbl.jprinter;

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

        AppConfig.createConfigFile();
        trayIconSetup(stage);

        stage.setTitle("Jprinter");
        stage.setResizable(false);

        stage.setScene(scene);

        controller.start(stage);
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


    public static void main(String[] args) {
        launch();
    }
}