package com.example.wayne.usernew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fonfan extends AppCompatActivity {

    private Button openfonfan;
    private ExecutorService mThreadPool;
    OutputStream outputStream;
    private Socket socket = SocketHolder.getInstance().getSocket();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fonfan);

        openfonfan = (Button) findViewById(R.id.openfonfan);
        mThreadPool = Executors.newCachedThreadPool();


        openfonfan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String ssend = "light";
                            byte[] buffer = new byte[200];
                            for (int i = 0; i < ssend.length(); i++) {
                                buffer[i] = (byte) ssend.charAt(i);
                            }
                            outputStream = socket.getOutputStream();
                            outputStream.write(buffer, 0, ssend.length());
                            outputStream.flush();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
