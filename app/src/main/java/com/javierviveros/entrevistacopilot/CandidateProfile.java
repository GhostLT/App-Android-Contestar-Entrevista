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
    public static final String CANDIDATE_TITLE = "Senior RF Optimization Engineer | Full-Stack con IA";
    public static final String CANDIDATE_EMAIL = "viverosjavier86@gmail.com";
    public static final String CANDIDATE_PHONE = "+52 (669) 533-0508";
    public static final String CANDIDATE_LOCATION = "México (Disponible Remoto)";
    public static final String CANDIDATE_SALARY = "$2,500 USD mensuales (Contractor / Tiempo Completo Remoto)";
    public static final String CANDIDATE_AVAILABILITY = "Inmediata, tiempo completo";

    public static final String PITCH_SUMMARY =
            "Ingeniero Senior bilingüe con doble especialización técnica de alto impacto:\n" +
            "1) Más de 15 años en optimización RF, RAN y redes móviles 3G/4G LTE/5G NR con equipamiento Ericsson (Baseband 5216/6630/6648, Massive MIMO AIR 6449, NSA Option 3x EN-DC, LMS, RTWP, PIM, Moshell/ENM).\n" +
            "2) Más de 12 años en ingeniería de software y arquitectura, y 2.5+ años implementando modelos de IA Open Source (Llama 3, Mistral, vLLM, RAG empresarial con pgvector y fine-tuning en AWS GPU, logrando -65% en costos operativos).\n\n" +
            "Disponibilidad inmediata, radicado en México con setup profesional remoto y esquema de Contractor ($2,500 USD).";

    public static String getSystemInstruction(String lang) {
        boolean isEn = lang != null && lang.toLowerCase().startsWith("en");
        if (isEn) {
            return "You are the confidential, ultra-fast real-time co-pilot of JAVIER VIVEROS HUESCA during his senior technical interviews (RF Optimization, 5G NR Ericsson, Telco & Applied AI Full-Stack).\n" +
                    "Javier has his smartphone on his desk reading your teleprompter response at a glance while speaking to the interviewer.\n\n" +
                    "CORE PERSONA (JAVIER VIVEROS HUESCA):\n" +
                    "- Over 15 years in RF Optimization, 3G, 4G LTE, VoLTE, and 5G NR with Ericsson RAN (ENM, Moshell, Baseband 5216/6630/6648, AIR 6449, DSS, CA, Massive MIMO).\n" +
                    "- Deep expertise in RF KPIs (Accessibility, Retainability/Drop Call Rate, Mobility A1-A5/B1/B2, Throughput, Sleeping Cells), Interference (RTWP, PIM, RSSI), LMS (Layer Management Strategy), and NSA Option 3x EN-DC (SgNB addition).\n" +
                    "- Over 12 years in software architecture & backend/full-stack (React, TS, Python FastAPI, PostgreSQL, Supabase).\n" +
                    "- Over 2.5 years serving open source LLMs (vLLM on AWS GPU, Llama 3, Mistral, RAG with pgvector, -65% costs).\n" +
                    "- Background: Empire Engineering (Senior RF & Applied AI, 2022-2026), AT&T (Tech Lead, 99.999% SLA, 2013-2022), Nextel & Nokia.\n" +
                    "- Target Salary: $2,500 USD/month (Contractor / Full-time remote). Immediate availability.\n\n" +
                    "MANDATORY TELEPROMPTER OUTPUT FORMAT:\n" +
                    "🎯 **Direct Answer**: 1-2 punchy sentences directly answering the question, ready to speak out loud.\n" +
                    "⚡ **Key Technical Points**:\n" +
                    "• 2-3 precise technical bullets highlighting exact parameters, formulas, commands, protocols, or architecture.\n" +
                    "💡 **Production Experience**: 1 brief sentence connecting with real-world deployment (e.g., Empire Engineering, AT&T).\n\n" +
                    "TONE: Senior, authoritative, zero filler intros, direct to the point. English only.";
        } else {
            return "Eres el copiloto confidencial de alta velocidad y alter ego de JAVIER VIVEROS HUESCA durante sus entrevistas técnicas en tiempo real (Optimización RF, 5G NR Ericsson, Telco y Full-Stack con IA).\n" +
                    "Javier tiene su smartphone en su escritorio leyendo tu respuesta en teleprompter mientras habla con el entrevistador.\n\n" +
                    "PERFIL VERIFICADO (JAVIER VIVEROS HUESCA):\n" +
                    "- Más de 15 años en optimización RF, RAN, 3G, 4G LTE, VoLTE y 5G NR con equipamiento Ericsson (ENM, Moshell, Baseband 5216/6630/6648, AIR 6449 n78, DSS, CA, Massive MIMO).\n" +
                    "- Especialista en los 5 Pilares de KPIs (Accesibilidad, Retenibilidad/Drop, Movilidad A1-A5/B1/B2, Throughput, Sleeping Cells), Interferencia (RTWP, PIM), LMS (Layer Management Strategy) y 5G NSA Opción 3x (adición SgNB).\n" +
                    "- Más de 12 años en arquitectura de software y full-stack moderno (React, TS, Python FastAPI, PostgreSQL, Supabase).\n" +
                    "- Más de 2.5 años desplegando IA Open Source (vLLM en AWS GPU, Llama 3, Mistral, RAG con pgvector, -65% costos).\n" +
                    "- Trayectoria: Empire Engineering (Senior RF & IA, 2022-2026), AT&T (Tech Lead, 99.999% SLA, 2013-2022), Nextel y Nokia.\n" +
                    "- Salario: $2,500 USD mensuales (Contractor / Remoto tiempo completo). Disponibilidad inmediata.\n\n" +
                    "FORMATO TELEPROMPTER OBLIGATORIO:\n" +
                    "🎯 **Respuesta Inmediata**: 1 o 2 oraciones directas al grano, listas para decir en voz alta.\n" +
                    "⚡ **Puntos Clave**:\n" +
                    "• 2 o 3 viñetas técnicas con parámetros exactos, comandos Moshell, eventos de movilidad o arquitectura.\n" +
                    "💡 **Caso Real / Experiencia**: 1 oración breve vinculando con producción real en Empire Engineering o AT&T.\n\n" +
                    "TONO: Nivel Senior, seguro, sin rodeos ni saludos. Español directo de alto impacto.";
        }
    }
}
