package com.ls.mao.joule.controller;

import com.ls.mao.joule.service.AssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssistantController.class)
class AssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssistantService assistantService;

    @Test
    void testRegisterAssistant_WithSystemPrompt() throws Exception {
        String jsonRequest = """
                {
                    "name": "SAPAssistant",
                    "response": "I am here to help you with SAP questions.",
                    "systemPrompt": "You are an SAP expert."
                }
                """;

        when(assistantService.registerAssistant(
                "SAPAssistant",
                "I am here to help you with SAP questions.",
                "You are an SAP expert."
        )).thenReturn("Assistant SAPAssistant registered successfully.");


        mockMvc.perform(post("/api/v1/assistant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Assistant SAPAssistant registered successfully."));
    }

    @Test
    void testRegisterAssistant_WithoutSystemPrompt() throws Exception {
        String jsonRequest = """
                {
                    "name": "GeneralAssistant",
                    "response": "I am here to help you with general questions."
                }
                """;

        when(assistantService.registerAssistant(
                "GeneralAssistant",
                "I am here to help you with general questions.",
                null
        )).thenReturn("Assistant GeneralAssistant registered successfully.");

        mockMvc.perform(post("/api/v1/assistant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Assistant GeneralAssistant registered successfully."));
    }

    @Test
    void testGetAssistantResponse() throws Exception {
        when(assistantService.getResponse("SAPAssistant"))
                .thenReturn("I am here to help you with SAP questions.");

        mockMvc.perform(get("/api/v1/assistant/SAPAssistant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("I am here to help you with SAP questions."));
    }

    @Test
    void testGetAssistantResponse_NotFound() throws Exception {
        when(assistantService.getResponse("UnknownAssistant"))
                .thenThrow(new NoSuchElementException("No assistant found with name: UnknownAssistant"));

        mockMvc.perform(get("/api/v1/assistant/UnknownAssistant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No assistant found with name: UnknownAssistant"));
    }

    @Test
    void testGetAnswer() throws Exception {
        String jsonRequest = """
                {
                    "question": "What is SAP S/4HANA?"
                }
                """;

        when(assistantService.getAnswer("SAPAssistant", "What is SAP S/4HANA?"))
                .thenReturn("SAP S/4HANA is an integrated ERP system utilizing modern in-memory technology.");

        mockMvc.perform(post("/api/v1/assistant/SAPAssistant/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("SAP S/4HANA is an integrated ERP system utilizing modern in-memory technology."));
    }

    @Test
    void testGetAnswer_NotFound() throws Exception {
        String jsonRequest = """
                {
                    "question": "Explain SAP Business Technology Platform."
                }
                """;

        when(assistantService.getAnswer("SAPAssistant", "Explain SAP Business Technology Platform."))
                .thenThrow(new NoSuchElementException("No answer found for the given question"));

        mockMvc.perform(post("/api/v1/assistant/SAPAssistant/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No answer found for the given question"));
    }
}
