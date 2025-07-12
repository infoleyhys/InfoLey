/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo.RES;

import android.app.Application;
import android.content.res.Resources;

import com.hys.carlosoft.leyinfo.R;
import com.hys.carlosoft.leyinfo.UI.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Clase que centraliza la gestión de la normativa y enlaces legales de la app.
 * Implementa el patrón Singleton para asegurar una única instancia.
 */
public class NormativaRES {
    // Constantes para los nombres de archivos HTML incluidos en la aplicación
    private static final String HTML_LEY19587 = "ley19587.html";
    private static final String HTML_DEC351_ANEXO1 = "dec351/anexo1.html";
    private static final String HTML_DEC351_ANEXO2 = "dec351/anexo2.html";
    private static final String HTML_DEC351_ANEXO3 = "dec351/anexo3.html";
    private static final String HTML_DEC351_ANEXO4 = "dec351/anexo4.html";
    private static final String HTML_DEC351_ANEXO5 = "dec351/anexo5.html";
    private static final String HTML_DEC351_ANEXO6 = "dec351/anexo6.html";
    private static final String HTML_DEC351_ANEXO7 = "dec351/anexo7.html";
    private static final String HTML_DEC351_RES295_A1 = "dec351/res295A1.html";
    private static final String HTML_DEC351_RES295_A2 = "dec351/res295A2.html";
    private static final String HTML_DEC911_DEC911 = "dec911/dec911.html";
    private static final String HTML_DEC911_RES231 = "dec911/res231.html";
    private static final String HTML_DEC911_RES51 = "dec911/res51.html";
    private static final String HTML_DEC911_RES503 = "dec911/res503.html";
    private static final String HTML_DEC911_RES550 = "dec911/res550.html";
    private static final String HTML_DEC911_RES319 = "dec911/res319.html";
    private static final String HTML_DEC911_RES35 = "dec911/res35.html";
    private static final String HTML_DEC911_RES42 = "dec911/res42.html";
    private static final String HTML_DEC911_RES61a1 = "dec911/res61anexo1.html";
    private static final String HTML_DEC911_RES61a2 = "dec911/res61anexo2.html";
    private static final String HTML_SRT617 = "srt/dec617.html";
    private static final String HTML_SRT658 = "srt/dec658.html";
    private static final String HTML_SRT249 = "srt/dec249.html";
    private static final String HTML_SRT1338 = "srt/dec1338.html";
    private static final String HTML_SRT10877 = "srt/dec10877.html";
    private static final String HTML_LEY24557 = "ley24557.html";
    private static final String HTML_LEY20744 = "otros/ley20744.html";
    private static final String HTML_LEY24051 = "otros/ley24051.html";
    private static final String HTML_LEY24430 = "otros/ley24430.html";
    private static final String HTML_SRT905 = "srt/srt905.html";
    private static final String HTML_SRT960 = "srt/srt960.html";
    private static final String HTML_SRT655 = "srt/srt655.html";
    private static final String HTML_SRT311 = "srt/srt311.html";
    private static final String HTML_SRT3068 = "srt/srt3068.html";
    private static final String HTML_SRT13 = "srt/srt13.html";
    private static final String HTML_SRT81 = "srt/srt81.html";
    // Base URL para los archivos HTML (puede ser local o remoto)
    private static final String ASSET_FOLDER = "file:///android_asset/";
    private static final String WEBAPP_DOMAIN = "https://infoley.consultoramartinez.com.ar/";
    private static final String WEBAPP_PAGEARG = "?page=";
    // Links a otras páginas de terceros
    private static final String LINK_STR = "https://www.argentina.gob.ar/srt";
    private static final String LINK_ARG = "https://www.argentina.gob.ar/normativa/buscar";
    private static final String LINK_OLD = "https://www.infoleg.gob.ar/";

    // Instancia única (Singleton)
    private static NormativaRES ourInstance;
    // Mapa que asocia el nombre del grupo con la lista de items de menú
    private final Map<String, List<MenuItem>> mItems = new LinkedHashMap<>(); // Mantiene el orden de los items
    // Contexto de la aplicación
    private final Application mApp;

    /**
     * Devuelve la instancia única de NormativaRES.
     * @param app Contexto de la aplicación
     * @return Instancia de NormativaRES
     */
    public static NormativaRES getInstance(Application app) {
        if (ourInstance == null)
            ourInstance = new NormativaRES(app);
        return ourInstance;
    }

    /**
     * Constructor privado para inicializar los datos de los items.
     * @param app Contexto de la aplicación
     */
    private NormativaRES(Application app) {
        mApp = app;
        prepareMenuListData(mApp, mItems);
    }

    /**
     * Devuelve la URL base de los assets locales.
     * @return String con la URL base
     */
    static public String getBaseUrl() {
        return ASSET_FOLDER;
    }

    /**
     * Devuelve la URL del dominio web, con o sin argumentos.
     * @param withargs Si es true, agrega el argumento de página
     * @return String con la URL
     */
    static public String getWebDomainUrl(boolean withargs) {
        if(withargs)
            return String.format("%s%s",  WEBAPP_DOMAIN, WEBAPP_PAGEARG);
        return WEBAPP_DOMAIN;
    }

    /**
     * Devuelve el nombre del archivo índice principal.
     * @return Nombre del archivo HTML principal
     */
    static public String getIndexFileName() {
        return HTML_LEY19587;
    }

    /**
     * Prepara la estructura de datos del menú, agrupando los items por categoría.
     * @param context Contexto de la aplicación
     * @param items Mapa donde se cargan los grupos y sus items
     */
    private static void prepareMenuListData(Application context, Map<String, List<MenuItem>> items) {
        // Iconos para los distintos tipos de documentos
        int leyico = R.drawable.ic_file_item;
        int decico = R.drawable.ic_file_dec;
        int srtico = R.drawable.ic_file_srt;

        // Lista de items para cada grupo/categoría
        List<MenuItem> ley19587 = new ArrayList<>();
        ley19587.add(new MenuItem(R.string.ley19587, R.string.ley19587, R.string.desc_19587, HTML_LEY19587, leyico));
        ley19587.add(new MenuItem(R.string.ley19587, R.string.decreto_617_97, R.string.desc_617, HTML_SRT617, R.drawable.ic_trigo));
        ley19587.add(new MenuItem(R.string.ley19587, R.string.res_311_03, R.string.desc_res_311, HTML_SRT311, R.drawable.ic_tv));
        ley19587.add(new MenuItem(R.string.ley19587, R.string.dec_249, R.string.desc_dec_249, HTML_SRT249, R.drawable.ic_minas));
        ley19587.add(new MenuItem(R.string.ley19587, R.string.decreto_1338_96, R.string.desc_1338, HTML_SRT1338, decico));
        ley19587.add(new MenuItem(R.string.ley19587, R.string.res_905_15, R.string.desc_res_905, HTML_SRT905, R.drawable.ic_file_srt));

        List<MenuItem> dec351 = new ArrayList<>();
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo1, R.string.desc_anexo1, HTML_DEC351_ANEXO1, R.drawable.ic_worker));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo2, R.string.desc_anexo2, HTML_DEC351_ANEXO2, R.drawable.ic_termico));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo3, R.string.desc_anexo3, HTML_DEC351_ANEXO3, R.drawable.ic_quimica));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo4, R.string.desc_anexo4, HTML_DEC351_ANEXO4, R.drawable.ic_iluminacion));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo5, R.string.desc_anexo5, HTML_DEC351_ANEXO5, R.drawable.ic_acustica));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo6, R.string.desc_anexo6, HTML_DEC351_ANEXO6, R.drawable.ic_electricidad));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo7, R.string.desc_anexo7, HTML_DEC351_ANEXO7, R.drawable.ic_incendio));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo1, R.string.desc_res_295A1, HTML_DEC351_RES295_A1, R.drawable.ic_ergonomia));
        dec351.add(new MenuItem(R.string.dec351, R.string.anexo2, R.string.desc_res_295A2, HTML_DEC351_RES295_A2, R.drawable.ic_radiaciones));

        List<MenuItem> dec911 = new ArrayList<>();
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.decreto_911_96, R.string.desc_911, HTML_DEC911_DEC911, R.drawable.ic_wall));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_321_96, R.string.desc_res_321, HTML_DEC911_RES231, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_319_99, R.string.desc_res_319, HTML_DEC911_RES319, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_51_97, R.string.desc_res_51, HTML_DEC911_RES51, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_35_98, R.string.desc_res_35, HTML_DEC911_RES35, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_550_11, R.string.desc_res_550, HTML_DEC911_RES550, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_503_14, R.string.desc_res_503, HTML_DEC911_RES503, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_42_18, R.string.desc_res_42, HTML_DEC911_RES42, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_61_23_a1, R.string.desc_res_61_23_a1, HTML_DEC911_RES61a1, srtico));
        dec911.add(new MenuItem(R.string.decreto_911_96, R.string.res_61_23_a2, R.string.desc_res_61_23_a2, HTML_DEC911_RES61a2, srtico));

        List<MenuItem> srt = new ArrayList<>();
        srt.add(new MenuItem(R.string.resoluciones_srt, R.string.res_3068_14, R.string.desc_res_3068, HTML_SRT3068, R.drawable.ic_electricidad));
        srt.add(new MenuItem(R.string.resoluciones_srt, R.string.res_960_15, R.string.desc_res_960, HTML_SRT960, R.drawable.ic_forklift));
        srt.add(new MenuItem(R.string.resoluciones_srt, R.string.res_655_03, R.string.desc_res_655, HTML_SRT655, R.drawable.ic_combustibles));
        srt.add(new MenuItem(R.string.resoluciones_srt, R.string.res_13_20, R.string.desc_res_13,HTML_SRT13, R.drawable.ic_ergonomia));
        srt.add(new MenuItem(R.string.resoluciones_srt, R.string.res_81_19, R.string.desc_res_81, HTML_SRT81, R.drawable.ic_enfermedades));

        List<MenuItem> ley13660 = new ArrayList<>();
        ley13660.add(new MenuItem(R.string.ley13660, R.string.dec10877, R.string.desc_dec10877, HTML_SRT10877, R.drawable.ic_combustibles));

        List<MenuItem> ley24051 = new ArrayList<>();
        ley24051.add(new MenuItem(R.string.ley24051, R.string.ley24051, R.string.desc_ley24051, HTML_LEY24051, R.drawable.ic_desechos));

        List<MenuItem> ley24557 = new ArrayList<>();
        ley24557.add(new MenuItem(R.string.ley24557, R.string.ley24557, R.string.desc_ley24557, HTML_LEY24557, leyico));
        ley24557.add(new MenuItem(R.string.ley24557, R.string.dec_658, R.string.desc_dec_658, HTML_SRT658, R.drawable.ic_enfermedades));

        List<MenuItem> otras = new ArrayList<>();
        otras.add(new MenuItem(R.string.otras, R.string.ley20744, R.string.desc_20744, HTML_LEY20744, leyico));
        otras.add(new MenuItem(R.string.otras, R.string.ley24430, R.string.desc_24430, HTML_LEY24430, leyico));

        List<MenuItem> enlaces = new ArrayList<>();
        enlaces.add(new MenuItem(R.string.enlaces, R.string.SRT, R.string.desc_SRT, LINK_STR, srtico));
        enlaces.add(new MenuItem(R.string.enlaces, R.string.ARG, R.string.desc_ARG, LINK_ARG, leyico));
        enlaces.add(new MenuItem(R.string.enlaces, R.string.OLD, R.string.desc_OLD, LINK_OLD, leyico));

        // Obtiene los recursos para los nombres de los grupos
        Resources res = context.getResources();
        // Asocia cada grupo con su lista de items
        items.put(res.getString(R.string.ley19587), ley19587); // Header, Child data
        items.put(res.getString(R.string.dec351), dec351);
        items.put(res.getString(R.string.decreto_911_96), dec911);
        items.put(res.getString(R.string.resoluciones_srt), srt);
        items.put(res.getString(R.string.ley13660), ley13660);
        items.put(res.getString(R.string.ley24051), ley24051);
        items.put(res.getString(R.string.ley24557), ley24557);
        items.put(res.getString(R.string.otras), otras);
        items.put(res.getString(R.string.enlaces), enlaces);
    }

    /**
     * Devuelve el mapa de items de menú agrupados por categoría.
     * @return Mapa de items
     */
    public Map<String, List<MenuItem>> getmItems() {
        return mItems;
    }

    /**
     * Devuelve un InputStream para un archivo de assets dado su URL relativa.
     * @param url Ruta del archivo en assets
     * @return InputStream del archivo, o null si hay error
     */
    public InputStream getInputStream(String url) {
        InputStream out = null;
        try {
            out = mApp.getAssets().open(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Busca un MenuItem a partir de los IDs de grupo y texto.
     * @param groupID ID del grupo (header)
     * @param textID ID del texto del item
     * @return MenuItem correspondiente, o null si no existe
     */
    public MenuItem getUrlItemFromIds(int groupID, int textID) {
        String headertext = mApp.getResources().getString(groupID);

        for (MenuItem item : Objects.requireNonNull(mItems.get(headertext))) {
            if (item != null)
                if (item.getTextID() == textID)
                    return item;
        }

        return null;
    }
}