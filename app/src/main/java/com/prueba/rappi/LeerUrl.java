package com.prueba.rappi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class LeerUrl {

    public static HttpURLConnection obtenerConexion(String url) {
        System.out.println("URL: " + url);
        HttpURLConnection hcon = null;
        try {
            hcon = (HttpURLConnection) new URL(url).openConnection();
            hcon.setReadTimeout(30000); // Timeout en 30 segundos
            hcon.setRequestProperty("User-Agent", "Alien V1.0");
        } catch (MalformedURLException e) {
            Log.e("obtenerConexion()",
                    "Invalid URL: " + e.toString());
        } catch (IOException e) {
            Log.e("obtenerConexion()",
                    "Sin conexión: " + e.toString());
        }
        return hcon;
    }

    public static String leerContenidos(String url) {
        HttpURLConnection hcon = obtenerConexion(url);
        if (hcon == null) return null;
        try {
            StringBuffer sb = new StringBuffer(8192);
            String tmp = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(hcon.getInputStream()));

            while ((tmp = br.readLine()) != null)
                sb.append(tmp).append("\n");

            br.close();

            return sb.toString();
        } catch (IOException e) {
            Log.d("Fallo en la lectura", e.toString());
            return null;
        }
    }
}
