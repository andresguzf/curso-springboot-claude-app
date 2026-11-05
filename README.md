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

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/greetings` | Endpoint de ejemplo que devuelve un saludo y la fecha actual en JSON |
| `GET` | `/swagger-ui.html` | Interfaz de Swagger UI para documentar y probar la API |
| `GET` | `/v3/api-docs` | Especificación OpenAPI en JSON |
| `GET` | `/actuator/health` | Estado de salud de la aplicación |
| `GET` | `/actuator/info` | Información de la aplicación |

Ejemplo de respuesta de `GET /api/greetings`:

```json
{
  "message": "¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente.",
  "date": "2026-11-05T19:03:43.956435"
}
```

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
├── models/            # Modelos y DTOs de las APIs
├── repositories/      # Acceso a datos
└── services/          # Lógica de negocio
```

La configuración está en `src/main/resources/application.properties`: puerto del servidor, DevTools, SpringDoc/Swagger UI y Actuator.
