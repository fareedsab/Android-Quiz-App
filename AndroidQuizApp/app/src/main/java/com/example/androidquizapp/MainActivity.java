package com.example.androidquizapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidquizapp.Interface.MyWebService;
import com.example.androidquizapp.Model.EmployeeModel;
import com.example.androidquizapp.Model.ResponseModel;
import com.example.androidquizapp.Utils.Helper;
import com.example.androidquizapp.Utils.Utils;
import com.example.androidquizapp.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    SharedPreferences sharedPreferences;
    DatePickerDialog.OnDateSetListener date;
    EmployeeModel employee =new EmployeeModel();
    DatePickerDialog datePickerDialog;
    Bitmap bitmap1 = null;
    String encodedImage;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);
        initview();
        employee.setDesignation("");
        employee.setDepartment("");
        employee.setUserName(sharedPreferences.getString("userName","-"));
        employee.setPassword(sharedPreferences.getString("password","-"));
        binding.addUsername.setText(employee.getUserName());
        binding.addPassword.setText(employee.getPassword());
        BitmapDrawable drawable = (BitmapDrawable) binding.image.getDrawable();
        employee.setPhoto(encodeImage(drawable.getBitmap()));


    }

    private void initview() {

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        binding.addCNIC.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                //view.getMinDate(myCalendar.getTimeInMillis());
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);


                updateLabel();
            }
        };
        datePickerDialog  = new DatePickerDialog(MainActivity.this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.ChheckAndRequestpermission(MainActivity.this))
                {
                    TakePicFromCamera();
                }
            }
        });
        binding.addDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    employee.setDepartment(binding.addDepartment.getSelectedItem().toString());
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                employee.setDepartment("");
            }
        });
        binding.addDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    employee.setDesignation(binding.addDesignation.getSelectedItem().toString());
                } else {
                    employee.setDesignation("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                employee.setDesignation("");
            }
        });


    }

    private void TakePicFromCamera() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takepic.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takepic, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    bitmap1 = (Bitmap) bundle.get("data");
                    encodedImage = encodeImage(bitmap1);
                    employee.setPhoto(encodedImage);
                    binding.image.setImageBitmap(bitmap1);
                }
                break;
        }
    }
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        binding.addjoiningDate.setText(dateFormat.format(myCalendar.getTime()));
        employee.setDateofjoining(dateFormat.format(myCalendar.getTime()));
    }

    public void onjoindatePressed(View view) {
        datePickerDialog.show();
    }

    public void onAddpressed(View view) {
        if(checkValidation())
        { try {
            if (Utils.isNetworkAvailable(this)) {

                MyWebService myWebService = Helper.myWebService();
                Call<ResponseModel> createemployee = myWebService.createEmployee(employee.getPhoto(), employee.getUserName(),
                        employee.getPassword(), employee.getFullName(), employee.getCnic(), employee.getDateofjoining(),
                        employee.getCode(), employee.getDepartment(), employee.getDesignation());
                Utils.showProgressDialog(MainActivity.this, "Please Wait...");
                createemployee.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful()) {
                            Log.d("TAG", "onResponse: " + response.body().toString());
                            if (response.code() >= 200 && response.code() <= 399) {
                                if (response.body() != null) {

                                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    if (response.body().getStatus().equals("1")) {
                                        Log.d("TAG", "onResponse: " + response.body().toString());
                                        Utils.hideProgressDialog();
                                        clearAllfields();
                                    } else {
                                        Utils.hideProgressDialog();
                                    }


                                } else {
                                    Utils.hideProgressDialog();
                                    Toast.makeText(MainActivity.this, "Error Please Try again", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Utils.hideProgressDialog();
                                Toast.makeText(MainActivity.this, "Response Code:" + response.code(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Utils.hideProgressDialog();
                            Toast.makeText(MainActivity.this, "Not Successfull", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.d("TAG", "onErrorResponse: error : [" + t.getMessage() + "]");
                    }
                });
            } else {
                Toast.makeText(this, "Network Not connected", Toast.LENGTH_SHORT).show();
            }
        }
            catch(Exception e)
                {
                    Toast.makeText(this, "Exception:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


        }
    }

    private void clearAllfields() {
        binding.addjoiningDate.setText("");
        binding.addFullName.setText("");
        binding.addCNIC.setText("");
        binding.addCode.setText("");
        binding.addDesignation.setSelection(0);
        binding.addDepartment.setSelection(0);
    }

    public Boolean checkValidation()
    {     Boolean valid=true;
        if(binding.addFullName.getText().equals(""))
        {binding.addFullName.setError("Enter Full Name");
        valid=false;}
        else {
            employee.setFullName(binding.addFullName.getText().toString());
        }
        if(binding.addCNIC.getText().equals("")||binding.addCNIC.getText().length()<13)
        {   binding.addCNIC.setError("Invalid CNIC");
            valid=false;
        }
        else {
            employee.setCnic(binding.addCNIC.getText().toString());
        }
        if(binding.addCode.getText().equals(""))
        {
            binding.addCode.setError("Enter Code");
            valid=false;
        }
        else {
            employee.setCode(binding.addCode.getText().toString());
        }
        if(binding.addjoiningDate.getText().equals(""))
        {
            binding.addjoiningDate.setError("Select Date");
            valid=false;
        }
        else {
            employee.setDateofjoining(binding.addjoiningDate.getText().toString());
        }
        if(employee.getDepartment().equals(""))
        {
            Toast.makeText(this, "Select Department", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if(employee.getDesignation().equals(""))
        {
            Toast.makeText(this, "Select Designation", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        return valid;
    }

    public void onlogoutPressed(View view) {
        sharedPreferences.edit().remove("IsLogin").commit();
        sharedPreferences.edit().remove("userName").commit();
        sharedPreferences.edit().remove("password").commit();

       Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}