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
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private File config_file;
    private Properties conf = new Properties();
    private Properties mailProps;
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

    public File getConf() {
        if (config_file == null) {
            if (Utils.getOs().equalsIgnoreCase("lin")) {
                File etcConf = new File("/etc/mail.conf");
                if (etcConf.exists()) {
                    config_file = etcConf;
                    return config_file;
                }
            }
            config_file = new File(Utils.getConfigurationDir() + File.separator + ".mail.conf");
            System.out.println(MessageFormat.format(R.string("using_conf"), config_file.getAbsolutePath()));
            if (!config_file.exists()) {
                try {
                    config_file.createNewFile();
                } catch (IOException ex) {
                    Utils.sendError(MessageFormat.format(R.string("error_creating_conf"), Utils.getConfigurationDir(), ex.getLocalizedMessage()));
                }
            }
        }

        return config_file;

    }

    private void loadMailServerData() {
        loadConf();
        mailProps = new Properties();
        mailProps.put("mail.transport.protocol", "smtp");
        if (smtp != null && port != null) {
            mailProps.put("mail.smtp.host", smtp);
            mailProps.put("mail.smtp.port", port);
        } else {
            Utils.sendError(MessageFormat.format(R.string("error_smtp_not_set"), getConf().getAbsolutePath()));
            System.exit(0);
        }
        if (needsAuth) {
            mailProps.put("mail.smtp.auth", "true");
            if (user != null && pass != null) {
                Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                };
                session = Session.getDefaultInstance(mailProps, authenticator);
            } else {
                Utils.sendError(MessageFormat.format(R.string("error_auth_failed"), getConf().getAbsolutePath()));
                System.exit(0);
            }
        } else {
            System.out.println(MessageFormat.format(R.string("info_auth_not_set"), getConf().getAbsolutePath()));
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
                Utils.sendError(MessageFormat.format(R.string("error_opening_inputstream"), e.getLocalizedMessage()));
            }
            try {
                conf.load(is);
                smtp = conf.getProperty("smtp") != null ? conf.getProperty("smtp") : null;
                port = conf.getProperty("port") != null ? conf.getProperty("port") : null;
                System.out.println(MessageFormat.format(R.string("smtp_info"), smtp, port));
                needsAuth = conf.getProperty("needs_auth") != null ? conf.getProperty("needs_auth").equalsIgnoreCase("true") : false;
                if (needsAuth) {
                    user = conf.getProperty("user") != null ? conf.getProperty("user") : null;
                    pass = conf.getProperty("password") != null ? conf.getProperty("password") : null;
                    System.out.println(MessageFormat.format(R.string("auth_needed"), user));
                }
                sender_mail = conf.getProperty("sender_mail") != null ? conf.getProperty("sender_mail") : null;
                sender_name = conf.getProperty("sender_name") != null ? conf.getProperty("sender_name") : null;
                System.out.println(MessageFormat.format(R.string("sender_info"), sender_name, sender_mail));
            } catch (IOException e) {
                Utils.sendError(MessageFormat.format(R.string("io_error_loading_conf"), e.getLocalizedMessage()));
                //Utils.showErrorMessage("Errore nel caricamento della configurazione: " + e.getLocalizedMessage());
            }
        } else {
            Utils.sendError(MessageFormat.format(R.string("file_not_found"), getConf().getAbsolutePath()));
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
                Utils.sendError(MessageFormat.format(R.string("sender_mail_error"), getConf().getAbsolutePath()));
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
            l.setToBeLogged(MessageFormat.format(R.string("log_mail_sent"), to, subject, message));
            l.writeLog();
            if (!to.equals("")) {
                Transport.send(msg);

            }

        } catch (AddressException e) {
            Utils.sendError(MessageFormat.format(R.string("address_invalid"), e.getLocalizedMessage()));

        } catch (MessagingException e) {
            Utils.sendError(MessageFormat.format(R.string("error_sending"), to, e.getLocalizedMessage()));
        } catch (UnsupportedEncodingException e) {
            Utils.sendError(MessageFormat.format(R.string("encoding_not_supported"), e.getLocalizedMessage()));

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
