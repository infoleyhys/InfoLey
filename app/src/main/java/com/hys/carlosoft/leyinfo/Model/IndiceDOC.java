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

import java.util.ArrayList;

/**
 * Created by Carlos on 10/3/2017.
 */
public class IndiceDOC {
    private final ArrayList<String> keysArray;
    private final ArrayList<String> valuesArray;


    public IndiceDOC(int size) {
        valuesArray = new ArrayList<>(size);
        keysArray = new ArrayList<>(size);
    }

    public void add(String key, String value) {
        valuesArray.add(value);
        keysArray.add(key);
    }

    public String getLink(int index) {
        return valuesArray.get(index);
    }

    public int size() {
        return keysArray.size();
    }


    public ArrayList<String> textArray() {
        return keysArray;
    }


}
