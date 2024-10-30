package com.ls.mao.joule.service;

import com.ls.mao.joule.model.AddQnARequest;
import com.ls.mao.joule.model.Assistant;
import com.ls.mao.joule.model.RegisterAssistantRequest;
import com.ls.mao.joule.repo.AssistantRepository;
import com.ls.mao.joule.service.impl.AssistantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.model.ChatModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssistantServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private AssistantRepository assistantRepository;

    @InjectMocks
    private AssistantServiceImpl assistantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterAssistant() {
        String name = "testAssistant";
        String response = "Hello! I am here to help.";
        RegisterAssistantRequest request = new RegisterAssistantRequest(name, response);

        when(assistantRepository.save(any(Assistant.class))).thenReturn(new Assistant(name, response));

        String result = assistantService.registerAssistant(request.name(), request.response());

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
    void testAddQuestionAnswer() {

        String name = "testAssistant";
        String question = "What is your name?";
        String answer = "I am your assistant.";
        AddQnARequest request = new AddQnARequest(question, answer);

        Assistant assistant = new Assistant(name, "Initial response");
        when(assistantRepository.findByName(name)).thenReturn(assistant);
        when(assistantRepository.save(any(Assistant.class))).thenReturn(assistant);

        String result = assistantService.addQuestionAnswer(name, question, answer);

        assertEquals("Question-Answer pair added successfully.", result);
        verify(assistantRepository, times(1)).save(assistant);
    }

    @Test
    void testGetAnswerUsingChatModel() {

        String name = "testAssistant";
        String question = "What is OpenAI?";
        String expectedAnswer = "OpenAI is an AI research and deployment company.";

        when(chatModel.call(question)).thenReturn(expectedAnswer);
        when(assistantRepository.findByName(name)).thenReturn(new Assistant(name, "Initial response"));

        String actualAnswer = assistantService.getAnswer(name, question);

        assertEquals(expectedAnswer, actualAnswer);
        verify(chatModel, times(1)).call(question);
    }

    @Test
    void testGetAnswerWithRAGIntegration() {

        String name = "testAssistant";
        String question = "Tell me about recent developments in AI.";
        String expectedAnswer = "Recent AI developments include advances in language models and machine learning applications.";

        when(chatModel.call(question)).thenReturn(expectedAnswer);
        when(assistantRepository.findByName(name)).thenReturn(new Assistant(name, "Initial response"));

        String actualAnswer = assistantService.getAnswer(name, question);

        assertEquals(expectedAnswer, actualAnswer);
        verify(chatModel, times(1)).call(question);
    }

}
