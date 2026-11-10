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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

// Levanta el contexto completo de Spring y simula el servidor HTTP con MockMvc
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTests {

    private static final String ISO_DATE = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*";

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

    // Formato elegido con el parámetro de la URL: ?format=json o ?format=xml

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/greeting", "/api/v1/user", "/api/v1/user-dto", "/api/v1/user-response-entity"})
    void formatJsonReturnsJson(String path) throws Exception {
        assertJsonResponse(mockMvc.perform(get(path).param("format", "json")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/greeting", "/api/v1/user", "/api/v1/user-dto", "/api/v1/user-response-entity"})
    void formatXmlReturnsXml(String path) throws Exception {
        assertXmlResponse(mockMvc.perform(get(path).param("format", "xml")));
    }

    @Test
    void unknownFormatIsNotAcceptable() throws Exception {
        mockMvc.perform(get("/api/v1/greeting").param("format", "yaml"))
                .andExpect(status().isNotAcceptable());
    }

    // Formato elegido con la cabecera Accept de la petición

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/greeting", "/api/v1/user", "/api/v1/user-dto", "/api/v1/user-response-entity"})
    void acceptJsonReturnsJson(String path) throws Exception {
        assertJsonResponse(mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/greeting", "/api/v1/user", "/api/v1/user-dto", "/api/v1/user-response-entity"})
    void acceptXmlReturnsXml(String path) throws Exception {
        assertXmlResponse(mockMvc.perform(get(path).accept(MediaType.APPLICATION_XML)));
    }

    @Test
    void formatParameterTakesPrecedenceOverAcceptHeader() throws Exception {
        assertJsonResponse(mockMvc.perform(get("/api/v1/greeting")
                .param("format", "json")
                .accept(MediaType.APPLICATION_XML)));
    }

    // Datos del usuario en XML

    @Test
    void handleGetUserReturnsUserDataInXml() throws Exception {
        mockMvc.perform(get("/api/v1/user").param("format", "xml"))
                .andExpect(xpath("/Map/user/name").string("Juan"))
                .andExpect(xpath("/Map/user/lastname").string("Pérez"))
                .andExpect(xpath("/Map/user/email").string("juan.perez@ejemplo.com"))
                .andExpect(xpath("/Map/user/age").string("30"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/user-dto", "/api/v1/user-response-entity"})
    void messageDtoReturnsUserDataInXml(String path) throws Exception {
        mockMvc.perform(get(path).param("format", "xml"))
                .andExpect(xpath("/MessageDto/user/name").string("Andrés"))
                .andExpect(xpath("/MessageDto/user/lastname").string("Guzmán"))
                .andExpect(xpath("/MessageDto/user/email").string("andres.guzman@example.com"))
                .andExpect(xpath("/MessageDto/user/age").string("30"));
    }

    private void assertJsonResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Hola Mundo")))
                .andExpect(jsonPath("$.date", matchesPattern(ISO_DATE)));
    }

    // "/*" es el elemento raíz del XML: Map o MessageDto según el endpoint
    private void assertXmlResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/*/message").string(containsString("Hola Mundo")))
                .andExpect(xpath("/*/date").string(matchesPattern(ISO_DATE)));
    }
}
