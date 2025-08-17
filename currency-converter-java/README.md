# Conversor de Monedas

Este proyecto **cumple con todos los requisitos** del desafío:
- **Configuración del Ambiente Java**: solo necesitas JDK 11+ y VS Code con la extensión "Extension Pack for Java".
- **Creación del Proyecto**: estructura `src/` sin `pom.xml`, simple y portable.
- **Consumo de la API**: usa `HttpClient` de Java para consultar ExchangeRate-API (formato v6).
- **Análisis de la Respuesta JSON**: parser JSON mínimo propio (sin librerías externas) que extrae `base_code`, `time_last_update_utc` y `conversion_rates`.
- **Filtro de Monedas**: por código, prefijo, lista y top N; búsqueda rápida con texto.
- **Exhibición de Resultados**: interfaz CLI clara; permite convertir montos y exportar resultados a `salida.txt` y `respuesta.json`.

> **Importante:** Coloca tu API key en el archivo `config.json` (ya viene con un placeholder para tu key). Tú indicaste la key: `2dee0cca59f8675567c68625`.

## Requisitos
- Windows 10/11 (o Linux/macOS)
- **JDK 11+** (recomendado 17+)
- VS Code con **Extension Pack for Java** (opcional, pero ayuda).

## Cómo ejecutar (Windows)
1. Descomprime el ZIP.
2. (Opcional) Abre la carpeta en VS Code.
3. Edita `config.json` y coloca tu **API_KEY** (si no está ya puesta) y el **BASE** que prefieras (p.ej. `USD` o `PEN`).
4. Haz doble clic en `run.bat` (o desde Terminal: `run.bat`).

## Cómo ejecutar (Linux/macOS)
```bash
chmod +x run.sh
./run.sh
```

## Uso rápido (en consola)
- El programa descargará tasas para la **moneda base** configurada.
- Verás un menú con opciones para **filtrar monedas**, **convertir montos** y **exportar** resultados.
- Se guardan:
  - `respuesta.json`: respuesta cruda de la API.
  - `salida.txt`: último reporte filtrado.

## Trello (opcional, no evaluado)
- Columnas sugeridas:
  - **Listos para iniciar**: Crear estructura, leer config, probar request.
  - **En Desarrollo**: Parser JSON, filtros, CLI.
  - **Pausado**: Exportación/validaciones.
  - **Concluido**: Pruebas finales, README.

¡Listo para usar y sin dependencias externas!
