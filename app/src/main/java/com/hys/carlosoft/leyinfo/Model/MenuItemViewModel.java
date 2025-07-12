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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hys.carlosoft.leyinfo.RES.NormativaRES;
import com.hys.carlosoft.leyinfo.UI.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel que gestiona los datos y la selección de MenuItem para la UI.
 * Permite obtener items a partir de URLs, IDs y provee acceso reactivo al item seleccionado.
 */
public class MenuItemViewModel extends AndroidViewModel {

    // LiveData que contiene el MenuItem actualmente seleccionado
    private final MutableLiveData<MenuItem> item = new MutableLiveData<>();
    // Referencia a la aplicación para acceder a recursos globales
    private final Application mApp;

    /**
     * Constructor del ViewModel.
     * @param application Instancia de la aplicación
     */
    public MenuItemViewModel(@NonNull Application application) {
        super(application);
        mApp = application;
    }

    /**
     * Busca y devuelve un MenuItem a partir de una URL en formato String.
     * @param urlstr URL del item a buscar
     * @return MenuItem correspondiente o null si no se encuentra
     */
    public MenuItem getItemFromUrlString(String urlstr) {
        String cleanurl;
        String webDomain = NormativaRES.getWebDomainUrl(false); // false = sin argumentos GET
     
        // redireciono los deeplink que apuntan a la página principal 
        if (urlstr.equals(webDomain) || urlstr.equals(webDomain + "index.html"))
            cleanurl = NormativaRES.getIndexFileName();
            // sumo "?page=" a la URL, ya que no es el mismo caso que la página principal
        else cleanurl = urlstr.substring(NormativaRES.getWebDomainUrl(true).length()); 

        Map<String, List<MenuItem>> map = NormativaRES.getInstance(mApp).getmItems();

        // Busca el MenuItem correspondiente en el mapa
        for (Map.Entry<String, List<MenuItem>> entry : map.entrySet()) {
            for (MenuItem item : entry.getValue()) {
                if (item.getUrlString().equals(cleanurl)) {
                    return item;
                }
            }
        }
        return null; // No se encontró el item, imposible que se ejecute...ponele
    }

    /**
     * Selecciona un MenuItem y lo publica en el LiveData.
     * @param urlItem MenuItem a seleccionar
     */
    public void select(MenuItem urlItem) {
        item.setValue(urlItem);
    }

    /**
     * Devuelve el LiveData con el MenuItem seleccionado.
     * @return LiveData<MenuItem>
     */
    public LiveData<MenuItem> getItem() {
        return item;
    }

   /**
    * Devuelve una lista de MenuItem a partir de listas de IDs de grupo y texto.
    * @param groupids Lista de IDs de grupo
    * @param textids Lista de IDs de texto
    * @return Lista de MenuItem encontrados
    */
    public List<MenuItem> getItemsFromIds(List<Integer> groupids, List<Integer> textids) {
        List<MenuItem> list = new ArrayList<>();
        int listsize = groupids.size(); // mismo tamaño que textids...ponele

        for (int index = 0; index < listsize; index++) {
            MenuItem item = NormativaRES.getInstance(mApp).getUrlItemFromIds(groupids.get(index), textids.get(index));
            if (item != null)
                list.add(item);
        }
        return list;
    }

    /**
     * Devuelve el mapa completo de items agrupados por clave.
     * @return Mapa de items
     */
    public Map<String, List<MenuItem>> getItemsMap() {
        return NormativaRES.getInstance(mApp).getmItems();
    }


}
