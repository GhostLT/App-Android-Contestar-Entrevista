package com.javierviveros.entrevistacopilot;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class GuideRepository {

    public static class ExtraSection {
        public String header;
        public String body;
    }

    public static class Question {
        public int num;
        public int moduleNum;
        public String moduleTitle;
        public String title;
        public String shortTitle;
        public String strategy;
        public String answer;
        public List<ExtraSection> extraSections;
        public String rawText;

        // Propiedades de búsqueda en tiempo de ejecución
        public transient int matchScore = 0;
        public transient List<String> matchedKeywords = new ArrayList<>();
    }

    public static class ModuleInfo {
        public int num;
        public String title;
        public String titleClean;
        public int questionCount;
    }

    public static class GuideData {
        public List<ModuleInfo> modules;
        public List<Question> questions;
    }

    private static GuideRepository instance;
    private final Context context;
    private GuideData dataEs;
    private GuideData dataEn;
    private String currentLang = "es";

    private static final Map<String, Integer> TECH_KEYWORDS = new HashMap<>();
    private static final Set<String> STOP_WORDS = new HashSet<>();

    static {
        // Ponderación de palabras clave técnicas (idéntica a rf_word_guide.py de Contestar-Entrevista)
        TECH_KEYWORDS.put("moshell", 15);
        TECH_KEYWORDS.put("amos", 15);
        TECH_KEYWORDS.put("kpi", 10);
        TECH_KEYWORDS.put("kpis", 10);
        TECH_KEYWORDS.put("handover", 15);
        TECH_KEYWORDS.put("ho", 10);
        TECH_KEYWORDS.put("drop", 15);
        TECH_KEYWORDS.put("retainability", 12);
        TECH_KEYWORDS.put("retenibilidad", 12);
        TECH_KEYWORDS.put("accessibility", 12);
        TECH_KEYWORDS.put("accesibilidad", 12);
        TECH_KEYWORDS.put("mobility", 12);
        TECH_KEYWORDS.put("movilidad", 12);
        TECH_KEYWORDS.put("throughput", 12);
        TECH_KEYWORDS.put("sleeping", 14);
        TECH_KEYWORDS.put("durmientes", 14);
        TECH_KEYWORDS.put("dormidas", 14);
        TECH_KEYWORDS.put("carrier aggregation", 18);
        TECH_KEYWORDS.put("ca", 12);
        TECH_KEYWORDS.put("mimo", 14);
        TECH_KEYWORDS.put("massive mimo", 18);
        TECH_KEYWORDS.put("beamforming", 16);
        TECH_KEYWORDS.put("dss", 18);
        TECH_KEYWORDS.put("dynamic", 15);
        TECH_KEYWORDS.put("spectrum", 12);
        TECH_KEYWORDS.put("espectro", 12);
        TECH_KEYWORDS.put("mlb", 16);
        TECH_KEYWORDS.put("load balancing", 16);
        TECH_KEYWORDS.put("balanceo", 12);
        TECH_KEYWORDS.put("5g", 10);
        TECH_KEYWORDS.put("nsa", 16);
        TECH_KEYWORDS.put("option 3x", 16);
        TECH_KEYWORDS.put("opcion 3x", 16);
        TECH_KEYWORDS.put("endc", 16);
        TECH_KEYWORDS.put("en-dc", 16);
        TECH_KEYWORDS.put("sgnb", 16);
        TECH_KEYWORDS.put("x2", 12);
        TECH_KEYWORDS.put("prach", 14);
        TECH_KEYWORDS.put("pci", 14);
        TECH_KEYWORDS.put("rtwp", 18);
        TECH_KEYWORDS.put("pim", 18);
        TECH_KEYWORDS.put("interferencia", 14);
        TECH_KEYWORDS.put("interference", 14);
        TECH_KEYWORDS.put("pre-post", 16);
        TECH_KEYWORDS.put("star", 12);
        TECH_KEYWORDS.put("python", 14);
        TECH_KEYWORDS.put("sql", 14);
        TECH_KEYWORDS.put("enm", 14);
        TECH_KEYWORDS.put("oss", 12);
        TECH_KEYWORDS.put("bb6630", 15);
        TECH_KEYWORDS.put("bb5216", 15);
        TECH_KEYWORDS.put("radio 4415", 15);
        TECH_KEYWORDS.put("air 6449", 15);
        TECH_KEYWORDS.put("volte", 14);
        TECH_KEYWORDS.put("srvcc", 14);
        TECH_KEYWORDS.put("qci", 10);
        TECH_KEYWORDS.put("5qi", 10);
        TECH_KEYWORDS.put("cqi", 12);
        TECH_KEYWORDS.put("mcs", 12);
        TECH_KEYWORDS.put("bler", 12);
        TECH_KEYWORDS.put("prb", 12);
        TECH_KEYWORDS.put("sinr", 12);
        TECH_KEYWORDS.put("rsrp", 10);
        TECH_KEYWORDS.put("rsrq", 10);
        TECH_KEYWORDS.put("a1", 12);
        TECH_KEYWORDS.put("a2", 12);
        TECH_KEYWORDS.put("a3", 15);
        TECH_KEYWORDS.put("a4", 12);
        TECH_KEYWORDS.put("a5", 12);
        TECH_KEYWORDS.put("b1", 15);
        TECH_KEYWORDS.put("b2", 15);
        TECH_KEYWORDS.put("b4", 12);
        TECH_KEYWORDS.put("b7", 12);
        TECH_KEYWORDS.put("b28", 14);
        TECH_KEYWORDS.put("b66", 12);
        TECH_KEYWORDS.put("n78", 15);
        TECH_KEYWORDS.put("n77", 14);
        TECH_KEYWORDS.put("vswr", 16);
        TECH_KEYWORDS.put("anr", 16);
        TECH_KEYWORDS.put("tilt", 12);
        TECH_KEYWORDS.put("ret", 12);
        TECH_KEYWORDS.put("azimuth", 12);
        TECH_KEYWORDS.put("lms", 20);
        TECH_KEYWORDS.put("traffic steering", 18);
        TECH_KEYWORDS.put("anchor", 16);
        TECH_KEYWORDS.put("ancla", 16);
        TECH_KEYWORDS.put("pscell", 16);
        TECH_KEYWORDS.put("rs power", 16);
        TECH_KEYWORDS.put("harq", 16);
        TECH_KEYWORDS.put("maxharqtx", 16);
        TECH_KEYWORDS.put("caso 1", 18);
        TECH_KEYWORDS.put("caso 2", 18);
        TECH_KEYWORDS.put("caso 3", 18);
        TECH_KEYWORDS.put("case 1", 18);
        TECH_KEYWORDS.put("case 2", 18);
        TECH_KEYWORDS.put("case 3", 18);
        TECH_KEYWORDS.put("rca", 16);
        TECH_KEYWORDS.put("cisco", 16);
        TECH_KEYWORDS.put("switch", 16);
        TECH_KEYWORDS.put("fastapi", 15);
        TECH_KEYWORDS.put("react", 15);
        TECH_KEYWORDS.put("typescript", 14);
        TECH_KEYWORDS.put("vllm", 18);
        TECH_KEYWORDS.put("rag", 18);
        TECH_KEYWORDS.put("clean architecture", 16);
        TECH_KEYWORDS.put("postgresql", 14);
        TECH_KEYWORDS.put("sueldo", 16);
        TECH_KEYWORDS.put("salario", 16);
        TECH_KEYWORDS.put("expectativa", 16);
        TECH_KEYWORDS.put("amdocs", 15);

        // Palabras vacías en español e inglés
        String[] stops = {
                "el", "la", "los", "las", "un", "una", "unos", "unas", "de", "del", "a", "al", "en", "con",
                "por", "para", "que", "como", "cual", "cuales", "es", "son", "fue", "era", "tu", "tus",
                "mi", "mis", "su", "sus", "me", "te", "se", "nos", "le", "les", "sobre", "este", "esta",
                "estos", "estas", "the", "an", "and", "or", "in", "on", "at", "to", "for", "of", "with",
                "by", "about", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do",
                "does", "did", "how", "what", "why", "when", "where", "which", "who", "tell", "explain",
                "explica", "cuentame", "dime", "hablame", "describe", "sabes", "conoces", "experiencia"
        };
        Collections.addAll(STOP_WORDS, stops);
    }

    private GuideRepository(Context context) {
        this.context = context.getApplicationContext();
        loadGuides();
    }

    public static synchronized GuideRepository getInstance(Context context) {
        if (instance == null) {
            instance = new GuideRepository(context);
        }
        return instance;
    }

    private void loadGuides() {
        dataEs = loadFromAsset("guide_es.json");
        dataEn = loadFromAsset("guide_en.json");
    }

    private GuideData loadFromAsset(String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type type = new TypeToken<GuideData>() {}.getType();
            GuideData data = gson.fromJson(reader, type);
            reader.close();
            is.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            GuideData empty = new GuideData();
            empty.modules = new ArrayList<>();
            empty.questions = new ArrayList<>();
            return empty;
        }
    }

    public void setLanguage(String lang) {
        if (lang != null && lang.toLowerCase().startsWith("en")) {
            this.currentLang = "en";
        } else {
            this.currentLang = "es";
        }
    }

    public String getLanguage() {
        return currentLang;
    }

    public List<Question> getAllQuestions() {
        GuideData data = "en".equals(currentLang) ? dataEn : dataEs;
        return data != null && data.questions != null ? data.questions : new ArrayList<>();
    }

    public List<ModuleInfo> getAllModules() {
        GuideData data = "en".equals(currentLang) ? dataEn : dataEs;
        return data != null && data.modules != null ? data.modules : new ArrayList<>();
    }

    /**
     * Búsqueda instantánea de alta precisión basada en términos clave y pesos.
     * Retorna resultados ordenados por (moduleNum, num) ascendente.
     */
    public List<Question> search(String query, int limit) {
        List<Question> all = getAllQuestions();
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        String qNorm = normalize(query).trim();
        String[] rawTokens = qNorm.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            if (!STOP_WORDS.contains(t) && t.length() > 0) {
                tokens.add(t);
            }
        }
        if (tokens.isEmpty()) {
            for (String t : rawTokens) {
                if (t.length() > 1) tokens.add(t);
            }
        }
        if (tokens.isEmpty()) {
            return Collections.emptyList();
        }

        boolean isSingleTerm = tokens.size() == 1;
        int minScore = isSingleTerm ? 10 : 14;

        List<Question> matches = new ArrayList<>();

        for (Question q : all) {
            String titleNorm = normalize(q.title);
            String modNorm = normalize(q.moduleTitle);
            String stratNorm = normalize(q.strategy);
            String ansNorm = normalize(q.answer);
            String rawNorm = normalize(q.rawText);

            int score = 0;
            List<String> matchedKws = new ArrayList<>();

            // 1. Coincidencia de frase exacta
            if (tokens.size() > 1 && titleNorm.contains(qNorm)) {
                score += 40;
                matchedKws.add(qNorm);
            } else if (tokens.size() > 1 && (stratNorm.contains(qNorm) || ansNorm.contains(qNorm))) {
                score += 25;
                matchedKws.add(qNorm);
            }

            // 2. Coincidencia por tokens con bonus técnico
            for (String tok : tokens) {
                int bonus = TECH_KEYWORDS.containsKey(tok) ? TECH_KEYWORDS.get(tok) : 0;
                boolean isShort = tok.length() <= 3;
                Pattern pattern = isShort ?
                        Pattern.compile("\\b" + Pattern.quote(tok) + "\\b", Pattern.CASE_INSENSITIVE) :
                        Pattern.compile(Pattern.quote(tok), Pattern.CASE_INSENSITIVE);

                if (pattern.matcher(titleNorm).find()) {
                    score += 35 + bonus;
                    if (!matchedKws.contains(tok)) matchedKws.add(tok);
                } else if (pattern.matcher(stratNorm).find()) {
                    score += 15 + bonus;
                    if (!matchedKws.contains(tok)) matchedKws.add(tok);
                } else if (pattern.matcher(ansNorm).find()) {
                    score += 12 + bonus;
                    if (!matchedKws.contains(tok)) matchedKws.add(tok);
                } else if (pattern.matcher(modNorm).find()) {
                    score += 10 + bonus;
                    if (!matchedKws.contains(tok)) matchedKws.add(tok);
                } else if (pattern.matcher(rawNorm).find()) {
                    score += 6 + bonus;
                    if (!matchedKws.contains(tok)) matchedKws.add(tok);
                }
            }

            if (score >= minScore) {
                q.matchScore = score;
                q.matchedKeywords = matchedKws;
                matches.add(q);
            }
        }

        // Ordenar primero por score descendente para seleccionar los top 'limit'
        Collections.sort(matches, (a, b) -> Integer.compare(b.matchScore, a.matchScore));
        List<Question> topList = matches.size() > limit ? new ArrayList<>(matches.subList(0, limit)) : matches;

        // Luego ordenar de menor a mayor por número de módulo y número de pregunta (como en Contestar-Entrevista)
        Collections.sort(topList, (a, b) -> {
            if (a.moduleNum != b.moduleNum) {
                return Integer.compare(a.moduleNum, b.moduleNum);
            }
            return Integer.compare(a.num, b.num);
        });

        return topList;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String s = input.toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.replaceAll("[^a-z0-9\\s\\-_]", " ");
    }
}
