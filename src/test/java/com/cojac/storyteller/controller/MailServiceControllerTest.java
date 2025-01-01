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
    public void sendEmailVerificationTest() throws Exception {
        doNothing().when(userService).sendCodeToEmail(anyString());

        EmailDTO emailDTO = new EmailDTO("test@example.com");

        mockMvc.perform(post("/emails/verification-requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isOk());

        verify(userService, times(1)).sendCodeToEmail(anyString());
    }
}
