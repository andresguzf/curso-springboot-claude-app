#!/usr/bin/env bash
# Imprime el día, la fecha, la hora y el clima actual de la ciudad del usuario.
# Uso: now.sh [ciudad] [zona-horaria]

CITY="${1:-Santiago}"
ZONE="${2:-America/Santiago}"

echo "fecha: $(TZ="$ZONE" LC_TIME=es_ES.UTF-8 date '+%A %-d de %B de %Y')"
echo "hora: $(TZ="$ZONE" date '+%H:%M')"

# El clima es opcional: si no hay conexión o el servicio falla, se omite.
WEATHER=$(curl -s -m 5 "https://wttr.in/${CITY}?format=%C+%t&lang=es&m" 2>/dev/null)
if [[ -n "$WEATHER" && "$WEATHER" != *"Unknown"* && "$WEATHER" != *"<"* ]]; then
  echo "clima: $WEATHER"
else
  echo "clima: no disponible"
fi
