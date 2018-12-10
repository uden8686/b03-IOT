package com.example.wayne.usernew;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class auto extends AppCompatActivity {

    private Handler mMainHandler;
    private Socket socket = SocketHolder.getInstance().getSocket();
    private ExecutorService mThreadPool;
    private Button btnSend;
    private EditText mOpen;
    private EditText mClose;
    private TextView openC, closeC;
    String da = "";
    InputStream dis;
    OutputStream outputStream;

    public Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
                default:
                    break;

            }
            return false;
        }
    });


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        btnSend = (Button) findViewById(R.id.send1);
        mOpen = (EditText) findViewById(R.id.open);
        mClose = (EditText) findViewById(R.id.close);
        openC = (TextView) findViewById(R.id.openC);
        openC.setText("開啟溫度");
        closeC = (TextView) findViewById(R.id.closeC);
        closeC.setText("關閉溫度");


        mThreadPool = Executors.newCachedThreadPool();
     /*   btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 利用執行緒池直接開啟一個執行緒 & 執行該執行緒


                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
// 建立Socket物件 & 指定服務端的IP 及 埠號
                            conset.setText("連線失敗 請再按一次");
                            socket = new Socket("172.20.10.3", 8686);
                            dis = socket.getInputStream();
                            uiHandler.sendEmptyMessage(0);
// 判斷客戶端和伺服器是否連線成功



                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });





                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        byte buf[] = new byte[100];


                        while (true) {

                            try {

                                String s = "";
                                int len = dis.read(buf);


                                for (int a = 0; a < len; a++) {
                                    char c = (char) buf[a];
                                    s += c;
                                }
                                String ggg="da";
                                if (!s.equals(ggg)) {
                                    if(len>2){
                                        da=s;
                                        synchronized ((auto.class)){
                                            auto.class.notify();
                                        }
                                    }else{
                                        receive_message.setText(s);
                                    }

                                } else {
                                    NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                                    //Step2. 設定當按下這個通知之後要執行的activity
                                    Intent notifyIntent = new Intent(auto.this, auto.class);
                                    notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                                    PendingIntent appIntent = PendingIntent.getActivity(auto.this, 0, notifyIntent, 0);

                                    //Step3. 透過 Notification.Builder 來建構 notification，
                                    //並直接使用其.build() 的方法將設定好屬性的 Builder 轉換
                                    //成 notification，最後開始將顯示通知訊息發送至狀態列上。
                                    Notification notification
                                            = new Notification.Builder(auto.this)
                                            .setContentIntent(appIntent)
                                            .setSmallIcon(R.drawable.ic_android_black_24dp) // 設置狀態列裡面的圖示（小圖示）
                                            .setTicker("notification on status bar.") // 設置狀態列的顯示的資訊
                                            .setWhen(System.currentTimeMillis())// 設置時間發生時間
                                            .setAutoCancel(false) // 設置通知被使用者點擊後是否清除  //notification.flags = Notification.FLAG_AUTO_CANCEL;
                                            .setContentTitle("< 智慧遙控通知 >") // 設置下拉清單裡的標題
                                            .setContentText("偵測到有害氣體!")// 設置上下文內容
                                            .setDefaults(Notification.DEFAULT_ALL) //使用所有默認值，比如聲音，震動，閃屏等等
                                            .build();

                                    mNotificationManager.notify(5, notification);
                                }

                            } catch (Exception e) {
                            }

                        }
                    }
                });

            }
        });   */


        /*
         * 傳送訊息 給 伺服器
         */
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 利用執行緒池直接開啟一個執行緒 & 執行該執行緒
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
// 步驟1：從Socket 獲得輸出流物件OutputStream
// 該物件作用：傳送資料
                            outputStream = socket.getOutputStream();
// 步驟2：寫入需要傳送的資料到輸出流物件中
                            int a = Integer.parseInt(mOpen.getText().toString());
                            int b = Integer.parseInt(mClose.getText().toString());
                            if (a < 100 && a > 0 && b > 0 && b < 100) {
                                String ssend = "op" + (mOpen.getText().toString()) + (mClose.getText().toString());
                                byte[] buffer = new byte[200];
                                for (int i = 0; i < ssend.length(); i++) {
                                    buffer[i] = (byte) ssend.charAt(i);
                                }

                                if (socket.isConnected()) {
                                    outputStream.write(buffer, 0, ssend.length());
                                }
                                // 特別注意：資料的結尾加上換行符才可讓伺服器端的readline()停止阻塞
                                // 步驟3：傳送資料到服務端
                                outputStream.flush();
                            } else {


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }


    protected void onDestroy() {
        super.onDestroy();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
