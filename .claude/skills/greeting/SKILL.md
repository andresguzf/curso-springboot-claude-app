---
name: greeting
description: Responde a los saludos del usuario de forma amable y amistosa, con el día, la fecha, la hora y el clima actuales de su ciudad (Santiago, Chile) y un pensamiento célebre para motivar el día. Úsalo siempre que el usuario salude, aunque no lo pida de forma explícita - "hola", "hola, ¿cómo estás?", "hola, ¿qué tal?", "buen día", "buenos días", "buenas tardes", "buenas noches", "buenas", "qué tal", "hey", "saludos" - incluso si el saludo viene junto a una pregunta o una tarea.
---

# Greeting

El usuario quiere que cada saludo se sienta cercano y le dé un empujón para empezar o seguir su día. La respuesta tiene tres partes: un saludo cálido, el contexto de su momento (día, fecha, hora y clima de su ciudad) y un pensamiento célebre que lo motive.

## 1. Obtén la fecha, la hora y el clima

Ejecuta el script incluido; usa la zona horaria de Santiago aunque el equipo esté configurado en otra:

```bash
bash .claude/skills/greeting/scripts/now.sh
```

Devuelve tres líneas (`fecha`, `hora`, `clima`). No inventes estos datos: si el clima sale "no disponible", omite esa parte sin comentarlo.

## 2. Responde

Escribe en español, con un tono cercano y alegre, sin exagerar. Usa este formato:

```
¡Hola, Andrés! 👋 [frase amable, adaptada a la hora: buenos días / buenas tardes / buenas noches]

📅 [Día], [fecha] · 🕐 [hora] en Santiago · 🌤️ [clima]

💭 *"[pensamiento célebre]"*
— [autor u origen]

[una línea breve que conecte el pensamiento con el día o lo anime]
```

- **Saludo según la hora:** antes de las 12:00 "buenos días", hasta las 20:00 "buenas tardes", después "buenas noches". Si el usuario usó un saludo que no calza con la hora, responde con naturalidad sin corregirlo.
- **Pensamiento célebre:** elige uno real y bien atribuido: proverbios chinos o japoneses, filósofos, científicos, escritores o figuras conocidas. Varía el origen y no repitas uno que ya hayas usado en esta conversación. Si no estás seguro del autor, usa un proverbio y atribúyelo a su origen (por ejemplo, "Proverbio chino").
- **Emoji del clima:** elige uno acorde (☀️ despejado, ⛅ nublado parcial, ☁️ nublado, 🌧️ lluvia, ⛈️ tormenta, ❄️ nieve).

## 3. Si el saludo trae algo más

Si el saludo viene con una pregunta o una tarea ("hola, ¿me ayudas con el controlador?"), saluda con el formato de arriba en versión breve y luego atiende la petición en la misma respuesta. El saludo no debe retrasar el trabajo que pidió.
