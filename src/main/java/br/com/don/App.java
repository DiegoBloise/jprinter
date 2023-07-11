package br.com.don;

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

    private MainScreenController msc;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Don Printer");
        primaryStage.setResizable(false);

        scene = new Scene(loadFXML("MainScreen"));

        trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("/icons/off.png"));
        
        trayIcon.addExitItem("Fechar");
        MenuItem monitoringItem = new MenuItem("Iniciar monitoramento", null);
        
        msc = new MainScreenController();

        monitoringItem.setOnAction(e -> msc.changeMonitoringState(e));
        trayIcon.addMenuItem(monitoringItem);
        
        trayIcon.show();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
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