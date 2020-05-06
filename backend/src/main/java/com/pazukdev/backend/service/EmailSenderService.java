package com.pazukdev.backend.service;

import com.pazukdev.backend.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Async
    public void send(final SimpleMailMessage email) {
        try {
            javaMailSender.send(email);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

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

    public void emailTo(final String toAddress, final String subject, final String text, final boolean html) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(Constant.EMAIL);
        mailMessage.setTo(toAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        send(mailMessage);
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


