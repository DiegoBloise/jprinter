package com.dbl.jprinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class AppConfig {

    public static final String CONFIG_FOLDER_PATH = System.getenv("APPDATA") + "\\JPrinter";
    public static final String FILE_PATH = CONFIG_FOLDER_PATH + "\\config.txt";
    public static final String LOG_FILE_PATH = CONFIG_FOLDER_PATH + "\\log.txt";


    public static void setup() {
        createConfigFolder();
        createConfigFile();
        createLogFile();
    }


    public static void createConfigFolder() {
        File jPrinterDir = new File(CONFIG_FOLDER_PATH);
        if (!jPrinterDir.exists()) {
            try {
                jPrinterDir.mkdirs();
            } catch (Exception e) {
                Log.Error("Erro ao criar a pasta de configuracoes: ", e);
            }
        }
    }


    public static void createConfigFile() {
        File configFile = new File(FILE_PATH);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                Log.Error("Erro ao criar arquivo de configuracao: ", e);
            }
        }
    }


    public static void createLogFile() {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                Log.Error("Erro ao criar arquivo de log: ", e);
            }
        }
    }


    public static void saveLastPrinter(String printerName) {
        File configFile = new File(FILE_PATH);
        if (configFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                writer.write(printerName);
            } catch (Exception e) {
                Log.Error("Erro ao salvar a impressora selecionada: ", e);
            }
        }
    }


    public static String loadLastPrinter() {
        File configFile = new File(FILE_PATH);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String printerName = reader.readLine();
                return printerName != null ? printerName : "";
            } catch (Exception e) {
                Log.Error("Erro ao carregar a impressora selecionada: ", e);
                return null;
            }
        }
        return null;
    }
}
