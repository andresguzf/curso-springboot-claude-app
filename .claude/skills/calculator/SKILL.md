---
name: calculator
description: Calculadora para listas de números separados por coma. Cuando el usuario envíe números separados por coma (por ejemplo "23,56,78" o "10, 4.5, 2"), ofrécele sumar, restar, multiplicar, dividir o sacar el promedio, y calcula el resultado exacto con el script incluido. Úsalo siempre que el mensaje sea una lista de números separados por coma, aunque no diga qué hacer con ellos, y también cuando pida directamente una de esas operaciones sobre la lista ("suma 3,5,8", "promedio de 7,8,9").
---

# Calculator

El usuario usa esta conversación como calculadora rápida: pega números separados por coma y quiere elegir qué hacer con ellos sin tener que explicarlo. Los cálculos se hacen con un script para que el resultado sea exacto y no dependa de hacer aritmética mental.

## 1. Interpreta los números

- La coma separa los números; los decimales van con punto (`1.5, 2, 3`).
- Si el usuario escribe algo ambiguo, como `1,5` cuando quizá quiso decir 1,5 (decimal chileno), trátalo como dos números (1 y 5) y menciónalo en una línea para que pueda corregirlo.

## 2. Pregunta la operación (si no la indicó)

Si el mensaje trae solo los números, muestra los números detectados y ofrece el menú, y espera la respuesta:

```
Números: 23, 56, 78

¿Qué quieres calcular?
1. ➕ Sumar
2. ➖ Restar
3. ✖️ Multiplicar
4. ➗ Dividir
5. 📊 Promedio
6. 🧮 Todas
```

El usuario puede responder con el número de la opción o con la palabra ("suma", "promedio", "todas"). Si ya indicó la operación en el mensaje ("multiplica 2,3,4"), sáltate el menú y calcula directamente.

## 3. Calcula con el script

```bash
python3 .claude/skills/calculator/scripts/calc.py "23,56,78" sumar
```

Operaciones: `sumar`, `restar`, `multiplicar`, `dividir`, `promedio`, `todas`. Restar y dividir van de izquierda a derecha (23 − 56 − 78). Si hay división por cero, el script lo indica en vez de fallar.

## 4. Responde

Muestra la operación completa y el resultado en negrita, con el formato que entrega el script (punto para miles y coma para decimales):

```
➕ 23 + 56 + 78 = **157**
```

Para `todas`, usa una línea por operación con su emoji. Mantén la respuesta breve: es una calculadora, no una explicación.
