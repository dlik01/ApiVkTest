package com.example.home.apivktest;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends Activity {
    //Список получаемых данных
    //private String[] score = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private String[] arrayStoryMovies = new String[0];
    private String[] arrayUrlImage = new String[0];
    private ArrayAdapter<String> arrayAdapterStory = new ArrayAdapter<String>(MainActivity.this, R.layout.row, R.id.tView);

    private ListView listView;

    public boolean notific = false;                         //для работы с уведомлениями
    private static final int NOTIFY_ID = 101;


    private class ParseTask extends AsyncTask<Void, Void, String>{

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... voids) {
            //получаем данные с внешнего рессурса
            try{
                URL url = new URL("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine())!=null){
                    buffer.append(line);
                }
                resultJson = buffer.toString();

            }catch (Exception e){
                Toast.makeText(MainActivity.this, "что-то пошло не так", Toast.LENGTH_SHORT).show();

            }

            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);

            JSONObject dataJsonObj = null;
            String nameMovies = "";

            //ArrayAdapter<String> arrayAdapterStory = new ArrayAdapter<String>(MainActivity.this, R.layout.row, R.id.tView);

            try{

                dataJsonObj = new JSONObject(strJson);
                JSONArray movies = dataJsonObj.getJSONArray("movies");

                for (int i = 0; i < movies.length(); i++) {
                    JSONObject film = movies.getJSONObject(i);

                    //JSONObject contacts = friend.getJSONObject("contacts");

                    String movie = film.getString("movie");
                    String year = film.getString("year");
                    String story = film.getString("story");
                    String urlImg = film.getString("image");
                    //arrayUrlImage[i] = urlImg;
                    //arrayStoryMovies[i] = story;
                    arrayAdapterStory.add(story);


                }
                //listView.setAdapter(arrayAdapterStory);

            }
            catch (Exception e){

            }


            //ArrayAdapter<String> arrayAdapterName = new ArrayAdapter<String>()
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();



        listView = (ListView)findViewById(R.id.listView);
        //new ParseTask();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //ArrayAdapter<String> arrayAdapterName = new ArrayAdapter<String>(MainActivity.this, R.layout.row, R.id.tView, arrayStoryMovies);

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.row, R.id.tView, arrayUser);
        listView.setAdapter(arrayAdapterStory);



    }

}
/* метод onItemClick для появления уведомлений.


ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.row, R.id.tView, list);

                listView.setAdapter(arrayAdapter);

public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = String.valueOf(list.get(position));
                        if (notific == false) {
                            notific = true;
                            builder.setContentIntent(contentIntent)
                                    //иконка для уведомления
                                    .setSmallIcon(R.mipmap.ic_persons)
                                     // Заголовок уведомления
                                    .setContentTitle("Напоминание")
                                    // Текст уведомления
                                    .setContentText("Вы хотели посмотреть " + name)
                                     // текст в строке состояния
                                    .setTicker("У вас с друзьях" + name)
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true);

                            notificationManager.notify(NOTIFY_ID, builder.build());
                        }
                        else {
                            notific = false;
                            notificationManager.cancel(NOTIFY_ID);
                        }

                    }
                });


                private String[] score = {"1", "2", "3", "4", "5", "6", "7", "8"};

 */