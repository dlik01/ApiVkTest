package com.example.home.apivktest;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import android.view.View;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

public class MainActivity extends Activity {
    //Список получаемых данных
    private String[] score = new String[]{VKScope.FRIENDS};
    private ListView listView;

    public boolean notific = false;                         //для работы с уведомлениями
    private static final int NOTIFY_ID = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VKSdk.login(this, score);

        listView = (ListView)findViewById(R.id.listView);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(this,0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                final VKList list = (VKList)response.parsedModel;
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.row, R.id.tView, list);

                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = String.valueOf(list.get(position));
                        //String text = String.format("Name: %s; Id: %s", list.get(position), position);
                        //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                        if (notific == false) {
                            notific = true;
                            builder.setContentIntent(contentIntent)
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



            }
        });
        Toast.makeText(getApplicationContext(), "Вы на равильном пути", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {


            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
