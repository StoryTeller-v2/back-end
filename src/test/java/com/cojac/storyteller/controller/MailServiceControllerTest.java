package com.cojac.storyteller.controller;

import com.cojac.storyteller.common.mail.MailService;
import com.cojac.storyteller.user.controller.UserController;
import com.cojac.storyteller.user.dto.EmailDTO;
import com.cojac.storyteller.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MailServiceControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void sendEmailWithSynchronousProcessingTest() throws Exception {
        // 동기 방식 Mock 설정
        doAnswer(invocation -> {
            Thread.sleep(4000); // 동기 처리 시뮬레이션
            return null;
        }).when(userService).sendCodeToEmail(anyString());

        EmailDTO emailDTO = new EmailDTO("sync@example.com");

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/emails/verification-requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Processing Time: " + (endTime - startTime) + "ms");

        verify(userService, times(1)).sendCodeToEmail(anyString());
    }

    @Test
    public void sendEmailWithAsynchronousProcessingTest() throws Exception {
        // 비동기 방식 Mock 설정
        doAnswer(invocation ->
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(4000); // 비동기 처리 시뮬레이션
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
        ).when(userService).sendCodeToEmail(anyString());

        EmailDTO emailDTO = new EmailDTO("async@example.com");

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/emails/verification-requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous Processing Time: " + (endTime - startTime) + "ms");

        verify(userService, times(1)).sendCodeToEmail(anyString());
    }

    @Test
    public void multipleSynchronousRequestsTest() throws Exception {
        // 동기 방식 Mock 설정
        doAnswer(invocation -> {
            Thread.sleep(4000); // 동기 처리 시뮬레이션
            return null;
        }).when(userService).sendCodeToEmail(anyString());

        EmailDTO emailDTO = new EmailDTO("sync@example.com");
        int requestCount = 5; // 요청 수 설정
        long totalDuration = 0;

        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();

            mockMvc.perform(post("/emails/verification-requests")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(emailDTO)))
                    .andExpect(status().isOk());

            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);
        }

        System.out.println("Synchronous Total Time for " + requestCount + " Requests: " + totalDuration + "ms");
        System.out.println("Synchronous Average Time per Request: " + (totalDuration / requestCount) + "ms");

        verify(userService, times(requestCount)).sendCodeToEmail(anyString());
    }

    @Test
    public void multipleAsynchronousRequestsTest() throws Exception {
        // 비동기 방식 Mock 설정
        doAnswer(invocation ->
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(4000); // 비동기 처리 시뮬레이션
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
        ).when(userService).sendCodeToEmail(anyString());

        EmailDTO emailDTO = new EmailDTO("async@example.com");
        int requestCount = 5; // 요청 수 설정
        long totalDuration = 0;

        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();

            mockMvc.perform(post("/emails/verification-requests")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(emailDTO)))
                    .andExpect(status().isOk());

            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);
        }

        System.out.println("Asynchronous Total Time for " + requestCount + " Requests: " + totalDuration + "ms");
        System.out.println("Asynchronous Average Time per Request: " + (totalDuration / requestCount) + "ms");

        verify(userService, times(requestCount)).sendCodeToEmail(anyString());
    }

}
