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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hys.carlosoft.leyinfo.Model.MenuItemViewModel;
import com.hys.carlosoft.leyinfo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fragmento que muestra un menú expandible de items agrupados.
 * Las actividades que contienen este fragmento deben implementar la interfaz
 * {@link OnMenu_Listener} para manejar los eventos de selección.
 */
public class MenuListFragment extends Fragment {

    private OnMenu_Listener mListener;
    private ArrayList<String> listMenuGroup = new ArrayList<>();
    private Map<String, List<MenuItem>> listMenuItem = new HashMap<>();

    private MenuItemViewModel viewModel;
    private ExpandableListView menuExpListView;

    /**
     * Constructor vacío requerido.
     */
    public MenuListFragment() {
    }

    /**
     * Selecciona un item del menú a partir de una Uri.
     * @param url Uri del item a seleccionar
     * @return true si el item fue encontrado y seleccionado, false en caso contrario
     */
    public boolean selectItem(Uri url) {
        MenuItem item;

        if (url != null && (item = viewModel.getItemFromUrlString(url.toString())) != null) {
            doSelectItem(item);
            return true;
        }
        return false;
    }

    /**
     * Realiza la selección de un Menuitem y notifica al listener.
     * @param item Item seleccionado
     */
    private void doSelectItem(MenuItem item) {
        if (mListener != null && viewModel != null) {
            viewModel.select(item);
            mListener.onMenuListItemSelect(item);

        }
    }

    /**
     * Infla la vista del fragmento y configura el menú expandible.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_urlitem, container, false);
        menuExpListView = view.findViewById(R.id.menu_expandable_list);

        viewModel = new ViewModelProvider(requireActivity()).get(MenuItemViewModel.class);
        listMenuItem = viewModel.getItemsMap();
        listMenuGroup = new ArrayList<>(listMenuItem.keySet());

        initRecyclerView();

        return view;
    }

    /**
     * Inicializa el adaptador y listeners del menú expandible.
     */
    private void initRecyclerView() {
        MenuListAdapter menuAdapter = new MenuListAdapter(getContext(), listMenuItem);
        menuExpListView.setAdapter(menuAdapter);
        menuExpListView.expandGroup(0); // Expande el primer grupo por defecto (Ley 19587)
        menuExpListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            MenuItem mItem = Objects.requireNonNull(listMenuItem.get(listMenuGroup.get(groupPosition))).get(childPosition);
            doSelectItem(mItem);
            return false;
        });

    }

    /**
     * Se llama cuando el fragmento se adjunta a una actividad.
     * Verifica que la actividad implemente la interfaz OnMenu_Listener.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMenu_Listener) {
            mListener = (OnMenu_Listener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnMenu_Listener");
        }
    }

    /**
     * Se llama cuando el fragmento se separa de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMenu_Listener {
        // Notifico a la UI que se seleccionó un item del menú
        void onMenuListItemSelect(MenuItem item);
    }
}
