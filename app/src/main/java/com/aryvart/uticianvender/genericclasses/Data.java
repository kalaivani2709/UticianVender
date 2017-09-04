package com.aryvart.uticianvender.genericclasses;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android1 on 25/4/16.
 */

public class Data {

    private static String loginURL = "http://utician.com/api/private_login.php";
    private static String registerUrl = "http://utician.com/api/register.php";
    private static String sendMailUrl = "http://utician.com/api/forgot.php";
    private JSONParsers jsonParser;


    public Data() {

        jsonParser = new JSONParsers();
    }
    public JSONObject loginUser_Social(String str_key,String social_id) {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(str_key, social_id));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);

        return json;
    }

    public JSONObject loginUser(String username, String password, String gcmid, String usertype) {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("gcmid", gcmid));
        params.add(new BasicNameValuePair("usertype ", usertype.trim()));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);

        return json;
    }


    public JSONObject register(String fullname, String username, String useremail, String userpassword, String userType,String gcmid) {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("fullname", fullname));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("email", useremail));
        params.add(new BasicNameValuePair("password", userpassword));
        params.add(new BasicNameValuePair("usertype", userType));
        params.add(new BasicNameValuePair("gcmid", gcmid));
        JSONObject json = jsonParser.getJSONFromUrl(registerUrl, params);

        return json;
    }

    public JSONObject forgetPassword(String useremail) {
        JSONObject json = null;
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", useremail));
            json = jsonParser.getJSONFromUrl(sendMailUrl, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }



}
