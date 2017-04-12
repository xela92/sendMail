/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendmail;

import java.io.File;
import java.util.Locale;

/**
 *
 * @author xela92
 */
public class SendMail {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Localization.setLocale(Locale.getDefault());
        String usage = R.string("usage");
        if (args.length > 2 && args.length < 5) {
            String to = args[0];
            String subject = args[1];
            String message = args[2];
            File attach = null;
            if (args.length > 3) {
                attach = new File(args[3]);
            }

            if (to == null) {
                System.err.println(R.string("recipient_needed") + "\n" + usage);
                System.exit(0);
            }
            Mail m = new Mail(to, subject, message, attach);
            Utils.logErrorsToFile();
            m.sendMail();
        } else {
            System.err.println(usage);
            System.exit(0);
        }
    }

}
