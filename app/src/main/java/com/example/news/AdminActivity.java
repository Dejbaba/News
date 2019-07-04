package com.example.news;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import com.example.news.Model.Article;
import com.example.news.utils.Connect;
import com.example.news.utils.DateFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    private static final int UPLOAD_ACTIVITY_REQUEST_CODE = 1;

    //firebase DB nodes
    private static final String API_DETAILS = "apiDetails";
    private static final String ARTICLES = "articles";

    static final String KEY_ID = "id";
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    String API_KEY;

    private FloatingActionButton addBtn;
    private String newsSource;
    private boolean retrieved = false;
    private ListView listNews;
    private ProgressBar loader;
    private TextView emptyTV;


    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();



    //firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef, saveRef, retrieveRef;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = firebaseDatabase.getReference(API_DETAILS);
        saveRef = firebaseDatabase.getReference(ARTICLES);
        retrieveRef = firebaseDatabase.getReference(ARTICLES);


        addBtn = findViewById(R.id.addBtn);
        listNews = findViewById(R.id.listNews);
        loader = findViewById(R.id.loader);
        emptyTV = findViewById(R.id.emptyListViewText);
        loader.setVisibility(View.INVISIBLE);
        listNews.setEmptyView(findViewById(R.id.emptyListViewText));

        /**
         * This method checks the firebase DB for saved articles
         * and populate the daata retrieved into listview.
         * if no data is found in the DB, set an emptyview state
         * for the listview.
         */
        retrieveArticlesFromDB();


        //Admin clicks the button to navigate to the upload activity
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upload = new Intent(AdminActivity.this, UploadActivity.class);
                startActivityForResult(upload, UPLOAD_ACTIVITY_REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            newsSource = data.getStringExtra(UploadActivity.SOURCE_ADDED);
            emptyTV.setVisibility(View.GONE);
            listNews.setEmptyView(loader);

            //fetch api key from firebase DB and pass it to the asyncTask class
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    API_KEY = dataSnapshot.child("apiKey").getValue(String.class);

                    if(Connect.isNetworkAvailable(getApplicationContext()))
                    {
                        DownloadNews newsTask = new DownloadNews(API_KEY, newsSource);
                        newsTask.execute();

                    }else{
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
        }
    }



    private void retrieveArticlesFromDB() {

        loader.setVisibility(View.VISIBLE);
        listNews.setEmptyView(loader);


        retrieveRef.addValueEventListener(new ValueEventListener() {

            Article article = new Article();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    article = ds.getValue(Article.class);
                    String author = article.getAuthor();
                    String title = article.getTitle();
                    String description = article.getDescription();
                    String url = article.getUrl();
                    String urlToImage = article.getUrltoimage();
                    String publishedAt = article.getPublishedat();

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(KEY_AUTHOR, author);
                    map.put(KEY_TITLE, title);
                    map.put(KEY_DESCRIPTION, description);
                    map.put(KEY_URL, url);
                    map.put(KEY_URLTOIMAGE, urlToImage);
                    map.put(KEY_PUBLISHEDAT, publishedAt);
                    dataList.add(map);

                }

                if (dataList.size() > 0){
                    retrieved = true;
                }

                if (retrieved){

                    loader.setVisibility(View.GONE);
                    emptyTV.setVisibility(View.GONE);
                    final ListNewsAdapter adapter = new ListNewsAdapter(AdminActivity.this, dataList);
                    listNews.setAdapter(adapter);

                    listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Intent i = new Intent(AdminActivity.this, DetailsActivity.class);
                            i.putExtra("url", dataList.get(+position).get(KEY_URL));
                            startActivity(i);
                        }
                    });

                    listNews.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                            new AlertDialog.Builder(AdminActivity.this)
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setTitle("PLEASE CONFIRM")
                                    .setMessage("Do you want to delete this article?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dataList.remove(position);
                                            adapter.notifyDataSetChanged();


                                           /* for (HashMap<String, String> map : dataList){

                                                int i = 0;
                                                String id = map.get(KEY_ID);
                                                String author = map.get(KEY_AUTHOR);
                                                String title = map.get(KEY_TITLE);
                                                String description = map.get(KEY_DESCRIPTION);
                                                String url = map.get(KEY_URL);
                                                String urlToImage = map.get(KEY_URLTOIMAGE);
                                                String publishedAt = map.get(KEY_PUBLISHEDAT);
                                                Log.d(TAG, "onClick: " + id);
                                                Article article = new Article(i, author, title, description, url, urlToImage, publishedAt);
                                                saveRef.child(String.valueOf(i)).setValue(article);
                                                i++;

                                                Toast.makeText(AdminActivity.this, "id : " + i + ":" + author + ":" + description, Toast.LENGTH_SHORT).show();
                                            }*/


                                        }
                                    })
                                    .setNegativeButton("NO", null)
                                    .show();
                            return true;
                        }
                    });

                }else{

                    Log.d(TAG, "onCreate: Nothing retrieved from DB");
                    loader.setVisibility(View.GONE);
                    listNews.setEmptyView(findViewById(R.id.emptyListViewText));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    /**
     * Async task class that helps to get and parse JSON data from the connection helper class
     */
    class DownloadNews extends AsyncTask<String, Void, String>  {

        private String apiKey, newsSource;

        public DownloadNews(String apiKey, String newsSource) {
            this.apiKey = apiKey;
            this.newsSource = newsSource;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            xml = Connect.excuteGet("https://newsapi.org/v1/articles?source="+newsSource+"&" +
                    "sortBy=top&apiKey="+apiKey, urlParameters);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            if(xml.length()>10){ // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    dataList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String author = jsonObject.optString(KEY_AUTHOR);
                        String title = jsonObject.optString(KEY_TITLE);
                        String description = jsonObject.optString(KEY_DESCRIPTION);
                        String url = jsonObject.optString(KEY_URL);
                        String urlToImage = jsonObject.optString(KEY_URLTOIMAGE);
                        String publishedAt = jsonObject.optString(KEY_PUBLISHEDAT);
                        String formatDate = DateFormatter.DateFormat(publishedAt);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_ID, String.valueOf(i));
                        map.put(KEY_AUTHOR, author);
                        map.put(KEY_TITLE, title);
                        map.put(KEY_DESCRIPTION, description);
                        map.put(KEY_URL, url);
                        map.put(KEY_URLTOIMAGE, urlToImage);
                        map.put(KEY_PUBLISHEDAT, formatDate);
                        dataList.add(map);



                        Article article = new Article(i, author, title, description, url, urlToImage, formatDate);

                        //save data into firebase DB
                        saveRef.child(String.valueOf(i)).setValue(article);

                    }
                    Log.d(TAG, "onPostExecute: SIZE OF DATALIST:" + dataList.size());
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }

                //set Listview adapter
                ListNewsAdapter adapter = new ListNewsAdapter(AdminActivity.this, dataList);
                listNews.setAdapter(adapter);


                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(AdminActivity.this, DetailsActivity.class);
                        i.putExtra("url", dataList.get(position).get(KEY_URL));
                        startActivity(i);
                    }
                });

            }else{
                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }


    }
}
