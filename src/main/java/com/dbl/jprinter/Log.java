package com.dbl.jprinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {


    public static void Error(String title, Exception except) {

        String string = getDateTime() + " | [ERROR] - " + title + "\n" + except.getStackTrace();

        System.err.println(string);

        saveLog(string);

        /* Platform.runLater(() -> {
            App.trayIcon.showErrorMessage(title, e.getMessage());
        }); */
    }


    public static void Info(String title) {
        String string = getDateTime() + " | [INFO] - " + title;

        System.out.println(string);

        saveLog(string);
    }


    private static void saveLog(String string) {
        File logFile = new File(AppConfig.LOG_FILE_PATH);
        if (logFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(AppConfig.LOG_FILE_PATH, true))) {
                writer.append(string + System.lineSeparator());
            } catch (Exception e) {
                Log.Error("Erro ao registrar log no arquivo: ", e);
            }
        }
    }


    private static String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return formattedDateTime;
    }
}
