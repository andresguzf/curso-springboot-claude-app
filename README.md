# 1-spring-app

Proyecto base de **Spring Boot 4** para construir APIs REST con Java 25. Incluye recarga en caliente con DevTools, documentación interactiva con Swagger UI y monitoreo con Actuator, listo para empezar a agregar lógica de negocio.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java (JDK) | 25 |
| Spring Boot | 4.1.2 |
| Maven | wrapper incluido (`mvnw`) |
| SpringDoc OpenAPI | 3.2.0 |

Dependencias:

- **Spring Web**: para crear APIs REST con Spring MVC.
- **Validation**: valida los datos de entrada con anotaciones como `@NotBlank`, `@Size` o `@Email`.
- **Jackson XML**: permite responder en XML además de JSON.
- **Spring Boot DevTools**: reinicia la aplicación automáticamente al recompilar y recarga el navegador con LiveReload.
- **Spring Boot Actuator**: endpoints de salud e información de la aplicación.
- **SpringDoc OpenAPI**: genera la especificación OpenAPI y la interfaz de Swagger UI.

## Requisitos

- JDK 25

No hace falta instalar Maven: el proyecto incluye el wrapper `mvnw`.

## Cómo ejecutarlo

Clona el repositorio y entra en la carpeta del proyecto:

```bash
git clone <url-del-repositorio>
cd 1-spring-app
```

Arranca la aplicación:

```bash
./mvnw spring-boot:run
```

En Windows usa `mvnw.cmd spring-boot:run`.

La aplicación queda disponible en http://localhost:8080.

Para compilar y ejecutar los tests:

```bash
./mvnw package
```

## Endpoints

Todos los endpoints de la API están bajo el prefijo `/api`.

### Ejemplos de respuesta (GET)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/greeting` | Saludo y fecha actual, armados con un `Map` |
| `GET` | `/api/user` | Saludo, fecha y un usuario anidado en un `Map`. Responde en JSON o XML |
| `GET` | `/api/user-dto` | Lo mismo que `/api/user`, pero con los records `MessageDto` y `UserDto` |
| `GET` | `/api/user-response-entity` | Igual que `/api/user-dto`, con `ResponseEntity`. Devuelve las cabeceras `X-Color` y `X-Token`, y en el JSON incluye las cabeceras del request `X-UserName` (por defecto `Anonymous`) y `Authorization` (obligatoria) |

### Parámetros de la URL

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/hello?name=...&limit=...&page=...` | Saluda por el nombre. Parámetros del query string obligatorios: `name` (4 a 14 caracteres), `limit` (5 a 50) y `page` (desde 0) |
| `GET` | `/api/hello/{name}/{age}` | Saluda con el nombre y la edad de la ruta: `name` de 3 a 18 caracteres y `age` de 1 a 110 |
| `GET` | `/api/products/{id}` | Devuelve el `productId` de la ruta. `id` numérico entre 1 y 45 |
| `GET` | `/api/code/{code}` | Devuelve el `productCode` de la ruta. `code` de 3 a 10 caracteres |

### Creación con POST

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/products` | Recibe un producto en JSON y responde `201` con el producto y un `id` UUID generado por el servidor |
| `POST` | `/api/users` | Recibe un usuario en JSON y responde `201` con el usuario y la fecha |

Validaciones del producto:

- `name`: obligatorio, de 4 a 40 caracteres.
- `price`: obligatorio y mayor que 2, con hasta 10 dígitos enteros y 6 decimales (`BigDecimal`).
- `quantity`: obligatoria y mayor que 1.

Validaciones del usuario:

- `name` y `lastname`: obligatorios, de 3 a 20 caracteres.
- `email`: obligatorio y con formato de correo válido.
- `age`: obligatoria, entre 1 y 110.
- `rut`: obligatorio y válido según su dígito verificador (validación personalizada `@Rut`). Acepta `12.345.678-5`, `12345678-5` o `123456785`.

Ejemplo para crear un usuario (los datos son ficticios):

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Juan", "lastname": "Pérez", "email": "juan.perez@ejemplo.com", "age": 30, "rut": "12.345.678-5"}'
```

### Documentación y monitoreo

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/swagger-ui.html` | Interfaz de Swagger UI para documentar y probar la API |
| `GET` | `/v3/api-docs` | Especificación OpenAPI en JSON |
| `GET` | `/actuator/health` | Estado de salud de la aplicación |
| `GET` | `/actuator/info` | Información de la aplicación |

Ejemplo de respuesta de `GET /api/greeting`:

```json
{
  "message": "¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente.",
  "date": "2026-11-05T19:03:43.956435"
}
```

### Respuesta en JSON o XML

Los endpoints `GET` pueden responder en JSON (por defecto) o en XML. El formato se elige con el parámetro `format` de la URL o con la cabecera `Accept`. Si vienen los dos, manda el parámetro:

```bash
curl "http://localhost:8080/api/user?format=xml"
curl -H "Accept: application/xml" http://localhost:8080/api/user
```

### Errores de validación

Cuando un dato no es válido, la API responde `400` solo con cada campo y su mensaje, sin mostrar la excepción. De esto se encarga `GlobalExceptionHandler`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "name": "el tamaño debe estar entre 3 y 20",
    "rut": "no es un RUT chileno válido"
  }
}
```

Si el cuerpo del request no es un JSON válido, el error aparece en el campo `body`.

## Documentación de la API con Swagger UI

Con la aplicación en marcha, abre http://localhost:8080/swagger-ui.html. Desde ahí puedes ver todos los endpoints y probarlos con el botón **Try it out**.

## Recarga en caliente con DevTools

DevTools reinicia la aplicación cada vez que cambian las clases compiladas, así que no hace falta detenerla y volver a arrancarla:

- **IntelliJ IDEA**: usa *Build > Build Project* (`Cmd+F9` en macOS, `Ctrl+F9` en Windows/Linux), o activa *Build project automatically* en la configuración del compilador.
- **VS Code / Eclipse**: recompilan al guardar, así que el reinicio es automático.

Los cambios en `static/` y `templates/` solo recargan el navegador, sin reiniciar la aplicación.

## Estructura del proyecto

```
src/main/java/com/andres/course/claude/springboot/
├── Application.java   # Clase principal
├── controllers/       # Controladores REST
├── entities/          # Entidades del dominio que se persisten
├── exceptions/        # Manejo global de errores (GlobalExceptionHandler)
├── models/            # Modelos y DTOs de las APIs
├── repositories/      # Acceso a datos
├── services/          # Lógica de negocio
└── validations/       # Validaciones personalizadas, como @Rut
```

La configuración está en `src/main/resources/application.properties`: puerto del servidor, formato JSON/XML, DevTools, SpringDoc/Swagger UI y Actuator.
