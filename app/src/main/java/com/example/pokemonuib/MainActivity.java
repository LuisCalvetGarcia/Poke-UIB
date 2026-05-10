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


public class MainActivity extends AppCompatActivity {


    ///////////////////////DECLARACIÓN VARIABLES///////////////////////
    private Button botonMaximizar;
    private Button botonMinimizar;
    private Button botonMaximizarMax;//*El boton que maximiza al maximo
    private Button botonMinimizarMin;//*El boton que minimiza al mínimo
    private Button botonMapa;
    private Button botonCriaturas;
    private Button botonInventario;
    private TextView textoUbicacion;
    private TextView textoPuntos;
    private TextView textoZoom;

    private EditText textoEntrada; //Per cercar zona
    public SurfaceView dibuix;
    private TextView textoCriaturas;
    private RadioButton botonesCriaturasRadio;
    private RadioButton botonesZonasRdio;
    private RadioButton botonesCapturadasRadio;
    private RadioButton botonesEscapadasRadio;

    private TextView informacio;


    private UnsortedArraySet<View> elementsView;
    private ConstraintLayout layoutPrincipal;//Hace referencia al fondo

    private boolean botonMapaPulsado = false;
    private boolean botonCriaturasPulasado = false;

    private boolean botonInventarioPulsado = false;

    private Context context;

    private static Bitmap bmp;

    private double zoomMin;

    private double zoomMax;

    private double zoomUpd;
    private double x, y;

    private double x1;
    private double y1;
    private double x2;
    private double y2;

    private double h;
    private double w;
    private double fe;
    private boolean primeraExecucio = true;

    double cursorX;
    double cursorY;
    private boolean zoom;
    private boolean arrossegar;

    private ScaleGestureDetector scaleDetector;

    private double cursorXPrevio;
    private double cursorYPrevio;

    private boolean ocultarInv = false;

    private Dialog dialog;

    Map<String, String> guanyador = new HashMap<>();
    private String[] opciones = {"pedra", "paper", "tisores"};

    boolean empat;

    private double puntsTotals = 0;

    private TextView missatgeSuperior;

    private Criatures criaturaActual = null;
    private String zonaActual = null;
    private String genereActual = null;

    private boolean mostrarDialeg = false;

    private String[] generes = {"vapordrac", "focguard", "tornadrac", "aiguard"};
    HashMap<String, String> nomsOficials = new HashMap<>();//zona popular → nom oficial
    TreeMap<String, Rect> zones = new TreeMap<>();
    HashMap<String, Double> velocitatPerGenere = new HashMap<>();
    HashMap<String, Integer> colorPerGenere = new HashMap<>();
    HashMap<String, Integer> puntsPerGenere = new HashMap<>();
    HashMap<String, Double> distanciaPerGenere = new HashMap<>();

    HashMap<String, TreeMap<String, HashSet<Criatures>>> critPerZona = new HashMap<>();

    TreeMap<String, HashSet<Criatures>> criaturesCapturades = new TreeMap<>();
    TreeMap<String, HashSet<Criatures>> criaturesEscapades = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ///////////////////////ASOSIACIÓN DE VARIABLES CON BOTONES///////////////////////
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
        BitmapFactory.Options opcions = new BitmapFactory.Options();
        opcions.inScaled = false;
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapam, opcions);

        scaleDetector = new ScaleGestureDetector(
                dibuix.getContext(), new ScaleListener());
        //Metodo para almacenar los Views en un conjunto
        omplirConjunt();


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


        try {
            String jsonString = llegirJSON(context, R.raw.zones);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray arr = jsonObject.getJSONArray("zones_coords");//Obtener array de zonas

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);//Obtener i-ésimo objeto zona

                String nomPopular = obj.getString("zona");
                String nomOficial = obj.getString("nom");


                int x1 = obj.getInt("x1");
                int y1 = obj.getInt("y1");
                int x2 = obj.getInt("x2");
                int y2 = obj.getInt("y2");

                Rect r = new Rect(x1, y1, x2, y2);
                nomsOficials.put(nomPopular, nomOficial);
                zones.put(nomPopular, r);// Guardar nombre oficial

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


        //Omplir els mappings
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

        guanyador.put("pedra", "tisores");
        guanyador.put("tisores", "paper");
        guanyador.put("paper", "pedra");

        informacio.setMovementMethod(new ScrollingMovementMethod());

        generarCriatures();

        textoEntrada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zonaInput = editable.toString().trim().toLowerCase();

                if (zonaInput.isEmpty()) return;//Si esta buida, retturn

                //Verificar si existeix la zona introduida
                if (zones.containsKey(zonaInput)) {
                    Rect r = zones.get(zonaInput);

                    //Càlcul del centre del rectangle
                    x = r.centerX();
                    y = r.centerY();

                    //Actualitzar fe
                    fe = zoomMax - 3 * zoomUpd;

                    //Mostrar el nombre "oficial" si existeix
                    String nomOficial = nomsOficials.get(zonaInput);
                    if (nomOficial != null) {
                        textoUbicacion.setText(nomOficial);
                    } else {
                        textoUbicacion.setText(zonaInput);
                    }
                } else {
                    textoUbicacion.setText("Zona no trobada");
                }
                repinta();//Cridam el repinta amb les noe
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void repinta() {


        if (dibuix.getHolder().getSurface().isValid()) {
            if (primeraExecucio) {
                zoomMin = (dibuix.getHeight() / (float) bmp.getHeight());
                //zoomMin=1;
                zoomMax = zoomMin * 10;
                zoomUpd = 0.2;
                fe = zoomMin;
                x = bmp.getWidth() / 2;
                y = bmp.getHeight() / 2;
                primeraExecucio = false;
            }

            textoZoom.setText("x " + String.format("%.2f", fe));
            int alt = dibuix.getHeight();
            int ampla = dibuix.getWidth();
            Canvas canvas = dibuix.getHolder().lockCanvas();
            canvas.drawColor(Color.BLACK);
            Rect src, dst;
            w = ampla / fe;
            h = alt / fe;
            x1 = x - (w / 2);
            y1 = y - (h / 2);
            x2 = x + (w / 2);
            y2 = y + (h / 2);

            String zona = calcZona((int) x, (int) y);
            String zonaOficial;
            if (nomsOficials.containsKey(zona)) {
                zonaOficial = nomsOficials.get(zona);
            } else {
                zonaOficial = zona;
            }
            textoUbicacion.setText(zonaOficial);

            src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            dst = new Rect(0, 0, dibuix.getWidth(), dibuix.getHeight());
            canvas.drawBitmap(bmp, src, dst, new Paint());

            pintarCriatura(canvas);

            dibuix.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void pintarCriatura(Canvas canvas) {
        boolean hiHaCriaturaVisible = false;

        Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
        double centreXmapa = src.centerX();
        double centreYmapa = src.centerY();

        for (String zona : new HashSet<>(critPerZona.keySet())) {
            TreeMap<String, HashSet<Criatures>> perGenere = critPerZona.get(zona);

            for (String genere : new HashSet<>(perGenere.keySet())) {
                HashSet<Criatures> conjunt = perGenere.get(genere);

                int colorCriatura = colorPerGenere.get(genere);
                double distanciaDeteccio = distanciaPerGenere.get(genere);
                double factorVisible = zoomMax - distanciaDeteccio * zoomUpd;

                if (fe < factorVisible) continue;

                Iterator<Criatures> it = conjunt.iterator();
                while (it.hasNext()) {
                    Criatures c = it.next();
                    int xc = c.getX();
                    int yc = c.getY();

                    if (xc >= src.left && xc <= src.right && yc >= src.top && yc <= src.bottom) {
                        double dx = xc - centreXmapa;
                        double dy = yc - centreYmapa;
                        double distancia = Math.sqrt(dx * dx + dy * dy);

                        //Moviment d'escapada del bitxet
                        if (distancia < 200) {
                            double velocitat = velocitatPerGenere.get(genere);
                            int nouX = (int) (xc + dx * velocitat);
                            int nouY = (int) (yc + dy * velocitat);

                            c.setX(nouX);
                            c.setY(nouY);

                            String zonaNova = calcZona(nouX, nouY);
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

                        // Coordenades al SurfaceView
                        float xs = (float) ((xc - src.left) * fe);
                        float ys = (float) ((yc - src.top) * fe);

                        float mida = 30;
                        float coordx1 = xs - mida / 2;
                        float coordy1 = ys - mida / 2;
                        float coordx2 = xs + mida / 2;
                        float coordy2 = ys + mida / 2;

                        Paint p = new Paint();
                        p.setColor(colorCriatura);
                        p.setStyle(Paint.Style.FILL);
                        canvas.drawRect(coordx1, coordy1, coordx2, coordy2, p);

                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.WHITE);
                        canvas.drawRect(coordx1, coordy1, coordx2, coordy2, p);

                        hiHaCriaturaVisible = true;

                        float centreXS = dibuix.getWidth() / 2f;
                        float centreYS = dibuix.getHeight() / 2f;
                        float distCentre = (float) Math.sqrt(Math.pow(xs - centreXS, 2) + Math.pow(ys - centreYS, 2));
                        if (distCentre < 20) {
                            criaturaActual = c;
                            zonaActual = zona;
                            genereActual = genere;
                            iniciarJocInventari();
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }


    float centreXS = dibuix.getWidth() / 2f;
        float centreYS = dibuix.getHeight() / 2f;
        Paint pCentre = new Paint();
        pCentre.setColor(Color.WHITE);
        pCentre.setStrokeWidth(5);

        if (hiHaCriaturaVisible) {
            pCentre.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centreXS, centreYS, 20, pCentre);
        } else {
            canvas.drawLine(centreXS - 20, centreYS, centreXS + 20, centreYS, pCentre);
            canvas.drawLine(centreXS, centreYS - 20, centreXS, centreYS + 20, pCentre);
        }
    }

    public void omplirConjunt() {
        elementsView = new
                UnsortedArraySet(17);//Hay 13 Views

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

    //Metodo para hacer visible el resto de botones al pulsar el botonMapa
    public void visibilidadBotonesMapa(View v) {

        //Instanciar iterador
        Iterator<View> iterator = elementsView.iterator();

        //Recorrer el conjunto haciendo visible todos los views uno a uno
        while (iterator.hasNext()) {

            View viewActual = iterator.next();
            if ((viewActual == textoCriaturas) || (viewActual == botonesCapturadasRadio) || (viewActual == botonesEscapadasRadio)
                    || (viewActual == botonesZonasRdio) || (viewActual == botonesCriaturasRadio)) {
                viewActual.setVisibility(View.INVISIBLE);
            } else {
                viewActual.setVisibility(View.VISIBLE);
            }
        }

    }

    public void quitarVisibilidadBotonesMapa(View v) {

        //Instanciar iterador
        Iterator<View> iterator = elementsView.iterator();

        //Recorrer el conjunto haciendo visible todos los views uno a uno
        while (iterator.hasNext()) {

            View viewActual = iterator.next();
            if ((viewActual != botonCriaturas) && (viewActual != botonInventario) && (viewActual != botonMapa)) {
                viewActual.setVisibility(View.INVISIBLE);

            }
        }
    }

    public void visibilidadBotonesCriaturas(View v) {

        //Volver a poner el fondo del menu principal
        layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
        // Instanciar iterador
        Iterator<View> iterator = elementsView.iterator();

        //Recorrer el conjunto de views
        while (iterator.hasNext()) {
            View viewActual = iterator.next();


            if (viewActual == botonCriaturas || viewActual == botonInventario || viewActual == botonMapa) {
                viewActual.setVisibility(View.VISIBLE);

                //Ocultar textoEntrada y botonMapa
            } else if (viewActual == textoEntrada || viewActual == botonMapa) {
                viewActual.setVisibility(View.INVISIBLE);

                //Mostrar los componentes relacionados con criaturas
            } else if (viewActual == textoCriaturas ||
                    viewActual == botonesCapturadasRadio ||
                    viewActual == botonesEscapadasRadio ||
                    viewActual == botonesZonasRdio ||
                    viewActual == botonesCriaturasRadio ||
                    viewActual==informacio) {

                viewActual.setVisibility(View.VISIBLE);

                // Todo lo demás se oculta
            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void quitarVisibilidadBotonesCriaturas(View v) {
        //Instanciar iterador
        Iterator<View> iterator = elementsView.iterator();

        //Recorrer el conjunto y volver a hacer visibles los elementos ocultos
        while (iterator.hasNext()) {
            View viewActual = iterator.next();
            if ((viewActual == botonCriaturas) || (viewActual == botonInventario) || (viewActual == botonMapa)) {
                viewActual.setVisibility(View.VISIBLE);

            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onClickBotMapa(View v) {
                ocultarInv=true;


        informacio.setVisibility(View.INVISIBLE);
        botonesCriaturasRadio.setVisibility(View.INVISIBLE);
        botonesZonasRdio.setVisibility(View.INVISIBLE);
        botonesCapturadasRadio.setVisibility(View.INVISIBLE);
        botonesEscapadasRadio.setVisibility(View.INVISIBLE);


        textoCriaturas.setVisibility(View.INVISIBLE);

        ocultarInventari();
        botonInventarioPulsado = false;
        botonCriaturasPulasado = false;
        //Cambiar el valor del booleano
        botonMapaPulsado = !botonMapaPulsado;

        if (botonMapaPulsado) {
            layoutPrincipal.setBackgroundColor(Color.WHITE);
            visibilidadBotonesMapa(v);

            repinta();

        } else {
            //Volver a poner el fondo del menu principal
            layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
            quitarVisibilidadBotonesMapa(v);
        }
    }


    public void onClickCriaturas(View v) {

        ocultarInv=true;
        botonMapaPulsado = false;
        botonCriaturasPulasado = !botonCriaturasPulasado;
        botonInventarioPulsado = false;
        ocultarInventari();
        informacio.scrollTo(0,0);// es posa al principi

        actualitzarTextCriatures();

        if (botonCriaturasPulasado) {
            informacio.setVisibility(View.VISIBLE);
            visibilidadBotonesCriaturas(v);
        } else {
            informacio.setVisibility(View.INVISIBLE);

            quitarVisibilidadBotonesCriaturas(v);
        }

    }

    public void onClickInventari(View v) {

        //Volver a poner el fondo del menu principal
        layoutPrincipal.setBackgroundResource(R.drawable.fonspokeuib);
        Iterator<View> iterator = elementsView.iterator();

        //Recorrer el conjunto haciendo visible todos los views uno a uno
        while (iterator.hasNext()) {

            View viewActual = iterator.next();
            if ((viewActual == botonCriaturas) || (viewActual == botonInventario) || (viewActual == botonMapa)
                    ||(viewActual==dibuix) ) {
                viewActual.setVisibility(View.VISIBLE);
            } else {
                viewActual.setVisibility(View.INVISIBLE);
            }
        }

        informacio.setVisibility(View.INVISIBLE);
        botonesCriaturasRadio.setVisibility(View.INVISIBLE);
        botonesZonasRdio.setVisibility(View.INVISIBLE);
        botonesCapturadasRadio.setVisibility(View.INVISIBLE);
        botonesEscapadasRadio.setVisibility(View.INVISIBLE);

        ocultarInv=!ocultarInv;
        if(ocultarInv){
            ocultarInventari();
        }else {
            informacio.setVisibility(View.INVISIBLE);
            botonMapaPulsado = false;
            botonCriaturasPulasado = false;
            botonInventarioPulsado = true;

            dibuix.setVisibility(View.VISIBLE);

            pintarInventari();

            botonInventarioPulsado = false;
        }
    }

    private void ocultarInventari() {
        if (!dibuix.getHolder().getSurface().isValid()) return;

        Canvas canvas = dibuix.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        dibuix.getHolder().unlockCanvasAndPost(canvas);

        dibuix.setVisibility(View.INVISIBLE);
    }


    private int obtenerImagen(String jugada) {
        switch (jugada) {
            case "pedra":
                return R.drawable.pedra;
            case "pedrax":
                return R.drawable.pedrax;
            case "paper":
                return R.drawable.paper;
            case "paperx":
                return R.drawable.paperx;
            case "tisores":
                return R.drawable.tisores;
            case "tisoresx":
                return R.drawable.tisoresx;
            default:
                return 0; // importante: 0 indica recurso no encontrado
        }
    }

    public void onClickMaximizar(View v) {
        if (fe == zoomMax) {
            repinta();
        } else {
            fe += zoomMin;
            repinta();

        }
    }

    public void onClickMinimizar(View v) {

        if (fe == zoomMin) {
            repinta();
        } else {
            fe -= zoomMin;
            if (fe < zoomMin) {
                x = bmp.getWidth() / 2;
                y = bmp.getHeight() / 2;
                fe = zoomMin;
            }
            repinta();
        }

    }

    public void onClickMaximizarMax(View v) {

        fe = zoomMax;
        repinta();
    }

    public void onClickMaximizarMin(View v) {
        x = bmp.getWidth() / 2;
        y = bmp.getHeight() / 2;

        fe = zoomMin;
        repinta();
    }

    public void onClickRadioCriaturas(View v){
        actualitzarTextCriatures();
    }

    //MÉTODES PER PODER FER POSSIBLE LA INTERACCIÓ AMB ELS DITS
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (botonMapaPulsado) {
            double xCentroAnterior;
            double yCentroAnterior;


            scaleDetector.onTouchEvent(event);

            cursorX = event.getX();
            cursorY = event.getY();


            double dx, dy;


            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    // quan es pitja amb un dit=trasladar
                    arrossegar = true;
                    zoom = false;
                    cursorXPrevio = cursorX;
                    cursorYPrevio = cursorY;

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    // quan es pitja amb un segon dit=arrosegar=false
                    arrossegar = false;
                    zoom = true;
                    break;
                case MotionEvent.ACTION_MOVE:


                    //Rellenar
                    if (arrossegar) {

                        xCentroAnterior = x;
                        yCentroAnterior = y;
                        dx = cursorX - cursorXPrevio;
                        dy = cursorY - cursorYPrevio;

                        cursorXPrevio = cursorX;
                        cursorYPrevio = cursorY;

                        x = x - 0.5 * dx;
                        y = y - 0.5 * dy;

                        x1 = x - (w / 2);
                        y1 = y - (h / 2);
                        x2 = x + (w / 2);
                        y2 = y + (h / 2);
                        if ((x1 > 0) && (y1 > 0) && (x2 < bmp.getWidth() && (y2 < bmp.getHeight()))) {
                            repinta();


                        } else {
                            x = xCentroAnterior;
                            y = yCentroAnterior;
                        }

                    }


                    // quan es va movent el dit
                    break;
                case MotionEvent.ACTION_UP:
                    // quan s’aixeca el dit
                    zoom = false;
                    arrossegar = false;
                    break;
            }
        }
        return false;

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // Aquí podem recuperar el factor d’escalat
            // que es detecti amb: detector.getScaleFactor();
            // i el podeu utilitzar per actualitzar el vostre
            // factor d’escalat (multiplicau els dos factors)

            fe = fe * detector.getScaleFactor();

            if (fe > zoomMax) {
                fe = zoomMax;


            } else if (fe < zoomMin) {
                x = bmp.getWidth() / 2;
                y = bmp.getHeight() / 2;
                fe = zoomMin;
            }

            repinta();
            return true;
        }
    }

    //Aquest mètode contè tota la lògica  referent a l'execució del joc "Pedra, paper i tisores"
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
        Button response = dialog.findViewById(R.id.creature_response);

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

            rock.setEnabled(false);
            paper.setEnabled(false);
            scissors.setEnabled(false);

            String jugadaCriatura = opciones[new Random().nextInt(3)];
            response.setVisibility(View.VISIBLE);
            response.setForeground(ContextCompat.getDrawable(context, obtenerImagen(jugadaCriatura)));

            empat = false;
            String missatge;

            if (userChoice.equals(jugadaCriatura)) {
                missatge = "Empat!";
                empat = true;
            } else if (guanyador.get(userChoice).equals(jugadaCriatura)) {
                missatge = "Has guanyat!";
                response.setForeground(ContextCompat.getDrawable(context, obtenerImagen(jugadaCriatura + "x")));
            } else {
                missatge = "Has perdut!";
                if (userChoice.equals("pedra")) rock.setForeground(ContextCompat.getDrawable(context, R.drawable.pedrax));
                else if (userChoice.equals("paper")) paper.setForeground(ContextCompat.getDrawable(context, R.drawable.paperx));
                else if (userChoice.equals("tisores")) scissors.setForeground(ContextCompat.getDrawable(context, R.drawable.tisoresx));
            }
            Toast.makeText(getApplicationContext(), missatge, Toast.LENGTH_SHORT).show();

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

                        if (missatge.equals("Has guanyat!")) {
                            missatgeSuperior.setText("Has agafat un " + criaturaActual.getNom() + "!");
                            if (!criaturesCapturades.containsKey(zonaOficial)) {
                                criaturesCapturades.put(zonaOficial, new HashSet<>());
                            }
                            criaturesCapturades.get(zonaOficial).add(criaturaActual);
                            // Sumar punts segons el gènere
                            if (puntsPerGenere.containsKey(genereActual)) {
                                puntsTotals += puntsPerGenere.get(genereActual);
                                textoPuntos.setText("Punts: "+puntsTotals);
                            }


                        } else if (missatge.equals("Has perdut!")) {
                            missatgeSuperior.setText("S'ha escapat un " + criaturaActual.getNom()+"!");
                            if (!criaturesEscapades.containsKey(zonaOficial)) {
                                criaturesEscapades.put(zonaOficial, new HashSet<>());
                            }
                            criaturesEscapades.get(zonaOficial).add(criaturaActual);

                        }
                        //Añadir un pequeño retardo antes de cerrar el diálogo para asegurarnos que funciona
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

                     if(empat) {
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

        if (criaturaActual != null) {
            String nomImatge = criaturaActual.getNom().split("_")[0];
            int resID = getResources().getIdentifier(nomImatge.toLowerCase(), "drawable", getPackageName());
                imgCreature.setImageResource(resID);

        }
        if(!mostrarDialeg) {
            dialog.show();
            mostrarDialeg=true;
        }

    }
    // método para permitir la lectura del archivo jason
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

    private String calcZona(int x, int y) {
        boolean trobat = false;
        String r = "Altres";
        Iterator iterator = zones.entrySet().iterator();
        while(iterator.hasNext() && !trobat){
           Map.Entry<String, Rect > entry = (Map.Entry<String, Rect>) iterator.next();
           Rect rect =  entry.getValue();
           if (rect.contains(x,y)){
               return  entry.getKey();
           }
        }
        return r;
    }
    private void generarCriatures() {
        Random rand = new Random();

        // Asegurar que "Altres" esté en el mapa
        if (!critPerZona.containsKey("Altres")) {
            TreeMap<String, HashSet<Criatures>> aux2 = new TreeMap<>();
            for (String g : generes) {
                aux2.put(g, new HashSet<>());
            }
            critPerZona.put("Altres", aux2);
        }

        for (String genere : generes) {

            for (int i = 0; i < 125; i++) {
                String nom = genere + (rand.nextInt(8) + 1) + "_" + i;
                Criatures criatura = new Criatures(nom, rand.nextInt(bmp.getWidth()), rand.nextInt(bmp.getHeight()));
                String zona = calcZona(criatura.getX(), criatura.getY());

                TreeMap<String, HashSet<Criatures>> aux = critPerZona.get(zona);
                if (aux == null) {
                    aux = new TreeMap<>();
                    for (String g : generes) {
                        aux.put(g, new HashSet<>());
                    }
                    critPerZona.put(zona, aux);
                }

                HashSet<Criatures> aux2 = aux.get(genere);
                if (aux2 == null) {
                    aux2 = new HashSet<>();
                }

                aux2.add(criatura);
                aux.put(genere, aux2);
            }
        }
    }

    //Aquest mètode conté el codi necessaru per actualitzar el text de criatures
    private void actualitzarTextCriatures() {
        StringBuilder text = new StringBuilder();

        if (botonesCriaturasRadio.isChecked()) {
            // Mostrar: criatures per zona i gÃ¨nere
            text.append("<strong>Criatures per zona:</strong><br>");
            for (String zona : critPerZona.keySet()) {
                text.append("A la zona ").append(zona).append(" hi ha:<br>");
                TreeMap<String, HashSet<Criatures>> perGenere = critPerZona.get(zona);
                for (String genere : perGenere.keySet()) {
                    int count = perGenere.get(genere).size();
                    String color = "gray";
                    if (genere.equals("aiguard")) color = "black";
                    else if (genere.equals("focguard")) color = "green";
                    else if (genere.equals("tornadrac")) color = "red";
                    else if (genere.equals("vapordrac")) color = "blue";

                    if(count>0) {
                        text.append("&nbsp;&nbsp;<font color='").append(color).append("'>")
                                .append(count).append(" ").append(genere).append("</font><br>");
                    }
                }
                text.append("<br>");
            }

        } else if (botonesZonasRdio.isChecked()) {
            // Mostrar zones amb coordenades (a partir dels Rect a TreeMap zones)
            text.append("<strong>ZONES DEL MAPA</strong><br>");
            for (Map.Entry<String, Rect> entry : zones.entrySet()) {
                String zona = entry.getKey();
                Rect r = entry.getValue();
                String nomOficial = nomsOficials.getOrDefault(zona, "Altres");
                text.append(zona).append(" (").append(nomOficial).append("): ")
                        .append("(").append(r.left).append(",").append(r.top).append(" - ")
                        .append(r.right).append(",").append(r.bottom).append(")<br>");
            }

        }else if (botonesCapturadasRadio.isChecked()) {
        // Mostrar criatures capturades
        text.append("<strong>Criatures que he agafat:</strong><br>");
        for (Map.Entry<String, HashSet<Criatures>> entry : criaturesCapturades.entrySet()) {
            String zona = entry.getKey();
            for (Criatures c : entry.getValue()) {
                text.append(c.getNom()).append(" a la zona ").append(zona).append("<br>");
            }
        }

    } else if (botonesEscapadasRadio.isChecked()) {
        // Mostrar criatures escapades
        text.append("<strong>Criatures que han escapat:</strong><br>");
        for (Map.Entry<String, HashSet<Criatures>> entry : criaturesEscapades.entrySet()) {
            String zona = entry.getKey();
            for (Criatures c : entry.getValue()) {
                text.append(c.getNom()).append(" a la zona ").append(zona).append("<br>");
            }
        }
    }

        informacio.setText(Html.fromHtml(text.toString()));
    }


    //Gràcies a aquest codi podem pintar les criatures quan es pitja sobre el botó criatures
    private void pintarInventari() {
        if (!dibuix.getHolder().getSurface().isValid()) return;

        Canvas canvas = dibuix.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);

        int marge = 20;
        int files = generes.length;
        int columnes = 8; //Espècies de 1 a 8
        int ample = dibuix.getWidth();
        int alt = dibuix.getHeight();

        int ampleCriatura = (ample - (columnes + 1) * marge) / columnes;
        int altCriatura = (alt - (files + 1) * marge) / files;

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
                //Cercar en tots els conjunts de criatures capturades
                for (HashSet<Criatures> conjunt : criaturesCapturades.values()) {
                    for (Criatures c : conjunt) {
                        if (c.getNom().startsWith(prefix)) {
                            capturada = true;
                            break;
                        }
                    }
                    if (capturada) break;
                }

                //Pintar imatge criatura
                String nomImatge = genere + especie;
                int resID = getResources().getIdentifier(nomImatge.toLowerCase(), "drawable", getPackageName());
                if (resID != 0) {
                    Bitmap bmpCriatura = BitmapFactory.decodeResource(getResources(), resID);
                    Rect src = new Rect(0, 0, bmpCriatura.getWidth(), bmpCriatura.getHeight());
                    Rect dst = new Rect(x, y, x + ampleCriatura, y + altCriatura);
                    canvas.drawBitmap(bmpCriatura, src, dst, new Paint());

                    //Si està capturada, posar un check
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
