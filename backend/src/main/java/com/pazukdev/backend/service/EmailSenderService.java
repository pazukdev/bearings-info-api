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

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Siarhei Sviarkaltsau
 */
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final SendGrid sendGrid;

    public void emailToYourself(@Nonnull final String subject, @Nonnull final String text) {
        emailTo(Constant.EMAIL, subject, text, false);
    }

    public void emailTo(@Nonnull final String toAddress,
                        @Nonnull final String subject,
                        @Nonnull final String text,
                        final boolean html) {
        final Email from = new Email(Constant.EMAIL);
        final Email to = new Email(toAddress);
        final Content content = new Content("text/plain", text);
        final Mail mail = new Mail(from, subject, to, content);
        final Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            final Response response = sendGrid.api(request);
            LoggerUtil.info("status code: " + response.getStatusCode());
            LoggerUtil.info("response body: " + response.getBody());
            LoggerUtil.info("response headers: " + response.getHeaders());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}


