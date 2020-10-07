package com.lember.gascalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
//regular varaibles
    private EditText etTripDistance,etFuelEconomy,etFuelPrice, etNumberPeople,etTripName;
    private TextView txtFuelUsed, txtCostPerPerson;

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

    private void externalSave() {

    }

    private void internalSave() {
        ///variable for file name that will be saved ->GAS_INTERNAL.txt
        String FILE_NAME="GAS_INTERNAL";
        String fuel = txtFuelUsed.getText().toString().trim();
        String price = etFuelPrice.getText().toString().trim();
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