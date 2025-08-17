# 💱 Conversor de Monedas - Desafío Alura Latam

Este proyecto fue desarrollado como parte del **Challenge de Alura Latam**, donde el objetivo principal es **crear un conversor de monedas funcional en Java** utilizando una API de tasas de cambio en tiempo real y procesando los datos con la librería **Gson**.

---

## 📌 Objetivos del Proyecto

✅ Conectar a una **API de tasas de cambio** para obtener información actualizada de monedas.  
✅ Procesar los datos en formato **JSON** utilizando la librería Gson.  
✅ Implementar un **menú interactivo** en consola para que el usuario seleccione las monedas a convertir.  
✅ Realizar la conversión de manera dinámica según el valor obtenido de la API.  
✅ Mantener un **código limpio, organizado y modular**.

---

## 🛠️ Tecnologías utilizadas

- **Java 17** ☕
- **Gson 2.13.1** 📦 (para parsear JSON)
- **API de Exchange Rates** 🌍
- **Git & GitHub** (control de versiones)

---

## 🚀 Funcionamiento

1. El usuario ejecuta el programa.  
2. Se conecta automáticamente a la **API de monedas** para obtener las tasas de cambio más recientes.  
3. Se muestra un **menú interactivo** en consola donde se puede elegir:
   1) Ver total de monedas disponibles
   2) Filtrar por prefijo (ej. 'US', 'PE')
   3) Buscar por texto (contiene)
   4) Top N monedas más comunes (USD, EUR, GBP, JPY, PEN, BRL, MXN, ARS, CLP, COP)
   5) Convertir monto entre dos monedas
   6) Exportar resultados filtrados a salida.txt
   7) Cambiar moneda base y actualizar
   0) Salir
   


