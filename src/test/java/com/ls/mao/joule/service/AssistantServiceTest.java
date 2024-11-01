package com.ls.mao.joule.service;

import com.ls.mao.joule.factory.ChatClientFactory;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.impl.AssistantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private AssistantRepository assistantRepository;

    @Mock
    private ChatClientFactory chatClientFactory;

    @Mock
    private ChatClient chatClient;

    private AssistantServiceImpl assistantService;

    @Mock
    private ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private CallResponseSpec callResponseSpec;

    private final String defaultSystemPrompt = "You are an AI assistant designed to answer questions accurately and concisely.";

    @BeforeEach
    void setUp() {
        assistantService = new AssistantServiceImpl(assistantRepository, chatClientFactory, defaultSystemPrompt);
    }

    @Test
    void testRegisterAssistant_WithSystemPrompt() {
        String name = "SAPAssistant";
        String response = "I am here to help you with SAP questions.";
        String systemPrompt = "You are an SAP expert.";

        Assistant assistant = new Assistant(name, response, systemPrompt);

        when(assistantRepository.findByName(name)).thenReturn(null);
        when(assistantRepository.save(any(Assistant.class))).thenReturn(assistant);

        String result = assistantService.registerAssistant(name, response, systemPrompt);

        assertEquals("Assistant " + name + " registered successfully.", result);
    }

    @Test
    void testRegisterAssistant_WithoutSystemPrompt() {
        String name = "GeneralAssistant";
        String response = "I am here to help you with general questions.";
        String systemPrompt = null;

        Assistant assistant = new Assistant(name, response, defaultSystemPrompt);
        when(assistantRepository.findByName(name)).thenReturn(null);
        when(assistantRepository.save(any(Assistant.class))).thenReturn(assistant);

        String result = assistantService.registerAssistant(name, response, systemPrompt);

        assertEquals("Assistant " + name + " registered successfully.", result);
    }

    @Test
    void testRegisterAssistant_AssistantAlreadyExists() {
        String name = "SAPAssistant";
        String response = "I am here to help you with SAP questions.";
        String systemPrompt = "You are an SAP expert.";

        Assistant existingAssistant = new Assistant(name, response, systemPrompt);

        when(assistantRepository.findByName(name)).thenReturn(existingAssistant);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assistantService.registerAssistant(name, response, systemPrompt);
        });

        assertEquals("Assistant with name " + name + " already exists.", exception.getMessage());
    }

    @Test
    void testGetAssistantResponse() {
        String name = "SAPAssistant";
        String expectedResponse = "I am here to help you with SAP questions.";
        String systemPrompt = "You are an SAP expert.";

        Assistant assistant = new Assistant(name, expectedResponse, systemPrompt);

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
        String response = "Default response";
        String systemPrompt = "You are an SAP expert.";

        Assistant assistant = new Assistant(name, response, systemPrompt);

        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(chatClientFactory.createChatClient(systemPrompt)).thenReturn(chatClient);
        when(chatClient.prompt(question)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.advisors(any(Consumer.class))).thenReturn(chatClientRequestSpec);
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
        String response = "Default response";
        String systemPrompt = "You are an SAP expert.";

        Assistant assistant = new Assistant(name, response, systemPrompt);

        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(chatClientFactory.createChatClient(systemPrompt)).thenReturn(chatClient);
        when(chatClient.prompt(question)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.advisors(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenThrow(new IllegalStateException("Chat client error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            assistantService.getAnswer(name, question);
        });

        assertEquals("An error occurred while retrieving the answer.", exception.getMessage());
    }
}
