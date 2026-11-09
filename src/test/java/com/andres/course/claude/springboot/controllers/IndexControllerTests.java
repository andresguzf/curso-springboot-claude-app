package com.andres.course.claude.springboot.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Levanta el contexto completo de Spring y simula el servidor HTTP con MockMvc
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleGetReturnsJson() throws Exception {
        mockMvc.perform(get("/api/v1/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void handleGetReturnsMessage() throws Exception {
        mockMvc.perform(get("/api/v1/greeting"))
                .andExpect(jsonPath("$.message")
                .value("¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente."));
    }

    @Test
    void handleGetReturnsDateInIsoFormat() throws Exception {
        mockMvc.perform(get("/api/v1/greeting"))
                .andExpect(jsonPath("$.date", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")));
    }

    @Test
    void postIsNotAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/greeting"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void handleGetUserReturnsUserData() throws Exception {
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("con los datos de un usuario")))
                .andExpect(jsonPath("$.date", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.lastname").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@ejemplo.com"));
    }
}
