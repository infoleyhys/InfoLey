/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo.Model;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hys.carlosoft.leyinfo.RES.NormativaRES;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel que gestiona la carga y el acceso a documentos NormativaDOC.
 * Permite cargar documentos HTML de normativa en segundo plano y exponerlos como LiveData.
 */
public class NormativaDOCViewModel extends AndroidViewModel {

    // URL base para los assets locales
    private static String mBaseUrl;
    // Contexto de la aplicación
    private final Application mApp;
    // LiveData que contiene el documento de normativa cargado
    private MutableLiveData<NormativaDOC> mDoc = null;
    // URL relativa del documento a cargar
    private String mUrl = "TESTA";

    /**
     * Constructor del ViewModel.
     * @param application Contexto de la aplicación
     */
    public NormativaDOCViewModel(@NonNull Application application) {
        super(application);
        mApp = application;
        mBaseUrl = NormativaRES.getBaseUrl();
    }

    /**
     * Devuelve la URL base de los assets.
     * @return String con la URL base
     */
    public static String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * Establece la URL relativa del documento a cargar.
     * @param url Ruta del archivo en assets
     */
    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * Devuelve el documento NormativaDOC actualmente cargado (puede ser null).
     * @return Instancia de NormativaDOC o null
     */
    public NormativaDOC getNormativaDOC() {
        return mDoc.getValue();
    }

    /**
     * Devuelve el LiveData asociado al documento de normativa.
     * Si aún no se ha cargado, inicia la carga asíncrona.
     * @return LiveData con el documento
     */
    public LiveData<NormativaDOC> loadNormativaDOC() {
        if (mDoc == null)
            loadDocAsync();
        return mDoc;
    }

    /**
     * Carga el documento de normativa en un hilo de fondo y actualiza el LiveData.
     * Utiliza un ExecutorService para la tarea en background y Handler para actualizar en el hilo principal.
     */
    private void loadDocAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        mDoc = new MutableLiveData<>();
        // Obtiene el InputStream del archivo HTML usando la URL
        NormativaDOC leydoc = new NormativaDOC(NormativaRES.getInstance(mApp).getInputStream(mUrl));
        executor.execute(() -> {
            // Parsea el HTML en background
            leydoc.parseHtml();
            // Actualiza el LiveData en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mDoc.setValue(leydoc);
                }
            });
        });
    }
}
