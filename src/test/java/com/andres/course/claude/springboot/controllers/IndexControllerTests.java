package com.andres.course.claude.springboot.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

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
                .andExpect(jsonPath("$.user.name").value("Juan"))
                .andExpect(jsonPath("$.user.lastname").value("Pérez"))
                .andExpect(jsonPath("$.user.email").value("juan.perez@ejemplo.com"))
                .andExpect(jsonPath("$.user.age").value(30));
    }

    @Test
    void handleGetUserKeepsFieldOrder() throws Exception {
        String json = mockMvc.perform(get("/api/v1/user"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JsonPath conserva el orden en que los campos aparecen en el JSON
        Map<String, Object> body = JsonPath.read(json, "$");

        assertEquals(List.of("message", "date", "user"), List.copyOf(body.keySet()));
    }

    @Test
    void handleGetUserDtoReturnsUserData() throws Exception {
        mockMvc.perform(get("/api/v1/user-dto"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("con los datos de un usuario")))
                .andExpect(jsonPath("$.date", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")))
                .andExpect(jsonPath("$.user.name").value("Andrés"))
                .andExpect(jsonPath("$.user.lastname").value("Guzmán"))
                .andExpect(jsonPath("$.user.email").value("andres.guzman@example.com"))
                .andExpect(jsonPath("$.user.age").value(30));
    }

    @Test
    void handleGetUserResponseEntityReturnsUserData() throws Exception {
        mockMvc.perform(get("/api/v1/user-response-entity"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("con los datos de un usuario")))
                .andExpect(jsonPath("$.date", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")))
                .andExpect(jsonPath("$.user.name").value("Andrés"))
                .andExpect(jsonPath("$.user.lastname").value("Guzmán"))
                .andExpect(jsonPath("$.user.email").value("andres.guzman@example.com"))
                .andExpect(jsonPath("$.user.age").value(30));
    }
}
