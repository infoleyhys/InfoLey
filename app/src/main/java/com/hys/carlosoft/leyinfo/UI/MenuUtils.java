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

import android.view.MenuItem;

public class MenuUtils {

    public static void setOn(MenuItem mi) {
        mi.setEnabled(true);
        mi.setVisible(true);
    }

    public static void setOff(MenuItem mi, boolean visibility) {
        mi.setEnabled(false);
        mi.setVisible(visibility);
    }
}
