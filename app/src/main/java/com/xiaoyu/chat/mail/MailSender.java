package com.xiaoyu.chat.mail;

import android.content.Context;

import java.io.InputStream;
import java.security.Security;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MailSender extends javax.mail.Authenticator  {

    // change this host name
    private String user;
    private String password;
    private Session session;
    Context context;
    private Multipart _multipart = new MimeMultipart();

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender(Context context,String user, String password) {
        this.user = user;
        this.password = password;
        this.context = context;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendUserDetailWithImage(String subject, String body,
                                                     String sender, String recipients,String username,String code) throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setFrom(new InternetAddress("no-reply@xyz.in"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        BodyPart messageBodyPart = new MimeBodyPart();
        InputStream is = context.getAssets().open("email_template.html");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String str = new String(buffer);
        str = str.replace("$$UserName$$", username);
        str = str.replace("$$VerifyCode$$", code);
        messageBodyPart.setContent(str,"text/html; charset=utf-8");

        _multipart.addBodyPart(messageBodyPart);

        // Put parts in message

        message.setContent(_multipart);


        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        Transport.send(message);
    }
}
