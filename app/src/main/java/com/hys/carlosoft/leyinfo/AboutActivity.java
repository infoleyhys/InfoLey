/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */
package com.hys.carlosoft.leyinfo;


import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private static final String ABOUT_HTML = "file:///android_asset/about/about.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Button okbutton = findViewById(R.id.buttonOk);

        okbutton.setOnClickListener(v -> exitAbout());
        String text = String.format("%s %s", getString(R.string.app_name), getString(R.string.infoley_version));
        TextView tv = findViewById(R.id.textView_About);
        tv.setText(text);
        WebView wb = findViewById(R.id.about_wb);
        wb.setBackgroundColor(0x252525);
        wb.loadUrl(ABOUT_HTML);
    }

    private void exitAbout() {
        this.finish();
    }

}
