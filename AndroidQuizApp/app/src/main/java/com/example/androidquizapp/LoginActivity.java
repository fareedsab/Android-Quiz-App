package com.example.androidquizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidquizapp.Interface.MyWebService;
import com.example.androidquizapp.Model.ResponseModel;
import com.example.androidquizapp.Utils.Helper;
import com.example.androidquizapp.Utils.Utils;
import com.example.androidquizapp.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
    }

    public void login_succesfull(View view) {
        if (binding.username.getText().toString().equalsIgnoreCase("")) {
            binding.username.setError("Enter Correct Username");
            return;
        } else if (binding.password.getText().toString().equalsIgnoreCase("")) {
            binding.password.setError("Enter Password");
            return;
        } else {
            sendloginresponse();
        }
    }

    private void sendloginresponse() {
        if (Utils.isNetworkAvailable(this)) {
            MyWebService myWebService = Helper.myWebService();
            Call<ResponseModel> getUserLogin = myWebService.getUserLogin(binding.username.getText().toString(),
                    binding.password.getText().toString());
            Utils.showProgressDialog(this, "Please Wait...");
            getUserLogin.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (response.isSuccessful()) {
                        Log.d("TAG", "onResponse: "+response.body().toString());
                        if (response.code() >= 200 && response.code() <= 399) {
                            if (response.body() != null) {

                                Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                if (response.body().getStatus().equals("1")) {
                                    SharedPreferences.Editor myEditor =sharedPreferences.edit();
                                    myEditor.putString("userName",binding.username.getText().toString());
                                        myEditor.putString("password",binding.password.getText().toString());
                                    myEditor.putString("IsLogin","true");
                                    myEditor.commit();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Utils.hideProgressDialog();
                                }


                            }
                            else {
                                Utils.hideProgressDialog();
                                Toast.makeText(LoginActivity.this, "Error Please Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Utils.hideProgressDialog();
                            Toast.makeText(LoginActivity.this, "Response Code:"+response.code(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Utils.hideProgressDialog();
                        Toast.makeText(LoginActivity.this, "Not Successfull", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("TAG", "onErrorResponse: error : [" + t.getMessage() + "]");

                }
            });
        }
        else {
            Toast.makeText(this, "Network Not connected", Toast.LENGTH_SHORT).show();
        }

    }
}