package com.example.sayan.wellfamesampleapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    EditText searchText;
    private ListView lv;
    int pagesize;
    ProgressBar pgBar;
    TextView pageText, noResultText;
    Button onPrev, onNext;
    ArrayAdapter<String> adapter;
    private static final String API_KEY = "33346107-cabf-45d2-8a39-589047f74e83";
    int pagecount = 1;
    ArrayList<String> titleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchText = (EditText)findViewById(R.id.searchText);
        onPrev = (Button)findViewById(R.id.prev);
        onNext = (Button)findViewById(R.id.next);
        pageText = (TextView)findViewById(R.id.pagenos);
        noResultText = (TextView)findViewById(R.id.noResultText);
        lv = (ListView)findViewById(R.id.listView);
        pgBar = (ProgressBar)findViewById(R.id.progressBar1);
        if (savedInstanceState != null) {
            String[] values = savedInstanceState.getStringArray("myKey");
            if (values != null) {
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, values);
                lv.setAdapter(adapter);
            }
        }

    }

    public void onSearch(View v) throws JSONException {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        pagecount = 1;
        lv.setVisibility(GONE);
        pageText.setVisibility(GONE);
        onPrev.setVisibility(GONE);
        onNext.setVisibility(GONE);
        noResultText.setVisibility(GONE);
        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                try {
                    pgBar.setVisibility(GONE);
                    onNext.setVisibility(VISIBLE);
                    retrieve(pagecount);
                    if(pagecount == pagesize){
                        onNext.setVisibility(GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onTick(long millisUntilFinished) {
                pgBar.setVisibility(VISIBLE);
            }
        }.start();


    }

    public void onNext(View v) throws JSONException {
        pagecount ++;
        retrieve(pagecount);
        onPrev.setVisibility(VISIBLE);
        if(pagecount == pagesize){
            onNext.setVisibility(GONE);
        }
        //display(object);
    }

    public void onPrevious(View v) throws JSONException {
        pagecount --;
        retrieve(pagecount);
        if(pagecount == 1){
            onPrev.setVisibility(GONE);
        }

    }

    public void retrieve(int pageno) throws JSONException {
        JSONParser jParser = new JSONParser();
        String search = replace(String.valueOf(searchText.getText()));
        String url = "http://content.guardianapis.com/search?q="+ search +"&page="+ pageno +"&api-key=" + API_KEY;
        try{
            JSONObject json = jParser.getJSONFromUrl(url);
            JSONObject object = json.getJSONObject("response");
            pagesize = object.getInt("pages");
            if(pagesize == 0){
                noResultText.setVisibility(VISIBLE);
                noResultText.setText("No Results Found");
                onNext.setVisibility(GONE);
            }
            else{
                titleList = new ArrayList<String>();
                JSONArray jsonArray = object.getJSONArray("results");
                for (int i=0; i < jsonArray.length(); i++){
                    JSONObject twoObject = jsonArray.getJSONObject(i);
                    String title = twoObject.getString("webTitle");
                    titleList.add(title);
                    pageText.setVisibility(VISIBLE);
                    pageText.setText("Page " + pageno +" of " + pagesize);
                }
                adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, titleList);
                lv.setAdapter(adapter);
                lv.setVisibility(VISIBLE);
            }
        }catch (Exception e){
            noResultText.setVisibility(VISIBLE);
            noResultText.setText("Connection Error");
            onNext.setVisibility(GONE);
        }

    }

    public String replace(String str) {
        String[] words = str.split(" ");
        StringBuilder sentence = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; ++i) {
            sentence.append("%20");
            sentence.append(words[i]);
        }

        return sentence.toString();
    }

}


