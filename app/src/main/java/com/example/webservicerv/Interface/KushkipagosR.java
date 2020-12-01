package com.example.webservicerv.Interface;

import com.example.webservicerv.Model.DataKushki;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface KushkipagosR {
    @Headers("Public-Merchant-Id: 8376ea5f58f44f2fb3304faddcfd9660")
    @GET("transfer/v1/bankList")
    Call< List<DataKushki> > getKushki();

}
