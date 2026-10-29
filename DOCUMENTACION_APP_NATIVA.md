# 📱 Copiloto de Entrevistas IA — Versión Android Nativa 2.0 (Búsqueda Instantánea & Teleprompter)

**Candidato:** Javier Viveros Huesca  
**Especialidades:** Senior RF Optimization & 5G NR Specialist (Ericsson) | Senior Full-Stack con IA  
**Versión:** 2.0.0 Nativa (Android SDK 34 / Java 17)  
**Enfoque de Operación:** Búsqueda Instantánea por Palabras Clave (Sin micrófono, 100% precisa y libre de inconsistencias)

---

## 🚀 Novedades de la Versión 2.0 (Basada en el Repositorio Contestar-Entrevista)

### 1. Eliminación de Inconsistencias de Micrófono
- **Problema en la versión previa:** En reuniones virtuales, el reconocimiento de voz por micrófono presentaba desfases, cortes por ruido ambiente o capturas incorrectas de tecnicismos (`dynamic`, `B2`, `RTWP`, `PIM`, `5QI`).
- **Solución implementada:** La aplicación ahora está **centrada en un campo de texto de búsqueda instantánea**. Conforme escribes (incluso una o dos palabras como `dynamic B2`, `rtwp`, `lms`, `cisco`, `sueldo`), la app filtra en tiempo real en menos de **5 milisegundos**.

### 2. Integración Total de la Guía Oficial (13 Módulos y 48 Preguntas)
La base de conocimiento local embebida en la app contiene los **12 módulos ampliados de `Contestar-Entrevista`** más el módulo de **Full-Stack con IA**:
- **Módulo 1:** Presentación profesional, Fit Cultural (Metodología STAR) y valor inmediato.
- **Módulo 2:** Ecosistema Ericsson, Hardware (Baseband 5216/6630/6648, AIR 6449, Radios 4415/2219) y MOM.
- **Módulo 3:** Inmersión técnica en los 5 Pilares de KPIs (Accesibilidad, Retenibilidad/Drop, Movilidad, Throughput, Disponibilidad / Sleeping Cells).
- **Módulo 4:** Especialización en 5G NR NSA / EN-DC (Opción 3x, SgNB Addition, Movilidad y PSCell change).
- **Módulo 5:** Funcionalidades avanzadas de RAN Ericsson (Carrier Aggregation CA, Massive MIMO, Beamforming, Mobility Load Balancing MLB, Dynamic Spectrum Sharing DSS).
- **Módulo 6:** Casos prácticos de resolución de problemas (RCA: Call Drops post-upgrade, Drive Test 5G n78, Interferencia RTWP vs PIM, HOSR 88%).
- **Módulo 7:** Metodología Pre-Post optimización formal.
- **Módulo 8:** Preguntas conductuales y de gestión de desacuerdos.
- **Módulo 9:** Preguntas estratégicas que Javier debe hacer al entrevistador.
- **Módulo 10:** Cheat Sheet rápido de parámetros y comandos Moshell / AMOS.
- **Módulo 11:** Dominio técnico avanzado 2022-2026: LMS (Layer Management Strategy), Traffic Steering, y 3 Casos Prácticos Reales en redes multi-capa.
- **Módulo 12:** Pronunciación fonética de términos de telecomunicaciones y frases de transición en inglés.
- **Módulo 13:** Full-Stack moderno, IA Open Source (vLLM, Llama 3, Mistral, RAG con pgvector), Clean Architecture y Networking (Switch Cisco, Capa 2/3).

### 3. Algoritmo de Búsqueda Ponderada Técnica
- Algoritmo con puntuación de relevancia para acrónimos críticos (`moshell`, `amos`, `kpi`, `rtwp`, `pim`, `dynamic`, `b2`, `a3`, `lms`, `nsa`, `ca`, `mimo`, `switch`, `cisco`, etc.).
- Normalización fonética y de acentos (NFD).
- **Ordenamiento inteligente:** Los resultados se ordenan de menor a mayor por número de módulo y número de pregunta, facilitando la lectura secuencial.

### 4. Teleprompter con IA Gemini y Refinamiento
- **Tocar cualquier pregunta de los resultados:** Muestra de inmediato la estrategia, la respuesta modelo ultra estructurada y los comandos/fórmulas técnicas.
- **Botón `🤖 Consultar a Gemini`:** Si la pregunta del entrevistador es improvisada o quieres una respuesta sintética inmediata, Gemini genera en 1 segundo una respuesta senior teleprompter.
- **Botones de Refinamiento:**
  - `⚡ Más breve`: Condensa la respuesta a 2 viñetas clave listas para teleprompter.
  - `🔍 Más detalle`: Profundiza con comandos Moshell y parámetros técnicos.
  - `📋 Copiar`: Copia la respuesta completa al portapapeles.

---

## 📥 Instalación del APK

El APK generado se encuentra en tu Escritorio:
📁 **`C:\Users\PC\Desktop\Copiloto_Entrevista_Javier.apk`**

### Instrucciones en el Celular:
1. Pásalo a tu celular Android (por WhatsApp Web, Telegram, Google Drive o USB).
2. Toca el archivo para instalarlo.
3. ¡No requiere permisos de micrófono! La aplicación abre de inmediato y funciona con o sin conexión a internet para el catálogo local, y con conexión a internet para las consultas de Gemini.

---

## 🎯 Guía de Uso Rápido en la Entrevista

1. **Colocación:** Pon el celular frente a ti sobre tu escritorio. La pantalla se mantendrá encendida automáticamente.
2. **Durante la pregunta:** En cuanto escuches la temática que formula el entrevistador, escribe rápidamente las palabras clave en el campo superior (ej: `dynamic b2` o `pim` o `cisco`).
3. **Lectura instantánea:** Toca la coincidencia que aparece en la lista; la respuesta modelo se abrirá al instante en el teleprompter con puntos técnicos contundentes.
4. **Si el entrevistador hace una pregunta abierta:** Escríbela y presiona **🤖 Consultar a Gemini** para que la IA elabore una respuesta a tu medida con tu perfil.
