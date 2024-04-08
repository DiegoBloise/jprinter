package com.dbl.jprinter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.naming.NameNotFoundException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.PrintModeStyle;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainScreenController implements Initializable {

    private static final String DEFAULT_FILE_EXTENSION = "djprt";
    private static String printFolder;
    private WatchService watchService;
    private Thread monitorThread;
    private boolean monitoring = false;
    private boolean hasConfig = false;

    @FXML
    private Button chooseFolderButton;

    @FXML
    private TextField folderPathField;

    @FXML
    private ChoiceBox<String> printerChoiceBox;

    @FXML
    private Label printerLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    private Circle statusIndicator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        folderPathField.setText(getDownloadFolderPath());
        printFolder = getDownloadFolderPath();

        ObservableList<String> printerNames = FXCollections.observableArrayList(getAvailablePrinters());
        printerChoiceBox.setItems(printerNames);

        String lastPrinter = AppConfig.loadLastPrinter();
        if (lastPrinter != null && printerNames.contains(lastPrinter)) {
            printerChoiceBox.setValue(lastPrinter);
            hasConfig = true;
        } else {
            printerChoiceBox.setValue(printerNames.get(0));
            hasConfig = false;
        }
    }


    @FXML
    public void changeMonitoringState(ActionEvent event) {
        System.out.println("CHANGING STATE");

        if (monitoring) {
            stopMonitoring();
        } else {
            if (printFolder != null) {
                try {
                    startMonitoring(printerChoiceBox.getValue());
                    AppConfig.saveLastPrinter(printerChoiceBox.getValue());
                } catch (Exception e) {
                    handleMonitoringError("Erro ao iniciar serviço.", e);
                }
            }
        }
    }


    private void handleMonitoringError(String title, Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
            App.trayIcon.showErrorMessage(title, e.getMessage());
            changeMonitoringState(null);
        });
    }


    private void handlePrintingError(String title, Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> App.trayIcon.showErrorMessage(title, e.getMessage()));
    }


    private void updateUI() {
        chooseFolderButton.setDisable(monitoring);
        folderPathField.setDisable(monitoring);
        printerChoiceBox.setDisable(monitoring);

        startButton.setText(monitoring ? "Parar Monitoramento" : "Iniciar Monitoramento");
        statusIndicator.setFill(monitoring ? Color.GREEN : Color.RED);

        App.trayIcon.setGraphic(App.class.getResource(monitoring ? "/icons/on.png" : "/icons/off.png"));
    }


    private void startMonitoring(String selectedPrinter) throws IOException {
        System.out.println("STARTING");

        monitoring = true;

        // Cria um WatchService para monitorar a pasta
        watchService = FileSystems.getDefault().newWatchService();

        // Registra a pasta para eventos de criação de arquivos
        Path folderPath = Paths.get(printFolder);
        folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        // Inicia um thread para ficar observando a pasta
        monitorThread = new Thread(() -> {
            try {
                while (monitoring) {
                    WatchKey watchKey = watchService.take(); // Aguarda um evento de criação de arquivo

                    for (WatchEvent<?> event : watchKey.pollEvents()) {

                        // Verifica se o evento é de criação de arquivo
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path filePath = folderPath.resolve((Path) event.context());

                            if (isApplicationFile(filePath)) {

                                processFileContents(filePath, selectedPrinter);

                                // Apaga o arquivo depois de impresso (opcional)
                                Files.delete(filePath);
                            }
                        }
                    }

                    // Reseta o watch key
                    watchKey.reset();
                }
            } catch (Exception e) {
                handleMonitoringError("Erro ao ler arquivo", e);
            }
        });

        // Inicia o thread de monitoramento
        monitorThread.start();

        updateUI();
    }


    private Boolean isApplicationFile(Path filePath) {
        return Files.isRegularFile(filePath) && filePath.toString().endsWith(DEFAULT_FILE_EXTENSION);
    }


    /* private void processFileContents(Path filePath, String selectedPrinter) throws IOException {
        List<String> lines = Files.readAllLines(filePath);

        // Extração dos dados do arquivo
        String nome = lines.get(0);
        String valor = lines.get(1);
        String data = lines.get(2);

        // Imprime os dados
        printData(nome, valor, data, selectedPrinter);
        System.out.println("IMPRIMINDO: " + nome + " - " + valor + " - " + data);
    } */


    private void processFileContents(Path filePath, String selectedPrinter) {
        int maxAttempts = 5;
        int attempt = 1;

        while (attempt <= maxAttempts) {
            try {
                if (Files.isReadable(filePath)) {
                    List<String> lines = Files.readAllLines(filePath);

                    if (lines.size() >= 3) {
                        String nome = lines.get(0);
                        String valor = lines.get(1);
                        String data = lines.get(2);


                        /* printData(nome, valor, data, selectedPrinter); */
                        System.out.println("IMPRIMINDO: " + nome + " - " + valor + " - " + data);
                        return;
                    } else {
                        System.out.println("O arquivo nao contem dados suficientes.");
                    }
                } else {
                    System.out.println("O arquivo nao esta acessivel. Tentativa " + attempt + "/" + maxAttempts);
                }

                attempt++;
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                handlePrintingError("Erro ao processar o arquivo: ", e);
            }
        }

        System.out.println("Nao foi possivel acessar o arquivo apos " + maxAttempts + " tentativas.");
    }


    private void stopMonitoring() {
        System.out.println("STOPING");

        monitoring = false;

        if (watchService != null) {
            try {
                watchService.close();
            } catch (Exception e) {
                handleMonitoringError("Erro ao encerrar serviço.", e);
            }
        }

        if (monitorThread != null) {
            monitorThread.interrupt();
        }

        updateUI();
    }


    @FXML
    void chooseFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            folderPathField.setText(selectedDirectory.getAbsolutePath());
            printFolder = selectedDirectory.getAbsolutePath();
        }
    }


    private List<String> getAvailablePrinters() {
        ObservableList<Printer> printers = FXCollections.observableArrayList();

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            printers.addAll(Printer.getAllPrinters());
            printerJob.endJob();
        }

        List<String> printerNames = new ArrayList<>();
        for (Printer printer : printers) {
            printerNames.add(printer.getName());
        }

        return printerNames;
    }


    public static String getDownloadFolderPath() {
        String userHome = System.getProperty("user.home");
        return userHome + "\\Downloads";
    }


    private void printData(String nome, String valor, String data, String selectedPrinter) throws IOException {
        PrintService printService = getPrintService(selectedPrinter);

        if (printService == null) {
            Exception e = new NameNotFoundException();
            handlePrintingError("Impressora não encontrada.", e);
            return;
        }


        PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
        EscPos escpos = new EscPos(printerOutputStream);

        try {

            PrintModeStyle normal = new PrintModeStyle();

            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._3)
                    .setJustification(EscPosConst.Justification.Center);

            /* Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick); */

            Style bold = new Style(escpos.getStyle())
                    .setBold(true);

            Style center = new Style()
                    .setJustification(EscPosConst.Justification.Center);


            escpos
                    .writeLF(title,"Recibo de Vale")

                    .feed(3)

                    .write(center, "Data: ")
                    .write(bold, data)

                    .feed(3)

                    .write(normal, "Colaborador:" + " ".repeat(36 - nome.length()))
                    .writeLF(bold, nome)
                    .writeLF("-".repeat(48))

                    .feed(2)

                    .write( "Valor:" + " ".repeat(39 - valor.length()))
                    .writeLF(bold, "R$ " + valor)
                    .writeLF("-".repeat(48))

                    .feed(2)

                    .writeLF(bold, "Assinatura:")
                    .writeLF("-".repeat(48))

                    .feed(7)

                    .cut(EscPos.CutMode.FULL);

            escpos.close();

            App.trayIcon.showInfoMessage("Impresso com sucesso");

        } catch (IOException e) {
            handlePrintingError("Erro ao imprimir.", e);
        }
    }


    private PrintService getPrintService(String selectedPrinter) {
        PrintService[] availablePrinters = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : availablePrinters) {
            if (printer.getName().equals(selectedPrinter)) {
                return printer;
            }
        }
        return null;
    }


    public void exitApplication() {
        if (monitoring) {
            stopMonitoring();
        }

        Platform.exit();
        System.exit(0);
    }


    public void start(Stage stage) {
        if (hasConfig) {
            stage.hide();
            changeMonitoringState(null);
            App.trayIcon.showInfoMessage("Serviço iniciado...");
        } else {
            stage.show();
        }
    }
}
