package com.lember.gascalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
//regular varaibles
    private EditText etTripDistance,etFuelEconomy,etFuelPrice, etNumberPeople,etTripName;
    private TextView txtFuelUsed, txtCostPerPerson;
    private ConstraintLayout main;
    String distance, economy,price,people,name;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFuelEconomy=findViewById(R.id.etFuelEconomy);
        etFuelPrice=findViewById(R.id.etFuelPrice);
        etNumberPeople=findViewById(R.id.etNumberPeople);
        etTripName=findViewById(R.id.etTripname);
        etTripDistance=findViewById(R.id.etTripDistance);
        txtFuelUsed=findViewById(R.id.txtFuelUsed);
        txtCostPerPerson=findViewById(R.id.txtCost);

        /// check if premission are granted
        if (isExternalStorageWriteable()){
            int writeExPerm = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExPerm!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                Log.i("External:",getResources().getString(R.string.mounted));
            else Log.i("External:", getResources().getString(R.string.notMounted));


        }else Snackbar.make(main,getResources().getString(R.string.exNonGrant),Snackbar.LENGTH_LONG).show();
    }

    private boolean isExternalStorageWriteable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("State","Writeable");
            return true;
        }else return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION){
            int grantResultLength=grantResults.length;
            if(grantResultLength>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Snackbar.make(main,getResources().getString(R.string.exGrant),Snackbar.LENGTH_LONG).show();
            }else{
                Snackbar.make(main,getResources().getString(R.string.exNonGrant),Snackbar.LENGTH_LONG).show();
            }
        }
    }

    ///method for checking that user has input all needed values
    private boolean checkValidation(){
        String distance = etTripDistance.getText().toString().trim();
        String economy = etFuelEconomy.getText().toString().trim();
        String price = etFuelPrice.getText().toString().trim();
        String people = etNumberPeople.getText().toString().trim();
        String tripName = etTripName.getText().toString().trim();

        if (TextUtils.isEmpty(distance)){
            etTripDistance.requestFocus();
            etTripDistance.setError("This field cannot be empty!");
            return false;
        }else if (TextUtils.isEmpty(economy)){
            etFuelEconomy.requestFocus();
            etFuelEconomy.setError("This field cannot be empty!");
            return false;
        } else if(TextUtils.isEmpty(price)){
            etFuelPrice.requestFocus();
            etFuelPrice.setError("This field cannot be empty!");
            return false;
        }else if(TextUtils.isEmpty(tripName)) {
            etTripName.requestFocus();
            etTripName.setError("This field cannot be empty!");
            return  false;
        }else if(TextUtils.isEmpty(people)){
            etNumberPeople.requestFocus();
            etNumberPeople.setError("This field cannot be empty!");
            return false;
        } else return true;
    }



    public void onCalculate(View view) {
        if(checkValidation()){
            double fuelUsed=Double.parseDouble(etTripDistance.getText().toString())/100*Double.parseDouble(etFuelEconomy.getText().toString());
            double cost=fuelUsed * Double.parseDouble(etFuelPrice.getText().toString());
            double perPerson=cost/Double.parseDouble(etNumberPeople.getText().toString());
            txtFuelUsed.setText(String.format(getString(R.string.fuelused),fuelUsed));
            txtCostPerPerson.setText("Fuel cost is: "+cost+"per person is: "+perPerson);
        }
    }
////save button action, using alert to give user choice for saving either internal or external
    public void onSave(View view) {
        new AlertDialog.Builder(this)
        .setPositiveButton("Internal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                internalSave();
            }
        }) .setNegativeButton("External", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              externalSave();
            }
        }).show();


    }
    ///external save
    private void externalSave() {
        String FILE_NAME="GAS_EXTERNAL";
        String fuel = txtFuelUsed.getText().toString().trim();
        ///String price = etFuelPrice.getText().toString().trim();
        String total = txtCostPerPerson.getText().toString().trim();
        String tripName = etTripName.getText().toString().trim();
        String data=tripName + ", "+fuel+ ", "+total;
        ///try catch block incase of errors
        try{
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),FILE_NAME);
            if(!file.exists()){
               /// Snackbar.make(main,getResources().getString(R.string.saveError),Snackbar.LENGTH_LONG).show();
                Toast.makeText(this,"File doesn't exist!",Toast.LENGTH_SHORT).show();
            }
            FileWriter fileWriter=new FileWriter(file,true);
            fileWriter.append(data).append("\n");
            fileWriter.flush();
            fileWriter.close();
            ///Snackbar.make(main,getResources().getString(R.string.saveSuccess,getFilesDir(),FILE_NAME),Snackbar.LENGTH_LONG).show();
            Toast.makeText(this,"File saved",Toast.LENGTH_SHORT).show();
        }catch (IOException ex){ex.printStackTrace();}

    }

    private void internalSave() {
        ///variable for file name that will be saved ->GAS_INTERNAL.txt
        String FILE_NAME="GAS_INTERNAL";
        String fuel = txtFuelUsed.getText().toString().trim();
        /////String price = etFuelPrice.getText().toString().trim();
        String total = txtCostPerPerson.getText().toString().trim();
        String tripName = etTripName.getText().toString().trim();
        String data=tripName + ", "+fuel+ ", "+total;
        ///try catch block incase of errors
        try{
            File file=new File (getFilesDir(),FILE_NAME);
            if(!file.exists()){
                Toast.makeText(this,"File doesn't exist!",Toast.LENGTH_SHORT).show();
            }
            FileWriter fileWriter=new FileWriter(file,true);
            fileWriter.append(data).append("\n");
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(this,"File saved",Toast.LENGTH_SHORT).show();
        }catch (IOException ex){ex.printStackTrace();}
    }

    //clear button action
    public void onClear(View view) {
        //using string array & for cycle to set all input fields blank
        String[] fields={"etTripDistance","etFuelEconomy","etFuelPrice","etNumberPeople","etTripName"}; ///masiiv
        for (String s : fields){
            int id=getResources().getIdentifier(s,"id",getPackageName());
            EditText field=findViewById(id);
            field.setText("");
        }
    }
}