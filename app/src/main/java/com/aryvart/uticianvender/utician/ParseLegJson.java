package com.aryvart.uticianvender.utician;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by android3 on 10/11/16.
 */
public class ParseLegJson {
    private final String USER_AGENT = "Mozilla/5.0";
    String strResult = "";
    String strURL;

    public ParseLegJson(String url) {
        strURL = url;
    }

    // HTTP GET request
    public String sendGet() throws Exception {

        // String url = "https://maps.googleapis.com/maps/api/directions/jso"
        //+ "n?origin=11.942195969521103,79.81850456446409&destination=12.994112027750981,80.17086669802666&sensor=false&alternatives=true&units=metric";

        String url = strURL;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        strResult = response.toString();
        org.json.JSONObject jsObj = new org.json.JSONObject(strResult);
        JSONArray jsArSteps = jsObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                .getJSONArray("steps");

       
        return jsArSteps.toString();
    }
}
