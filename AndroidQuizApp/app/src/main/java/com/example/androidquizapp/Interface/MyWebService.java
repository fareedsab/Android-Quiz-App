package com.example.androidquizapp.Interface;

import com.example.androidquizapp.Model.EmployeeModel;
import com.example.androidquizapp.Model.ResponseModel;
import com.example.androidquizapp.Utils.Urls;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MyWebService {
    @FormUrlEncoded
    @POST(Urls.Get_LOGIN_URL)
    Call<ResponseModel> getUserLogin(@Field("Username")String userName
            , @Field("Password")String password);

    @FormUrlEncoded
    @POST(Urls.Get_NewEmployee_URL)
    Call<ResponseModel> createEmployee(@Field("Photo") String image,@Field("Username")String userName
            , @Field("Password")String password,@Field("FullName")String Fullname,@Field("CNIC")String cnic
            , @Field("DateOfJoining")String joining,@Field("Code")String code
            , @Field("Department")String department, @Field("Designation")String designation);




}
