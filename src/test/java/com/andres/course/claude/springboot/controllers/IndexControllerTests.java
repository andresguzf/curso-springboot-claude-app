package com.andres.course.claude.springboot.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

// Levanta el contexto completo de Spring y simula el servidor HTTP con MockMvc
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTests {

    private static final String ISO_DATE = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*";

    // UUID: 8-4-4-4-12 caracteres hexadecimales, por ejemplo 123e4567-e89b-12d3-a456-426614174000
    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    // Token de prueba: /user-response-entity exige la cabecera Authorization
    private static final String TOKEN = "Bearer abc.def.ghi";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleGetReturnsJson() throws Exception {
        mockMvc.perform(get("/api/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void handleGetReturnsMessage() throws Exception {
        mockMvc.perform(get("/api/greeting"))
                .andExpect(jsonPath("$.message")
                .value("¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente."));
    }

    @Test
    void handleGetReturnsDateInIsoFormat() throws Exception {
        mockMvc.perform(get("/api/greeting"))
                .andExpect(jsonPath("$.date", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")));
    }

    @Test
    void postIsNotAllowed() throws Exception {
        mockMvc.perform(post("/api/greeting"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void handleGetUserReturnsUserData() throws Exception {
        mockMvc.perform(get("/api/user"))
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
        String json = mockMvc.perform(get("/api/user"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JsonPath conserva el orden en que los campos aparecen en el JSON
        Map<String, Object> body = JsonPath.read(json, "$");

        assertEquals(List.of("message", "date", "user"), List.copyOf(body.keySet()));
    }

    @Test
    void handleGetUserDtoReturnsUserData() throws Exception {
        mockMvc.perform(get("/api/user-dto"))
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
        mockMvc.perform(get("/api/user-response-entity").header(HttpHeaders.AUTHORIZATION, TOKEN))
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
    @ValueSource(strings = {"/api/greeting", "/api/user", "/api/user-dto", "/api/user-response-entity"})
    void formatJsonReturnsJson(String path) throws Exception {
        assertJsonResponse(mockMvc.perform(get(path).param("format", "json").header(HttpHeaders.AUTHORIZATION, TOKEN)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/greeting", "/api/user", "/api/user-dto", "/api/user-response-entity"})
    void formatXmlReturnsXml(String path) throws Exception {
        assertXmlResponse(mockMvc.perform(get(path).param("format", "xml").header(HttpHeaders.AUTHORIZATION, TOKEN)));
    }

    @Test
    void unknownFormatIsNotAcceptable() throws Exception {
        mockMvc.perform(get("/api/greeting").param("format", "yaml"))
                .andExpect(status().isNotAcceptable());
    }

    // Formato elegido con la cabecera Accept de la petición

    @ParameterizedTest
    @ValueSource(strings = {"/api/greeting", "/api/user", "/api/user-dto", "/api/user-response-entity"})
    void acceptJsonReturnsJson(String path) throws Exception {
        assertJsonResponse(mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, TOKEN)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/greeting", "/api/user", "/api/user-dto", "/api/user-response-entity"})
    void acceptXmlReturnsXml(String path) throws Exception {
        assertXmlResponse(mockMvc.perform(get(path).accept(MediaType.APPLICATION_XML).header(HttpHeaders.AUTHORIZATION, TOKEN)));
    }

    @Test
    void formatParameterTakesPrecedenceOverAcceptHeader() throws Exception {
        assertJsonResponse(mockMvc.perform(get("/api/greeting")
                .param("format", "json")
                .accept(MediaType.APPLICATION_XML)));
    }

    // Datos del usuario en XML

    @Test
    void handleGetUserReturnsUserDataInXml() throws Exception {
        mockMvc.perform(get("/api/user").param("format", "xml"))
                .andExpect(xpath("/Map/user/name").string("Juan"))
                .andExpect(xpath("/Map/user/lastname").string("Pérez"))
                .andExpect(xpath("/Map/user/email").string("juan.perez@ejemplo.com"))
                .andExpect(xpath("/Map/user/age").string("30"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/user-dto", "/api/user-response-entity"})
    void messageDtoReturnsUserDataInXml(String path) throws Exception {
        mockMvc.perform(get(path).param("format", "xml").header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(xpath("/*/user/name").string("Andrés"))
                .andExpect(xpath("/*/user/lastname").string("Guzmán"))
                .andExpect(xpath("/*/user/email").string("andres.guzman@example.com"))
                .andExpect(xpath("/*/user/age").string("30"));
    }

    @Test
    void handleGetUserResponseEntityReturnsCustomHeaders() throws Exception {
        mockMvc.perform(get("/api/user-response-entity").header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Color", matchesPattern("#[0-9A-F]{6}")))
                // Un JWT tiene tres partes separadas por punto: header.payload.firma
                .andExpect(header().string("X-Token", matchesPattern("Bearer [\\w-]+\\.[\\w-]+\\.[\\w-]+")));
    }

    // Cabeceras del request que el endpoint devuelve en el cuerpo de la respuesta

    @Test
    void handleGetUserResponseEntityReturnsRequestHeaders() throws Exception {
        mockMvc.perform(get("/api/user-response-entity")
                .header("X-UserName", "andres")
                .header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andres"))
                .andExpect(jsonPath("$.accessToken").value(TOKEN));
    }

    @Test
    void missingUserNameUsesDefaultValue() throws Exception {
        mockMvc.perform(get("/api/user-response-entity").header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Anonymous"));
    }

    @Test
    void missingAuthorizationIsBadRequest() throws Exception {
        assertBadRequestOnlyFor(HttpHeaders.AUTHORIZATION,
                mockMvc.perform(get("/api/user-response-entity").header("X-UserName", "andres")))
                .andExpect(jsonPath("$.errors.Authorization").value("la cabecera es obligatoria"));
    }

    // Saludo con el parámetro name del query string

    @Test
    void handleGetHelloGreetsByName() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Andrés").param("limit", "10").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("¡Hola, Andrés! Bienvenido a Spring Boot."))
                .andExpect(jsonPath("$.name").value("Andrés"))
                .andExpect(jsonPath("$.date", matchesPattern(ISO_DATE)));
    }

    @Test
    void handleGetHelloReturnsXml() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Andrea").param("limit", "10").param("page", "0").param("format", "xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/Map/message").string("¡Hola, Andrea! Bienvenido a Spring Boot."))
                .andExpect(xpath("/Map/name").string("Andrea"));
    }

    @Test
    void handleGetHelloReturnsLimitAndPage() throws Exception {
        mockMvc.perform(get("/api/hello")
                .param("name", "Andrés")
                .param("limit", "5")
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Andrés"))
                .andExpect(jsonPath("$.limit").value(5))
                .andExpect(jsonPath("$.page").value(2));
    }

    // Límites válidos: limit entre 5 y 50, page desde 0
    @ParameterizedTest
    @CsvSource({"5, 0", "50, 0", "20, 100"})
    void handleGetHelloAcceptsLimitsInRange(String limit, String page) throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Andrés").param("limit", limit).param("page", page))
                .andExpect(status().isOk());
    }

    // Cada fila deja un parámetro inválido y los demás correctos.
    // El cuerpo del error solo trae ese parámetro con su mensaje, sin la excepción
    @ParameterizedTest
    @CsvSource(value = {
            "'',              10,  0,   name",   // name vacío
            "'   ',           10,  0,   name",   // name solo con espacios
            "Ana,             10,  0,   name",   // name con menos de 4 letras
            "Maximiliano Ruiz, 10,  0,   name",  // name con más de 14 letras
            "Andrea,          4,   0,   limit",  // limit menor que 5
            "Andrea,          51,  0,   limit",  // limit mayor que 50
            "Andrea,          10,  -1,  page",   // page negativo
            "Andrea,          abc, 0,   limit",  // limit no numérico
            "Andrea,          10,  abc, page"    // page no numérico
    })
    void invalidHelloParamsAreBadRequest(String name, String limit, String page, String field) throws Exception {
        assertBadRequestOnlyFor(field,
                mockMvc.perform(get("/api/hello").param("name", name).param("limit", limit).param("page", page)));
    }

    @Test
    void nonNumericLimitShowsTypeMessage() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Andrea").param("limit", "abc").param("page", "0"))
                .andExpect(jsonPath("$.errors.limit").value("debe ser de tipo Integer"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "limit", "page"})
    void missingHelloParamIsBadRequest(String missing) throws Exception {
        Map<String, String> params = new HashMap<>(Map.of("name", "Andrea", "limit", "10", "page", "0"));
        params.remove(missing);

        var request = get("/api/hello");
        params.forEach(request::param);

        assertBadRequestOnlyFor(missing, mockMvc.perform(request))
                .andExpect(jsonPath("$.errors." + missing).value("es obligatorio"));
    }

    // Producto por id con path variable

    @Test
    void findByIdReturnsProductId() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(7));
    }

    @Test
    void findByIdWithNonNumericIdIsBadRequest() throws Exception {
        assertBadRequestOnlyFor("id", mockMvc.perform(get("/api/products/{id}", "abc")))
                .andExpect(jsonPath("$.errors.id").value("debe ser de tipo Long"));
    }

    // id válido: entre 1 y 45
    @ParameterizedTest
    @ValueSource(longs = {1, 45})
    void findByIdAcceptsIdsInRange(long id) throws Exception {
        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(id));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 46})
    void findByIdOutOfRangeIsBadRequest(long id) throws Exception {
        assertBadRequestOnlyFor("id", mockMvc.perform(get("/api/products/{id}", id)));
    }

    // Producto por código: parte estática "code" + path variable

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "ABC123", "ABCDEFGHIJ"})
    void findByCodeReturnsProductCode(String code) throws Exception {
        mockMvc.perform(get("/api/code/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productCode").value(code));
    }

    // Menos de 3 o más de 10 caracteres
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCDEFGHIJK"})
    void findByCodeWithInvalidLengthIsBadRequest(String code) throws Exception {
        assertBadRequestOnlyFor("code", mockMvc.perform(get("/api/code/{code}", code)));
    }

    // Saludo con dos path variables: name y age

    @Test
    void handleGetHelloPathReturnsNameAndAge() throws Exception {
        mockMvc.perform(get("/api/hello/{name}/{age}", "Andrés", 30))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("¡Hola, Andrés! Tienes 30 años."))
                .andExpect(jsonPath("$.name").value("Andrés"))
                .andExpect(jsonPath("$.age").value(30));
    }

    // Límites válidos: name de 3 a 18 caracteres, age de 1 a 110
    @ParameterizedTest
    @CsvSource({"Ana, 1", "Andrés, 30", "Maximiliano Rodrig, 110"})
    void handleGetHelloPathAcceptsValuesInRange(String name, int age) throws Exception {
        mockMvc.perform(get("/api/hello/{name}/{age}", name, age))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    // Cada fila deja un valor inválido; el error solo trae ese campo
    @ParameterizedTest
    @CsvSource({
            "Al,                  30,  name", // name con menos de 3 caracteres
            "Maximiliano Rodrigo, 30,  name", // name con más de 18 caracteres
            "'    ',              30,  name", // name solo con espacios
            "Andrés,              0,   age",  // age menor que 1
            "Andrés,              111, age"   // age mayor que 110
    })
    void handleGetHelloPathWithInvalidValuesIsBadRequest(String name, int age, String field) throws Exception {
        assertBadRequestOnlyFor(field, mockMvc.perform(get("/api/hello/{name}/{age}", name, age)));
    }

    @Test
    void handleGetHelloPathWithNonNumericAgeIsBadRequest() throws Exception {
        assertBadRequestOnlyFor("age", mockMvc.perform(get("/api/hello/{name}/{age}", "Andrés", "treinta")))
                .andExpect(jsonPath("$.errors.age").value("debe ser de tipo Integer"));
    }

    // POST de un producto con JSON en el cuerpo del request

    @Test
    void createReturnsCreatedProduct() throws Exception {
        String json = """
                {"name": "Teclado", "price": 29990.5, "quantity": 3}
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Producto creado con éxito"))
                .andExpect(jsonPath("$.product.name").value("Teclado"))
                .andExpect(jsonPath("$.product.price").value(29990.5))
                .andExpect(jsonPath("$.product.id", matchesPattern(UUID_PATTERN)))
                .andExpect(jsonPath("$.product.quantity").value(3));
    }

    // El DTO de respuesta trae el mensaje y el producto completo anidado
    @Test
    void createReturnsMessageAndNestedProduct() throws Exception {
        String json = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Mouse", "price": 9990, "quantity": 2}
                        """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> body = JsonPath.read(json, "$");
        assertEquals(List.of("message", "product"), List.copyOf(body.keySet()));
        Map<String, Object> product = JsonPath.read(json, "$.product");
        assertEquals(List.of("id", "name", "price", "quantity"), List.copyOf(product.keySet()));
    }

    // El id lo genera el servidor como UUID: cada POST recibe uno distinto
    @Test
    void createAssignsUniqueUuid() throws Exception {
        String first = createProductAndGetId("""
                {"name": "Monitor", "price": 159990, "quantity": 2}
                """);
        String second = createProductAndGetId("""
                {"name": "Parlante", "price": 19990, "quantity": 5}
                """);

        assertTrue(first.matches(UUID_PATTERN));
        assertTrue(second.matches(UUID_PATTERN));
        assertNotEquals(first, second);
    }

    // Si el cliente envía un id, se ignora y se usa el generado por el servidor
    @Test
    void createIgnoresIdSentByClient() throws Exception {
        String clientId = "123e4567-e89b-12d3-a456-426614174000";
        String id = createProductAndGetId("""
                {"id": "%s", "name": "Webcam", "price": 24990, "quantity": 2}
                """.formatted(clientId));

        assertNotEquals(clientId, id);
    }

    private String createProductAndGetId(String json) throws Exception {
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.product.id");
    }

    // price es BigDecimal: acepta hasta 10 enteros y 6 decimales sin perder precisión
    @ParameterizedTest
    @ValueSource(strings = {"2.000001", "29990.123456", "1234567890.123456"})
    void createKeepsExactPrice(String price) throws Exception {
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Teclado", "price": %s, "quantity": 3}
                        """.formatted(price)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Se compara el texto del JSON: un double redondearía 1234567890.123456
        assertTrue(response.contains("\"price\":" + price), response);
    }

    // Más de 10 dígitos enteros o más de 6 decimales: 400 solo con el campo price
    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "3.1234567"})
    void createWithPriceOutOfDigitsIsBadRequest(String price) throws Exception {
        assertBadRequestOnlyFor("price", mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Teclado", "price": %s, "quantity": 3}
                        """.formatted(price))));
    }

    // OutputCaptureExtension captura lo que se imprime en consola, incluidos los logs
    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void createLogsReceivedProduct(CapturedOutput output) throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Audífonos", "price": 49990.99, "quantity": 4}
                        """))
                .andExpect(status().isCreated());

        assertTrue(output.getOut().contains(
                "Producto recibido: {\"id\":null,\"name\":\"Audífonos\",\"price\":49990.99,\"quantity\":4}"));
    }

    // Valores en el límite que se aceptan: name de 4 y 40 caracteres, price apenas mayor que 2, quantity 2
    @ParameterizedTest
    @CsvSource({"Lupa, 2.01, 2", "Destornillador eléctrico inalámbrico 18V, 2.000001, 2"})
    void createAcceptsProductInRange(String name, String price, int quantity) throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "%s", "price": %s, "quantity": %d}
                        """.formatted(name, price, quantity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.name").value(name))
                .andExpect(jsonPath("$.product.quantity").value(quantity));
    }

    // Cada fila envía un producto con un solo campo inválido: el error solo trae ese campo
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            {"price": 10, "quantity": 3}                                  | name
            {"name": null, "price": 10, "quantity": 3}                    | name
            {"name": "    ", "price": 10, "quantity": 3}                  | name
            {"name": "Ana", "price": 10, "quantity": 3}                   | name
            {"name": "Destornillador eléctrico inalámbrico 18VX", "price": 10, "quantity": 3} | name
            {"name": "Teclado", "quantity": 3}                            | price
            {"name": "Teclado", "price": null, "quantity": 3}             | price
            {"name": "Teclado", "price": 2, "quantity": 3}                | price
            {"name": "Teclado", "price": 1.5, "quantity": 3}              | price
            {"name": "Teclado", "price": 10}                              | quantity
            {"name": "Teclado", "price": 10, "quantity": null}            | quantity
            {"name": "Teclado", "price": 10, "quantity": 1}               | quantity
            {"name": "Teclado", "price": 10, "quantity": 0}               | quantity
            """)
    void createWithInvalidProductIsBadRequest(String json, String field) throws Exception {
        assertBadRequestOnlyFor(field, mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)));
    }

    @Test
    void createWithMalformedJsonIsBadRequest() throws Exception {
        assertBadRequestOnlyFor("body", mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Teclado\", ")))
                .andExpect(jsonPath("$.errors.body").value("el cuerpo del request no es un JSON válido"));
    }

    // POST de un usuario con UserDto en el cuerpo del request

    @Test
    void createUserReturnsCreatedUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "María", "lastname": "González", "email": "maria.gonzalez@ejemplo.com", "age": 28}
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Usuario creado con éxito"))
                .andExpect(jsonPath("$.date", matchesPattern(ISO_DATE)))
                .andExpect(jsonPath("$.user.name").value("María"))
                .andExpect(jsonPath("$.user.lastname").value("González"))
                .andExpect(jsonPath("$.user.email").value("maria.gonzalez@ejemplo.com"))
                .andExpect(jsonPath("$.user.age").value(28));
    }

    // Valores en el límite que se aceptan: name y lastname de 3 y 20 caracteres
    @ParameterizedTest
    @CsvSource({"Ana, Paz", "Maximiliano Andrés J, Fernández de la Riva"})
    void createUserAcceptsNamesInRange(String name, String lastname) throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "%s", "lastname": "%s", "email": "test@ejemplo.com", "age": 30}
                        """.formatted(name, lastname)))
                .andExpect(status().isCreated());
    }

    // Cada fila envía un usuario con un solo campo inválido: el error solo trae ese campo
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            {"lastname": "Pérez", "email": "test@ejemplo.com", "age": 30}                              | name
            {"name": "   ", "lastname": "Pérez", "email": "test@ejemplo.com", "age": 30}               | name
            {"name": "Al", "lastname": "Pérez", "email": "test@ejemplo.com", "age": 30}                | name
            {"name": "Maximiliano Andrés Jo", "lastname": "Pérez", "email": "test@ejemplo.com", "age": 30} | name
            {"name": "Juan", "email": "test@ejemplo.com", "age": 30}                                    | lastname
            {"name": "Juan", "lastname": "", "email": "test@ejemplo.com", "age": 30}                    | lastname
            {"name": "Juan", "lastname": "Li", "email": "test@ejemplo.com", "age": 30}                  | lastname
            {"name": "Juan", "lastname": "Fernández de la Rivas", "email": "test@ejemplo.com", "age": 30} | lastname
            {"name": "Juan", "lastname": "Pérez", "age": 30}                                            | email
            {"name": "Juan", "lastname": "Pérez", "email": "", "age": 30}                               | email
            {"name": "Juan", "lastname": "Pérez", "email": "juan.perez", "age": 30}                     | email
            {"name": "Juan", "lastname": "Pérez", "email": "juan@@ejemplo.com", "age": 30}              | email
            {"name": "Juan", "lastname": "Pérez", "email": "@ejemplo.com", "age": 30}                   | email
            {"name": "Juan", "lastname": "Pérez", "email": "test@ejemplo.com"}                          | age
            {"name": "Juan", "lastname": "Pérez", "email": "test@ejemplo.com", "age": null}             | age
            {"name": "Juan", "lastname": "Pérez", "email": "test@ejemplo.com", "age": 0}                | age
            {"name": "Juan", "lastname": "Pérez", "email": "test@ejemplo.com", "age": 111}              | age
            """)
    void createUserWithInvalidDataIsBadRequest(String json, String field) throws Exception {
        assertBadRequestOnlyFor(field, mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)));
    }

    // JSON vacío: llega a la validación y muestra todos los campos obligatorios, no un error de lectura
    @Test
    void createUserWithEmptyJsonShowsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.*", hasSize(4)))
                .andExpect(jsonPath("$.errors.name").isNotEmpty())
                .andExpect(jsonPath("$.errors.lastname").isNotEmpty())
                .andExpect(jsonPath("$.errors.email").isNotEmpty())
                .andExpect(jsonPath("$.errors.age").isNotEmpty())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    // Edades en el límite que se aceptan: 1 y 110
    @ParameterizedTest
    @ValueSource(ints = {1, 110})
    void createUserAcceptsAgeInRange(int age) throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Juan", "lastname": "Pérez", "email": "juan.perez@ejemplo.com", "age": %d}
                        """.formatted(age)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.age").value(age));
    }

    @Test
    void createUserWithMalformedJsonIsBadRequest() throws Exception {
        assertBadRequestOnlyFor("body", mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": ")));
    }

    // Respuesta de error: status, error y errors, sin la excepción ni la traza
    private ResultActions assertBadRequestOnlyFor(String field, ResultActions result) throws Exception {
        String json = result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors.*", hasSize(1)))
                .andExpect(jsonPath("$.errors." + field).isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> body = JsonPath.read(json, "$");
        assertEquals(List.of("status", "error", "errors"), List.copyOf(body.keySet()));
        return result;
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
