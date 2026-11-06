"""Calculadora para una lista de números separados por coma.

Uso:
    python3 calc.py "<números>" <operación>

Operaciones: sumar, restar, multiplicar, dividir, promedio, todas.
Las restas y divisiones se hacen de izquierda a derecha (a - b - c, a / b / c).
Los decimales se escriben con punto: "1.5, 2, 3".
"""

import sys
from fractions import Fraction

OPERATIONS = ("sumar", "restar", "multiplicar", "dividir", "promedio")


def parse(text):
    parts = [p.strip() for p in text.split(",") if p.strip()]
    if not parts:
        raise ValueError("no se encontraron números")
    try:
        return [Fraction(p) for p in parts]
    except ValueError:
        raise ValueError(f"valor no numérico en: {text!r}")


def calculate(numbers, operation):
    result = numbers[0]
    if operation == "sumar":
        return sum(numbers)
    if operation == "promedio":
        return sum(numbers) / len(numbers)
    for n in numbers[1:]:
        if operation == "restar":
            result -= n
        elif operation == "multiplicar":
            result *= n
        elif operation == "dividir":
            if n == 0:
                raise ZeroDivisionError("no se puede dividir por cero")
            result /= n
    return result


def fmt(value):
    # Enteros sin decimales; el resto con hasta 6 decimales y coma decimal.
    if value.denominator == 1:
        text = f"{value.numerator:,}".replace(",", ".")
    else:
        text = f"{float(value):,.6f}".rstrip("0").rstrip(".")
        text = text.replace(",", "_").replace(".", ",").replace("_", ".")
    return text


def symbol(operation):
    return {"sumar": " + ", "restar": " − ", "multiplicar": " × ", "dividir": " ÷ "}.get(operation)


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    try:
        numbers = parse(sys.argv[1])
    except ValueError as e:
        sys.exit(f"error: {e}")
    op = sys.argv[2].lower()
    ops = OPERATIONS if op == "todas" else (op,)
    for o in ops:
        if o not in OPERATIONS:
            sys.exit(f"operación desconocida: {o}")
        try:
            value = fmt(calculate(numbers, o))
        except ZeroDivisionError as e:
            value = f"error: {e}"
        if o == "promedio":
            expr = f"({' + '.join(fmt(n) for n in numbers)}) ÷ {len(numbers)}"
        else:
            expr = symbol(o).join(fmt(n) for n in numbers)
        print(f"{o}: {expr} = {value}")


if __name__ == "__main__":
    main()
