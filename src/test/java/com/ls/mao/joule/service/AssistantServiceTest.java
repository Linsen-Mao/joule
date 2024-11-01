package com.ls.mao.joule.service;

import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.impl.AssistantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
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
        String name = "SAPAssistant";
        String response = "I am here to help you with SAP questions.";

        when(assistantRepository.findByName(name)).thenReturn(null);
        when(assistantRepository.save(any(Assistant.class))).thenReturn(new Assistant(name, response));

        String result = assistantService.registerAssistant(name, response);

        assertEquals("Assistant " + name + " registered successfully.", result);
    }

    @Test
    void testRegisterAssistant_AssistantAlreadyExists() {
        String name = "SAPAssistant";
        String response = "I am here to help you with SAP questions.";
        Assistant existingAssistant = new Assistant(name, response);

        when(assistantRepository.findByName(name)).thenReturn(existingAssistant);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assistantService.registerAssistant(name, response);
        });

        assertEquals("Assistant with name " + name + " already exists.", exception.getMessage());
    }

    @Test
    void testGetAssistantResponse() {
        String name = "SAPAssistant";
        String expectedResponse = "I am here to help you with SAP questions.";
        Assistant assistant = new Assistant(name, expectedResponse);

        when(assistantRepository.findByName(name)).thenReturn(assistant);

        String actualResponse = assistantService.getResponse(name);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetAssistantResponse_AssistantNotFound() {
        String name = "UnknownAssistant";

        when(assistantRepository.findByName(name)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            assistantService.getResponse(name);
        });

        assertEquals("No assistant found with name: " + name, exception.getMessage());
    }

    @Test
    void testGetAnswer() {
        String name = "SAPAssistant";
        String question = "What is SAP S/4HANA?";
        String expectedAnswer = "SAP S/4HANA is an integrated ERP system utilizing modern in-memory technology.";

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
        String name = "UnknownAssistant";
        String question = "Explain SAP Business Technology Platform.";

        when(assistantRepository.findByName(name)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            assistantService.getAnswer(name, question);
        });

        assertEquals("No assistant found with name: " + name, exception.getMessage());
    }

    @Test
    void testGetAnswer_ChatClientError() {
        String name = "SAPAssistant";
        String question = "What is SAP S/4HANA?";

        Assistant assistant = new Assistant(name, "Default response");

        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(chatClient.prompt(question)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenThrow(new IllegalStateException("Chat client error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            assistantService.getAnswer(name, question);
        });

        assertEquals("An error occurred while retrieving the answer.", exception.getMessage());
    }
}
