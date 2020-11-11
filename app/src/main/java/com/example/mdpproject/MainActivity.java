package com.example.mdpproject;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 0;
    //Defining the Views
    EditText name_view, age_view, height_view, weight_view;
    Button bt;
    Spinner gender_view;
    String [] gender_array = {"Man", "Woman"};

    String name, age, gender, height, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referring the Views
        name_view = (EditText) findViewById(R.id.editText);
        age_view = (EditText) findViewById(R.id.editText2);
        height_view = findViewById(R.id.editText3);
        weight_view = findViewById(R.id.editText4);
        bt= (Button) findViewById(R.id.submit);
        gender_view = (Spinner) findViewById(R.id.genderSpinner);

        // Adapter to adapt the data from array to Spinner

        //CountryArrayAdapter countryArrayAdapter = new CountryArrayAdapter(this, countryData.getCountryDataList());
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_item, gender_array);
        gender_view.setAdapter(adapter);

        //Setting Listener for Submit Button
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ///Getting the Values from Views(Edittext & Spinner)
        name = name_view.getText().toString();
        age = age_view.getText().toString();
        gender = gender_view.getSelectedItem().toString();
        height = height_view.getText().toString();
        weight = weight_view.getText().toString();

        // Creating Intent For Navigating to Second Activity (Explicit Intent)
        Intent i = new Intent(MainActivity.this,SecondActivity.class);

        // Adding values to the intent to pass them to Second Activity
        i.putExtra("name_key", name);
        i.putExtra("age_key",age);
        i.putExtra("gender_key", gender);
        i.putExtra("weight_key", weight);
        i.putExtra("height_key",height);

        // Once the intent is parametrized, start the second activity:
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Configuration cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClearClick(View view) {
        name_view.setText(" ", TextView.BufferType.EDITABLE );
        age_view.setText(" ", TextView.BufferType.EDITABLE);
        height_view.setText(" ", TextView.BufferType.EDITABLE);
        weight_view.setText(" ", TextView.BufferType.EDITABLE);
    }
}