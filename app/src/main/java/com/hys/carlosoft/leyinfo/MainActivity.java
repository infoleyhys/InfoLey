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

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hys.carlosoft.leyinfo.PRINT.webPrint;
import com.hys.carlosoft.leyinfo.UI.HistoryListFragment;
import com.hys.carlosoft.leyinfo.UI.NormativaDOCFragment;
import com.hys.carlosoft.leyinfo.UI.MenuItem;
import com.hys.carlosoft.leyinfo.UI.MenuListFragment;
import com.hys.carlosoft.leyinfo.UI.MenuUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;


/**
 * Actividad principal de la aplicación InfoLey.
 * Gestiona la navegación, fragmentos, preferencias y acciones principales del usuario.
 */
public class MainActivity extends AppCompatActivity
        implements MenuListFragment.OnMenu_Listener, NormativaDOCFragment.docListener, HistoryListFragment.OnHistory_Listener {
    // Constantes para preferencias y estado
    public static final String SP_NIGHT_MODE = "SP_NIGHT_MODE";
    public static final String SP_ZOOM = "SP_ZOOM";
    private static final String CURRENT_URL = "CURRENT_URL";
    public static final String SHARED_PREFERENCE_KEY = "666";
    // Fragmentos principales
    private NormativaDOCFragment currentDoc;
    private HistoryListFragment historyListFragment;
    private MenuListFragment menuListFragment;
    // Preferencias de usuario
    private boolean mNightMode, mZoom;
    // Flag para búsqueda en WebView
    private boolean flag = false;


    /**
     * Método de inicialización de la actividad.
     * Configura la interfaz, preferencias y fragmentos principales.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences(getPackageName() + SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        mNightMode = sp.getBoolean(SP_NIGHT_MODE, false);
        mZoom = sp.getBoolean(SP_ZOOM, false);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Configuración del Drawer para móviles
        View tabletView = findViewById(R.id.tabletview);
        if (tabletView == null) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
        // Acción para el botón "Acerca de"
        ImageView img = findViewById(R.id.imgview_about);
        img.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);

        });
        // Inicialización de fragmentos
        FragmentManager fm = getSupportFragmentManager();
        historyListFragment = (HistoryListFragment) fm.findFragmentById(R.id.history_fragment);
        menuListFragment = (MenuListFragment) fm.findFragmentById(R.id.menu_fragment);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifica si la app fue abierta por un deep link
        checkDeepLink(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Verifica si el intent recibido es un deep link y lo procesa.
     */
    void checkDeepLink(Intent deepintent) {
        Uri appLinkData;

        if (deepintent != null && Objects.equals(deepintent.getAction(), Intent.ACTION_VIEW)) {
            appLinkData = deepintent.getData();

            if (appLinkData != null && Objects.equals(appLinkData.getScheme(), "https")) {

                openDeepLink(appLinkData);
            }
        }
    }

    /**
     * Abre el enlace profundo recibido.
     */
    void openDeepLink(Uri link) {
        menuListFragment.selectItem(link);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Guarda el estado del fragmento actual

        if (currentDoc != null) {
            outState.putString(CURRENT_URL, currentDoc.getTag());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String url = savedInstanceState.getString(CURRENT_URL);
        FragmentManager fm = getSupportFragmentManager();

        if (url != null) {
            currentDoc = (NormativaDOCFragment) fm.findFragmentByTag(url);
        }

    }


    /**
     * Maneja la navegación hacia atrás, Drawer, historial y navegación interna.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isNavOpen()) {
            closeNav();
        } else if (!isHistoryOpen()) { //siempre que tenga items
            openHistory();
        } else {
            super.onBackPressed();
        }

    }

    /**
     * Crea el menú de opciones y configura la búsqueda en el WebView.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.mi_search).getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        if(searchView !=null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (currentDoc != null) {
                        if (!query.isEmpty()) {
                            if (!flag) {
                                currentDoc.getWebView().findAllAsync(query);
                                flag = true;
                                try {
                                    Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                                    m.invoke(currentDoc.getWebView(), true);
                                } catch (Throwable ignored) {
                                }
                            } else {
                                currentDoc.getWebView().findNext(true);
                            }
                        }

                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (currentDoc != null) {
                        if (newText.isEmpty()) {
                            try {
                                Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                                m.invoke(currentDoc.getWebView(), false);
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                    flag = false;
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Prepara el menú de opciones según el estado de la navegación y preferencias.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        android.view.MenuItem mi_nav = menu.findItem(R.id.navigate);
        android.view.MenuItem mi_night = menu.findItem(R.id.mi_modonoche);
        android.view.MenuItem mi_zoom = menu.findItem(R.id.mi_small_text);
        android.view.MenuItem mi_search = menu.findItem(R.id.mi_search);
        android.view.MenuItem mi_print = menu.findItem(R.id.mi_imprimir);

        if (isNavigationEnable()) {
            if (webPrint.isPrintingEnable()) {
                MenuUtils.setOn(mi_print);
                Objects.requireNonNull(mi_print.getIcon()).setAlpha(255);
            } else MenuUtils.setOff(mi_print, false);
            MenuUtils.setOn(mi_nav);
            MenuUtils.setOn(mi_search);
            MenuUtils.setOn(mi_night);
            MenuUtils.setOn(mi_zoom);
            Objects.requireNonNull(mi_nav.getIcon()).setAlpha(255);
            Objects.requireNonNull(mi_search.getIcon()).setAlpha(255);

            updateToolBarTitle();
        } else {
            if (webPrint.isPrintingEnable()) {
                MenuUtils.setOff(mi_print, true);
                Objects.requireNonNull(mi_print.getIcon()).setAlpha(100);
            } else MenuUtils.setOff(mi_print, false);

            MenuUtils.setOff(mi_nav, true);
            MenuUtils.setOff(mi_search, true);
            MenuUtils.setOff(mi_night, true);
            MenuUtils.setOff(mi_zoom, true);
            Objects.requireNonNull(mi_nav.getIcon()).setAlpha(100);
            Objects.requireNonNull(mi_search.getIcon()).setAlpha(100);

        }

        mi_night.setChecked(mNightMode);
        mi_zoom.setChecked(mZoom);

        if (isNavOpen())
            mi_nav.setIcon(R.drawable.ic_navopen);
        else mi_nav.setIcon(R.drawable.ic_navclose);

        return super.onPrepareOptionsMenu(menu);

    }

    /**
     * Abre un nuevo documento en un fragmento.
     */
    private void newDocActivity(MenuItem item) {
        //Log.d("onnewDoc", "Activity start");
        FragmentManager fm = getSupportFragmentManager();
        NormativaDOCFragment doc = (NormativaDOCFragment) fm.findFragmentByTag(item.getUrlString());

        if (doc == null) {

            doc = NormativaDOCFragment.newInstance(item.getUrlString(), getString(item.getTextID()) + " - " + getString(item.getDescription()));
            FragmentTransaction ft = fm.beginTransaction();
            if (currentDoc != null) {
                ft.hide(currentDoc);
            }
            ft.add(R.id.doc_container, doc, item.getUrlString());
            ft.commit();
        } else {
            if (currentDoc != null && currentDoc != doc) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.hide(currentDoc);
                ft.show(doc);
                ft.commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle(doc.getTitle());
            }
        }
        currentDoc = doc;

        invalidateOptionsMenu();

    }

    /**
     * Maneja la selección de opciones del menú.
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        boolean checked = false;

        if (item.isCheckable()) {
            item.setChecked(!item.isChecked());
            checked = item.isChecked();
        }

        /* case R.id.mi_rate:
                gotoRate();
                break;*/
        if (id == R.id.mi_imprimir) {
            printDoc();
        } else if (id == R.id.mi_search) {
        } else if (id == R.id.mi_small_text) {
            mZoom = checked;
            textZoomChangeAll(checked);
        } else if (id == R.id.mi_modonoche) {
            mNightMode = checked;
            nightModeChangeAll(checked);
        } else if (id == R.id.navigate) {
            if (isNavOpen())
                closeNav();
            else openNav();
        }

        return super.onOptionsItemSelected(item);
    }

    // Métodos auxiliares para cambiar preferencias y actualizar fragmentos
    private void textZoomChangeAll(boolean checked) {
        if (currentDoc != null) {
            List<Fragment> fraglist = getLeyDocFragments();
            currentDoc.smallTextEnable(checked, true);

            for (int i = 0; i < fraglist.size(); i++) {
                NormativaDOCFragment doc = (NormativaDOCFragment) fraglist.get(i);
                doc.smallTextEnable(checked, true);
            }
            saveZoomMode(checked);
        }
    }

    private List<Fragment> getLeyDocFragments() {
        List<Fragment> fraglist;
        fraglist = getSupportFragmentManager().getFragments();
        fraglist.remove(currentDoc);
        fraglist.remove(historyListFragment);
        fraglist.remove(menuListFragment);
        return fraglist;
    }

    private void nightModeChangeAll(boolean checked) {
        if (currentDoc != null) {
            List<Fragment> fraglist = getLeyDocFragments();

            currentDoc.nightModeEnable(checked, true);

            for (int i = 0; i < fraglist.size(); i++) {
                NormativaDOCFragment doc = (NormativaDOCFragment) fraglist.get(i);
                doc.nightModeEnable(checked, true);
            }
            saveNightMode(checked);
        }
    }

    private void saveNightMode(boolean mode) {
        SharedPreferences.Editor mEditor = getSharedPreferences(getPackageName() + SHARED_PREFERENCE_KEY, MODE_PRIVATE).edit();
        mEditor.putBoolean(SP_NIGHT_MODE, mode);
        mEditor.apply();
    }

    private void saveZoomMode(boolean mode) {
        SharedPreferences.Editor mEditor = getSharedPreferences(getPackageName() + SHARED_PREFERENCE_KEY, MODE_PRIVATE).edit();
        mEditor.putBoolean(SP_ZOOM, mode);
        mEditor.apply();
    }

    /**
     * Abre un documento desde el menú.
     */
    private void openUrlItem(MenuItem item) {
        closeDrawerView();
        newDocActivity(item);
    }

    /**
     * Acción para el menú "Acerca de".
     */
    public void onClickAbout(android.view.MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);

    }

    /**
     * Cierra el Drawer si está abierto (solo en móviles).
     */
    private void closeDrawerView() {

        View tabletView = findViewById(R.id.tabletview);
        if (tabletView == null) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }
    }

    /**
     * Muestra el historial.
     */
    private void openHistory() {
        if (historyListFragment != null)
            historyListFragment.showHistory();
    }

    /**
     * Oculta el historial.
     */
    private void closeHistory() {
        if (historyListFragment != null)
            historyListFragment.hideHistory();
    }

    /**
     * Indica si el historial está abierto.
     */
    private boolean isHistoryOpen() {
        if (historyListFragment != null)
            return historyListFragment.isHistoryOpen();
        return false;
    }

    /**
     * Indica si la navegación está habilitada (documento parseado).
     */
    private boolean isNavigationEnable() {
        if (currentDoc != null) {
            return currentDoc.isParsingComplete();
        }
        return false;

    }

    /**
     * Indica si el panel de navegación está abierto.
     */
    private boolean isNavOpen() {
        if (currentDoc != null) {
            return currentDoc.isNavOpen();
        }
        return false;

    }

    /**
     * Llama al diálogo de impresión del documento actual.
     */
    private void printDoc() {
        if (currentDoc != null) {
            currentDoc.showPrintDialog();

        }
    }

    /**
     * Abre el panel de navegación.
     */
    private void openNav() {
        if (currentDoc != null) {
            currentDoc.showNavigationControl(true);
            if (isHistoryOpen())
                closeHistory();
            invalidateOptionsMenu();
        }
    }

    /**
     * Cierra el panel de navegación.
     */
    private void closeNav() {
        if (currentDoc != null) {
            currentDoc.showNavigationControl(false);
            invalidateOptionsMenu();
        }
    }

    /**
     * Actualiza el título de la barra de herramientas según el documento actual.
     */
    private void updateToolBarTitle() {
        if (currentDoc != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(currentDoc.getTitle());
        }

    }

    /**
     * Maneja la selección de un ítem del menú principal.
     */
    @Override
    public void onMenuListItemSelect(MenuItem item) {
        if (item.getGroupID() == R.string.enlaces) {
            openWeb(item.getUrlString());
            closeDrawerView();

        } else openUrlItem(item);

        if (isHistoryOpen())
            closeHistory();


    }

    /**
     * Abre un enlace web externo.
     */
    void openWeb(String url) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception ignored) {

        }
    }

    /**
     * Callback cuando termina el parsing del documento.
     */
    @Override
    public void onParsingComplete() {
        invalidateOptionsMenu();
    }

    /**
     * Maneja la selección de un ítem del historial.
     */
    @Override
    public void onHistoryItemSelect(MenuItem item) {
        closeHistory();
        if (item.getGroupID() == R.string.enlaces) {
            openWeb(item.getUrlString());
        } else openUrlItem(item);
    }

}
