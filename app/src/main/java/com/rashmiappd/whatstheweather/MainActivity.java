package com.rashmiappd.whatstheweather;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText cityName ;
    TextView resulttextview;


    public void FindWeather(View view)
    {
        Log.i("City:" ,cityName.getText().toString() );

        /*runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Something", Toast.LENGTH_SHORT).show();
            }
        });*/

        //Below two lines are for hiding keyboard upon typing on city
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        String encodedCity = null;
        try {

            //City name is encoded to handle names which have spaces in between - "San Francisco"
            encodedCity = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ encodedCity +"&appid=10a46b3015d75683aa2aff09169b65f7");


        } catch (UnsupportedEncodingException e) {

            Toast.makeText(this,"Couldn't find the weather", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityNameID);
        resulttextview = findViewById(R.id.resultText);


    }

    public class DownloadTask extends AsyncTask<String , Void , String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;

            HttpURLConnection urlConnection ;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1)
                {
                    char current = (char)(data);
                    result += current;
                    data = reader.read();

                }
                return  result;

            } catch (Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't find the weather", Toast.LENGTH_LONG).show();
                    }
                });


            }


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String weatherinfo = jsonObject.getString("weather");
                Log.i("Weather content:", weatherinfo);

                JSONArray arr = new JSONArray(weatherinfo);
                String message = "";
                for(int i = 0 ; i < arr.length() ; i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main += jsonPart.getString("main");
                    description += jsonPart.getString("description");

                    if(main != "" && description != "")
                    {
                        message += main + ": " + description +"\r\n";
                    }
                }
                    if(message!="")
                    {
                        resulttextview.setText(message);
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Couldn't find the weather", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

            } catch (JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't find the weather", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }



}
