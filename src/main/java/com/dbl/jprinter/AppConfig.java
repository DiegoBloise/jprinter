package com.dbl.jprinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AppConfig {

    private static final String FILE_PATH = "config.txt";


    public static void createConfigFile() {
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


    public static void saveLastPrinter(String printerName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(printerName);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao salvar a impressora selecionada: " + e.getMessage());
        }
    }


    public static String loadLastPrinter() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String printerName = reader.readLine();
            return printerName != null ? printerName : "";
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a impressora selecionada: " + e.getMessage());
            return null;
        }
    }
}
