package com.aryvart.uticianvender.Interface;

/**
 * Created by android3 on 23/7/16.
 */
public interface GetBookingId {

    public void bookingid(String id, String strTitle);
    public void serviceendid (String id, String strTitle);
    public void navigatepage(String iscomplete,String paidstatus,String generate,String reached,String pse,String review,String strtype,String strresponse,String leave,String responseval);
    public void paidstatus (String id, String strTitle,String payservices,String rate,String name,String image,String prid );


}
