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
import sendmail.Utils;

/**
 *
 * @author user
 */
public class Mail {

    private Properties props;
    private Session session;
    private String to, subject, message;
    private File attachment;
    private Log l;

    public Mail(String to, String subject, String message, File attach) {
        this.to = to;
        this.attachment = attach;
        this.subject = subject;
        this.message = message;
        l = new Log(new File(Log.getLogFilePath()));
        props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "authsmtp.easytechmedolago.it");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("smtp@easytechmedolago.it", "E@sytech15");
            }
        };
        session = Session.getDefaultInstance(props, authenticator);
    }

    public Mail() {
        props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "authsmtp.easytechmedolago.it");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("smtp@easytechmedolago.it", "E@sytech15");
            }
        };
        session = Session.getDefaultInstance(props, authenticator);

    }

    public Mail(File attach) {
        attachment = attach;
        props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "authsmtp.easytechmedolago.it");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("smtp@easytechmedolago.it", "E@sytech15");
            }
        };
        session = Session.getDefaultInstance(props, authenticator);
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
            msg.setFrom(new InternetAddress("xmediaserver@easytechmedolago.it", "XMediaServer"));
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
