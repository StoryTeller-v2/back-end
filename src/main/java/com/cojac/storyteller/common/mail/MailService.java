package com.cojac.storyteller.common.mail;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.user.exception.EmailSendingException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender emailSender;

    // 자체 스레드 풀을 설정
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);  // 스레드 풀 크기 10

    /**
     * 비동기적으로 이메일을 보냄
     */
    public CompletableFuture<Void> sendEmailWithAsync(String toEmail,
                                                      String title,
                                                      String text) {

        return CompletableFuture.runAsync(() -> {
                    SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);

                    try {
                        emailSender.send(emailForm); // 이메일 발송
                    } catch (RuntimeException e) {
                        log.debug("MailService.sendEmail 예외 발생 toEmail: {}", e);
                        throw new EmailSendingException(ErrorCode.UNABLE_TO_SEND_EMAIL);
                    }
                }, executorService)
                .exceptionally(throwable -> {
                    // 개발자 담당자한테 web hook 및 전달할 있게 처리하기.
                    log.error("Exception occurred: " + throwable.getMessage());
                    return null;
                });
    }

    public void sendEmail(String toEmail,
                          String title,
                          String text) {

        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);

        try {
            emailSender.send(emailForm); // 이메일 발송
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail 예외 발생 toEmail: {}", e);
            throw new EmailSendingException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();  // 자체 스레드 풀 종료
        log.info("ExecutorService has been shut down.");
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
