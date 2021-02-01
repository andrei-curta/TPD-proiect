package util;

import com.sun.mail.smtp.SMTPAddressFailedException;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailClient {

    public static void sendMail(String mailTo, String subject, String text) throws MessagingException {
        String from = "app@tpd.com";
        String host = "127.0.0.1";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.auth", "false");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(text);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException mex) {
            System.out.println("The recipient address " + mailTo + " is not a valid RFC-5321 address");
            throw mex;
        }
    }
}