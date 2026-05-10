package com.example.pokemonuib;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Main Activity for the Mystic World UIB game.
 * Handles the main UI layout, map rendering, touch gestures, and core game mechanics.
 */
public class MainActivity extends AppCompatActivity {

    // --- UI Elements ---
    private Button botonMaximizar;
    private Button botonMinimizar;
    private Button botonMaximizarMax; // Maximizes to the maximum allowed zoom
    private Button botonMinimizarMin; // Minimizes to the minimum allowed zoom
    private Button botonMapa;
    private Button botonCriaturas;
    private Button botonInventario;
    private TextView textoUbicacion;
    private TextView textoPuntos;
    private TextView textoZoom;

    private EditText textoEntrada;    // Input field to search for a specific zone
    public SurfaceView dibuix;        // Canvas surface for rendering the map and creatures
    private TextView textoCriaturas;
    private RadioButton botonesCriaturasRadio;
    private RadioButton botonesZonasRdio;
    private RadioButton botonesCapturadasRadio;
    private RadioButton botonesEscapadasRadio;
    private TextView informacio;
    private ConstraintLayout layoutPrincipal; // Reference to the main background layout

    // --- State Variables ---
    private UnsortedArraySet<View> elementsView; // Custom set to manage UI views
    private boolean botonMapaPulsado = false;
    private boolean botonCriaturasPulasado = false;
    private boolean botonInventarioPulsado = false;
    private Context context;
    private static Bitmap bmp; // Main map image

    // --- Map & Camera Coordinates ---
    private double zoomMin;
    private double zoomMax;
    private double zoomUpd;
    private double x, y; // Current camera center coordinates
    private double x1, y1, x2, y2; // Bounding box coordinates
    private double h, w; // Screen dimensions
    private double fe;   // Scale factor
    private boolean primeraExecucio = true;

    // --- Touch & Gesture Controls ---
    double cursorX;
    double cursorY;
    private boolean zoom;
    private boolean arrossegar; // Dragging state flag
    private ScaleGestureDetector scaleDetector;
    private double cursorXPrevio;
    private double cursorYPrevio;

    // --- Game Logic Variables ---
    private boolean ocultarInv = false;
    private Dialog dialog;
    Map<String, String> guanyador = new HashMap<>(); // Rock-Paper-Scissors rules
    private String[] opciones = {"pedra", "paper", "tisores"};
    boolean empat;
    private double puntsTotals = 0;
    private TextView missatgeSuperior;
    private Criatures criaturaActual = null;
    private String zonaActual = null;
    private String genereActual = null;
    private boolean mostrarDialeg = false;

    // --- Data Structures (Sets & Mappings) ---
    private String[] generes = {"vapordrac", "focguard", "tornadrac", "aiguard"};
    HashMap<String, String> nomsOficials = new HashMap<>(); // Popular zone name -> Official zone name
    TreeMap<String, Rect> zones = new TreeMap<>();          // Popular zone name -> Bounding Rectangle
    
    // Creature Attributes Mappings
    HashMap<String, Double> velocitatPerGenere = new HashMap<>();
    HashMap<String, Integer> colorPerGenere = new HashMap<>();
    HashMap<String, Integer> puntsPerGenere = new HashMap<>();
    HashMap<String, Double> distanciaPerGenere = new HashMap<>();

    // Nested Mapping: Zone -> (Genre -> Set of Creatures)
    HashMap<String, TreeMap<String, HashSet<Criatures>>> critPerZona = new HashMap<>();

    // Inventory Mappings: Official Zone Name -> Set of Captured/Escaped Creatures
    TreeMap<String, HashSet<Criatures>> criaturesCapturades = new TreeMap<>();
    TreeMap<String, HashSet<Criatures>> criaturesEscapades = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force landscape orientation for better map viewing
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- UI Initialization & Binding ---
        botonMaximizar = findViewById(R.id.button9);
        botonMinimizar = findViewById(R.id.button10);
        botonMaximizarMax = findViewById(R.id.button8);
        botonMinimizarMin = findViewById(R.id.button4);
        botonMapa = findViewById(R.id.button6);
        botonCriaturas = findViewById(R.id.button5);
        botonInventario = findViewById(R.id.button7);
        textoUbicacion = findViewById(R.id.textView3);
        textoPuntos = findViewById(R.id.textView2);
        textoZoom = findViewById(R.id.textView4);
        textoEntrada = findViewById(R.id.editTextText);
        dibuix = findViewById(R.id.surfaceView);
        layoutPrincipal = findViewById(R.id.main);
        botonesCriaturasRadio = findViewById(R.id.radioButton);
        botonesZonasRdio = findViewById(R.id.radioButton2);
        botonesCapturadasRadio = findViewById(R.id.radioButton3);
        botonesEscapadasRadio = findViewById(R.id.radioButton4);
        textoCriaturas = findViewById(R.id.textView);
        informacio = findViewById(R.id.textView5);

        context = getApplicationContext();
        
        // Load map image without automatic scaling to preserve coordinates
        BitmapFactory.Options opcions = new BitmapFactory.Options();
        opcions.inScaled = false;
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapam, opcions);

        scaleDetector = new ScaleGestureDetector(dibuix.getContext(), new ScaleListener());
        
        // Store view references in our custom set for bulk visibility operations
        omplirConjunt();

        // Initially hide all map-related UI elements
        botonMaximizar.setVisibility(View.INVISIBLE);
        botonMinimizar.setVisibility(View.INVISIBLE);
        botonMaximizarMax.setVisibility(View.INVISIBLE);
        botonMinimizarMin.setVisibility(View.INVISIBLE);
        textoUbicacion.setVisibility(View.INVISIBLE);
        textoPuntos.setVisibility(View.INVISIBLE);
        textoZoom.setVisibility(View.INVISIBLE);
        textoEntrada.setVisibility(View.INVISIBLE);
        dibuix.setVisibility(View.INVISIBLE);
        textoCriaturas.setVisibility(View.INVISIBLE);
        botonesCriaturasRadio.setVisibility(View.INVISIBLE);
        botonesZonasRdio.setVisibility(View.INVISIBLE);
        botonesCapturadasRadio.setVisibility(View.INVISIBLE);
        botonesEscapadasRadio.setVisibility(View.INVISIBLE);

        // --- JSON Parsing & Zone Initialization ---
        try {
            String jsonString = llegirJSON(context, R.raw.zones);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray arr = jsonObject.getJSONArray("zones_coords"); // Retrieve zones array

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i); // Get i-th zone object

                String nomPopular = obj.getString("zona");
                String nomOficial = obj.getString("nom");

                int x1 = obj.getInt("x1");
                int y1 = obj.getInt("y1");
                int x2 = obj.getInt("x2");
                int y2 = obj.getInt("y2");

                Rect r = new Rect(x1, y1, x2, y2);
                nomsOficials.put(nomPopular, nomOficial);
                zones.put(nomPopular, r); // Store bounding box linked to popular name

                // Initialize empty creature sets for each genre in this zone
                TreeMap<String, HashSet<Criatures>> aux2 = new TreeMap<>();
                for (String g : generes) {
                    aux2.put(g, new HashSet<Criatures>());
                }
                
                criaturesCapturades.put(nomOficial, new HashSet<>());
                criaturesEscapades.put(nomOficial, new HashSet<>());
                critPerZona.put(nomPopular, aux2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Populate Creature Attributes Mappings ---
        velocitatPerGenere.put("aiguard", 0.01 * 2);
        velocitatPerGenere.put("focguard", 0.015 * 2);
        velocitatPerGenere.put("tornadrac", 0.02 * 2);
        velocitatPerGenere.put("vapordrac", 0.025 * 2);

        colorPerGenere.put("aiguard", Color.BLACK);
        colorPerGenere.put("focguard", Color.GREEN);
        colorPerGenere.put("tornadrac", Color.RED);
        colorPerGenere.put("vapordrac", Color.BLUE);

        puntsPerGenere.put("aiguard", 10);
        puntsPerGenere.put("focguard", 15);
        puntsPerGenere.put("tornadrac", 20);
        puntsPerGenere.put("vapordrac", 30);

        distanciaPerGenere.put("aiguard", 0.0);
        distanciaPerGenere.put("focguard", 1.0);
        distanciaPerGenere.put("tornadrac", 2.5);
        distanciaPerGenere.put("vapordrac", 3.5);

        // Define winning rules for Rock-Paper-Scissors (Key beats Value)
        guanyador.put("pedra", "tisores");
        guanyador.put("tisores", "paper");
        guanyador.put("paper", "pedra");

        informacio.setMovementMethod(new ScrollingMovementMethod());

        // Spawn initial creatures across the map
        generarCriatures();

        // --- Search Bar Listener ---
        textoEntrada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String zonaInput = editable.toString().trim().toLowerCase();

                if (zonaInput.isEmpty()) return; // Exit if input is empty

                // Verify if the requested zone exists
                if (zones.containsKey(zonaInput)) {
                    Rect r = zones.get(zonaInput);

                    // Calculate center coordinates of the zone's bounding box
                    x = r.centerX();
                    y = r.centerY();

                    // Adjust zoom scale to focus on the zone
                    fe = zoomMax - 3 * zoomUpd;

                    // Display official name if available
                    String nomOficial = nomsOficials.get(zonaInput);
                    if (nomOficial != null) {
                        textoUbicacion.setText(nomOficial);
                    } else {
                        textoUbicacion.setText(zonaInput);
                    }
                } else {
                    textoUbicacion.setText("Zona no trobada"); // Zone not found
                }
                
                repinta(); // Request map redraw with new coordinates
            }
        });

        // Handle edge-to-edge system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

/**
     * Main rendering loop for the map and its entities.
     * Calculates the bounding box of the camera, handles zoom logic, 
     * and triggers the painting of creatures on the active Canvas.
     */
    public void repinta() {
        if (dibuix.getHolder().getSurface().isValid()) {
            
            // Initialization block: Runs only on the first execution to set initial bounds
            if (primeraExecucio) {
                zoomMin = (dibuix.getHeight() / (float) bmp.getHeight());
                zoomMax = zoomMin * 10;
                zoomUpd = 0.2;
                fe = zoomMin; // Initial scale factor
                x = bmp.getWidth() / 2f;
                y = bmp.getHeight() / 2f;
                primeraExecucio = false;
            }

            textoZoom.setText("x " + String.format("%.2f", fe));
            int alt = dibuix.getHeight();
            int ampla = dibuix.getWidth();
            
            // Lock the canvas for exclusive drawing
            Canvas canvas = dibuix.getHolder().lockCanvas();
            canvas.drawColor(Color.BLACK);
            
            // Calculate source dimensions based on the current scale factor
            w = ampla / fe;
            h = alt / fe;
            
            // Define the bounding box of the map section currently visible on screen
            x1 = x - (w / 2);
            y1 = y - (h / 2);
            x2 = x + (w / 2);
            y2 = y + (h / 2);

            // Determine which zone the center of the camera is currently looking at
            String zona = calcZona((int) x, (int) y);
            String zonaOficial;
            if (nomsOficials.containsKey(zona)) {
                zonaOficial = nomsOficials.get(zona);
            } else {
                zonaOficial = zona;
            }
            textoUbicacion.setText(zonaOficial);

            // Draw the map image
            Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            Rect dst = new Rect(0, 0, dibuix.getWidth(), dibuix.getHeight());
            canvas.drawBitmap(bmp, src, dst, new Paint());

            // Draw the creatures over the map
            pintarCriatura(canvas);

            // Unlock the canvas and render to the screen
            dibuix.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Renders creatures on the screen, calculates distances between the player and creatures, 
     * handles their escape behavior, and detects capture collisions.
     * * @param canvas The canvas to draw the creatures on
     */
    public void pintarCriatura(Canvas canvas) {
        boolean hiHaCriaturaVisible = false;

        Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
        double centreXmapa = src.centerX();
        double centreYmapa = src.centerY();

        // Iterate through all zones and their creature sets safely using copies of the key sets
        for (String zona : new HashSet<>(critPerZona.keySet())) {
            TreeMap<String, HashSet<Criatures>> perGenere = critPerZona.get(zona);

            for (String genere : new HashSet<>(perGenere.keySet())) {
                HashSet<Criatures> conjunt = perGenere.get(genere);

                int colorCriatura = colorPerGenere.get(genere);
                double distanciaDeteccio = distanciaPerGenere.get(genere);
                
                // Calculate at which zoom level this specific species becomes visible
                double factorVisible = zoomMax - distanciaDeteccio * zoomUpd;

                // If the current zoom level is not close enough, skip rendering this species
                if (fe < factorVisible) continue;

                Iterator<Criatures> it = conjunt.iterator();
                while (it.hasNext()) {
                    Criatures c = it.next();
                    int xc = c.getX();
                    int yc = c.getY();

                    // Check if the creature's coordinates fall within the current visible screen bounds
                    if (xc >= src.left && xc <= src.right && yc >= src.top && yc <= src.bottom) {
                        
                        double dx = xc - centreXmapa;
                        double dy = yc - centreYmapa;
                        double distancia = Math.sqrt(dx * dx + dy * dy);

                        // Escape Movement Logic: If the player gets too close (distance < 200)
                        if (distancia < 200) {
                            double velocitat = velocitatPerGenere.get(genere);
                            int nouX = (int) (xc + dx * velocitat);
                            int nouY = (int) (yc + dy * velocitat);

                            c.setX(nouX);
                            c.setY(nouY);

                            String zonaNova = calcZona(nouX, nouY);
                            
                            // If the creature escapes into a different zone, transfer it to the new zone's set
                            if (!zonaNova.equals(zona)) {
                                if (critPerZona.containsKey(zonaNova)) {
                                    critPerZona.get(zonaNova).get(genere).add(c);
                                }
                                it.remove();
                                continue;
                            } else {
                                xc = nouX;
                                yc = nouY;
                                dx = xc - centreXmapa;
                                dy = yc - centreYmapa;
                                distancia = Math.sqrt(dx * dx + dy * dy);
                            }
                        }

                        // Translate map coordinates to screen coordinates
                        float xs = (float) ((xc - src.left) * fe);
                        float ys = (float) ((yc - src.top) * fe);

                        // Dimensions for rendering the creature
                        float mida = 30;
                        float coordx1 = xs - mida / 2;
                        float coordy1 = ys - mida / 2;
                        float coordx2 = xs + mida / 2;
                        float coordy2 = ys + mida / 2;

                        Paint p = new Paint();
                        p.setColor(colorCriatura);
                        p.setStyle(Paint.Style.FILL);
                        canvas.drawRect(coordx1, coordy1, coordx2, coordy2, p);

                        // Draw a white border around the creature
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.WHITE);
                        canvas.drawRect(coordx1, coordy1, coordx2, coordy2, p);

                        hiHaCriaturaVisible = true;

                        // Collision Detection: If the player is exactly on the creature (distance < 20)
                        float centreXS = dibuix.getWidth() / 2f;
                        float centreYS = dibuix.getHeight() / 2f;
                        float distCentre = (float) Math.sqrt(Math.pow(xs - centreXS, 2) + Math.pow(ys - centreYS, 2));
                        
                        if (distCentre < 20) {
                            criaturaActual = c;
                            zonaActual = zona;
                            genereActual = genere;
                            
                            // Trigger the mini-game/capture sequence
                            iniciarJocInventari();
                            it.remove(); // Remove creature from map immediately upon interaction
                            break;
                        }
                    }
                }
            }
        }

        // --- Draw User Reticle ---
        float centreXS = dibuix.getWidth() / 2f;
        float centreYS = dibuix.getHeight() / 2f;
        Paint pCentre = new Paint();
        pCentre.setColor(Color.WHITE);
        pCentre.setStrokeWidth(5);

        // Change reticle shape depending on whether creatures are currently visible on screen
        if (hiHaCriaturaVisible) {
            pCentre.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centreXS, centreYS, 20, pCentre); // Draw circle if creatures are nearby
        } else {
            canvas.drawLine(centreXS - 20, centreYS, centreXS + 20, centreYS, pCentre); // Draw crosshair if alone
            canvas.drawLine(centreXS, centreYS - 20, centreXS, centreYS + 20, pCentre);
        }
    }

    /**
     * Populates the custom Set containing all UI views.
     * This allows for efficient O(N) iteration when toggling visibility states.
     */
    public void omplirConjunt() {
        // Initialize custom UnsortedArraySet with capacity for 17 views
        elementsView = new UnsortedArraySet<>(17); 

        elementsView.add(botonMaximizar);
        elementsView.add(botonMinimizar);
        elementsView.add(botonMaximizarMax);
        elementsView.add(botonMinimizarMin);
        elementsView.add(botonMapa);
        elementsView.add(botonCriaturas);
        elementsView.add(botonInventario);
        elementsView.add(textoUbicacion);
        elementsView.add(textoPuntos);
        elementsView.add(textoZoom);
        elementsView.add(textoEntrada);
        elementsView.add(dibuix);
        elementsView.add(textoCriaturas);
        elementsView.add(botonesCapturadasRadio);
        elementsView.add(botonesEscapadasRadio);
        elementsView.add(botonesZonasRdio);
        elementsView.add(botonesCriaturasRadio);
        elementsView.add(informacio);
    }

    /**
     * Toggles visibility of UI components when the Map mode is activated.
     * Utilizes the custom iterator from UnsortedArraySet.
     * * @param v The view that triggered the event (Map button)
     */
    public void visibilidadBotonesMapa(View v) {
        Iterator<View> iterator = elementsView.iterator();

        while (iterator.hasNext()) {
            View viewActual = iterator.next();
            
            // Keep List/Radio button views hidden while in Map mode
            if ((viewActual == textoCriaturas) || (viewActual == botonesCapturadasRadio) || 
                (viewActual == botonesEscapadasRadio) || (viewActual == botonesZonasRdio) || 
                (viewActual == botonesCriaturasRadio)) {
                viewActual.setVisibility(View.INVISIBLE);
            } else {
                viewActual.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Hides all secondary UI components, retaining only the main navigation buttons.
     * * @param v The view that triggered the event
     */
    public void quitarVisibilidadBotonesMapa(View v) {
        Iterator<View> iterator = elementsView.iterator();

        while (iterator.hasNext()) {
            View viewActual = iterator.next();
            
            // Hide everything except the three main navigational buttons
            if ((viewActual != botonCriaturas) && (viewActual != botonInventario) && (viewActual != botonMapa)) {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }
    }

/**
     * Toggles visibility of UI components when the "Creatures" (List/Info) mode is activated.
     * Shows the text info and radio buttons while hiding map controls.
     * * @param v The view that triggered the event
     */
    public void visibilidadBotonesCriaturas(View v) {
        // Restore the main menu background image
        layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
        
        Iterator<View> iterator = elementsView.iterator();

        // Iterate through the custom set of views to update their visibility
        while (iterator.hasNext()) {
            View viewActual = iterator.next();

            // Always keep the main navigation buttons visible
            if (viewActual == botonCriaturas || viewActual == botonInventario || viewActual == botonMapa) {
                viewActual.setVisibility(View.VISIBLE);

            // Explicitly hide the search input and the map button (if applicable to logic)
            } else if (viewActual == textoEntrada || viewActual == botonMapa) {
                viewActual.setVisibility(View.INVISIBLE);

            // Show all components related to the Creatures information panel
            } else if (viewActual == textoCriaturas ||
                    viewActual == botonesCapturadasRadio ||
                    viewActual == botonesEscapadasRadio ||
                    viewActual == botonesZonasRdio ||
                    viewActual == botonesCriaturasRadio ||
                    viewActual == informacio) {
                viewActual.setVisibility(View.VISIBLE);

            // Hide everything else (map, zoom buttons, location texts)
            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Hides the Creatures information panel and restores the default state.
     * * @param v The view that triggered the event
     */
    public void quitarVisibilidadBotonesCriaturas(View v) {
        Iterator<View> iterator = elementsView.iterator();

        while (iterator.hasNext()) {
            View viewActual = iterator.next();
            
            if ((viewActual == botonCriaturas) || (viewActual == botonInventario) || (viewActual == botonMapa)) {
                viewActual.setVisibility(View.VISIBLE);
            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }
    }

    // --- OnClick Listeners for Main Navigation ---

    /**
     * Handles the click event for the "Map" button.
     * Toggles the map view, updates the background, and triggers rendering.
     * * @param v The view that triggered the event
     */
    public void onClickBotMapa(View v) {
        ocultarInv = true;

        // Hide creature-related UI components
        informacio.setVisibility(View.INVISIBLE);
        botonesCriaturasRadio.setVisibility(View.INVISIBLE);
        botonesZonasRdio.setVisibility(View.INVISIBLE);
        botonesCapturadasRadio.setVisibility(View.INVISIBLE);
        botonesEscapadasRadio.setVisibility(View.INVISIBLE);
        textoCriaturas.setVisibility(View.INVISIBLE);

        ocultarInventari();
        
        // Reset other button states
        botonInventarioPulsado = false;
        botonCriaturasPulasado = false;
        
        // Toggle map state
        botonMapaPulsado = !botonMapaPulsado;

        if (botonMapaPulsado) {
            layoutPrincipal.setBackgroundColor(Color.WHITE); // Solid background for map rendering
            visibilidadBotonesMapa(v);
            repinta(); // Start drawing the map
        } else {
            // Restore default main menu background
            layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
            quitarVisibilidadBotonesMapa(v);
        }
    }

    /**
     * Handles the click event for the "Creatures" list button.
     * * @param v The view that triggered the event
     */
    public void onClickCriaturas(View v) {
        ocultarInv = true;
        botonMapaPulsado = false;
        botonCriaturasPulasado = !botonCriaturasPulasado;
        botonInventarioPulsado = false;
        
        ocultarInventari();
        informacio.scrollTo(0,0); // Scroll text view to the top

        actualitzarTextCriatures(); // Refresh the text data

        if (botonCriaturasPulasado) {
            informacio.setVisibility(View.VISIBLE);
            visibilidadBotonesCriaturas(v);
        } else {
            informacio.setVisibility(View.INVISIBLE);
            quitarVisibilidadBotonesCriaturas(v);
        }
    }

    /**
     * Handles the click event for the "Inventory" button.
     * Repurposes the SurfaceView (dibuix) to render the captured creatures grid.
     * * @param v The view that triggered the event
     */
    public void onClickInventari(View v) {
        // Restore main menu background
        layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
        Iterator<View> iterator = elementsView.iterator();

        while (iterator.hasNext()) {
            View viewActual = iterator.next();
            // Keep main buttons and the SurfaceView (canvas) visible
            if ((viewActual == botonCriaturas) || (viewActual == botonInventario) || (viewActual == botonMapa) || (viewActual == dibuix)) {
                viewActual.setVisibility(View.VISIBLE);
            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }

        // Hide list UI elements
        informacio.setVisibility(View.INVISIBLE);
        botonesCriaturasRadio.setVisibility(View.INVISIBLE);
        botonesZonasRdio.setVisibility(View.INVISIBLE);
        botonesCapturadasRadio.setVisibility(View.INVISIBLE);
        botonesEscapadasRadio.setVisibility(View.INVISIBLE);

        ocultarInv = !ocultarInv;
        
        if(ocultarInv){
            ocultarInventari();
        } else {
            informacio.setVisibility(View.INVISIBLE);
            botonMapaPulsado = false;
            botonCriaturasPulasado = false;
            botonInventarioPulsado = true;

            dibuix.setVisibility(View.VISIBLE);
            pintarInventari(); // Render the grid of captured creatures
            botonInventarioPulsado = false;
        }
    }

    /**
     * Clears the SurfaceView and hides it when closing the inventory.
     */
    private void ocultarInventari() {
        if (!dibuix.getHolder().getSurface().isValid()) return;

        // Paint the canvas white to clear previous drawings before hiding
        Canvas canvas = dibuix.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        dibuix.getHolder().unlockCanvasAndPost(canvas);

        dibuix.setVisibility(View.INVISIBLE);
    }

    /**
     * Helper method to map a Rock-Paper-Scissors move string to its drawable resource ID.
     * * @param jugada The move played ("pedra", "paper", "tisores", etc.)
     * @return The resource ID of the corresponding image, or 0 if not found
     */
    private int obtenerImagen(String jugada) {
        switch (jugada) {
            case "pedra": return R.drawable.pedra;
            case "pedrax": return R.drawable.pedrax;
            case "paper": return R.drawable.paper;
            case "paperx": return R.drawable.paperx;
            case "tisores": return R.drawable.tisores;
            case "tisoresx": return R.drawable.tisoresx;
            default: return 0; // 0 indicates resource not found
        }
    }

    // --- OnClick Listeners for Map Zoom Controls ---

    /**
     * Increases the map zoom scale by one step.
     * * @param v The view that triggered the event
     */
    public void onClickMaximizar(View v) {
        if (fe == zoomMax) {
            repinta();
        } else {
            fe += zoomMin; // Increase scale factor
            repinta();
        }
    }

    /**
     * Decreases the map zoom scale by one step.
     * * @param v The view that triggered the event
     */
    public void onClickMinimizar(View v) {
        if (fe == zoomMin) {
            repinta();
        } else {
            fe -= zoomMin; // Decrease scale factor
            
            // Prevent zooming out beyond the minimum scale
            if (fe < zoomMin) {
                x = bmp.getWidth() / 2f;
                y = bmp.getHeight() / 2f;
                fe = zoomMin;
            }
            repinta();
        }
    }

    /**
     * Maximizes the map zoom to the highest allowed limit instantly.
     * * @param v The view that triggered the event
     */
    public void onClickMaximizarMax(View v) {
        fe = zoomMax;
        repinta();
    }

    /**
     * Minimizes the map zoom to the lowest allowed limit instantly, centering the map.
     * * @param v The view that triggered the event
     */
    public void onClickMaximizarMin(View v) {
        // Reset coordinates to the center of the map
        x = bmp.getWidth() / 2f;
        y = bmp.getHeight() / 2f;
        fe = zoomMin;
        repinta();
    }

    /**
     * Handles clicks on the RadioButtons to update the information displayed in the text area.
     * * @param v The view that triggered the event
     */
    public void onClickRadioCriaturas(View v){
        actualitzarTextCriatures();
    }
   // --- Touch Interaction Methods ---

    /**
     * Handles touch screen motion events for dragging and zooming the map.
     * * @param event The motion event triggered by the user's touch
     * @return boolean True if the event was handled, false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Only process map interactions if the Map view is active
        if (botonMapaPulsado) {
            double xCentroAnterior;
            double yCentroAnterior;

            // Pass the event to the scale detector for pinch-to-zoom gestures
            scaleDetector.onTouchEvent(event);

            cursorX = event.getX();
            cursorY = event.getY();

            double dx, dy;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    // Single finger touch: Initiate dragging
                    arrossegar = true;
                    zoom = false;
                    cursorXPrevio = cursorX;
                    cursorYPrevio = cursorY;
                    break;
                    
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Second finger touch: Stop dragging, initiate zooming
                    arrossegar = false;
                    zoom = true;
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    // Finger moving across the screen
                    if (arrossegar) {
                        xCentroAnterior = x;
                        yCentroAnterior = y;
                        
                        dx = cursorX - cursorXPrevio;
                        dy = cursorY - cursorYPrevio;

                        cursorXPrevio = cursorX;
                        cursorYPrevio = cursorY;

                        // Adjust camera center based on drag delta (scaled for smoothness)
                        x = x - 0.5 * dx;
                        y = y - 0.5 * dy;

                        // Calculate new bounding box
                        x1 = x - (w / 2);
                        y1 = y - (h / 2);
                        x2 = x + (w / 2);
                        y2 = y + (h / 2);
                        
                        // Prevent dragging the camera outside the map boundaries
                        if ((x1 > 0) && (y1 > 0) && (x2 < bmp.getWidth() && (y2 < bmp.getHeight()))) {
                            repinta();
                        } else {
                            // Revert to previous position if boundary is hit
                            x = xCentroAnterior;
                            y = yCentroAnterior;
                        }
                    }
                    break;
                    
                case MotionEvent.ACTION_UP:
                    // Finger lifted: End dragging and zooming
                    zoom = false;
                    arrossegar = false;
                    break;
            }
        }
        return false;
    }

    /**
     * Inner class to handle scale gestures (pinch-to-zoom).
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Update the current scale factor by multiplying with the detector's factor
            fe = fe * detector.getScaleFactor();

            // Clamp the scale factor to defined min and max bounds
            if (fe > zoomMax) {
                fe = zoomMax;
            } else if (fe < zoomMin) {
                // If zoomed out completely, re-center the map
                x = bmp.getWidth() / 2f;
                y = bmp.getHeight() / 2f;
                fe = zoomMin;
            }

            repinta(); // Redraw the map with the new scale
            return true;
        }
    }

    // --- Mini-Game & Core Logic ---

    /**
     * Contains the logic for executing the "Rock, Paper, Scissors" mini-game
     * triggered upon capturing a creature.
     */
    private void iniciarJocInventari() {

        dialog = new Dialog(dibuix.getContext());
        dialog.setContentView(R.layout.dialog_joc);
        dialog.getWindow().setLayout((int) (dibuix.getWidth() * 0.9), (int) (dibuix.getHeight() * 0.9));
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgCreature = dialog.findViewById(R.id.creature_image);
        missatgeSuperior = dialog.findViewById(R.id.dialog_message);

        missatgeSuperior.setText("Pedra, paper o tisores?");

        Button rock = dialog.findViewById(R.id.button_rock);
        Button paper = dialog.findViewById(R.id.button_paper);
        Button scissors = dialog.findViewById(R.id.button_scissors);
        Button response = dialog.findViewById(R.id.creature_response); // Creature's move display

        rock.setEnabled(true);
        paper.setEnabled(true);
        scissors.setEnabled(true);

        rock.setForeground(ContextCompat.getDrawable(context, R.drawable.pedra));
        paper.setForeground(ContextCompat.getDrawable(context, R.drawable.paper));
        scissors.setForeground(ContextCompat.getDrawable(context, R.drawable.tisores));

        response.setVisibility(View.INVISIBLE);

        View.OnClickListener listener = v -> {
            String userChoice = "";

            if (v == rock) userChoice = "pedra";
            else if (v == paper) userChoice = "paper";
            else if (v == scissors) userChoice = "tisores";

            // Disable buttons after user makes a choice
            rock.setEnabled(false);
            paper.setEnabled(false);
            scissors.setEnabled(false);

            // Generate a random move for the creature
            String jugadaCriatura = opciones[new Random().nextInt(3)];
            response.setVisibility(View.VISIBLE);
            response.setForeground(ContextCompat.getDrawable(context, obtenerImagen(jugadaCriatura)));

            empat = false;
            String missatge;

            // Determine winner based on the Rock-Paper-Scissors rules mapping
            if (userChoice.equals(jugadaCriatura)) {
                missatge = "Empat!"; // Tie
                empat = true;
            } else if (guanyador.get(userChoice).equals(jugadaCriatura)) {
                missatge = "Has guanyat!"; // Win
                response.setForeground(ContextCompat.getDrawable(context, obtenerImagen(jugadaCriatura + "x")));
            } else {
                missatge = "Has perdut!"; // Lose
                if (userChoice.equals("pedra")) rock.setForeground(ContextCompat.getDrawable(context, R.drawable.pedrax));
                else if (userChoice.equals("paper")) paper.setForeground(ContextCompat.getDrawable(context, R.drawable.paperx));
                else if (userChoice.equals("tisores")) scissors.setForeground(ContextCompat.getDrawable(context, R.drawable.tisoresx));
            }
            Toast.makeText(getApplicationContext(), missatge, Toast.LENGTH_SHORT).show();

            // Delay before closing dialog to show results
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    if (!empat && criaturaActual != null && zonaActual != null && genereActual != null) {
                        String zonaOficial;
                        if (nomsOficials.containsKey(zonaActual)) {
                            zonaOficial = nomsOficials.get(zonaActual);
                        } else {
                            zonaOficial = zonaActual;
                        }

                        // Process Capture or Escape logic based on game result
                        if (missatge.equals("Has guanyat!")) {
                            missatgeSuperior.setText("Has agafat un " + criaturaActual.getNom() + "!");
                            if (!criaturesCapturades.containsKey(zonaOficial)) {
                                criaturesCapturades.put(zonaOficial, new HashSet<>());
                            }
                            criaturesCapturades.get(zonaOficial).add(criaturaActual);
                            
                            // Add score based on creature genre
                            if (puntsPerGenere.containsKey(genereActual)) {
                                puntsTotals += puntsPerGenere.get(genereActual);
                                textoPuntos.setText("Punts: " + puntsTotals);
                            }

                        } else if (missatge.equals("Has perdut!")) {
                            missatgeSuperior.setText("S'ha escapat un " + criaturaActual.getNom() + "!");
                            if (!criaturesEscapades.containsKey(zonaOficial)) {
                                criaturesEscapades.put(zonaOficial, new HashSet<>());
                            }
                            criaturesEscapades.get(zonaOficial).add(criaturaActual);
                        }

                        // Short delay before completely closing the dialog
                        new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {}

                            @Override
                            public void onFinish() {
                                if (!empat && mostrarDialeg) {
                                    dialog.dismiss();
                                    mostrarDialeg = false;
                                }
                            }
                        }.start();
                    }

                    // If tie, reset buttons to play again
                    if (empat) {
                        response.setVisibility(View.INVISIBLE);
                        rock.setEnabled(true);
                        paper.setEnabled(true);
                        scissors.setEnabled(true);
                    }
                }
            }.start();
        };

        rock.setOnClickListener(listener);
        paper.setOnClickListener(listener);
        scissors.setOnClickListener(listener);

        // Load correct creature image for the dialog
        if (criaturaActual != null) {
            String nomImatge = criaturaActual.getNom().split("_")[0];
            int resID = getResources().getIdentifier(nomImatge.toLowerCase(), "drawable", getPackageName());
            imgCreature.setImageResource(resID);
        }
        
        if (!mostrarDialeg) {
            dialog.show();
            mostrarDialeg = true;
        }
    }

    /**
     * Reads a JSON file from the raw resources directory.
     * * @param context Application context
     * @param id Resource ID (e.g., R.raw.zones)
     * @return String containing the JSON data
     */
    public String llegirJSON(Context context, int id) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(id);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Determines which predefined map zone a specific coordinate pair falls into.
     * * @param x X-coordinate
     * @param y Y-coordinate
     * @return The internal name of the zone, or "Altres" (Others) if outside defined zones
     */
    private String calcZona(int x, int y) {
        boolean trobat = false;
        String r = "Altres"; // Default fallback zone
        Iterator<Map.Entry<String, Rect>> iterator = zones.entrySet().iterator();
        
        // Traverse the map of zones to check for bounding box collisions
        while (iterator.hasNext() && !trobat) {
            Map.Entry<String, Rect> entry = iterator.next();
            Rect rect = entry.getValue();
            if (rect.contains(x, y)) {
                return entry.getKey();
            }
        }
        return r;
    }

    /**
     * Procedurally generates the initial population of 500 creatures (125 of each genre)
     * and assigns them to random coordinates across the map.
     */
    private void generarCriatures() {
        Random rand = new Random();

        // Ensure the fallback "Altres" zone exists in the data structure
        if (!critPerZona.containsKey("Altres")) {
            TreeMap<String, HashSet<Criatures>> aux2 = new TreeMap<>();
            for (String g : generes) {
                aux2.put(g, new HashSet<>());
            }
            critPerZona.put("Altres", aux2);
        }

        for (String genere : generes) {
            // Spawn 125 creatures per genre
            for (int i = 0; i < 125; i++) {
                // Generate name format: Genre[1-8]_[ID]
                String nom = genere + (rand.nextInt(8) + 1) + "_" + i;
                Criatures criatura = new Criatures(nom, rand.nextInt(bmp.getWidth()), rand.nextInt(bmp.getHeight()));
                String zona = calcZona(criatura.getX(), criatura.getY());

                // Retrieve or initialize the zone map
                TreeMap<String, HashSet<Criatures>> aux = critPerZona.get(zona);
                if (aux == null) {
                    aux = new TreeMap<>();
                    for (String g : generes) {
                        aux.put(g, new HashSet<>());
                    }
                    critPerZona.put(zona, aux);
                }

                // Add creature to the corresponding Set
                HashSet<Criatures> aux2 = aux.get(genere);
                if (aux2 == null) {
                    aux2 = new HashSet<>();
                }

                aux2.add(criatura);
                aux.put(genere, aux2);
            }
        }
    }

    /**
     * Updates the text view displaying detailed game statistics (Zones, Captures, Escapes)
     * dynamically formatting the text with HTML tags for coloring and bolding.
     */
    private void actualitzarTextCriatures() {
        StringBuilder text = new StringBuilder();

        if (botonesCriaturasRadio.isChecked()) {
            // Display: Active creatures per zone and genre
            text.append("<strong>Criatures per zona:</strong><br>");
            for (String zona : critPerZona.keySet()) {
                text.append("A la zona ").append(zona).append(" hi ha:<br>");
                TreeMap<String, HashSet<Criatures>> perGenere = critPerZona.get(zona);
                
                for (String genere : perGenere.keySet()) {
                    int count = perGenere.get(genere).size();
                    String color = "gray";
                    
                    // Apply visual color coding based on creature type
                    if (genere.equals("aiguard")) color = "black";
                    else if (genere.equals("focguard")) color = "green";
                    else if (genere.equals("tornadrac")) color = "red";
                    else if (genere.equals("vapordrac")) color = "blue";

                    if (count > 0) {
                        text.append("&nbsp;&nbsp;<font color='").append(color).append("'>")
                                .append(count).append(" ").append(genere).append("</font><br>");
                    }
                }
                text.append("<br>");
            }

        } else if (botonesZonasRdio.isChecked()) {
            // Display: List of zones and their map coordinates
            text.append("<strong>ZONES DEL MAPA</strong><br>");
            for (Map.Entry<String, Rect> entry : zones.entrySet()) {
                String zona = entry.getKey();
                Rect r = entry.getValue();
                String nomOficial = nomsOficials.getOrDefault(zona, "Altres");
                
                text.append(zona).append(" (").append(nomOficial).append("): ")
                        .append("(").append(r.left).append(",").append(r.top).append(" - ")
                        .append(r.right).append(",").append(r.bottom).append(")<br>");
            }

        } else if (botonesCapturadasRadio.isChecked()) {
            // Display: Successfully captured creatures log
            text.append("<strong>Criatures que he agafat:</strong><br>");
            for (Map.Entry<String, HashSet<Criatures>> entry : criaturesCapturades.entrySet()) {
                String zona = entry.getKey();
                for (Criatures c : entry.getValue()) {
                    text.append(c.getNom()).append(" a la zona ").append(zona).append("<br>");
                }
            }

        } else if (botonesEscapadasRadio.isChecked()) {
            // Display: Escaped creatures log
            text.append("<strong>Criatures que han escapat:</strong><br>");
            for (Map.Entry<String, HashSet<Criatures>> entry : criaturesEscapades.entrySet()) {
                String zona = entry.getKey();
                for (Criatures c : entry.getValue()) {
                    text.append(c.getNom()).append(" a la zona ").append(zona).append("<br>");
                }
            }
        }

        // Parse HTML and set the text
        informacio.setText(Html.fromHtml(text.toString()));
    }

    /**
     * Renders the Inventory view on the Canvas.
     * Displays a matrix of all possible species and adds a checkmark if captured.
     */
    private void pintarInventari() {
        if (!dibuix.getHolder().getSurface().isValid()) return;

        Canvas canvas = dibuix.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE); // Inventory background

        int marge = 20;
        int files = generes.length;
        int columnes = 8; // Species 1 through 8 per genre
        int ample = dibuix.getWidth();
        int alt = dibuix.getHeight();

        int ampleCriatura = (ample - (columnes + 1) * marge) / columnes;
        int altCriatura = (alt - (files + 1) * marge) / files;

        // Ensure consistent alphabetical rendering order
        TreeSet<String> generesOrdenats = new TreeSet<>();
        for (String g : generes) generesOrdenats.add(g);

        int fila = 0;
        for (String genere : generesOrdenats) {
            for (int especie = 1; especie <= 8; especie++) {
                int col = especie - 1;
                int x = marge + col * (ampleCriatura + marge);
                int y = marge + fila * (altCriatura + marge);

                String prefix = genere + especie;
                boolean capturada = false;
                
                // Check if this specific species variant has been captured in any zone
                for (HashSet<Criatures> conjunt : criaturesCapturades.values()) {
                    for (Criatures c : conjunt) {
                        if (c.getNom().startsWith(prefix)) {
                            capturada = true;
                            break;
                        }
                    }
                    if (capturada) break;
                }

                // Render the base species image
                String nomImatge = genere + especie;
                int resID = getResources().getIdentifier(nomImatge.toLowerCase(), "drawable", getPackageName());
                if (resID != 0) {
                    Bitmap bmpCriatura = BitmapFactory.decodeResource(getResources(), resID);
                    Rect src = new Rect(0, 0, bmpCriatura.getWidth(), bmpCriatura.getHeight());
                    Rect dst = new Rect(x, y, x + ampleCriatura, y + altCriatura);
                    canvas.drawBitmap(bmpCriatura, src, dst, new Paint());

                    // Render a checkmark overlay if the creature has been captured
                    if (capturada) {
                        Bitmap bmpCheck = BitmapFactory.decodeResource(getResources(), R.drawable.check);
                        Rect dstCheck = new Rect(x, y, x + ampleCriatura, y + altCriatura);
                        canvas.drawBitmap(bmpCheck, null, dstCheck, new Paint());
                    }
                }
            }
            fila++;
        }

        dibuix.getHolder().unlockCanvasAndPost(canvas);
    }
}
