package com.example.news;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {


    public static final String SOURCE_ADDED = "com.example.news.SOURCE_ADDED";
    private TextView newsSource;
    private Button uploadBtn;
    private Spinner sources;

    private List<String> list;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);




        uploadBtn = findViewById(R.id.uploadBtn);
        sources = findViewById(R.id.spinner);


        list = new ArrayList<>();
        list.add("Select a News Source");
        list.add("bbc-news");
        list.add("ign");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sources.setAdapter(adapter);

        //get the news source selected by the admin in the spinner widget
        sources.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                source = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         *  on click of the upload button, get the news source from the spinner and return it to
         *  the admin activity
         */
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.equals("Select a News Source")) {
                    Toast.makeText(UploadActivity.this, "Please select a News Source", Toast.LENGTH_SHORT).show();
                } else {

                    Intent resultIntent = new Intent();
                    if (source.equals("")) {
                        setResult(RESULT_CANCELED, resultIntent);
                    } else {

                        resultIntent.putExtra(SOURCE_ADDED, source);
                        setResult(RESULT_OK, resultIntent);
                    }
                    finish();
                }
            }
        });
    }
}
