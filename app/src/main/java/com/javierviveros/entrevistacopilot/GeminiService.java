package com.javierviveros.entrevistacopilot;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    public interface GeminiCallback {
        void onSuccess(String answer, String model);
        void onIgnored();
        void onError(String errorMessage);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    public GeminiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void generateAnswer(String question, String apiKey, String preferredModel, String language, GeminiCallback callback) {
        String[] fallbackModels = new String[]{
                preferredModel != null && !preferredModel.isEmpty() ? preferredModel : "gemini-3.5-flash-lite",
                "gemini-3.5-flash-lite",
                "gemini-3.1-flash-lite",
                "gemini-3.8-flash"
        };

        tryModelChain(question, apiKey, fallbackModels, 0, language, callback);
    }

    private void tryModelChain(String question, String apiKey, String[] models, int index, String language, GeminiCallback callback) {
        if (index >= models.length) {
            postError(callback, "No se pudo conectar con los modelos de Gemini. Verifica tu API Key y conexión a internet.");
            return;
        }

        String model = models[index];
        // Evitar duplicados seguidos
        if (index > 0 && model.equals(models[0])) {
            tryModelChain(question, apiKey, models, index + 1, language, callback);
            return;
        }

        String url = BASE_URL + model + ":generateContent?key=" + apiKey;

        boolean isEn = language != null && language.toLowerCase().startsWith("en");
        String userPrompt = isEn ?
                "Question from the interviewer in the meeting:\n\"" + question + "\"" :
                "Pregunta del entrevistador en la reunión:\n\"" + question + "\"";

        String systemInstruction = CandidateProfile.getSystemInstruction(language);

        JsonObject root = new JsonObject();

        // contents
        JsonArray contentsArray = new JsonArray();
        JsonObject contentObj = new JsonObject();
        contentObj.addProperty("role", "user");
        JsonArray partsArray = new JsonArray();
        JsonObject partObj = new JsonObject();
        partObj.addProperty("text", userPrompt);
        partsArray.add(partObj);
        contentObj.add("parts", partsArray);
        contentsArray.add(contentObj);
        root.add("contents", contentsArray);

        // systemInstruction
        JsonObject sysObj = new JsonObject();
        JsonArray sysPartsArray = new JsonArray();
        JsonObject sysPartObj = new JsonObject();
        sysPartObj.addProperty("text", systemInstruction);
        sysPartsArray.add(sysPartObj);
        sysObj.add("parts", sysPartsArray);
        root.add("systemInstruction", sysObj);

        // generationConfig
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.2);
        genConfig.addProperty("maxOutputTokens", 650);
        root.add("generationConfig", genConfig);

        RequestBody requestBody = RequestBody.create(root.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Reintentar con el siguiente modelo en la cadena
                tryModelChain(question, apiKey, models, index + 1, language, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    response.close();
                    tryModelChain(question, apiKey, models, index + 1, language, callback);
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                try {
                    JsonObject resObj = gson.fromJson(responseBody, JsonObject.class);
                    JsonArray candidates = resObj.getAsJsonArray("candidates");
                    if (candidates != null && candidates.size() > 0) {
                        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = firstCandidate.getAsJsonObject("content");
                        if (content != null) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts != null && parts.size() > 0) {
                                String answerText = parts.get(0).getAsJsonObject().get("text").getAsString();
                                if (answerText != null) {
                                    answerText = answerText.trim();
                                    if (answerText.contains("[IGNORAR]")) {
                                        postIgnored(callback);
                                        return;
                                    }
                                    postSuccess(callback, answerText, model);
                                    return;
                                }
                            }
                        }
                    }
                    tryModelChain(question, apiKey, models, index + 1, language, callback);
                } catch (Exception parseException) {
                    tryModelChain(question, apiKey, models, index + 1, language, callback);
                }
            }
        });
    }

    private void postSuccess(GeminiCallback callback, String answer, String model) {
        mainHandler.post(() -> callback.onSuccess(answer, model));
    }

    private void postIgnored(GeminiCallback callback) {
        mainHandler.post(callback::onIgnored);
    }

    private void postError(GeminiCallback callback, String error) {
        mainHandler.post(() -> callback.onError(error));
    }
}
