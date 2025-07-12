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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hys.carlosoft.leyinfo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

/**
 * Adaptador personalizado para mostrar un menú expandible de elementos agrupados.
 * Cada grupo representa una categoría y cada hijo es un MenuItem con ícono, título y descripción.
 */
class MenuListAdapter extends BaseExpandableListAdapter {
    private final Context _context;
    private final List<String> mGroupList;
    private final Map<String, List<MenuItem>> mListDataChild;

    /**
     * Constructor del adaptador.
     * @param context Contexto de la aplicación.
     * @param menuItems Mapa de grupos (keys) y sus hijos (MenuItem).
     */
    public MenuListAdapter(Context context, Map<String, List<MenuItem>> menuItems) {
        _context = context;
        mGroupList = new ArrayList<>(menuItems.keySet());
        mChildList = menuItems;
    }

    /**
     * Devuelve el hijo de un grupo en una posición específica.
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        try{
            return Objects.requireNonNull(this.mListDataChild.get(mGroupList.get(groupPosition))).get(childPosititon);
        }catch (NullPointerException ignored)//	¯\(°_o)/¯
        {
        }
        return 0;
    }

    /**
     * Devuelve el ID del hijo (posición).
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Crea y retorna la vista para un hijo del grupo.
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        MenuItem mItem = (MenuItem) getChild(groupPosition, childPosition);


        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.url_item, parent, false);
        }

        // Asigna los datos del MenuItem a las vistas
        ((TextView) convertView.findViewById(R.id.itemDesc)).setText(mItem.getDescription());
        ((TextView) convertView.findViewById(R.id.itemTitle)).setText(mItem.getTextID());
        Drawable leyico = getDrawable(_context, mItem.getIconID());

        ((ImageView) convertView.findViewById(R.id.itemIcon)).setImageDrawable(leyico);
        return convertView;
    }

    /**
     * Devuelve la cantidad de hijos de un grupo.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
       try{
       return  Objects.requireNonNull(this.mListDataChild.get(this.mGroupList.get(groupPosition)))
                    .size();
    }catch (NullPointerException ignored)//	¯\(°_o)/¯
       {
       }
        return 0;
    }

    /**
     * Devuelve el objeto del grupo en la posición indicada.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this.mGroupList.get(groupPosition);
    }

    /**
     * Devuelve la cantidad de grupos.
     */
    @Override
    public int getGroupCount() {
        return this.mGroupList.size();
    }

    /**
     * Devuelve el ID del grupo (posición).
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Crea y retorna la vista para un grupo.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView lblListHeader;
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null)
                convertView = infalInflater.inflate(R.layout.url_group_item, parent, false);
        }

        if (convertView != null) {
            lblListHeader = convertView.findViewById(R.id.headerTitle);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
        }

        return convertView;
    }

    /**
     * Indica si los IDs son estables.
     */
    @Override
    public boolean hasStableIds() {
        return false; //podria ser true....
    }

    /**
     * Indica si un hijo es seleccionable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
