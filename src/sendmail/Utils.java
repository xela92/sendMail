/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendmail;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author xela92
 */
public class Utils {

    public static int buildVersion = 4;

    public static PrintStream fileLogWriter;

    public static void logErrorsToFile() {

        try {
            String dir = Log.getDefaultLogPath();
            if (Log.getLogFile() != null) {
                dir = Log.getLogDirectoryPath();
            }
            File mailLog = new File(dir + "/mail_errors.log");
            long sizeInBytes = mailLog.length();
            //transform in MB
            long sizeInMb = sizeInBytes / (1024 * 1024);
            if (sizeInMb > 10) {
                mailLog.delete();
            }
            //System.setErr(new PrintStream(paybackErrorLog.getAbsolutePath()));
            fileLogWriter = new PrintStream(mailLog.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void sendError(String message) {
        System.err.println(message);
        if (fileLogWriter != null) {
            fileLogWriter.println(message);
        }
    }

    static String getConfigurationDir() {
        return System.getProperty("user.home");
    }

    public static String getOs() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "win";
        } else if (os.contains("mac")) {
            return "mac";
        } else {

            return "lin";
        }
    }
}
