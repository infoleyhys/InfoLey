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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hys.carlosoft.leyinfo.R;
import java.util.List;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

/**
 * Adaptador RecyclerView, para mostrar un historial de MenuItems abiertos.
 */
public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    // Lista de objetos MenuItem a mostrar
    private final List<MenuItem> mValues;
    // Listener para manejar eventos de selección de elementos
    private final HistoryListFragment.OnHistory_Listener mListener;
    // Contexto para acceder a recursos
    private final Context _context;

    /**
     * Constructor del adaptador.
     * @param context El contexto para acceso a recursos.
     * @param items La lista de objetos MenuItem a mostrar.
     * @param listener Listener para eventos de selección de elementos.
     */
    public HistoryListAdapter(Context context, List<MenuItem> items, HistoryListFragment.OnHistory_Listener listener) {
        mValues = items;
        mListener = listener;
        _context = context;
    }

    /**
     * Infla el layout del elemento y crea un ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_urlitem, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Asocia los datos al ViewHolder para una posición dada.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MenuItem urlItem = mValues.get(position);
        holder.mItem = urlItem;
        holder.mIdView.setText(urlItem.getTextID());
        holder.mContentView.setText(urlItem.getDescription());
        holder.mIcon.setImageDrawable(getDrawable(_context, urlItem.getIconID()));
        // Asigna un listener para notificar cuando se selecciona un elemento
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onHistoryItemSelect(holder.mItem);
            }
        });
    }

    /**
     * Devuelve el número total de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Clase ViewHolder para contener las vistas de cada elemento.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView; // Vista raíz del elemento
        final TextView mIdView; // TextView para el título del elemento
        final TextView mContentView; // TextView para la descripción del elemento
        final ImageView mIcon; // ImageView para el ícono del elemento
        MenuItem mItem; // El MenuItem representado por este ViewHolder

        /**
         * Constructor del ViewHolder.
         * @param view La vista raíz del layout del elemento.
         */
        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.itemTitle);
            mContentView = view.findViewById(R.id.itemDesc);
            mIcon = view.findViewById(R.id.itemIcon);
        }

        /**
         * Devuelve el texto (R.id.itemDesc) del ViewHolder.
         */
        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
