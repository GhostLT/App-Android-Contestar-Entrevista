package com.javierviveros.entrevistacopilot;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_RECORD_AUDIO = 1001;
    private static final String PREFS_NAME = "CopilotoPrefs";
    private static final String PREF_API_KEY = "api_key";
    private static final String PREF_MODEL = "model";
    private static final String PREF_LANG = "language";
    private static final String PREF_KEEP_SCREEN = "keep_screen_on";
    private static final String PREF_VIBRATION = "vibration";
    private static final String PREF_AUTO_SEND = "auto_send";

    // Clave Gemini preconfigurada desde BuildConfig (local.properties)
    private static final String DEFAULT_API_KEY = BuildConfig.DEFAULT_GEMINI_API_KEY;
    private static final String DEFAULT_MODEL = "gemini-3.5-flash-lite";

    // Componentes principales
    private GeminiService geminiService;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    // Reconocimiento de voz
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening = false;
    private final Handler speechRestartHandler = new Handler(Looper.getMainLooper());

    // Tabs / Containers
    private ScrollView tabLive;
    private View tabHistory;
    private ScrollView tabPitch;
    private ScrollView tabSettings;
    private BottomNavigationView bottomNav;

    // Header UI
    private TextView tvHeaderStatus;
    private TextView tvHeaderLanguage;

    // Tab 1: Live UI
    private MaterialButton btnToggleListening;
    private EditText etDetectedQuestion;
    private MaterialButton btnSendQuestion;
    private MaterialButton btnClearQuestion;
    private TextView tvTeleprompterText;
    private TextView tvPrompterModelBadge;
    private ProgressBar pbLoading;
    private MaterialButton btnRefineBrief;
    private MaterialButton btnRefineDetail;
    private MaterialButton btnCopyPrompter;

    // Tab 2: History UI
    private EditText etHistorySearch;
    private TextView tvHistoryCount;
    private TextView tvHistoryEmpty;
    private MaterialButton btnClearHistory;
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;

    // Tab 3: Pitch UI
    private TextView tvPitchSummary;
    private RecyclerView rvPitchQuestions;
    private PitchAdapter pitchAdapter;

    // Tab 4: Settings UI
    private EditText etApiKey;
    private MaterialButton btnSaveApiKey;
    private Spinner spinnerModel;
    private Spinner spinnerLanguage;
    private MaterialSwitch swKeepScreenOn;
    private MaterialSwitch swVibration;
    private MaterialSwitch swAutoSend;

    // Estado en memoria
    private String lastQuestionSent = "";
    private String lastAnswerReceived = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar persistencia y servicios
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        geminiService = new GeminiService();

        // Inicializar vistas
        initViews();
        setupNavigation();
        setupLiveTab();
        setupHistoryTab();
        setupPitchTab();
        setupSettingsTab();

        // Aplicar opciones de pantalla y hardware
        applyScreenSettings();

        // Inicializar SpeechRecognizer nativo
        initSpeechRecognizer();
    }

    private void initViews() {
        tabLive = findViewById(R.id.tabLive);
        tabHistory = findViewById(R.id.tabHistory);
        tabPitch = findViewById(R.id.tabPitch);
        tabSettings = findViewById(R.id.tabSettings);
        bottomNav = findViewById(R.id.bottomNavigation);

        tvHeaderStatus = findViewById(R.id.tvHeaderStatus);
        tvHeaderLanguage = findViewById(R.id.tvHeaderLanguage);

        // Live views
        btnToggleListening = findViewById(R.id.btnToggleListening);
        etDetectedQuestion = findViewById(R.id.etDetectedQuestion);
        btnSendQuestion = findViewById(R.id.btnSendQuestion);
        btnClearQuestion = findViewById(R.id.btnClearQuestion);
        tvTeleprompterText = findViewById(R.id.tvTeleprompterText);
        tvPrompterModelBadge = findViewById(R.id.tvPrompterModelBadge);
        pbLoading = findViewById(R.id.pbLoading);
        btnRefineBrief = findViewById(R.id.btnRefineBrief);
        btnRefineDetail = findViewById(R.id.btnRefineDetail);
        btnCopyPrompter = findViewById(R.id.btnCopyPrompter);

        // History views
        etHistorySearch = findViewById(R.id.etHistorySearch);
        tvHistoryCount = findViewById(R.id.tvHistoryCount);
        tvHistoryEmpty = findViewById(R.id.tvHistoryEmpty);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        rvHistory = findViewById(R.id.rvHistory);

        // Pitch views
        tvPitchSummary = findViewById(R.id.tvPitchSummary);
        rvPitchQuestions = findViewById(R.id.rvPitchQuestions);

        // Settings views
        etApiKey = findViewById(R.id.etApiKey);
        btnSaveApiKey = findViewById(R.id.btnSaveApiKey);
        spinnerModel = findViewById(R.id.spinnerModel);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        swKeepScreenOn = findViewById(R.id.swKeepScreenOn);
        swVibration = findViewById(R.id.swVibration);
        swAutoSend = findViewById(R.id.swAutoSend);
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            tabLive.setVisibility(id == R.id.nav_live ? View.VISIBLE : View.GONE);
            tabHistory.setVisibility(id == R.id.nav_history ? View.VISIBLE : View.GONE);
            tabPitch.setVisibility(id == R.id.nav_pitch ? View.VISIBLE : View.GONE);
            tabSettings.setVisibility(id == R.id.nav_settings ? View.VISIBLE : View.GONE);

            if (id == R.id.nav_history) {
                loadHistoryData();
            }
            return true;
        });

        // Header Language Click Toggle
        tvHeaderLanguage.setOnClickListener(v -> toggleLanguage());
    }

    private void setupLiveTab() {
        updateLanguageHeader();
        updateModelBadge();

        btnToggleListening.setOnClickListener(v -> {
            if (isListening) {
                stopListening();
            } else {
                checkPermissionAndStartListening();
            }
        });

        btnSendQuestion.setOnClickListener(v -> {
            String question = etDetectedQuestion.getText().toString().trim();
            if (!question.isEmpty()) {
                sendQuestionToGemini(question);
            } else {
                Toast.makeText(this, "Escribe o dicta una pregunta primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearQuestion.setOnClickListener(v -> {
            etDetectedQuestion.setText("");
        });

        // Quick question chips
        findViewById(R.id.chipSwitchCisco).setOnClickListener(v -> {
            etDetectedQuestion.setText("¿Qué es un switch Cisco y en qué capa del modelo OSI opera?");
            sendQuestionToGemini(etDetectedQuestion.getText().toString());
        });
        findViewById(R.id.chipRag).setOnClickListener(v -> {
            etDetectedQuestion.setText("Explica cómo diseñaste tu arquitectura RAG empresarial y cómo mitigas alucinaciones.");
            sendQuestionToGemini(etDetectedQuestion.getText().toString());
        });
        findViewById(R.id.chipVllm).setOnClickListener(v -> {
            etDetectedQuestion.setText("¿Qué beneficios y optimizaciones obtuviste al implementar vLLM en producción frente a APIs externas?");
            sendQuestionToGemini(etDetectedQuestion.getText().toString());
        });
        findViewById(R.id.chipCleanArch).setOnClickListener(v -> {
            etDetectedQuestion.setText("Describe tu experiencia aplicando Clean Architecture y cómo desacoplas el núcleo del negocio.");
            sendQuestionToGemini(etDetectedQuestion.getText().toString());
        });
        findViewById(R.id.chipSueldo).setOnClickListener(v -> {
            etDetectedQuestion.setText("¿Cuál es tu expectativa económica mensual y disponibilidad para esta posición?");
            sendQuestionToGemini(etDetectedQuestion.getText().toString());
        });

        // Teleprompter actions
        btnCopyPrompter.setOnClickListener(v -> {
            String text = tvTeleprompterText.getText().toString();
            copyToClipboard("Respuesta Gemini", text);
            Toast.makeText(this, "Respuesta copiada al portapapeles", Toast.LENGTH_SHORT).show();
        });

        btnRefineBrief.setOnClickListener(v -> {
            if (!lastQuestionSent.isEmpty()) {
                String refined = lastQuestionSent + " (Dame una versión aún más breve, directa en 2 viñetas para teleprompter)";
                sendQuestionToGemini(refined);
            }
        });

        btnRefineDetail.setOnClickListener(v -> {
            if (!lastQuestionSent.isEmpty()) {
                String refined = lastQuestionSent + " (Profundiza técnicamente con arquitectura y patrones aplicados)";
                sendQuestionToGemini(refined);
            }
        });
    }

    private void setupHistoryTab() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(this);
        rvHistory.setAdapter(historyAdapter);

        etHistorySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (historyAdapter != null) {
                    historyAdapter.setItems(dbHelper.searchConversations(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearHistory.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Borrar Historial")
                    .setMessage("¿Estás seguro de que deseas eliminar todas las conversaciones guardadas?")
                    .setPositiveButton("Sí, borrar", (dialog, which) -> {
                        dbHelper.clearAll();
                        loadHistoryData();
                        Toast.makeText(this, "Historial eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void loadHistoryData() {
        String query = etHistorySearch.getText().toString();
        var items = dbHelper.searchConversations(query);
        historyAdapter.setItems(items);

        int count = items.size();
        tvHistoryCount.setText(count + (count == 1 ? " pregunta registrada" : " preguntas registradas"));
        tvHistoryEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        rvHistory.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    private void setupPitchTab() {
        tvPitchSummary.setText(CandidateProfile.PITCH_SUMMARY);

        rvPitchQuestions.setLayoutManager(new LinearLayoutManager(this));
        pitchAdapter = new PitchAdapter(this, CandidateProfile.getQuestions());
        rvPitchQuestions.setAdapter(pitchAdapter);
    }

    private void setupSettingsTab() {
        // Cargar API Key
        String savedApiKey = prefs.getString(PREF_API_KEY, DEFAULT_API_KEY);
        etApiKey.setText(savedApiKey);

        btnSaveApiKey.setOnClickListener(v -> {
            String key = etApiKey.getText().toString().trim();
            prefs.edit().putString(PREF_API_KEY, key).apply();
            Toast.makeText(this, "API Key guardada correctamente", Toast.LENGTH_SHORT).show();
        });

        // Configurar Modelos en Spinner
        String[] models = new String[]{"gemini-3.5-flash-lite", "gemini-3.1-flash-lite", "gemini-3.8-flash"};
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, models);
        spinnerModel.setAdapter(modelAdapter);

        String savedModel = prefs.getString(PREF_MODEL, DEFAULT_MODEL);
        for (int i = 0; i < models.length; i++) {
            if (models[i].equals(savedModel)) {
                spinnerModel.setSelection(i);
                break;
            }
        }
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = models[position];
                prefs.edit().putString(PREF_MODEL, selected).apply();
                updateModelBadge();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar Idiomas en Spinner
        String[] languages = new String[]{"Español (es-ES)", "English (en-US)"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.setAdapter(langAdapter);

        String currentLang = getSelectedLanguage();
        spinnerLanguage.setSelection(currentLang.startsWith("en") ? 1 : 0);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = position == 1 ? "en-US" : "es-ES";
                prefs.edit().putString(PREF_LANG, code).apply();
                updateLanguageHeader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Switches
        swKeepScreenOn.setChecked(prefs.getBoolean(PREF_KEEP_SCREEN, true));
        swKeepScreenOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_KEEP_SCREEN, isChecked).apply();
            applyScreenSettings();
        });

        swVibration.setChecked(prefs.getBoolean(PREF_VIBRATION, true));
        swVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_VIBRATION, isChecked).apply();
        });

        swAutoSend.setChecked(prefs.getBoolean(PREF_AUTO_SEND, true));
        swAutoSend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_AUTO_SEND, isChecked).apply();
        });
    }

    private void applyScreenSettings() {
        boolean keepScreenOn = prefs.getBoolean(PREF_KEEP_SCREEN, true);
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private String getSelectedLanguage() {
        return prefs.getString(PREF_LANG, "es-ES");
    }

    private void toggleLanguage() {
        String current = getSelectedLanguage();
        String next = current.startsWith("en") ? "es-ES" : "en-US";
        prefs.edit().putString(PREF_LANG, next).apply();
        updateLanguageHeader();
        spinnerLanguage.setSelection(next.startsWith("en") ? 1 : 0);
        Toast.makeText(this, "Idioma cambiado a: " + (next.startsWith("en") ? "English" : "Español"), Toast.LENGTH_SHORT).show();

        // Reiniciar escucha para actualizar locale si está activa
        if (isListening) {
            stopListening();
            startListening();
        }
    }

    private void updateLanguageHeader() {
        String lang = getSelectedLanguage();
        tvHeaderLanguage.setText(lang.startsWith("en") ? "🌐 EN" : "🌐 ES");
    }

    private void updateModelBadge() {
        String model = prefs.getString(PREF_MODEL, DEFAULT_MODEL);
        tvPrompterModelBadge.setText(model);
    }

    // ==========================================
    // RECONOCIMIENTO DE VOZ CONTINUO
    // ==========================================
    private void initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            tvTeleprompterText.setText("Aviso: El reconocimiento de voz de Google no está disponible en este dispositivo. Puedes usar el teclado para ingresar preguntas.");
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                tvHeaderStatus.setText("🎙️ ESCUCHANDO");
                tvHeaderStatus.setBackgroundResource(R.drawable.bg_status_listening);
                tvHeaderStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent_green));
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                // Si la escucha sigue activa, reintentar automáticamente tras breve pausa
                if (isListening) {
                    speechRestartHandler.postDelayed(() -> {
                        if (isListening) {
                            startSpeechService();
                        }
                    }, 400);
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    etDetectedQuestion.setText(recognizedText);
                    etDetectedQuestion.setSelection(recognizedText.length());

                    // Envío automático al detectar silencio si está configurado
                    boolean autoSend = prefs.getBoolean(PREF_AUTO_SEND, true);
                    if (autoSend && recognizedText.trim().length() > 5) {
                        sendQuestionToGemini(recognizedText);
                    }
                }

                // Continuar en bucle de escucha activa
                if (isListening) {
                    startSpeechService();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    etDetectedQuestion.setText(matches.get(0));
                    etDetectedQuestion.setSelection(matches.get(0).length());
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void checkPermissionAndStartListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQ_RECORD_AUDIO);
        } else {
            startListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            } else {
                Toast.makeText(this, "Permiso de micrófono requerido para escuchar la reunión", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startListening() {
        isListening = true;
        btnToggleListening.setText("⏸️ PAUSAR ESCUCHA ACTIVA");
        btnToggleListening.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_amber));
        startSpeechService();
    }

    private void startSpeechService() {
        if (speechRecognizer == null) {
            initSpeechRecognizer();
        }
        if (speechRecognizer != null) {
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            String lang = getSelectedLanguage();
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    private void stopListening() {
        isListening = false;
        speechRestartHandler.removeCallbacksAndMessages(null);
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
        btnToggleListening.setText("🎙️ INICIAR ESCUCHA ACTIVA");
        btnToggleListening.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));

        tvHeaderStatus.setText("⏸ EN PAUSA");
        tvHeaderStatus.setBackgroundResource(R.drawable.bg_status_idle);
        tvHeaderStatus.setTextColor(ContextCompat.getColor(this, R.color.text_dim));
    }

    // ==========================================
    // CONSULTA A GOOGLE GEMINI
    // ==========================================
    private void sendQuestionToGemini(String question) {
        if (question == null || question.trim().isEmpty()) return;

        lastQuestionSent = question.trim();
        String apiKey = prefs.getString(PREF_API_KEY, DEFAULT_API_KEY);
        String model = prefs.getString(PREF_MODEL, DEFAULT_MODEL);
        String lang = getSelectedLanguage();

        // Mostrar indicador de carga
        pbLoading.setVisibility(View.VISIBLE);
        tvTeleprompterText.setText("⚡ Pensando respuesta senior de alto impacto...");

        geminiService.generateAnswer(lastQuestionSent, apiKey, model, lang, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String answer, String usedModel) {
                pbLoading.setVisibility(View.GONE);
                lastAnswerReceived = answer;
                tvTeleprompterText.setText(answer);
                tvPrompterModelBadge.setText(usedModel);

                // Disparar vibración táctil discreta
                triggerVibration();

                // Guardar en la base de datos SQLite local
                dbHelper.insertConversation(lastQuestionSent, answer, lang);
            }

            @Override
            public void onIgnored() {
                pbLoading.setVisibility(View.GONE);
                tvTeleprompterText.setText("🔇 Audio clasificado como saludo o charla casual.\nListo para la siguiente pregunta técnica.");
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                tvTeleprompterText.setText("⚠️ " + errorMessage);
                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void triggerVibration() {
        boolean enabled = prefs.getBoolean(PREF_VIBRATION, true);
        if (!enabled) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vibratorManager != null) {
                    vibratorManager.getDefaultVibrator().vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(180);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        if (cm != null) {
            cm.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
