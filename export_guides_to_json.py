# -*- coding: utf-8 -*-
import os
import re
import json
import unicodedata

SRC_DIR = r"C:\proyectos\Contestar Entrevista"
OUT_DIR = r"C:\proyectos\App_Android_Nativa_Entrevistas\app\src\main\assets"
os.makedirs(OUT_DIR, exist_ok=True)

def parse_markdown_guide(file_path, lang="es"):
    with open(file_path, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()

    module_pattern = r'\n(?=#\s+(?:M[ÓO]DULO|MODULE)\s+\d+:?)'
    chunks = re.split(module_pattern, content, flags=re.IGNORECASE)
    
    modules_list = []
    all_questions = []

    mod_counter = 1
    q_global_counter = 1

    for chunk in chunks[1:]:
        lines = chunk.strip().splitlines()
        if not lines:
            continue
        mod_title = lines[0].lstrip('#').strip()
        
        q_pattern = r'\n(?=###\s+(?:(?:Pregunta|Question)\s+\d+:?|📊|💻|🎙️|💬))'
        q_chunks = re.split(q_pattern, chunk, flags=re.IGNORECASE)
        intro = q_chunks[0].strip()

        mod_questions = []

        for q_chunk in q_chunks[1:]:
            q_lines = q_chunk.strip().splitlines()
            if not q_lines:
                continue
            q_title = q_lines[0].lstrip('#').strip()
            
            m_num = re.search(r'(?:Pregunta|Question)\s*(\d+)', q_title, re.IGNORECASE)
            q_num = int(m_num.group(1)) if m_num else q_global_counter

            # Parse subtitulos ####
            parts = re.split(r'\n(?=#### )', q_chunk.strip())
            strategy = ""
            answer = ""
            extra_sections = []

            for p in parts:
                p = p.strip()
                if not p:
                    continue
                p_lines = p.splitlines()
                header = p_lines[0].lstrip('#').strip()
                body = "\n".join(p_lines[1:]).strip()

                h_lower = header.lower()
                if "estrategia" in h_lower or "strategy" in h_lower:
                    strategy = body
                elif "respuesta" in h_lower or "answer" in h_lower:
                    if not answer:
                        answer = body
                    else:
                        extra_sections.append({"header": header, "body": body})
                elif header != q_title:
                    extra_sections.append({"header": header, "body": body})

            # Short title
            st = re.sub(r'^(Pregunta|Question)\s*\d+:\s*', '', q_title, flags=re.IGNORECASE).strip('"\'')
            if len(st) > 60:
                short_title = f"Q{q_num}: {st[:57]}..."
            else:
                short_title = f"Q{q_num}: {st}"

            # Clean teleprompter answer
            clean_ans = answer
            if not clean_ans and extra_sections:
                clean_ans = "\n\n".join(f"{x['header']}:\n{x['body']}" for x in extra_sections)
            clean_lines = [re.sub(r'^\s*>\s?', '', l) for l in clean_ans.splitlines()]
            clean_teleprompter = "\n".join(clean_lines).strip()

            q_data = {
                "num": q_num,
                "moduleNum": mod_counter,
                "moduleTitle": mod_title,
                "title": q_title,
                "shortTitle": short_title,
                "strategy": strategy,
                "answer": clean_teleprompter,
                "extraSections": extra_sections,
                "rawText": q_chunk.strip()
            }
            mod_questions.append(q_data)
            all_questions.append(q_data)
            q_global_counter += 1

        if not mod_questions and intro:
            st = mod_title
            q_data = {
                "num": q_global_counter,
                "moduleNum": mod_counter,
                "moduleTitle": mod_title,
                "title": mod_title,
                "shortTitle": f"Q{q_global_counter}: {st}",
                "strategy": "",
                "answer": intro,
                "extraSections": [],
                "rawText": chunk.strip()
            }
            mod_questions.append(q_data)
            all_questions.append(q_data)
            q_global_counter += 1

        mod_clean = re.sub(r'^(?:M[ÓO]DULO|MODULE)\s+\d+:\s*', '', mod_title, flags=re.IGNORECASE).strip()
        modules_list.append({
            "num": mod_counter,
            "title": mod_title,
            "titleClean": mod_clean,
            "questionCount": len(mod_questions)
        })
        mod_counter += 1

    return {"modules": modules_list, "questions": all_questions}

es_file = os.path.join(SRC_DIR, "Guia_Entrevista_Senior_RF_Ericsson_4G_5G_ES.md")
en_file = os.path.join(SRC_DIR, "Interview_Guide_Senior_RF_Ericsson_4G_5G_EN.md")

data_es = parse_markdown_guide(es_file, "es")
data_en = parse_markdown_guide(en_file, "en")

# Agregar preguntas complementarias de Full-Stack & IA + Cisco
fs_questions_es = [
    {
        "num": 44,
        "moduleNum": 13,
        "moduleTitle": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
        "title": "Pregunta 44: ¿Cuántos años de experiencia tienes desarrollando software y cuáles son tus principales tecnologías o skills?",
        "shortTitle": "Q44: Años de experiencia & Tecnologías clave Full-Stack",
        "strategy": "Enfatizar 12+ años de experiencia total en software y arquitectura, 4+ años continuos full-stack moderno (React, TS, Python FastAPI, PostgreSQL).",
        "answer": "🎯 Más de 12 años de trayectoria técnica profesional en desarrollo de software y arquitectura backend/full-stack, con más de 4 años continuos construyendo aplicaciones escalables con React, TypeScript, FastAPI y PostgreSQL.\n\n⚡ Stack Principal:\n• Frontend: React 18/19, TypeScript, Next.js, Tailwind CSS, TanStack Query y streaming con SSE.\n• Backend: Python (FastAPI asíncrono, Flask, Django) y Node.js/TS con esquemas Pydantic.\n• Bases de Datos: PostgreSQL avanzado, Supabase (pgvector, RLS, Edge Functions), Redis.\n• Cloud & DevOps: Docker, AWS (EC2 GPU g5/g6 para inferencia, S3, RDS), GitHub Actions CI/CD (>85% cobertura).",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 45,
        "moduleNum": 13,
        "moduleTitle": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
        "title": "Pregunta 45: ¿Cuántos años de experiencia tienes trabajando con modelos de IA Open Source y en qué proyectos?",
        "shortTitle": "Q45: Experiencia con IA Open Source (Llama, Mistral, vLLM)",
        "strategy": "Resaltar más de 2 años y medio (30+ meses) continuos desde LLaMA 1 y Mistral (2023) hasta Llama 3.2, Qwen 2.5 y Whisper v3.",
        "answer": "🎯 Más de 2 años y medio (30+ meses) de trabajo práctico desplegando y sirviendo modelos Open Source en producción.\n\n⚡ Puntos Clave:\n• Modelos: Familia Llama 3/3.1/3.2 (1B a 70B), Mistral 7B, Mixtral 8x7B MoE, Qwen 2.5, DeepSeek-Coder y Whisper v2/v3.\n• Despliegue en Producción: Motores vLLM y TGI sobre Docker en AWS GPU (g5/g6), cuantización AWQ/GPTQ/GGUF y continuous batching (-65% costos vs APIs propietarias).\n• RAG Empresarial: PostgreSQL con pgvector y Supabase, chunking semántico y re-ranking con Cross-Encoders.\n• Fine-Tuning: PEFT con LoRA y QLoRA en Hugging Face para terminología de dominio y JSON estructurado.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 46,
        "moduleNum": 13,
        "moduleTitle": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
        "title": "Pregunta 46: ¿Qué experiencia tienes con arquitectura de software y qué patrones utilizas?",
        "shortTitle": "Q46: Arquitectura de Software, Clean Architecture & Patrones",
        "strategy": "Mencionar diseño para misión crítica (SLA 99.999% en AT&T y sistemas distribuidos en Empire Engineering).",
        "answer": "🎯 Más de una década definiendo arquitectura para plataformas de alta concurrencia y misión crítica (99.999% SLA en AT&T).\n\n⚡ Arquitecturas y Patrones:\n• Clean Architecture / Hexagonal: Aislamiento estricto del núcleo de negocio frente a frameworks web, proveedores de LLM y bases de datos.\n• Microservicios & Monolitos Modulares: Contratos de API OpenAPI y tipado compartido TS/Python.\n• Event-Driven Architecture (EDA): Webhooks, Server-Sent Events (SSE) para streaming continuo de LLMs y colas Redis.\n• Patrones de Diseño: Repository Pattern (persistencia), Strategy Pattern (enrutar peticiones al LLM óptimo por costo/latencia), Factory e Inyección de Dependencias.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 47,
        "moduleNum": 13,
        "moduleTitle": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
        "title": "Pregunta 47: ¿Qué es un switch Cisco y en qué capa del modelo OSI opera?",
        "shortTitle": "Q47: Redes: Switch Cisco, Capa OSI y Funcionamiento",
        "strategy": "Definición directa, capa 2/3, tabla CAM, VLANs y aplicación en centros de datos / telco.",
        "answer": "🎯 Un switch Cisco es un dispositivo de interconexión de red que conecta equipos dentro de una misma red de área local (LAN) transmitiendo paquetes a nivel de tramas (frames).\n\n⚡ Características Técnicas:\n• Capa OSI: Opera principalmente en Capa 2 (Enlace de Datos / Data Link) utilizando direcciones MAC para reenviar tramas eficientemente mediante su tabla CAM (Content Addressable Memory).\n• Switches Capa 3 (Multilayer): También realizan enrutamiento IP entre VLANs sin necesidad de router externo (Inter-VLAN Routing).\n• Funcionalidades clave: VLANs (802.1Q), Spanning Tree Protocol (STP / RSTP para evitar bucles), agregación de enlaces EtherChannel (LACP) y QoS para priorizar tráfico de voz y señalización.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 48,
        "moduleNum": 13,
        "moduleTitle": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
        "title": "Pregunta 48: ¿Cuál es tu expectativa económica y disponibilidad para este proyecto?",
        "shortTitle": "Q48: Expectativa Salarial y Disponibilidad",
        "strategy": "Ser transparente, profesional y flexible según el paquete integral.",
        "answer": "🎯 Mi expectativa se sitúa en $2,500 USD mensuales bajo esquema de Contractor (Tiempo Completo Remoto), estando completamente abierto a evaluar y acordar la propuesta según el alcance del proyecto y beneficios.\n\n⚡ Disponibilidad:\n• Disponibilidad inmediata para incorporación a tiempo completo.\n• Residencia fija en México con setup profesional remoto de alta conectividad y hardware.",
        "extraSections": [],
        "rawText": ""
    }
]

data_es["modules"].append({
    "num": 13,
    "title": "MÓDULO 13: FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
    "titleClean": "FULL-STACK CON IA & ARQUITECTURA (PCoS / HIRELINE)",
    "questionCount": len(fs_questions_es)
})
data_es["questions"].extend(fs_questions_es)

# English equivalent for module 13
fs_questions_en = [
    {
        "num": 44,
        "moduleNum": 13,
        "moduleTitle": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
        "title": "Question 44: How many years of experience do you have developing software and what are your core tech skills?",
        "shortTitle": "Q44: Years of experience & Core Tech Skills",
        "strategy": "Highlight 12+ years in software and architecture, 4+ continuous years in modern full-stack (React, TS, Python FastAPI, PostgreSQL).",
        "answer": "🎯 Over 12 years of professional software engineering and backend/full-stack architecture experience, with 4+ continuous years building scalable applications using React, TypeScript, FastAPI, and PostgreSQL.\n\n⚡ Core Stack:\n• Frontend: React 18/19, TypeScript, Next.js, Tailwind CSS, TanStack Query, and real-time streaming with SSE.\n• Backend: Python (async FastAPI, Flask, Django) and Node.js/TS with strict Pydantic schemas.\n• Databases: Advanced PostgreSQL, Supabase (pgvector, RLS, Edge Functions), Redis.\n• Cloud & DevOps: Docker, AWS (EC2 GPU g5/g6 for inference, S3, RDS), GitHub Actions CI/CD (>85% test coverage).",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 45,
        "moduleNum": 13,
        "moduleTitle": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
        "title": "Question 45: What experience do you have with Open Source AI models and in which projects?",
        "shortTitle": "Q45: Experience with Open Source AI (Llama, Mistral, vLLM)",
        "strategy": "Highlight 30+ months deploying and serving open source models in production.",
        "answer": "🎯 Over 2.5 years (30+ continuous months) deploying and serving open source models in enterprise production.\n\n⚡ Key Highlights:\n• Models: Llama 3/3.1/3.2 family (1B to 70B), Mistral 7B, Mixtral 8x7B MoE, Qwen 2.5, DeepSeek-Coder, and Whisper v2/v3.\n• Production Inference: vLLM and TGI engines on Docker over AWS GPU (g5/g6), with AWQ/GPTQ quantization and continuous batching (-65% inference costs vs proprietary APIs).\n• Enterprise RAG: PostgreSQL with pgvector and Supabase, semantic chunking, and Cross-Encoder reranking.\n• Fine-Tuning: PEFT with LoRA/QLoRA via Hugging Face for domain terminology and structured JSON output.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 46,
        "moduleNum": 13,
        "moduleTitle": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
        "title": "Question 46: What software architecture experience and design patterns do you use?",
        "shortTitle": "Q46: Software Architecture, Clean Architecture & Design Patterns",
        "strategy": "Mention mission-critical 99.999% SLA at AT&T and distributed systems at Empire Engineering.",
        "answer": "🎯 Over a decade architecting high-concurrency, mission-critical systems (99.999% SLA at AT&T).\n\n⚡ Architecture & Patterns:\n• Clean Architecture / Hexagonal: Decoupling core business domain from web frameworks, LLM providers, and databases.\n• Microservices & Modular Monoliths: Strict OpenAPI contracts and end-to-end typed interfaces between frontend and backend.\n• Event-Driven Architecture (EDA): Webhooks, Server-Sent Events (SSE) for streaming LLM tokens, and Redis message queues.\n• Patterns: Repository Pattern (persistence), Strategy Pattern (dynamic LLM routing by cost/latency), Factory, and Dependency Injection.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 47,
        "moduleNum": 13,
        "moduleTitle": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
        "title": "Question 47: What is a Cisco switch and at which layer of the OSI model does it operate?",
        "shortTitle": "Q47: Networking: Cisco Switch, OSI Layer & Operations",
        "strategy": "Direct definition, Layer 2/3, CAM table, VLANs, and enterprise data center application.",
        "answer": "🎯 A Cisco switch is a network device that connects devices within a Local Area Network (LAN) forwarding data frames efficiently.\n\n⚡ Technical Specs:\n• OSI Layer: Operates primarily at Layer 2 (Data Link Layer) using MAC addresses to forward frames via its CAM (Content Addressable Memory) table.\n• Layer 3 Switches (Multilayer): Perform wire-speed IP routing between VLANs (Inter-VLAN Routing) without an external router.\n• Core Features: VLANs (802.1Q tagging), Spanning Tree Protocol (STP / RSTP to prevent loops), Link Aggregation (LACP EtherChannel), and QoS prioritizing voice and signaling.",
        "extraSections": [],
        "rawText": ""
    },
    {
        "num": 48,
        "moduleNum": 13,
        "moduleTitle": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
        "title": "Question 48: What is your salary expectation and availability?",
        "shortTitle": "Q48: Salary Expectation and Availability",
        "strategy": "Transparent, professional, and open to overall package evaluation.",
        "answer": "🎯 My target is $2,500 USD per month under a Contractor / Full-time Remote arrangement, and I am open to discussing the complete compensation package.\n\n⚡ Availability:\n• Immediate availability for full-time engagement.\n• Fixed residence in Mexico with dedicated high-speed remote workstation.",
        "extraSections": [],
        "rawText": ""
    }
]

data_en["modules"].append({
    "num": 13,
    "title": "MODULE 13: FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
    "titleClean": "FULL-STACK WITH AI & ARCHITECTURE (PCoS / HIRELINE)",
    "questionCount": len(fs_questions_en)
})
data_en["questions"].extend(fs_questions_en)

out_es = os.path.join(OUT_DIR, "guide_es.json")
out_en = os.path.join(OUT_DIR, "guide_en.json")

with open(out_es, "w", encoding="utf-8") as f:
    json.dump(data_es, f, ensure_ascii=False, indent=2)

with open(out_en, "w", encoding="utf-8") as f:
    json.dump(data_en, f, ensure_ascii=False, indent=2)

print(f"Exported ES: {len(data_es['modules'])} modules, {len(data_es['questions'])} questions -> {out_es}")
print(f"Exported EN: {len(data_en['modules'])} modules, {len(data_en['questions'])} questions -> {out_en}")
