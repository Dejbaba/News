package com.example.news;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import com.example.news.Model.Article;
import com.example.news.utils.DateFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private static final String ARTICLES = "articles";

    static final String KEY_ID = "id";
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    private ListView listNews;
    private ProgressBar loader;
    private TextView emptyTV;


    Article article;

    private Boolean retrieved = false;

    //firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference retrieveRef;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        firebaseDatabase = FirebaseDatabase.getInstance();
        retrieveRef = firebaseDatabase.getReference(ARTICLES);

        article = new Article();



        listNews = findViewById(R.id.listNews);
        loader = findViewById(R.id.userloader);
        emptyTV = findViewById(R.id.emptyView);
        listNews.setEmptyView(loader);


    /**
     * retrieve articles uploaded by the admin from the DB
     * if no articles found in the DB, set an empty view for
     * the listview
     */
        retrieveArticlesFromDB();


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
                    final ListNewsAdapter adapter = new ListNewsAdapter(UserActivity.this, dataList);
                    listNews.setAdapter(adapter);


                    /**
                     * on ItemClick Listener to navigate the user to a webview to read the full article
                     */
                    listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Intent i = new Intent(UserActivity.this, DetailsActivity.class);
                            i.putExtra("url", dataList.get(+position).get(KEY_URL));
                            startActivity(i);
                        }
                    });



                }else{

                    Log.d(TAG, "onCreate: Nothing retrieved from DB");
                    loader.setVisibility(View.GONE);
                    listNews.setEmptyView(findViewById(R.id.emptyView));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }








}
