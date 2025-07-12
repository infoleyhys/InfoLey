/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hys.carlosoft.leyinfo.MainActivity;
import com.hys.carlosoft.leyinfo.Model.NormativaDOCViewModel;
import com.hys.carlosoft.leyinfo.Model.IndiceDOC;
import com.hys.carlosoft.leyinfo.PRINT.webPrint;
import com.hys.carlosoft.leyinfo.R;

import java.util.ArrayList;

/**
 * Fragmento encargado de mostrar y navegar documentos normativos en formato HTML.
 * Permite navegación por artículos, capítulos, títulos y otros elementos.
 */
public class NormativaDOCFragment extends Fragment {
    // Constantes para argumentos
    private static final String ARG_URLDOC = "ARG_URLDOC";
    private static final String ARG_DOCTITLE = "ARG_DOCTITLE";
    // Variables de estado y UI
    private static String mBaseUrl;
    private docListener mCallBack;
    private String currentURL;
    private boolean mNightMode, mZoom;
    private String currentTitle;
    private NormativaDOCViewModel mViewModel;
    private WebView mWebView;
    private boolean mNavOpen;
    private boolean mParsingComplete;
    private Spinner mSpinner;
    private TextView mTextCurrentValue;
    private SeekBar mSeekBar;
    private IndiceDOC mCurrentSearchByItem;
    private View mNavControl;

    /**
     * Crea una nueva instancia del fragmento con los argumentos necesarios.
     */
    public static NormativaDOCFragment newInstance(String url, String title) {
        //SUPER MEGA IMPORTANTE
        NormativaDOCFragment normativaDOCFragment = new NormativaDOCFragment();
        Bundle args = new Bundle();

        args.putString(ARG_URLDOC, url);
        args.putString(ARG_DOCTITLE, title);

        normativaDOCFragment.setArguments(args);
        //mCallBack = (docListener) getActivity();
        return normativaDOCFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guarda el estado del WebView
        mWebView.saveState(outState);
    }

    /**
     * Inicializa la vista y los controles del fragmento.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doc_fragment, container, false);
        // Inicialización de controles de navegación y visualización
        mNavControl = view.findViewById(R.id.include);
        mWebView = view.findViewById(R.id.docWebView);
        mSpinner = view.findViewById(R.id.spinner_nav);
        mTextCurrentValue = view.findViewById(R.id.lbl_nav_text);
        mSeekBar = view.findViewById(R.id.seekBar_nav);

        // Obtiene preferencias de usuario
        SharedPreferences sp = this.requireContext().getSharedPreferences(requireContext().getPackageName() + MainActivity.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        mNightMode = sp.getBoolean(MainActivity.SP_NIGHT_MODE, false);
        mZoom = sp.getBoolean(MainActivity.SP_ZOOM, false);

        // Configuración del WebView
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(mZoom ? 50 : 100);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Muestra el WebView si estaba oculto y aplica modo nocturno si corresponde
                if (view.getVisibility() == View.GONE)
                    view.setVisibility(View.VISIBLE);
                if (mNightMode)
                    refreshWebviewColors(false);
            }
        });

        // Listener para cambios en la SeekBar de navegación
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.isEnabled() && mCallBack != null && mParsingComplete) {
                    updateSeekBarText(progress);
                    updateWebViewURL(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No se utiliza
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No se utiliza
            }
        });

        // Listener para selección de ítems en el Spinner de navegación
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinner.isEnabled() && mCallBack != null && mParsingComplete) {
                    resetSpinner(parent.getItemAtPosition(position).toString());
                    resetSeekBar();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se utiliza
            }
        });

        // Inicializa el fragmento y carga el documento
        InitializeDocFragment();
        FunAtActivityCreated(savedInstanceState);

        return view;
    }

    /**
     * Llena el control de navegación con los textos correspondientes.
     */
    private void populateNavigationControl(ArrayList<String> sp_adapter_text) {
        ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), R.layout.doc_fragment_nav_spiner_item, sp_adapter_text);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        resetSpinner(mSpinnerAdapter.getItem(0));
        resetSeekBar();
    }

    /**
     * Reinicia el Spinner de navegación según el tipo seleccionado.
     */
    private void resetSpinner(String from) {
        mCurrentSearchByItem = getCurrentSearchBy(from);
        mTextCurrentValue.setText(R.string.lbl_searchtext);
    }

    /**
     * Reinicia la SeekBar de navegación.
     */
    private void resetSeekBar() {
        //mAnchorTag = "";
        mSeekBar.setEnabled(false);
        mSeekBar.setProgress(0);
        if (mCurrentSearchByItem != null) {
            mSeekBar.setMax(mCurrentSearchByItem.size() - 1);
            mSeekBar.setEnabled(true);
        }
    }

    /**
     * Actualiza el texto mostrado según la posición de la SeekBar.
     */
    private void updateSeekBarText(int index) {
        mTextCurrentValue.setText(mCurrentSearchByItem.textArray().get(index));
    }

    /**
     * Actualiza la URL del WebView para navegar al elemento seleccionado.
     */
    private void updateWebViewURL(int index) {
        mWebView.loadUrl(String.format("%s%s#%s", mBaseUrl, currentURL, getLinkFromKey(index)));
    }

    /**
     * Obtiene el enlace correspondiente a la clave seleccionada.
     */
    private String getLinkFromKey(int key) {
        return mCurrentSearchByItem.getLink(key);
    }

    /**
     * Devuelve el índice de navegación correspondiente al tipo seleccionado.
     */
    private IndiceDOC getCurrentSearchBy(String from) {
        if (getString(R.string.capitulo).equals(from))
            return mViewModel.getNormativaDOC().getCapitulos();
        if (getString(R.string.titulo).equals(from))
            return mViewModel.getNormativaDOC().getTitulos();
        if (getString(R.string.articulo).equals(from))
            return mViewModel.getNormativaDOC().getArticulos();
        if (getString(R.string.otros).equals(from))
            return mViewModel.getNormativaDOC().getResaltados();
        return null;
    }

    /**
     * Crea los datos para el control de navegación según los índices disponibles.
     */
    private void createNavigatorControlData(NormativaDOCViewModel viewModel) {
        //Intento no recrear al dope
        ArrayList<String> adapter = new ArrayList<>();

        if (viewModel.getNormativaDOC().getTitulos().size() > 0)
            adapter.add(getString(R.string.titulo));
        if (viewModel.getNormativaDOC().getCapitulos().size() > 0)
            adapter.add(getString(R.string.capitulo));
        if (viewModel.getNormativaDOC().getArticulos().size() > 0)
            adapter.add(getString(R.string.articulo));
        if (viewModel.getNormativaDOC().getResaltados().size() > 0)
            adapter.add(getString(R.string.otros));
        if (adapter.isEmpty())
            return;
        populateNavigationControl(adapter);
    }

    /**
     * Muestra u oculta el control de navegación.
     */
    public void showNavigationControl(boolean enable) {
        if (mParsingComplete) {
            if (mNavControl != null) {
                if (enable) {
                    mNavControl.setVisibility(View.VISIBLE);
                    mNavOpen = true;
                } else {
                    mNavControl.setVisibility(View.GONE);
                    mNavOpen = false;
                }
                mTextCurrentValue.setEnabled(enable);
                mSpinner.setEnabled(enable);
                mSeekBar.setEnabled(enable);
            }
        }
    }

    /**
     * Muestra el diálogo de impresión del documento.
     */
    public void showPrintDialog() {
        if (mParsingComplete) {
            webPrint.createWebPrintJob(requireContext(), mWebView, currentTitle);
        }
    }

    /**
     * Cambia el color de fondo del WebView según el modo nocturno.
     */
    private void setWebViewBackground() {
        if (mNightMode)
            mWebView.setBackgroundColor(Color.BLACK);
        else mWebView.setBackgroundColor(Color.WHITE);
    }

    /**
     * Refresca los colores del WebView (fondo y fuente) según el modo nocturno.
     */
    private void refreshWebviewColors(boolean refreshbackground) {
        if (refreshbackground)
            setWebViewBackground();
        if (mNightMode)
            mWebView.loadUrl("javascript:changeFontColor('LightGray')");
        else mWebView.loadUrl("javascript:changeFontColor('Black')");
    }

    /**
     * Habilita/deshabilita el texto pequeño y refresca la vista si se solicita.
     */
    public void smallTextEnable(boolean enable, boolean refresh) {
        mZoom = enable;
        if (refresh) {
            mWebView.getSettings().setTextZoom(enable ? 50 : 100); // where 90 is 90%; default value is ... 100
            mWebView.reload();
            refreshWebviewColors(true);
        }
    }

    /**
     * Habilita/deshabilita el modo nocturno y refresca la vista si se solicita.
     */
    public void nightModeEnable(boolean enable, boolean refresh) {
        mNightMode = enable;
        if (refresh)
            refreshWebviewColors(true);
    }

    // Métodos de acceso a propiedades del fragmento
    public WebView getWebView() {
        return mWebView;
    }

    public boolean isParsingComplete() {
        return mParsingComplete;
    }

    public String getTitle() {
        return currentTitle;
    }

    public boolean isNavOpen() {
        return mNavOpen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // InitializeDocFragment();
    }

    /**
     * Inicializa el fragmento y carga el documento normativo.
     */
    private void InitializeDocFragment() {
        Bundle args = getArguments();
        mParsingComplete = false;
        mNavOpen = false;

        if (args != null) {
            currentURL = args.getString(ARG_URLDOC); //cuando se crea, se pasan
            currentTitle = args.getString(ARG_DOCTITLE);
            mCallBack = (docListener) getActivity();
            mViewModel = new ViewModelProvider(this).get(NormativaDOCViewModel.class);
            mBaseUrl = NormativaDOCViewModel.getBaseUrl();
            mViewModel.setUrl(currentURL);
            mViewModel.loadNormativaDOC().observe(getViewLifecycleOwner(), leyDoc -> {
                if (mCallBack != null) {
                    mParsingComplete = true;
                    createNavigatorControlData(mViewModel);
                    mCallBack.onParsingComplete();
                }
            });
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // FunAtActivityCreated(savedInstanceState);
    }

    /**
     * Restaura el estado del WebView o carga la URL inicial.
     */
    private void FunAtActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&  mWebView.restoreState(savedInstanceState) != null) {
            nightModeEnable(mNightMode, true);
            smallTextEnable(mZoom, true);
            return;
        }
        mWebView.loadUrl(mBaseUrl + currentURL);
        setWebViewBackground();
    }

    /**
     * Interfaz para notificar eventos de parsing al Activity.
     */
    public interface docListener {
        void onParsingComplete();
    }

}
