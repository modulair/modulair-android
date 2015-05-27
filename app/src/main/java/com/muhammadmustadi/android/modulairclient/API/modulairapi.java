package com.muhammadmustadi.android.modulairclient.API;

import com.muhammadmustadi.android.modulairclient.Model.sessionmodel;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by odi on 5/13/15.
 */
public interface modulairapi {
    @POST("/sessions/login")      //here is the other url part.best way is to start using /
    public void getFeed(@Query("user") String user, Callback<sessionmodel> response);     //string user is for passing values from edittext for eg: user=basil2style,google
    //response is the response from the server which is now in the POJO

}
