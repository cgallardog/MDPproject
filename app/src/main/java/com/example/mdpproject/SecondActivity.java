package com.example.mdpproject;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
public class SecondActivity extends AppCompatActivity{
    TextView t1;
    String name,age, gender, height, weight;
    String mText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        t1= (TextView) findViewById(R.id.textView);
        //Getting the Intent
        Intent i = getIntent();
        //Getting the Values from First Activity using the Intent received
        name=i.getStringExtra("name_key");
        age=i.getStringExtra("age_key");
        gender=i.getStringExtra("gender_key");
        height=i.getStringExtra("height_key");
        weight=i.getStringExtra("weight_key");
        mText = "Form data received:"      +
                "\n  Name: " + name     +
                "\n  Age: "  + age     +
                "\n  Gender: "  + gender +
                "\n  Weight: " + weight     +
                "\n  Height: "  + height;

        // Showing the text in the TextView:
        t1.setText(mText);
    }
    public void onAcceptClick(View view) {

        // Creating Intent For Navigating to Second Activity (Explicit Intent)
        Intent i = new Intent(SecondActivity.this,MenuActivity.class);

        // Adding values to the intent to pass them to Second Activity
        i.putExtra("name_key", name);
        i.putExtra("age_key",age);
        i.putExtra("gender_key", gender);
        i.putExtra("weight_key", weight);
        i.putExtra("height_key",height);

        Intent resultI = new Intent().putExtra("Button", "Accept");
        setResult(RESULT_OK, resultI);
        finish();
    }
    public void onCancelClick(View view) {
        Intent resultI = new Intent().putExtra("Button", "Cancel");
        setResult(RESULT_CANCELED, resultI);
        finish();
    }
}
