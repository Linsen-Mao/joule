package com.ls.mao.joule.service;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.impl.AssistantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private AssistantRepository assistantRepository;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private AssistantServiceImpl assistantService;

    @Mock
    private ChatClientRequestSpec chatClientRequestSpec;
    @Mock
    private CallResponseSpec callResponseSpec;

    @Test
    void testRegisterAssistant() {
        String name = "testAssistant";
        String response = "Hello! I am here to help.";
        Assistant expectedAssistant = new Assistant(name, response);
        when(assistantRepository.save(any(Assistant.class))).thenReturn(expectedAssistant);

        String result = assistantService.registerAssistant(name, response);

        assertEquals("Assistant " + name + " registered successfully.", result);
    }

    @Test
    void testGetAssistantResponse() {
        String name = "testAssistant";
        String expectedResponse = "Hello! I am here to help.";
        when(assistantRepository.findByName(name)).thenReturn(new Assistant(name, expectedResponse));

        String actualResponse = assistantService.getResponse(name);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetAnswer() {
        String name = "testAssistant";
        String question = "What is the weather today?";
        String expectedAnswer = "The weather today is sunny.";

        Assistant assistant = new Assistant(name, "Default response");
        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(chatClient.prompt(question)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedAnswer);

        String actualAnswer = assistantService.getAnswer(name, question);

        assertEquals(expectedAnswer, actualAnswer);
    }

    @Test
    void testGetAnswer_AssistantNotFound() {
        String name = "unknownAssistant";
        String question = "What is the weather today?";

        when(assistantRepository.findByName(name)).thenReturn(null);

        String actualAnswer = assistantService.getAnswer(name, question);

        assertEquals("Assistant unknownAssistant not found.", actualAnswer);
    }

    @Test
    void testGetAnswer_ChatClientError() {
        String name = "testAssistant";
        String question = "What is the weather today?";

        Assistant assistant = new Assistant(name, "Default response");
        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(chatClient.prompt(question)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenThrow(new IllegalStateException("Chat client error"));

        String actualAnswer = assistantService.getAnswer(name, question);

        assertEquals("An error occurred while retrieving the answer.", actualAnswer);
    }
}
