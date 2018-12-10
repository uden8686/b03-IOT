package com.example.wayne.usernew;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

class SocketHolder {
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private static final SocketHolder holder = new SocketHolder();

    public static SocketHolder getInstance() {
        return holder;
    }
}

public class MainActivity extends AppCompatActivity {


    // 主執行緒Handler
// 用於將從伺服器獲取的訊息顯示出來
    private Handler mMainHandler;
    // Socket變數
    private Socket socket;
    // 執行緒池
// 為了方便展示,此處直接採用執行緒池進行執行緒管理,而沒有一個個開執行緒
    private ExecutorService mThreadPool;
    // 輸入流物件
    InputStream dis;
    // 輸出流物件
    OutputStream outputStream;
    /*
       按鈕 變數
      */
    // 連線 傳送資料到伺服器 的按鈕變數
    private Button btnConnect;
    // 顯示接收伺服器訊息 按鈕
    private TextView receive_message, conset, viewC, viewS, ServerS;
    // 輸入需要傳送的訊息 輸入框
    String da = "";
    String s = "";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.GoAuto:
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, auto.class);
                        startActivity(intent);
                    }
                });
                return true;
            case R.id.today:
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            outputStream = socket.getOutputStream();

                            String ssend = "chart";
                            byte[] buffer = new byte[200];
                            for (int i = 0; i < ssend.length(); i++) {
                                buffer[i] = (byte) ssend.charAt(i);
                            }

                            outputStream.write(buffer, 0, ssend.length());
                            outputStream.flush();

                        } catch (Exception e) {
                        }

                        synchronized (MainActivity.class) {
                            try {
                                MainActivity.class.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }


                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("c", da);

                        intent.setClass(MainActivity.this, chartMain.class);
                        intent.putExtras(bundle);

                        startActivity(intent);

                        //MainActivity.this.finish();

                    }
                });
                return true;
            case R.id.hand:
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, hand.class);
                        startActivity(intent);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    receive_message.setVisibility(View.VISIBLE);
                    receive_message.setEnabled(true);
                    conset.setVisibility(View.INVISIBLE);
                    conset.setEnabled(false);
                    btnConnect.setVisibility(View.INVISIBLE);
                    btnConnect.setEnabled(false);
                    viewC.setVisibility(View.VISIBLE);
                    viewC.setEnabled(true);
                    viewS.setVisibility(View.VISIBLE);
                    viewS.setEnabled(true);
                    ServerS.setVisibility(View.VISIBLE);
                    ServerS.setEnabled(true);
                    break;
                case 1:
                    conset.setText("連線失敗 請再按一次");
                    break;
                case 2:
                    receive_message.setText(s);
                    s = "";
                    break;
                case 3:
                    ServerS.setText("Connected");
                    break;
                case 4:
                    ServerS.setText("Disconnected");
                    break;
                default:
                    break;

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// 初始化所有按鈕

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnConnect = (Button) findViewById(R.id.connect);
        receive_message = (TextView) findViewById(R.id.receive_message);
        conset = (TextView) findViewById(R.id.context);
        viewC = (TextView) findViewById(R.id.viewC);
        viewS = (TextView) findViewById(R.id.viewS);
        ServerS = (TextView) findViewById(R.id.ServerS);

        //conset.setText("請按下連線");
        receive_message.setVisibility(View.INVISIBLE);
        receive_message.setEnabled(false);
        viewC.setVisibility(View.INVISIBLE);
        viewC.setEnabled(false);
        viewC.setText("現在溫度 :");
        viewS.setVisibility(View.INVISIBLE);
        viewS.setEnabled(false);
        viewS.setText("Server :");
        ServerS.setEnabled(false);
        ServerS.setVisibility(View.INVISIBLE);


// 初始化執行緒池
        mThreadPool = Executors.newCachedThreadPool();


        /*
         * 建立客戶端 & 伺服器的連線
         */

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 利用執行緒池直接開啟一個執行緒 & 執行該執行緒


                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
// 建立Socket物件 & 指定服務端的IP 及 埠號
                            uiHandler.sendEmptyMessage(1);
                            socket = new Socket("172.20.10.3", 8686);
                            dis = socket.getInputStream();
                            uiHandler.sendEmptyMessage(0);
// 判斷客戶端和伺服器是否連線成功
                            SocketHolder.getInstance().setSocket(socket);


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

                                int len = dis.read(buf);


                                for (int a = 0; a < len; a++) {
                                    char c = (char) buf[a];
                                    s += c;
                                }
                                String ggg = "da";
                                if (!s.equals(ggg)) {
                                    if (s.length() > 2) {
                                        da = s;
                                        synchronized ((MainActivity.class)) {
                                            MainActivity.class.notify();
                                        }
                                        s = "";
                                    } else {
                                        uiHandler.sendEmptyMessage(2);

                                    }

                                } else {
                                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                    //Step2. 設定當按下這個通知之後要執行的activity
                                    Intent notifyIntent = new Intent(MainActivity.this, MainActivity.class);
                                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    PendingIntent appIntent = PendingIntent.getActivity(MainActivity.this, 0, notifyIntent, 0);

                                    //Step3. 透過 Notification.Builder 來建構 notification，
                                    //並直接使用其.build() 的方法將設定好屬性的 Builder 轉換
                                    //成 notification，最後開始將顯示通知訊息發送至狀態列上。
                                    Notification notification
                                            = new Notification.Builder(MainActivity.this)
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

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                Thread.sleep(1000);
                                String ssend = "test";
                                byte[] buffer = new byte[200];
                                for (int i = 0; i < ssend.length(); i++) {
                                    buffer[i] = (byte) ssend.charAt(i);
                                }
                                outputStream = socket.getOutputStream();
                                outputStream.write(buffer, 0, ssend.length());
                                outputStream.flush();
                                uiHandler.sendEmptyMessage(3);
                          }
                        }catch(InterruptedException e){

                        }catch (Exception e){
                            e.printStackTrace();
                            uiHandler.sendEmptyMessage(4);
                        }
                    }
                });


            }
        });




        /*btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 利用執行緒池直接開啟一個執行緒 & 執行該執行緒
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            outputStream = socket.getOutputStream();

                            String ssend = "chart";
                            byte[] buffer = new byte[200];
                            for (int i = 0; i < ssend.length(); i++) {
                                buffer[i] = (byte) ssend.charAt(i);
                            }

                            outputStream.write(buffer, 0, ssend.length());
                            outputStream.flush();

                        }catch(Exception e){}

                        synchronized (MainActivity.class){
                            try{
                                MainActivity.class.wait();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

                        }


                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("c",da);

                        intent.setClass(MainActivity.this, chartMain.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        //MainActivity.this.finish();

                    }
                });
            }
        });*/


    }

    public void sayGoodBye() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("cclo", "Activity Stoped!");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.v("cclo", "Activity Destroyed!");
        try {
            String ssend = "Bye";
            byte[] buffer = new byte[200];
            for (int i = 0; i < ssend.length(); i++) {
                buffer[i] = (byte) ssend.charAt(i);
            }
            outputStream = socket.getOutputStream();
            outputStream.write(buffer, 0, ssend.length());
            outputStream.flush();
            Thread.sleep(1000);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
