/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo.PRINT;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import com.hys.carlosoft.leyinfo.R;


public class webPrint {

    public static void createWebPrintJob(Context context, WebView webView, String name) {

    //SOURCE: https://developer.android.com/training/printing/html-docs?hl=es-419#java

        // Get a PrintManager instance
        PrintManager printManager;
        printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        String jobName = context.getString(R.string.app_name) + " " + name;

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter;
        printAdapter = webView.createPrintDocumentAdapter(jobName);

        if (printManager != null) {
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    public static boolean isPrintingEnable() {
        return true; // Impresion a PDF siempre disponible...ponele. 
    }
}
