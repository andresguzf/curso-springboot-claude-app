# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

API REST base con Spring Boot 4.1.2, Java 25 y Maven. Proyecto de curso: el código se muestra en video y se publica en GitHub, así que se prefiere lo simple y didáctico.

## Comandos

Se usa el wrapper de Maven (`./mvnw`); no hay linter ni formateador configurado.

```bash
./mvnw spring-boot:run                                   # arrancar en http://localhost:8080
./mvnw compile                                           # recompilar (DevTools reinicia la app en marcha)
./mvnw package                                           # compilar + tests + jar en target/
./mvnw test                                              # todos los tests
./mvnw test -Dtest=ApplicationTests#contextLoads         # un solo test
```

## Desarrollo con la app en marcha

- Lo habitual es que la app ya esté corriendo desde el IDE en el puerto 8080 con DevTools. Antes de arrancar otra instancia, comprueba si el puerto está ocupado; si lo está, prueba contra esa instancia.
- Para que un cambio se aplique en caliente basta con `./mvnw compile`. **No uses `./mvnw clean`** mientras la app corre: borra `target/classes` y DevTools reinicia la app sin clases, lo que provoca 404 temporales.

## Arquitectura

- Paquete raíz y groupId: `com.andres.course.claude.springboot`. Capas en subpaquetes: `controllers`, `services`, `repositories`, `entities`, `models` (DTOs). Los paquetes aún vacíos tienen un `package-info.java` para que Git los conserve; no los borres.
- `GreetingController` (`GET /api/greetings`) es el endpoint de ejemplo: devuelve un `Map<String, Object>` (no un record/DTO, por decisión del usuario) que Spring serializa a JSON.
- Toda la configuración va en `src/main/resources/application.properties` (no YAML): DevTools, SpringDoc y Actuator (solo `health` e `info` expuestos).
- Swagger UI en `/swagger-ui.html` y OpenAPI en `/v3/api-docs`. SpringDoc solo escanea `com.andres.course.claude.springboot` (`springdoc.packages-to-scan`), así que los controladores deben estar bajo ese paquete.

## Available skills

| Skill | Ubicación | Qué hace |
|---|---|---|
| `greeting` | `.claude/skills/greeting/` | Responde a los saludos con cariño, el día, la fecha, la hora y el clima de Santiago, y un pensamiento célebre motivador |

## Skills trigger rules

- **`greeting`**: úsalo automáticamente, sin que el usuario lo pida, cada vez que salude. Por ejemplo: "hola", "hola, ¿cómo estás?", "hola, ¿qué tal?", "buen día", "buenos días", "buenas tardes", "buenas noches", "buenas", "qué tal", "hey", "saludos". También cuando el saludo viene junto a una pregunta o tarea: saluda primero, en versión breve, y luego atiende la petición.

## Particularidades de Spring Boot 4

- El starter web se llama `spring-boot-starter-webmvc` (y `spring-boot-starter-webmvc-test` para tests), no `spring-boot-starter-web`.
- La versión del parent en `pom.xml` es `4.1.2`. Spring Initializr la genera como `4.1.2.RELEASE`, que no existe en Maven Central.
- SpringDoc usa la línea 3.x (`springdoc-openapi-starter-webmvc-ui` 3.2.0), compatible con Boot 4.
- No hay base de datos todavía: para usar `entities` y `repositories` falta agregar Spring Data JPA y un driver.
