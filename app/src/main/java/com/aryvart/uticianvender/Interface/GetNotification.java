package com.aryvart.uticianvender.Interface;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by aryvart2 on 2/5/16.
 */
public interface GetNotification {
    public void setNotificationId(String strGCMid);


    public void setProviderDetails(String strGCMid, String strName, String strImage, String straddress,String expertiseid,String strReferalcdoe);

    public void getProviderId(String strProviderId, String strType);

    public void getServicePrice(String n_price);
    public void getServiceDuration(String n_duration);

    public ArrayList<String> getServicName(TreeMap<Integer, String> str_serviceName);


    public void setProviderdet(String strGCMid, String strName, String strImage, String straddress,String strExpertise,String strReferalcode);

    public void NotifyFilter(String strFilterVal);

    public void setProvideraddress(String strlatitude, String strlongitude, String straddress);
}
