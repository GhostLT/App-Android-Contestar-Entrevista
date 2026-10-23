# 📱 Copiloto de Entrevistas IA — Aplicación Nativa Android

**Candidato:** Javier Viveros Huesca  
**Posición Objetivo:** Ingeniero Senior Full-Stack con IA  
**Empresa / Plataforma:** PCoS / Hireline (Entrevistadora: Angélica)  
**Versión:** 1.0.0 Nativa (Android SDK 34 / Java 17)

---

## 🚀 Resumen del Proyecto

Esta aplicación es un **copiloto nativo autónomo para dispositivos Android**, diseñado para ejecutarse discretamente en tu teléfono móvil colocado en tu escritorio frente a ti durante una reunión o entrevista técnica virtual.

A diferencia de la versión de escritorio basada en Python, esta versión:
1. **Es 100% nativa de Android**: No requiere servidor Python, ni PC encendida, ni cables adicionales. Funciona de manera autónoma en el celular conectado a WiFi o datos móviles.
2. **Reconocimiento de voz continuo**: Escucha las preguntas del entrevistador a través del micrófono del celular con auto-detección de silencios.
3. **Caja de corrección manual**: Muestra el texto detectado en un cuadro editable donde puedes tocar, corregir o escribir manualmente cualquier palabra antes de enviar la consulta a Gemini.
4. **Respuestas ultrarrápidas con Gemini**: Conexión directa a Google Gemini (`gemini-3.5-flash-lite`, `gemini-3.1-flash-lite` y `gemini-3.8-flash`) para generar respuestas de nivel Senior directas y al grano.
5. **Formato Teleprompter OLED**: Optimizado para lectura rápida a un vistazo:
   - 🎯 **Respuesta Inmediata**: 1 a 2 oraciones directas listas para decir en voz alta.
   - ⚡ **Puntos Clave**: 2 o 3 viñetas técnicas con protocolos y arquitectura.
   - 💡 **Aplicación Real**: 1 frase conectando con casos reales de producción.
6. **Filtro de charla casual y ruido**: Si el audio detectado es solo un saludo ("¿me escuchan?", "buenos días", ruidos de tos), la IA responde `[IGNORAR]` para no saturar tu pantalla.
7. **Bilingüe instantáneo (ES / EN)**: Conmutador en la barra superior para cambiar entre entrevistas en español o en inglés con un solo toque.
8. **Pestaña "Mi Pitch & Formación"**: Contiene tu CV completo, métricas clave (-65% costos con vLLM, 99.999% SLA en AT&T, etc.) y las **7 preguntas oficiales del proceso de Angélica**, con respuestas cortas y explicaciones desplegables.
9. **Historial local en SQLite**: Todas las preguntas y respuestas se guardan en la base de datos interna del celular (`entrevistas.db`) con buscador en tiempo real y opción de copiar o limpiar.
10. **Ajustes de hardware**: Mantener pantalla encendida (`FLAG_KEEP_SCREEN_ON`) para evitar bloqueos durante la reunión y vibración táctil discreta cuando llega la respuesta.

---

## 🛠️ Arquitectura Técnica

### Stack de Desarrollo
- **Lenguaje**: Java 17
- **Target SDK**: Android 14 (API 34)
- **Min SDK**: Android 8.0 Oreo (API 26) — Compatible con el 98% de teléfonos Android activos
- **UI / Layout**: Material Design 3, ConstraintLayout, CardView, RecyclerView, BottomNavigationView
- **Networking**: OkHttp 4.12.0 con reintentos automáticos y timeout optimizado (15s/25s)
- **JSON**: Google Gson 2.10.1
- **Voz**: `android.speech.SpeechRecognizer` nativo con detección parcial y bucle continuo
- **Persistencia**: SQLite local con `SQLiteOpenHelper`

### Estructura del Código
```
app/src/main/
├── AndroidManifest.xml
├── java/com/javierviveros/entrevistacopilot/
│   ├── CandidateProfile.java     # Perfil de Javier, 7 preguntas clave, trayectoria y System Prompt
│   ├── DatabaseHelper.java       # Manejador SQLite para el historial local de preguntas y respuestas
│   ├── GeminiService.java        # Cliente REST asíncrono con cadena de modelos (fallback)
│   ├── HistoryAdapter.java       # Adaptador RecyclerView para el historial de conversaciones
│   ├── PitchAdapter.java         # Adaptador con tarjetas desplegables para las 7 preguntas
│   └── MainActivity.java         # Controlador central: navegación, audio, teleprompter y ajustes
└── res/
    ├── layout/
    │   ├── activity_main.xml     # Vista principal con 4 pestañas desacopladas
    │   ├── item_history.xml      # Tarjeta de elemento del historial
    │   └── item_pitch_qa.xml     # Tarjeta desplegable para preguntas de Angélica
    ├── menu/
    │   └── bottom_nav_menu.xml   # Barra de navegación inferior (En Vivo, Historial, Mi Pitch, Ajustes)
    └── values/
        ├── colors.xml            # Paleta Dark OLED de bajo consumo y alto contraste
        ├── strings.xml           # Textos y localizaciones
        └── themes.xml            # Tema Material 3 Dark NoActionBar
```

---

## 📥 Cómo Instalar el APK en tu Celular Android

El archivo compilado listo para instalar se encuentra en:
📁 **`C:\Users\PC\Desktop\Copiloto_Entrevista_Javier.apk`**

### Pasos de Instalación:
1. **Transferir el archivo al celular**:
   - Puedes enviártelo por WhatsApp Web, Telegram, Google Drive, o conectando el celular por cable USB y copiándolo a la carpeta `Descargas` (Downloads).
2. **Habilitar instalación de orígenes desconocidos**:
   - Al tocar el archivo `.apk` en el celular, Android te preguntará: *"Por seguridad, tu teléfono no tiene permitido instalar apps desconocidas de esta fuente"*.
   - Presiona **Ajustes** -> Marca **Permitir desde esta fuente** (o "Instalar apps desconocidas").
3. **Instalar y Abrir**:
   - Toca **Instalar**. En pocos segundos aparecerá el ícono de **Copiloto Entrevista IA**.
4. **Conceder Permisos**:
   - Al pulsar **🎙️ INICIAR ESCUCHA ACTIVA**, la app te solicitará permiso para **Grabar Audio / Micrófono**. Selecciona **"Mientras la app esté en uso"**.

---

## 🎯 Guía de Uso Durante la Entrevista

1. **Ubicación del Celular**:
   - Coloca el celular en un soporte o recostado debajo de tu monitor / laptop.
   - La pantalla se mantendrá encendida automáticamente sin bloquearse.
2. **Iniciar Escucha**:
   - Presiona el botón verde/azul **🎙️ INICIAR ESCUCHA ACTIVA**.
   - El indicador superior cambiará a `🎙️ ESCUCHANDO` en verde.
3. **Detección Automática**:
   - Habla el entrevistador: verás cómo sus palabras se transcriben en vivo en el cuadro de texto.
   - Al haber una pausa de silencio de ~1.5 segundos, la app enviará la pregunta a Gemini.
4. **Corrección Manual (Opcional)**:
   - Si el micrófono captó una palabra técnica de forma imprecisa (por ejemplo "switch" como "suich"), simplemente toca el cuadro de texto, edítalo rápidamente y pulsa **🚀 Enviar a Gemini**.
5. **Lectura en Teleprompter**:
   - Tu teléfono vibrará suavemente cuando la respuesta esté lista (tarda entre 0.8 y 1.5 segundos).
   - Lee la **🎯 Respuesta Inmediata** mientras mantienes contacto visual natural con la cámara.
6. **Botones de Refinamiento**:
   - **⚡ Más breve**: Si la respuesta es larga, este botón pide un resumen ultra compacto en 2 viñetas.
   - **🔍 Más detalle**: Si el entrevistador repregunta "cuéntame más", este botón profundiza en arquitectura y métricas.
7. **Pestaña Mi Pitch**:
   - Si te preguntan sobre tu expectativa económica o sobre vLLM, puedes tocar rápidamente la pestaña **Mi Pitch** para ver el acordeón con las 7 respuestas preparadas para Hireline.
