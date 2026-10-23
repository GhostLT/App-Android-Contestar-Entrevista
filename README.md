# 📱 Copiloto de Entrevistas IA — Android Nativo

Aplicación Android nativa para asistencia en entrevistas de trabajo técnicas en tiempo real, impulsada por Google Gemini y personalizada para el perfil de **Javier Viveros Huesca** (Ingeniero Senior Full-Stack con IA).

---

## 🌟 Características Principales

- 🎙️ **Modo Escucha Activa**: Reconocimiento de voz continuo sin interrupciones que detecta preguntas del entrevistador.
- ✏️ **Edición y Corrección Manual**: Campo de texto editable para modificar o afinar cualquier palabra captada por el micrófono antes de consultar a la IA.
- ⚡ **Teleprompter de Alta Velocidad**: Respuestas estructuradas con formato directo:
  - 🎯 **Respuesta Inmediata**: 1-2 oraciones para decir al instante.
  - ⚡ **Puntos Clave**: Viñetas con protocolos, librerías y arquitectura.
  - 💡 **Aplicación / Producción**: Caso de éxito real.
- 🌐 **Soporte Bilingüe Instantáneo**: Alternancia rápida entre Español (`es-ES`) e Inglés (`en-US`) con un solo toque.
- 🔇 **Filtro de Ruido & Charla Casual**: Ignora automáticamente saludos, tos, ruidos o frases de cortesía (`[IGNORAR]`).
- 📋 **Pestaña "Mi Pitch & Formación"**:
  - Resumen ejecutivo de postulación.
  - Las 7 preguntas y respuestas oficiales del proceso técnico (Hireline / PCoS / Angélica).
  - Trayectoria laboral detallada (Empire Engineering, AT&T, Nextel & Nokia).
- 💾 **Historial Local SQLite**: Almacenamiento privado y permanente de todas las preguntas de la entrevista en el celular, con buscador integrado.
- 📱 **Optimizaciones para Entrevistas**:
  - Pantalla siempre encendida para evitar bloqueos durante la videollamada.
  - Vibración táctil discreta cuando llega la respuesta.
  - Preguntas rápidas frecuentes en un toque (Switch Cisco, Arquitectura RAG, vLLM, Clean Architecture, Salario).

---

## 🚀 Instalación

El archivo APK compilado se encuentra listo para instalar en:
`C:\Users\PC\Desktop\Copiloto_Entrevista_Javier.apk`

1. Pásalo a tu celular Android por cable USB, WhatsApp o Google Drive.
2. Ábrelo e instálalo (permite "Instalar apps de orígenes desconocidos").
3. Concede el permiso de micrófono.
4. ¡Listo! Ya viene con tu API Key y configuración precargada para funcionar de inmediato.

---

## 💻 Compilación desde Código Fuente

Requisitos en PC:
- OpenJDK 17 (`JAVA_HOME`)
- Android SDK (API 34, Build-Tools 34.0.0)

Comando de compilación:
```bash
./gradlew assembleDebug
```
El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.
