/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendmail;

/**
 *
 * @author xela92
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author user
 */
public class Mail {

    private Properties conf = new Properties();
    private Properties props;
    private Session session;
    private String to, subject, message;
    private File attachment;
    private Log l;
    private String smtp;
    private String port;
    private boolean needsAuth;
    private String user;
    private String pass;
    private String sender_mail;
    private String sender_name;

    public Mail(String to, String subject, String message, File attach) {
        this.to = to;
        this.attachment = attach;
        this.subject = subject;
        this.message = message;
        l = new Log(new File(Log.getLogFilePath()));
        loadMailServerData();
    }

    public Mail() {
        loadMailServerData();
    }

    public Mail(File attach) {
        attachment = attach;
        loadMailServerData();
    }

    public static File getConf() {
        return new File(Utils.getConfigurationDir() + File.separator + ".mail.conf");

    }

    private void loadMailServerData() {
        loadConf();
        props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        if (smtp != null && port != null) {
            props.put("mail.smtp.host", smtp);
            props.put("mail.smtp.port", port);
        } else {
            Utils.sendError("Error - please set at least SMTP server and port in " + Utils.getConfigurationDir() + File.separator + ".mail.conf");
            System.exit(0);
        }
        if (needsAuth) {
            props.put("mail.smtp.auth", "true");
            if (user != null && pass != null) {
                Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                };
                session = Session.getDefaultInstance(props, authenticator);
            } else {
                Utils.sendError("Error - please set username and password in " + Utils.getConfigurationDir() + File.separator + ".mail.conf, or set needs_authentication = false");
                System.exit(0);
            }
        }
    }

    private void loadConf() {
        conf = new Properties();
        InputStream is = null;

        if (getConf().exists()) {

            try {
                is = new FileInputStream(getConf());
            } catch (Exception e) {
                is = null;
                Utils.sendError("Errore apertura inputstream: " + e.getLocalizedMessage());
            }
            try {
                conf.load(is);
                smtp = conf.getProperty("smtp") != null ? props.getProperty("smtp") : null;
                port = conf.getProperty("port") != null ? props.getProperty("port") : null;
                needsAuth = props.getProperty("needs_authentication") != null ? props.getProperty("needs_authentication").equalsIgnoreCase("true") : false;
                if (needsAuth) {
                    user = conf.getProperty("user") != null ? props.getProperty("user") : null;
                    pass = conf.getProperty("password") != null ? props.getProperty("password") : null;
                }
                sender_mail = conf.getProperty("sender_mail") != null ? props.getProperty("sender_mail") : null;
            } catch (IOException e) {
                Utils.sendError("Errore di I/O nel caricamento delle opzioni: " + e.getLocalizedMessage());
                //Utils.showErrorMessage("Errore nel caricamento della configurazione: " + e.getLocalizedMessage());
            } catch (NullPointerException npe) {

            }
        }

    }

    /*public boolean hasSentMailToday() {
        if (OptionsController.getLastSentMailDay() != 0) {
            return Long.parseLong(OptionsController.getOptions().get("last_sent_mail_day").toString()) >= System.currentTimeMillis();
        } else {
            return false;
        }
    }*/
    public void sendMail() {
        /* if (hasSentMailToday()) {
            return;
        }*/
        try {
            Message msg = new MimeMessage(session);
            if (sender_mail == null) {
                Utils.sendError("Error - please set a sender_mail in " + Utils.getConfigurationDir() + File.separator + ".mail.conf");
                System.exit(0);
            }
            msg.setFrom(new InternetAddress(sender_mail, sender_name != null ? sender_name : sender_mail));
            msg.setSubject(subject);
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, ""));
            msg.setText(message);
            if (attachment != null) {
                Multipart multipart = new MimeMultipart();
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                String fileName = attachment.getName();
                DataSource source = new FileDataSource(attachment.getAbsolutePath());
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileName);
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
            }
            l.setToBeLogged("inviata mail a " + to + " con contenuto: " + subject + " - " + message);
            l.writeLog();
            if (!to.equals("")) {
                Transport.send(msg);

            }

        } catch (AddressException e) {
            Utils.sendError("Indirizzo inesistente! " + e.getLocalizedMessage());

        } catch (MessagingException e) {
            Utils.sendError("Errore nell'invio. L'indirizzo " + to + " potrebbe non esistere o e' temporaneamente impossibile collegarsi all'SMTP. Riprova piu' tardi. " + e.getLocalizedMessage());
        } catch (UnsupportedEncodingException e) {
            Utils.sendError("Encoding non supportato: " + e.getLocalizedMessage());

        }
    }

    public void setTo(String newValue) {
        this.to = newValue;
    }

    public void setSubject(String newValue) {
        this.subject = newValue;
    }

    public void setMessage(String newValue) {
        this.message = newValue;
    }

    public void setAttachment(File newValue) {
        this.attachment = newValue;

    }

}
