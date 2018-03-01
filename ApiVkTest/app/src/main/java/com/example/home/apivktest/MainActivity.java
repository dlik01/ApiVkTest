package com.example.home.apivktest;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;







public class MainActivity extends Activity {
    //Список получаемых данных

    public boolean notific = false;                         //для работы с уведомлениями
    private static final int NOTIFY_ID = 101;


    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();


        listView = (ListView) findViewById(R.id.listView);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);


        //подготавливаем переменные для уведомлений
        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        listView = (ListView)findViewById(R.id.listView);

        // метод onItemClick для появления уведомлений.

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                if (notific == false) {
                    notific = true;
                            builder.setContentIntent(contentIntent)
                            //иконка для уведомления
                            .setSmallIcon(R.mipmap.ic_film_round)
                            // Заголовок уведомления
                            .setContentTitle("Напоминание")
                            // Текст уведомления
                            .setContentText("Вы хотели посмотреть этот фильм")
                            // текст в строке состояния
                            .setTicker("Историю фильма прочитайте сами ")
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

    }

    private class ParseTask extends AsyncTask<String, String, List<CustomList> > {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        @Override
        protected List<CustomList> doInBackground(String... params) {
            // получаем данные с внешнего ресурса
            String resultJson = null;
            List<CustomList> movieList = null;
            try {
                URL url = new URL("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();

                JSONObject parentObject = new JSONObject(resultJson);
                JSONArray parentArray = parentObject.getJSONArray("movies");

                movieList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    CustomList customList = gson.fromJson(finalObject.toString(), CustomList.class);

                    movieList.add(customList);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(final List<CustomList> strJson) {
            super.onPostExecute(strJson);

            if (strJson != null){
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(),R.layout.row, strJson);
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(getApplicationContext(), "не удалось получить данные с сервера", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public class MovieAdapter extends ArrayAdapter {

        private List<CustomList> movieModelList;
        private int resource;
        private LayoutInflater inflater;
        public MovieAdapter(Context context, int resource, List<CustomList> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.imageView2);
                holder.tvStory = (TextView)convertView.findViewById(R.id.tView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            // Это нужно для отображения картинки
            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.ivMovieIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }
            });


            holder.tvStory.setText(movieModelList.get(position).getStory());
            return convertView;
        }


        class ViewHolder{
            private ImageView ivMovieIcon;
            private TextView tvStory;
        }

    }

}

