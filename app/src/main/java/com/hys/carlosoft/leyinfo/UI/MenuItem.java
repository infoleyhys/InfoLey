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

public class MenuItem {
    private final int mText;
    private final String mUrl;
    private final int mDesc;
    private final int mIco;


    private final int mGroup;

    public MenuItem(int item_group, int item_text, int desc_text, String url, int icon_id) {
        this.mGroup = item_group;
        this.mUrl = url;
        this.mText = item_text;
        this.mDesc = desc_text;
        this.mIco = icon_id;
    }


    public int getTextID() {
        return mText;
    }

    public String getUrlString() {
        return mUrl;
    }

    public int getIconID() {
        return mIco;
    }

    public int getDescription() {
        return mDesc;
    }

    public int getGroupID() {
        return mGroup;
    }
}
