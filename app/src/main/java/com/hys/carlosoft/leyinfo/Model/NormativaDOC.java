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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Clase que representa un documento normativo en formato HTML.
 * Permite parsear y extraer índices de artículos, capítulos, títulos y resaltados.
 */
public class NormativaDOC {
    // Fuente HTML de la normativa
    private final InputStream htmlsource;
    // Índices extraídos del documento
    private IndiceDOC articulos;
    private IndiceDOC capitulos;
    private IndiceDOC titulos;
    private IndiceDOC resaltados;

    /**
     * Constructor que recibe el InputStream del HTML.
     * @param input InputStream del archivo HTML
     */
    public NormativaDOC(InputStream input) {
        htmlsource = input;
    }

    /**
     * Parsea los artículos del documento HTML y los almacena en el índice correspondiente.
     * @param str Documento HTML parseado
     */
    private void parseArticulos(Document str) {
        Elements nodes = str.body().select("div.articulo_container");
        articulos = new IndiceDOC(nodes.size());
        if (!nodes.isEmpty()) {
            for (Element node : nodes) {
                String key, value;

                value = node.id();
                key = Objects.requireNonNull(node.select("h5.articulo").first()).text();

                articulos.add(key, value);
            }
        }
    }

    /**
     * Parsea los capítulos del documento HTML y los almacena en el índice correspondiente.
     * @param str Documento HTML parseado
     */
    private void parseCapitulos(Document str) {
        Elements nodes = str.body().select("h3.capitulo");
        capitulos = new IndiceDOC(nodes.size());
        if (!nodes.isEmpty())
            if (nodes.size() > 1) {
                for (Element node : nodes) {
                    String key, value;

                    value = node.id();
                    key = node.text();
                    capitulos.add(key, value);
                }
            }
    }

    /**
     * Parsea los títulos del documento HTML y los almacena en el índice correspondiente.
     * @param str Documento HTML parseado
     */
    private void parseTitulos(Document str) {
        Elements nodes = str.body().select("h3.titulo");
        titulos = new IndiceDOC(nodes.size());
        if (!nodes.isEmpty())
            if (nodes.size() > 1) {
                for (Element node : nodes) {
                    String key, value;

                    value = node.id();
                    key = node.text();
                    titulos.add(key, value);
                }
            }
    }

    /**
     * Parsea los resaltados del documento HTML y los almacena en el índice correspondiente.
     * @param str Documento HTML parseado
     */
    private void parseResaltados(Document str) {
        Elements nodes = str.body().select("h4.upper");
        resaltados = new IndiceDOC(nodes.size());
        if (!nodes.isEmpty())
            if (nodes.size() > 1) {
                for (Element node : nodes) {
                    String key, value;

                    value = node.id();
                    key = node.text();
                    resaltados.add(key, value);
                }
            }
    }

    /**
     * Método principal para parsear el HTML y extraer los índices.
     */
    public void parseHtml() {
        // fi = htmlsource;
        try {

            if (htmlsource != null) {
                Document htmldoc = Jsoup.parse(htmlsource, "UTF-8", "");
                parseCapitulos(htmldoc);
                parseTitulos(htmldoc);
                parseArticulos(htmldoc);
                parseResaltados(htmldoc);
            } else return;

        } catch (IOException e) {
            e.printStackTrace();
            closeFile();
            return;
        }
        closeFile();
    }

    /**
     * Cierra el InputStream del archivo HTML.
     */
    private void closeFile() {
        try {
            if (htmlsource != null)
                htmlsource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve el índice de artículos.
     * @return IndiceDOC de artículos
     */
    public IndiceDOC getArticulos() {
        return articulos;
    }

    /**
     * Devuelve el índice de capítulos.
     * @return IndiceDOC de capítulos
     */
    public IndiceDOC getCapitulos() {
        return capitulos;
    }

    /**
     * Devuelve el índice de títulos.
     * @return IndiceDOC de títulos
     */
    public IndiceDOC getTitulos() {
        return titulos;
    }

    /**
     * Devuelve el índice de resaltados.
     * @return IndiceDOC de resaltados
     */
    public IndiceDOC getResaltados() {
        return resaltados;
    }

}