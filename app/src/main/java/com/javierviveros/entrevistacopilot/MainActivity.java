package com.javierviveros.entrevistacopilot;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CopilotoPrefs";
    private static final String PREF_API_KEY = "api_key";
    private static final String PREF_MODEL = "model";
    private static final String PREF_LANG = "language";
    private static final String PREF_KEEP_SCREEN = "keep_screen_on";
    private static final String PREF_VIBRATION = "vibration";

    private static final String DEFAULT_API_KEY = BuildConfig.DEFAULT_GEMINI_API_KEY;
    private static final String DEFAULT_MODEL = "gemini-3.5-flash-lite";

    // Servicios & Repositorio
    private GuideRepository guideRepo;
    private GeminiService geminiService;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    // Tabs
    private ScrollView tabLive;
    private View tabCatalog;
    private ScrollView tabPitch;
    private ScrollView tabSettings;
    private BottomNavigationView bottomNav;

    // Header
    private TextView tvHeaderMode;
    private TextView tvHeaderLanguage;

    // Tab 1: Live & Instant Search
    private EditText etSearchKeywords;
    private TextView tvSearchRealtimeCount;
    private MaterialButton btnAskGemini;
    private MaterialButton btnClearSearch;
    private LinearLayout layoutSearchResults;
    private TextView tvSearchResultsHeader;
    private RecyclerView rvSearchResults;
    private QuestionMatchAdapter searchAdapter;

    // Teleprompter
    private TextView tvPrompterTitle;
    private TextView tvPrompterSourceBadge;
    private TextView tvPrompterQuestion;
    private ProgressBar pbLoading;
    private LinearLayout layoutStrategy;
    private TextView tvStrategyText;
    private TextView tvTeleprompterText;
    private MaterialButton btnRefineBrief;
    private MaterialButton btnRefineDetail;
    private MaterialButton btnCopyPrompter;

    // Tab 2: Catalog
    private EditText etCatalogFilter;
    private TextView tvCatalogCount;
    private RecyclerView rvCatalog;
    private CatalogAdapter catalogAdapter;

    // Tab 3: Pitch
    private TextView tvPitchSummary;

    // Tab 4: Settings
    private EditText etApiKey;
    private MaterialButton btnSaveApiKey;
    private Spinner spinnerModel;
    private Spinner spinnerLanguage;
    private MaterialSwitch swKeepScreenOn;
    private MaterialSwitch swVibration;

    // Estado
    private String currentPromptQuestion = "";
    private String currentPromptAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        geminiService = new GeminiService();
        guideRepo = GuideRepository.getInstance(this);

        String savedLang = prefs.getString(PREF_LANG, "es-ES");
        guideRepo.setLanguage(savedLang);

        initViews();
        setupNavigation();
        setupSearchAndTeleprompter();
        setupCatalogTab();
        setupPitchTab();
        setupSettingsTab();
        applyScreenSettings();
    }

    private void initViews() {
        tabLive = findViewById(R.id.tabLive);
        tabCatalog = findViewById(R.id.tabCatalog);
        tabPitch = findViewById(R.id.tabPitch);
        tabSettings = findViewById(R.id.tabSettings);
        bottomNav = findViewById(R.id.bottomNavigation);

        tvHeaderMode = findViewById(R.id.tvHeaderMode);
        tvHeaderLanguage = findViewById(R.id.tvHeaderLanguage);

        // Search & prompter
        etSearchKeywords = findViewById(R.id.etSearchKeywords);
        tvSearchRealtimeCount = findViewById(R.id.tvSearchRealtimeCount);
        btnAskGemini = findViewById(R.id.btnAskGemini);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        layoutSearchResults = findViewById(R.id.layoutSearchResults);
        tvSearchResultsHeader = findViewById(R.id.tvSearchResultsHeader);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        tvPrompterTitle = findViewById(R.id.tvPrompterTitle);
        tvPrompterSourceBadge = findViewById(R.id.tvPrompterSourceBadge);
        tvPrompterQuestion = findViewById(R.id.tvPrompterQuestion);
        pbLoading = findViewById(R.id.pbLoading);
        layoutStrategy = findViewById(R.id.layoutStrategy);
        tvStrategyText = findViewById(R.id.tvStrategyText);
        tvTeleprompterText = findViewById(R.id.tvTeleprompterText);
        btnRefineBrief = findViewById(R.id.btnRefineBrief);
        btnRefineDetail = findViewById(R.id.btnRefineDetail);
        btnCopyPrompter = findViewById(R.id.btnCopyPrompter);

        // Catalog
        etCatalogFilter = findViewById(R.id.etCatalogFilter);
        tvCatalogCount = findViewById(R.id.tvCatalogCount);
        rvCatalog = findViewById(R.id.rvCatalog);

        // Pitch
        tvPitchSummary = findViewById(R.id.tvPitchSummary);

        // Settings
        etApiKey = findViewById(R.id.etApiKey);
        btnSaveApiKey = findViewById(R.id.btnSaveApiKey);
        spinnerModel = findViewById(R.id.spinnerModel);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        swKeepScreenOn = findViewById(R.id.swKeepScreenOn);
        swVibration = findViewById(R.id.swVibration);
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            tabLive.setVisibility(id == R.id.nav_live ? View.VISIBLE : View.GONE);
            tabCatalog.setVisibility(id == R.id.nav_catalog ? View.VISIBLE : View.GONE);
            tabPitch.setVisibility(id == R.id.nav_pitch ? View.VISIBLE : View.GONE);
            tabSettings.setVisibility(id == R.id.nav_settings ? View.VISIBLE : View.GONE);

            if (id == R.id.nav_catalog) {
                loadCatalogData();
            }
            return true;
        });

        tvHeaderLanguage.setOnClickListener(v -> toggleLanguage());
    }

    private void setupSearchAndTeleprompter() {
        updateLanguageHeader();

        // Setup Search Results RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new QuestionMatchAdapter(this::displayQuestionInTeleprompter);
        rvSearchResults.setAdapter(searchAdapter);

        // Instant search on text change
        etSearchKeywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performInstantSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Search on Enter key
        etSearchKeywords.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String q = etSearchKeywords.getText().toString().trim();
                if (!q.isEmpty()) {
                    List<GuideRepository.Question> matches = guideRepo.search(q, 6);
                    if (!matches.isEmpty()) {
                        displayQuestionInTeleprompter(matches.get(0));
                    } else {
                        askGemini(q);
                    }
                }
                return true;
            }
            return false;
        });

        // Botón Consultar a Gemini
        btnAskGemini.setOnClickListener(v -> {
            String q = etSearchKeywords.getText().toString().trim();
            if (!q.isEmpty()) {
                askGemini(q);
            } else {
                Toast.makeText(this, "Escribe una pregunta o término primero", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Limpiar
        btnClearSearch.setOnClickListener(v -> {
            etSearchKeywords.setText("");
            layoutSearchResults.setVisibility(View.GONE);
            searchAdapter.setItems(null);
            tvSearchRealtimeCount.setText("Escribe para filtrar al instante");
        });

        // Quick keyword chips
        findViewById(R.id.chipDynamicB2).setOnClickListener(v -> triggerChipSearch("dynamic B2"));
        findViewById(R.id.chipRtwpPim).setOnClickListener(v -> triggerChipSearch("RTWP PIM"));
        findViewById(R.id.chipLms).setOnClickListener(v -> triggerChipSearch("LMS Traffic Steering"));
        findViewById(R.id.chipNsa).setOnClickListener(v -> triggerChipSearch("5G NSA Option 3x"));
        findViewById(R.id.chipMoshell).setOnClickListener(v -> triggerChipSearch("Moshell comandos"));
        findViewById(R.id.chipMimo).setOnClickListener(v -> triggerChipSearch("Massive MIMO"));
        findViewById(R.id.chipSwitchCisco).setOnClickListener(v -> triggerChipSearch("Switch Cisco"));
        findViewById(R.id.chipVllm).setOnClickListener(v -> triggerChipSearch("vLLM RAG"));
        findViewById(R.id.chipSueldo).setOnClickListener(v -> triggerChipSearch("expectativa salarial"));

        // Acciones teleprompter
        btnCopyPrompter.setOnClickListener(v -> {
            String text = tvTeleprompterText.getText().toString();
            copyToClipboard("Respuesta Copiloto", text);
            Toast.makeText(this, "Respuesta copiada al portapapeles", Toast.LENGTH_SHORT).show();
        });

        btnRefineBrief.setOnClickListener(v -> {
            if (!currentPromptQuestion.isEmpty()) {
                String prompt = currentPromptQuestion + "\n(Genera una versión ultra-breve, directa en 2 viñetas listas para teleprompter)";
                askGemini(prompt);
            }
        });

        btnRefineDetail.setOnClickListener(v -> {
            if (!currentPromptQuestion.isEmpty()) {
                String prompt = currentPromptQuestion + "\n(Profundiza técnicamente con comandos Moshell, parámetros Ericsson y arquitectura)";
                askGemini(prompt);
            }
        });
    }

    private void triggerChipSearch(String keywords) {
        etSearchKeywords.setText(keywords);
        etSearchKeywords.setSelection(keywords.length());
        performInstantSearch(keywords);

        // Auto cargar la coincidencia #1 inmediatamente en el teleprompter
        List<GuideRepository.Question> matches = guideRepo.search(keywords, 6);
        if (!matches.isEmpty()) {
            displayQuestionInTeleprompter(matches.get(0));
        }
    }

    private void performInstantSearch(String query) {
        if (query == null || query.trim().length() < 2) {
            layoutSearchResults.setVisibility(View.GONE);
            searchAdapter.setItems(null);
            tvSearchRealtimeCount.setText("Escribe para filtrar al instante");
            return;
        }

        List<GuideRepository.Question> results = guideRepo.search(query.trim(), 8);
        if (!results.isEmpty()) {
            searchAdapter.setItems(results);
            layoutSearchResults.setVisibility(View.VISIBLE);
            tvSearchResultsHeader.setText("📋 " + results.size() + " COINCIDENCIA" + (results.size() > 1 ? "S ENCONTRADAS:" : " ENCONTRADA:"));
            tvSearchRealtimeCount.setText(results.size() + " encontrada" + (results.size() > 1 ? "s" : ""));
        } else {
            layoutSearchResults.setVisibility(View.GONE);
            searchAdapter.setItems(null);
            tvSearchRealtimeCount.setText("Pulsa 'Consultar a Gemini'");
        }
    }

    private void displayQuestionInTeleprompter(GuideRepository.Question q) {
        currentPromptQuestion = q.title;
        currentPromptAnswer = q.answer;

        tvPrompterQuestion.setText(q.title);
        tvPrompterSourceBadge.setText("MOD " + q.moduleNum + " • P" + q.num);

        if (q.strategy != null && !q.strategy.trim().isEmpty()) {
            tvStrategyText.setText(q.strategy.trim());
            layoutStrategy.setVisibility(View.VISIBLE);
        } else {
            layoutStrategy.setVisibility(View.GONE);
        }

        StringBuilder fullText = new StringBuilder();
        if (q.answer != null && !q.answer.trim().isEmpty()) {
            fullText.append(q.answer.trim());
        }
        if (q.extraSections != null && !q.extraSections.isEmpty()) {
            for (GuideRepository.ExtraSection sec : q.extraSections) {
                fullText.append("\n\n📌 ").append(sec.header).append(":\n").append(sec.body);
            }
        }
        tvTeleprompterText.setText(fullText.toString().trim());

        triggerVibration();
        dbHelper.insertConversation(q.title, fullText.toString(), guideRepo.getLanguage());

        // Hacer scroll suave hacia el teleprompter
        tabLive.post(() -> tabLive.smoothScrollTo(0, findViewById(R.id.cardTeleprompter).getTop() - 20));
    }

    private void askGemini(String question) {
        if (question == null || question.trim().isEmpty()) return;

        currentPromptQuestion = question.trim();
        String apiKey = prefs.getString(PREF_API_KEY, DEFAULT_API_KEY);
        String model = prefs.getString(PREF_MODEL, DEFAULT_MODEL);
        String lang = guideRepo.getLanguage();

        pbLoading.setVisibility(View.VISIBLE);
        tvPrompterQuestion.setText(currentPromptQuestion);
        tvPrompterSourceBadge.setText(model);
        layoutStrategy.setVisibility(View.GONE);
        tvTeleprompterText.setText("⚡ Pensando respuesta senior de alto impacto...");

        geminiService.generateAnswer(currentPromptQuestion, apiKey, model, lang, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String answer, String usedModel) {
                pbLoading.setVisibility(View.GONE);
                currentPromptAnswer = answer;
                tvTeleprompterText.setText(answer);
                tvPrompterSourceBadge.setText(usedModel);
                triggerVibration();
                dbHelper.insertConversation(currentPromptQuestion, answer, lang);
            }

            @Override
            public void onIgnored() {
                pbLoading.setVisibility(View.GONE);
                tvTeleprompterText.setText("🔇 Audio clasificado como saludo o charla casual.");
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                tvTeleprompterText.setText("⚠️ " + errorMessage);
                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        tabLive.post(() -> tabLive.smoothScrollTo(0, findViewById(R.id.cardTeleprompter).getTop() - 20));
    }

    private void setupCatalogTab() {
        rvCatalog.setLayoutManager(new LinearLayoutManager(this));
        catalogAdapter = new CatalogAdapter(this);
        rvCatalog.setAdapter(catalogAdapter);

        loadCatalogData();

        etCatalogFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = s.toString().trim();
                if (filter.length() >= 2) {
                    catalogAdapter.setItems(guideRepo.search(filter, 48));
                } else {
                    catalogAdapter.setItems(guideRepo.getAllQuestions());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCatalogData() {
        var all = guideRepo.getAllQuestions();
        catalogAdapter.setItems(all);
        tvCatalogCount.setText(all.size() + " preguntas en " + guideRepo.getAllModules().size() + " módulos oficiales");
    }

    private void setupPitchTab() {
        tvPitchSummary.setText(CandidateProfile.PITCH_SUMMARY);
    }

    private void setupSettingsTab() {
        String savedApiKey = prefs.getString(PREF_API_KEY, DEFAULT_API_KEY);
        etApiKey.setText(savedApiKey);

        btnSaveApiKey.setOnClickListener(v -> {
            String key = etApiKey.getText().toString().trim();
            prefs.edit().putString(PREF_API_KEY, key).apply();
            Toast.makeText(this, "API Key guardada correctamente", Toast.LENGTH_SHORT).show();
        });

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
                prefs.edit().putString(PREF_MODEL, models[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] languages = new String[]{"Español (es-ES)", "English (en-US)"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.setAdapter(langAdapter);

        String currentLang = guideRepo.getLanguage();
        spinnerLanguage.setSelection(currentLang.startsWith("en") ? 1 : 0);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = position == 1 ? "en-US" : "es-ES";
                prefs.edit().putString(PREF_LANG, code).apply();
                guideRepo.setLanguage(code);
                updateLanguageHeader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swKeepScreenOn.setChecked(prefs.getBoolean(PREF_KEEP_SCREEN, true));
        swKeepScreenOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_KEEP_SCREEN, isChecked).apply();
            applyScreenSettings();
        });

        swVibration.setChecked(prefs.getBoolean(PREF_VIBRATION, true));
        swVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_VIBRATION, isChecked).apply();
        });
    }

    private void toggleLanguage() {
        String current = guideRepo.getLanguage();
        String next = current.startsWith("en") ? "es-ES" : "en-US";
        prefs.edit().putString(PREF_LANG, next).apply();
        guideRepo.setLanguage(next);
        updateLanguageHeader();
        spinnerLanguage.setSelection(next.startsWith("en") ? 1 : 0);
        loadCatalogData();
        Toast.makeText(this, "Idioma: " + (next.startsWith("en") ? "English" : "Español"), Toast.LENGTH_SHORT).show();
    }

    private void updateLanguageHeader() {
        String lang = guideRepo.getLanguage();
        tvHeaderLanguage.setText(lang.startsWith("en") ? "🌐 EN" : "🌐 ES");
    }

    private void applyScreenSettings() {
        boolean keepScreenOn = prefs.getBoolean(PREF_KEEP_SCREEN, true);
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void triggerVibration() {
        boolean enabled = prefs.getBoolean(PREF_VIBRATION, true);
        if (!enabled) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vm != null) {
                    vm.getDefaultVibrator().vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE));
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
}
