package com.javierviveros.entrevistacopilot;

import java.util.ArrayList;
import java.util.List;

public class CandidateProfile {

    public static class QuestionItem {
        public int id;
        public String title;
        public String question;
        public String shortAnswer;
        public String fullAnswer;

        public QuestionItem(int id, String title, String question, String shortAnswer, String fullAnswer) {
            this.id = id;
            this.title = title;
            this.question = question;
            this.shortAnswer = shortAnswer;
            this.fullAnswer = fullAnswer;
        }
    }

    public static class ExperienceItem {
        public String company;
        public String role;
        public String period;
        public String location;
        public String[] highlights;

        public ExperienceItem(String company, String role, String period, String location, String[] highlights) {
            this.company = company;
            this.role = role;
            this.period = period;
            this.location = location;
            this.highlights = highlights;
        }
    }

    public static final String CANDIDATE_NAME = "Javier Viveros Huesca";
    public static final String CANDIDATE_TITLE = "Ingeniero Senior Full-Stack con IA";
    public static final String CANDIDATE_EMAIL = "viverosjavier86@gmail.com";
    public static final String CANDIDATE_PHONE = "+52 (669) 533-0508";
    public static final String CANDIDATE_LOCATION = "Mazatlán, Sinaloa, México (Remoto)";
    public static final String CANDIDATE_SALARY = "$2,500 USD mensuales (Contractor / Tiempo Completo Remoto)";
    public static final String CANDIDATE_AVAILABILITY = "Inmediata, tiempo completo";

    public static final String PITCH_SUMMARY =
            "Me entusiasma enormemente tomar posesión técnica y potenciar PCoS, su plataforma de asistente ejecutivo personal " +
            "con inteligencia artificial, así como expandirse a sectores de salud y operaciones empresariales. Coincido plenamente " +
            "con su estrategia: adaptar, ajustar e implementar modelos Open Source (Llama 3/3.1/3.2, Mistral, Qwen, DeepSeek, Whisper) " +
            "para reducir drásticamente los costos operativos de inferencia (-65%) y garantizar total control y soberanía de los datos.\n\n" +
            "Cuento con más de 12 años de experiencia en ingeniería y arquitectura de software, sólida especialización en React, " +
            "TypeScript, Python (FastAPI), PostgreSQL y Supabase, y más de 2 años y medio desarrollando e implementando soluciones con modelos " +
            "de IA Open Source tanto en arquitecturas RAG como en inferencia optimizada con vLLM y fine-tuning en producción.";

    public static List<QuestionItem> getQuestions() {
        List<QuestionItem> list = new ArrayList<>();
        list.add(new QuestionItem(
                1,
                "Años de experiencia & Tecnologías clave",
                "¿Cuántos años de experiencia tienes desarrollando software y cuáles son tus principales tecnologías o skills?",
                "12+ años en software y arquitectura, 4+ años full-stack moderno (React, TS, Python FastAPI, PostgreSQL).",
                "• Años de experiencia: Más de 12 años de trayectoria técnica profesional en desarrollo de software, arquitectura de sistemas y backend/full-stack, con más de 4 años continuos construyendo aplicaciones full-stack modernas y escalables en entornos de alta exigencia.\n" +
                "• Frontend: React (Hooks, Context, Server Components, arquitectura modular), TypeScript, Next.js, Tailwind CSS, Zustand, Redux Toolkit, React Query / TanStack Query, y streaming reactivo de respuestas LLM en tiempo real mediante SSE y WebSockets.\n" +
                "• Backend & APIs: Python (FastAPI asíncrono, Flask, Django) y Node.js / TypeScript (Express, NestJS). Diseño de APIs RESTful de alto rendimiento, Webhooks, endpoints seguros con autenticación JWT/OAuth2 y serialización tipada con Pydantic e Instructor.\n" +
                "• Bases de Datos & Persistencia: PostgreSQL avanzado (diseño relacional, optimización de consultas, CTEs, vistas, índices, transacciones ACID), Supabase (Auth, Row Level Security - RLS, realtime, edge functions y pgvector), MySQL, Redis (caching semántico y colas asíncronas), SQLAlchemy y Alembic.\n" +
                "• DevOps, Cloud & CI/CD: Docker, Docker Compose, AWS (EC2 con GPU g5/g6 para inferencia, S3, RDS PostgreSQL, Lambda, CloudWatch), pipelines en GitHub Actions, Linux (Ubuntu/RHEL) y testing con Pytest y Vitest (>85% cobertura)."
        ));
        list.add(new QuestionItem(
                2,
                "Experiencia con IA Open Source",
                "¿Cuántos años o meses de experiencia tienes trabajando con modelos de IA Open Source?",
                "Más de 2 años y medio (30+ meses) continuos, desde primeras versiones de LLaMA y Mistral (2023) hasta Llama 3.2 y Qwen 2.5.",
                "Cuento con más de 2 años y medio (30+ meses) de trabajo continuo y práctico con modelos de IA de código abierto (Open Source), desde las primeras versiones de LLaMA y Mistral en 2023 hasta los ecosistemas de última generación (Llama 3, 3.1, 3.2, Qwen 2.5, DeepSeek y Whisper). Mi trabajo abarca desde la experimentación y benchmarking local hasta el empaquetado, despliegue y servicio en entornos de producción en la nube."
        ));
        list.add(new QuestionItem(
                3,
                "Modelos utilizados y proyectos",
                "¿Qué modelos has utilizado y en qué tipo de proyectos?",
                "Llama 3/3.1/3.2, Mistral/Mixtral, Qwen 2.5, Whisper v2/v3, BGE-m3; en asistentes ejecutivos agénticos y RAG empresarial.",
                "• Modelos utilizados:\n" +
                "- LLMs: Llama 3 / 3.1 / 3.2 (1B, 3B, 8B, 70B), Mistral 7B, Mixtral 8x7B (MoE), Qwen 2.5 (Coder/Instruct), DeepSeek-Coder, Phi-3/3.5.\n" +
                "- Audio & Multimodal: Whisper (v2/v3) para transcripción/diarización, Llava y Florence-2 para análisis de documentos/imágenes.\n" +
                "- Embeddings: BAAI bge-large-en/m3, all-MiniLM-L6-v2 y multilingües de Hugging Face.\n" +
                "• Tipos de proyectos:\n" +
                "- Asistentes Ejecutivos y Flujos Agénticos (similares a PCoS): Tool calling para calendarización, redacción, resúmenes y salida estructurada validada con Pydantic.\n" +
                "- Sistemas RAG Empresariales: Motores de búsqueda semántica con PostgreSQL pgvector y Supabase, chunking semántico y re-ranking con Cross-Encoders.\n" +
                "- Automatización y Extracción de Datos: Procesamiento inteligente de contratos y reportes con LLMs locales sin exponer datos confidenciales a terceros."
        ));
        list.add(new QuestionItem(
                4,
                "IA en ambientes de producción",
                "¿Has desarrollado o implementado soluciones de IA en ambientes de producción? Si es así, describe brevemente tu experiencia.",
                "Sí: Despliegue con vLLM en AWS GPU (-65% costos), fallbacks automáticos, colas Redis/Celery, fine-tuning LoRA/QLoRA y Supabase RLS.",
                "Sí, completamente:\n" +
                "• Inferencia y Despliegue Open Source: Modelos servidos con vLLM y TGI sobre Docker en AWS GPU (g5/g6), cuantización AWQ/GPTQ/GGUF y continuous batching, reduciendo costos >65% vs APIs comerciales.\n" +
                "• Confiabilidad y Manejo de Errores: Reintentos exponenciales, fallbacks entre modelos locales y APIs externas, colas con Redis/Celery para Whisper/embeddings, y caché semántica en Redis.\n" +
                "• Fine-Tuning: PEFT con LoRA/QLoRA usando Hugging Face (TRL, Transformers) para adaptar modelos de 7B/8B a terminología de dominio y JSON estructurado.\n" +
                "• Seguridad y Privacidad: Row Level Security (RLS) en Supabase/PostgreSQL para que las consultas vectoriales respeten estrictamente la privacidad por usuario y organización."
        ));
        list.add(new QuestionItem(
                5,
                "Arquitectura de software y patrones",
                "¿Qué experiencia tienes diseñando o tomando decisiones de arquitectura de software? ¿Con qué arquitecturas o patrones has trabajado?",
                "10+ años en arquitectura de misión crítica (99.999% SLA en AT&T). Clean Architecture, Hexagonal, EDA, Repository y Strategy Patterns.",
                "• Experiencia en Arquitectura: Más de una década definiendo arquitectura para plataformas de misión crítica (SLAs 99.999% en AT&T y sistemas distribuidos en Empire Engineering).\n" +
                "• Arquitecturas y Patrones:\n" +
                "- Clean Architecture & Arquitectura Hexagonal: Desacoplamiento del core de negocio frente a frameworks web, proveedores LLM y bases de datos.\n" +
                "- Microservicios & Monolitos Modulares: Contratos OpenAPI y tipado estricto compartido entre frontend y backend.\n" +
                "- Event-Driven Architecture (EDA) & Procesamiento Asíncrono: Webhooks, Server-Sent Events (SSE) para streaming continuo de LLMs, y colas Redis.\n" +
                "- Patrones de Diseño: Repository Pattern (persistencia en Postgres/Supabase), Strategy Pattern (enrutar peticiones al modelo óptimo por costo/latencia), Factory e Inyección de Dependencias."
        ));
        list.add(new QuestionItem(
                6,
                "Liderazgo técnico de desarrolladores",
                "¿Has liderado desarrolladores? Si es así, ¿cuántas personas y cuáles eran tus principales responsabilidades?",
                "Sí: He liderado equipos de 4 a 7 ingenieros (AT&T y Empire Engineering). Ownership técnico, code reviews, QA (>85% Pytest), Scrum y mentoría.",
                "Sí. He liderado y mentoreado equipos de 4 a 7 ingenieros en entornos distribuidos y remotos (AT&T y Empire Engineering).\n" +
                "• Responsabilidades clave:\n" +
                "- Ownership y Dirección Técnica: Definición de arquitectura base, estándares de código limpio y tipado estricto en TS/Python.\n" +
                "- Code Reviews & QA: Auditoría de Pull Requests en GitHub/GitLab, estándares de seguridad y cobertura de pruebas automatizadas (>85% Pytest/Vitest).\n" +
                "- Ciclo de Vida y Entregas: Ceremonias Scrum/Kanban en Jira (Sprint Planning, Dailies, Retrospectivas) y desglose de epics en tareas ejecutables.\n" +
                "- Mentoring: Mentoría 1 a 1 para desarrolladores junior y mid-level para estabilizar bases de código complejas con autonomía."
        ));
        list.add(new QuestionItem(
                7,
                "Expectativa económica",
                "¿Cuál es tu expectativa económica mensual para una posición de tiempo completo?",
                "$2,500 USD mensuales (Contractor / Remoto tiempo completo), abierto a evaluar el paquete integral.",
                "Mi expectativa se ubica en el rango de $2,500 USD mensuales (bajo esquema de Contractor / Tiempo Completo Remoto), estando completamente abierto a evaluar y negociar la propuesta final en función del paquete integral de beneficios, esquema de compensación y alcance del rol. Cuento con disponibilidad inmediata y residencia fija en México."
        ));
        return list;
    }

    public static List<ExperienceItem> getExperience() {
        List<ExperienceItem> list = new ArrayList<>();
        list.add(new ExperienceItem(
                "Empire Engineering",
                "Senior Full-Stack & Applied AI Engineer",
                "Nov 2022 – Jul 2026",
                "Estados Unidos & Remoto (100% inglés)",
                new String[]{
                        "Lideró desarrollo full-stack con React, TypeScript, Python (FastAPI) y PostgreSQL.",
                        "Pipeline de IA en producción con Llama 3 y Mistral servidos con vLLM en AWS GPU (-65% costo).",
                        "Streaming reactivo por SSE para chat con IA y tipado estricto en TypeScript.",
                        "RAG empresarial con PostgreSQL pgvector, chunking semántico y rerankers (<2% alucinaciones).",
                        "Lideró y mentoreó equipo de 5 ingenieros con Clean Architecture y CI/CD en GitHub Actions (>85% pruebas)."
                }
        ));
        list.add(new ExperienceItem(
                "AT&T",
                "Senior Software & Backend Engineer / Systems Tech Lead",
                "Jun 2013 – Oct 2022",
                "México",
                new String[]{
                        "SDLC y arquitectura de software para supervisión y gestión de más de 120 plataformas críticas (99.999% SLA).",
                        "APIs REST de alto rendimiento en Python (Flask/Django) con PostgreSQL y Oracle (-40% tiempos).",
                        "Arquitecturas modulares aplicando Repository, Factory y Strategy con principios SOLID.",
                        "Servicios de ingesta con APIs de Ericsson y Huawei; coordinó equipo de 6 ingenieros bajo Scrum."
                }
        ));
        list.add(new ExperienceItem(
                "Nextel & Nokia",
                "Systems & Software Automation Engineer",
                "Ene 2010 – May 2013",
                "México",
                new String[]{
                        "Automatización en Python y Bash en servidores Linux para diagnóstico en tiempo real.",
                        "Esquemas relacionales en MySQL, resolución de incidentes bajo SLAs y monitoreo continuo 24/7."
                }
        ));
        return list;
    }

    public static String getSystemInstruction(String lang) {
        boolean isEn = lang != null && lang.toLowerCase().startsWith("en");
        if (isEn) {
            return "You are the secret, ultra-fast co-pilot and alter ego of JAVIER VIVEROS HUESCA during his real-time technical job interviews.\n" +
                    "Javier is in an online interview meeting and has his Android smartphone on his desk reading your answers at a glance while speaking to the interviewer.\n\n" +
                    "MANDATORY RULES:\n" +
                    "1. CASUAL CHAT FILTER:\n" +
                    "   If the audio is NOT an interview or technical question (e.g. 'can you hear me?', 'hello', 'good morning', 'let me share my screen', coughing, noise), answer ONLY with the exact word: [IGNORAR].\n\n" +
                    "2. FIRST-PERSON PERSONA ('I'):\n" +
                    "   - If asked about personal background, experience, projects, or leadership: ALWAYS answer in first person as JAVIER VIVEROS HUESCA ('I have over 12 years of experience...', 'At Empire Engineering I deployed open source LLMs with vLLM...', 'At AT&T I maintained 99.999% SLA...').\n" +
                    "   - Use his verified background, real metrics (-65% inference cost), and stack (React, TypeScript, FastAPI, PostgreSQL, Supabase, vLLM, Docker, AWS GPU).\n" +
                    "   - Compensation: $2,500 USD/month (Contractor / Full-time remote) with immediate availability.\n\n" +
                    "3. DIRECT TECHNICAL QUESTIONS (e.g. 'What is a Cisco switch?', 'What is RAG?', 'REST vs GraphQL', 'Explain NodeJS Event Loop'):\n" +
                    "   - Provide an authoritative, senior-level definition right away.\n" +
                    "   - List 2-3 core technical pillars, protocols, or architecture choices.\n" +
                    "   - Add a practical real-world production tie-in.\n\n" +
                    "4. ULTRA-CONCISE TELEPROMPTER FORMAT:\n" +
                    "   🎯 **Direct Answer**: 1-2 punchy sentences ready to speak out loud.\n" +
                    "   ⚡ **Key Technical Points**:\n" +
                    "   • 2-3 brief bullets highlighting exact protocols, architectural trade-offs, or components.\n" +
                    "   💡 **Production Insight**: 1 brief sentence connecting to real enterprise deployment.\n\n" +
                    "5. TONE: Strictly English. Zero filler intros. Direct, high-impact.";
        } else {
            return "Eres el copiloto secreto de alta velocidad y alter ego de JAVIER VIVEROS HUESCA durante sus entrevistas de trabajo técnicas en tiempo real.\n" +
                    "Javier está en una reunión virtual y tiene su teléfono celular frente a él leyendo tus respuestas mientras habla con el entrevistador.\n\n" +
                    "REGLAS OBLIGATORIAS:\n" +
                    "1. FILTRO DE CHARLA CASUAL Y RUIDO:\n" +
                    "   Si lo escuchado NO es una pregunta técnica ni sobre su perfil (por ejemplo: 'hola me escuchan', 'can you hear me', 'buenas tardes', 'un momento por favor', 'espera que comparto pantalla', ruidos), responde ÚNICAMENTE con la palabra exacta: [IGNORAR].\n\n" +
                    "2. ENCARNACIÓN EN PRIMERA PERSONA ('YO'):\n" +
                    "   - Si la pregunta es sobre experiencia, proyectos o decisiones: responde SIEMPRE en primera persona como JAVIER VIVEROS HUESCA ('Tengo más de 12 años...', 'En Empire Engineering implementé vLLM...', 'En AT&T lideré la arquitectura con 99.999% SLA...').\n" +
                    "   - Utiliza exclusivamente sus datos verificados y proyectos reales.\n" +
                    "   - Salario: $2,500 USD mensuales (Contractor / Remoto) con disponibilidad inmediata.\n\n" +
                    "3. PREGUNTAS CONCEPTUALES O TÉCNICAS DIRECTAS (ej. '¿Qué es un switch Cisco?', '¿Qué es RAG?', 'Diferencia entre REST y GraphQL', 'Explica el Event Loop'):\n" +
                    "   - Da una definición técnica de nivel Senior inmediata y contundente.\n" +
                    "   - Incluye los 2-3 puntos o protocolos fundamentales.\n" +
                    "   - Agrega un toque práctico de aplicación en producción o infraestructura.\n\n" +
                    "4. FORMATO VISUAL ULTRA CONCISO (TELEPROMPTER PARA CELULAR):\n" +
                    "   🎯 **Respuesta Inmediata**: 1 o 2 oraciones directas al grano, listas para decir en voz alta.\n" +
                    "   ⚡ **Puntos Clave**:\n" +
                    "   • 2 o 3 viñetas muy breves con términos técnicos precisos.\n" +
                    "   💡 **Aplicación / Experiencia**: 1 oración corta vinculando con casos prácticos reales.\n\n" +
                    "5. TONO: Español. Cero rodeos, cero saludos. Directo y de alto impacto.";
        }
    }
}
