package com.aryvart.uticianvender.genericclasses;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 07-03-2016.
 */
public class APIcalls {
    private final String USER_AGENT = "Mozilla/5.0";
    String Base_URL = "";
    String URL_Parameters = "";

    public APIcalls(String strURL) {
        this.Base_URL = strURL;
        Log.i("RS", "Base_URL : " + strURL);
    }

    public APIcalls(String strURL, String strUrlParameters) {
        this.Base_URL = strURL;
        this.URL_Parameters = strUrlParameters;
        Log.i("RS", "Base_URL : " + strURL);
        Log.i("RS", "URL_Parameters : " + strUrlParameters);
    }


    public String Process() {
        String strResponse = null;
        try {
            strResponse = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                Log.i("SR", "Login URL : " + Base_URL);
                System.out.print("strHTTP_LOGIN_URL");
                url = new URL(Base_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                strResponse = readStream(urlConnection.getInputStream());

                Log.i("SR", "APIcalls_Process : " + strResponse);


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("SR", "Exception_Process : " + e.getMessage());
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strResponse;
    }

    public String Process_for_post() {
        String strResponse = null;
        try {
            strResponse = sendPost(Base_URL, URL_Parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strResponse;
    }


    // HTTP GET request
    private void sendGet() throws Exception {


        try {
            URL obj = new URL("http://utician.com/register.php ");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + "http://utician.com/register.php ");
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // HTTP POST request
    private String sendPost(String strBaseURL, String strParameters) throws Exception {
        String strResp = "";
        try {
//String url = "http://www.immanuvel.com/carmodel/product/register_user";
            String url = strBaseURL;


            URL obj = new URL(url);

            String urlParameters = strParameters;

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

//add reuqest header
            con.setDoOutput(true);
            con.setRequestMethod("POST");


            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setFixedLengthStreamingMode(urlParameters.getBytes().length);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


//DataOutputStream wr = new DataOutputStream(con.getOutputStream());
/* OutputStream os = con.getOutputStream();
BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
writer.write(urlParameters);
writer.close();
os.close();*/

            PrintWriter out = new PrintWriter(con.getOutputStream());
            out.print(urlParameters);
            out.close();

            int responseCode = con.getResponseCode();
            InputStream responseErrorMessage = con.getErrorStream();


            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                strResp += response.append(inputLine);
            }
            in.close();

//print result
            System.out.println(response.toString());

            System.out.println("strResp : " + strResp);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResp;

    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.i("SR", "Exception_readStream : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }


}

