package com.pazukdev.backend.service;

import com.pazukdev.backend.constant.Constant;
import com.pazukdev.backend.util.LoggerUtil;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
@RequiredArgsConstructor
public class EmailSenderService {

//    private final JavaMailSender javaMailSender;
//
//    @Async
//    public void send(final SimpleMailMessage email) {
//        try {
//            javaMailSender.send(email);
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Async
//    public void send(final MimeMessagePreparator message) {
//        javaMailSender.send(message);
//    }

//    @Async
//    public void send(final Object message) {
//        try {
//            if (message instanceof SimpleMailMessage) {
//                javaMailSender.send((SimpleMailMessage) message);
//            } else if (message instanceof MimeMessagePreparator) {
//                javaMailSender.send((MimeMessagePreparator) message);
//            }
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void emailToYourself(final String subject, final String text) {
        emailTo(Constant.EMAIL, subject, text, false);
    }

    public void emailTo(final String toAddress,
                        final String subject,
                        final String text,
                        final boolean html) {
//        final SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(Constant.EMAIL);
//        mailMessage.setTo(toAddress);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(text);
//
//        send(mailMessage);

        Email from = new Email(Constant.EMAIL);
        Email to = new Email(toAddress);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LoggerUtil.info("status code: " + response.getStatusCode());
            LoggerUtil.info("response body: " + response.getBody());
            LoggerUtil.info("response headers: " + response.getHeaders());
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

//    public void emailTo(final String toAddress,
//                        final String subject,
//                        final String text,
//                        final boolean html) {
//        MimeMessagePreparator message = mimeMessage -> {
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//            messageHelper.setFrom(Constant.EMAIL);
//            messageHelper.setTo(toAddress);
//            messageHelper.setSubject(subject);
//            messageHelper.setText(text, html);
//        };
//        send(message);
//    }

}


