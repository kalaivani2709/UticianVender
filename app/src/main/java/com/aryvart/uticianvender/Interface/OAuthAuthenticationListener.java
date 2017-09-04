package com.aryvart.uticianvender.Interface;

/**
 * Created by ${Rajaji} on 19-05-2016.
 */
public interface OAuthAuthenticationListener {

    public abstract void onSuccess();

    public abstract void onFail(String error);

    public void onReceived(String strMessage, String strId);
}

