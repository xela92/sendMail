/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendmail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author xela92
 */
public class Log {

    private static File logFile = null;
    private String toBeLogged;

    public Log(String toBeLogged, File logFile) {
        this.logFile = logFile;
        this.toBeLogged = toBeLogged;
    }

    public Log(File logFile) {
        this.logFile = logFile;
    }

    public static String getCurrentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH.mm.ss");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateFormat.format(cal.getTime());
    }

    public static String getLogDirectoryPath() {
        return logFile != null ? logFile.getParent() : getDefaultLogPath();
    }
    public static File getLogFile() {
        return logFile;
    }
    public static String getLogFilePath() {
        return logFile != null ? logFile.getAbsolutePath() : getDefaultLogFilePath();
    }

    public static String getDefaultLogPath() {
        return System.getProperty("user.home").concat(File.separator);
    }

    public static String getDefaultLogFilePath() {
        return System.getProperty("user.home").concat(File.separator).concat("mail.log");
    }

    public void writeLog() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (logFile != null && toBeLogged != null) {
                        FileWriter fw = new FileWriter(logFile, true);
                        fw.append(Log.getCurrentDateAndTime() + ": " + toBeLogged + System.getProperty("line.separator"));
                        fw.flush();
                    } else {
                        Utils.sendError("Impostare file di log e contenuto prima!");
                    }
                } catch (IOException ex) {
                    Utils.sendError("Errore nella scrittura del file di log: " + ex.getLocalizedMessage());
                }

            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void setLogFile(File newValue) {
        this.logFile = newValue;
    }

    public void setToBeLogged(String newValue) {
        this.toBeLogged = newValue;
    }

    public File toFile() {
        return this.logFile;
    }

}
