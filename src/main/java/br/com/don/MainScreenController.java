package br.com.don;

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

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

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

public class MainScreenController implements Initializable {

    private static String printFolder;
    private WatchService watchService;
    private Thread monitorThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> printerNames = FXCollections.observableArrayList(getAvailablePrinters());
        printerChoiceBox.setItems(printerNames);
        printerChoiceBox.setValue(printerNames.get(0));
        folderPathField.setText(getDownloadFolderPath());
        printFolder = getDownloadFolderPath();
    }
    
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
    private Circle statusIndicator;

    @FXML
    public void changeMonitoringState(ActionEvent event) {
        // Define o evento de clique do botão "Iniciar Monitoramento"
        if (monitorThread != null && monitorThread.isAlive()) {
            // Se o monitoramento estiver em andamento, para o monitoramento
            stopMonitoring();
        } else {
            // Caso contrário, inicia o monitoramento
            if (printFolder != null) {
                try {
                    startMonitoring(printerChoiceBox.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                    App.trayIcon.showErrorMessage("Erro ao iniciar serviço.", e.getMessage());
                }
            }
        }
    }


    @FXML
    void chooseFolder(ActionEvent event) {
        // Define o evento de clique do botão "Escolher Pasta"
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            folderPathField.setText(selectedDirectory.getAbsolutePath());
            printFolder = selectedDirectory.getAbsolutePath();
        }
    }

    // Obtém a lista de impressoras disponíveis
    private List<String> getAvailablePrinters() {
        ObservableList<Printer> printers = FXCollections.observableArrayList();

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            printers.addAll(Printer.getAllPrinters());
            printerJob.endJob();
        }

        // Converter a ObservableList<Printer> para uma List<String>
        List<String> printerNames = new ArrayList<>();
        for (Printer printer : printers) {
            printerNames.add(printer.getName());
        }

        return printerNames;
    }

    // Inicia o monitoramento da pasta
    private void startMonitoring(String selectedPrinter) throws IOException {
        // Cria um WatchService para monitorar a pasta
        watchService = FileSystems.getDefault().newWatchService();

        // Registra a pasta para eventos de criação de arquivos
        Path folderPath = Paths.get(printFolder);
        folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        // Inicia um thread para ficar observando a pasta
        monitorThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take(); // Aguarda um evento de criação de arquivo

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // Verifica se o evento é de criação de arquivo
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path filePath = folderPath.resolve((Path) event.context());

                            // Verifica se o arquivo é um arquivo de texto
                            if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".txt")) {
                                // Lê o conteúdo do arquivo
                                List<String> lines = Files.readAllLines(filePath);

                                // Aqui você pode extrair os dados do arquivo (nome, valor, data) e enviá-los para impressão
                                // Exemplo de extração de dados simples
                                String name = lines.get(0);
                                String value = lines.get(1);
                                String date = lines.get(2);

                                // Imprime os dados
                                printData(name, value, date, selectedPrinter);
                                App.trayIcon.showInfoMessage("Imprimindo...");

                                // Apaga o arquivo depois de impresso (opcional)
                                Files.delete(filePath);
                            }
                        }
                    }

                    // Reseta o watch key
                    watchKey.reset();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                App.trayIcon.showErrorMessage("Erro ao ler arquivo", e.getMessage());
                stopMonitoring();
            }
        });

        // Inicia o thread de monitoramento
        monitorThread.start();
        
        chooseFolderButton.setDisable(true);
        folderPathField.setDisable(true);
        
        startButton.setText("Parar Monitoramento");
        statusIndicator.setFill(Color.GREEN);

        App.trayIcon.setGraphic(App.class.getResource("/icons/on.png"));
        App.trayIcon.showInfoMessage("Serviço iniciado...");
    }

    // Para o monitoramento
    private void stopMonitoring() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                e.printStackTrace();
                App.trayIcon.showErrorMessage("Erro ao encerrar serviço.", e.getMessage());
            }
        }

        if (monitorThread != null) {
            monitorThread.interrupt();
        }

        chooseFolderButton.setDisable(false);
        folderPathField.setDisable(false);

        startButton.setText("Iniciar Monitoramento");
        statusIndicator.setFill(Color.RED);

        App.trayIcon.setGraphic(App.class.getResource("/icons/off.png"));
        App.trayIcon.showInfoMessage("Serviço encerrado...");
    }

    public static String getDownloadFolderPath() {
        String userHome = System.getProperty("user.home");
        String downloadFolder = userHome + "\\Downloads";
        return downloadFolder;
    }
    
    // Método para imprimir os dados
    private void printData(String name, String value, String date, String selectedPrinter) {
        PrintService printService = null;
        
        PrintService[] availablePrinters = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : availablePrinters) {
            if (printer.getName().equals(selectedPrinter)) {
                printService = printer;
            }
        }

        if (printService == null) {
            System.out.println("Impressora não encontrada.");
            App.trayIcon.showErrorMessage("Erro ao imprimir.", "Impressora não encontrada.");
            return;
        }

        // Texto a ser impresso
        String text = "Exemplo de texto para impressão";

        // Criar o conjunto de atributos de impressão
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        //attributes.add(new Copies(1));  // Definir o número de cópias

        // Criar o documento a ser impresso
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc document = new SimpleDoc(text.getBytes(), flavor, null);

        // Criar a tarefa de impressão
        DocPrintJob task = printService.createPrintJob();

        try {
            // Enviar o documento para impressão
            task.print(document, attributes);
        } catch (PrintException e) {
            e.printStackTrace();
            App.trayIcon.showErrorMessage("Erro ao imprimir.", e.getMessage());
        }
    }
}
