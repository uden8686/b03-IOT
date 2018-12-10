package teaservertest;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.sql.*;

public class Teaservertest {

    ArrayList<ClientTalk> cliTlk = new ArrayList<ClientTalk>();
    int x;
    int count = 0;

    public Teaservertest() {

    }

    public void broadCast(String str) {
        try {
            byte[] buffer = new byte[1000];
            for (int i = 0; i < str.length(); i++) {
                buffer[i] = (byte) str.charAt(i);
            }
            for (ClientTalk ct : cliTlk) {
                ct.dos.write(buffer, 0, str.length());
            }

        } catch (Exception e) {
            cliTlk.removeAll(cliTlk);
            e.printStackTrace();
        }

    }

    public void runServer() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            int port = 8686;
            //System.out.print("請輸入port : ");
            //port = Integer.parseInt(in.readLine());
            System.out.println("等待連線..");

            ServerSocket serverSocket = new ServerSocket(port);//開始監聽port連線請求。

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (clientSocket.isConnected()) {
                    try {
                        
                        // System.out.println("您是第" + count + "位Client");
                        ClientTalk ct = new ClientTalk(this, clientSocket);
                        Thread thread = new Thread(ct);//建立一個多執行緒(錯在這)
                        cliTlk.add(ct);
                        thread.start(); //啟動該多執行緒
                        count = cliTlk.size();
                        System.out.println("Conncetion Count is: " + count );
                        

                    } catch (Exception e) {
                        count--;
                        System.out.println(e.toString() + "count = " + count);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    class ClientTalk implements Runnable {

        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://localhost:3306/da?useSSL=false&serverTimezone=UTC";
        static final String USER = "root";
        static final String PASS = "togo0602";

        private InputStream input;
        InputStream dis;//宣告一個讀取Client傳送過來的字串物件
        OutputStream dos;
        protected int count;
        protected Socket clientSocket;
        Teaservertest server;

        public ClientTalk(Teaservertest server_, Socket socket_) {
            server = server_;
            clientSocket = socket_;
            try {
                dos = clientSocket.getOutputStream();
                dos.flush();
                // dos.writeUTF("您是第" + server.cliTlk.size() + "位Client");
                dis = clientSocket.getInputStream();//宣告一個將server端資料寫出的變數
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        public void jchart() {
            Connection conn = null;
            Statement stmt = null;
            int r_c = 0, r_t = 0;
            int count = 1;
            String s = "s";
            String st = "";
            try {
                Class.forName("com.mysql.jdbc.Driver");

                // 打开链接
                System.out.println("連接資料庫...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                // 执行查询
                //System.out.println(" 實例化Statement對象...");
                stmt = conn.createStatement();
                String sql = "select r_id,C,T,ti from record where r_id between (select max(r_id) from record)-10 and (select max(r_id) from record)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {//while loop 一筆一筆iterate
                    r_c = rs.getInt("C");
                    r_t = rs.getInt("T");
                    st += r_c;
                }
                //System.out.println(st);

            } catch (SQLException se) {
                // 处理 JDBC 错误
                se.printStackTrace();
            } catch (Exception e) {
                // 处理 Class.forName 错误
                e.printStackTrace();
            } finally {
                // 关闭资源
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException se2) {
                }// 什么都不做
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            server.broadCast(st);

        }

        public void DataTransfer(String read) {
            Connection conn = null;
            Statement stmt = null;
            try {
                // 注册 JDBC 驱动
                Class.forName("com.mysql.jdbc.Driver");

                // 打开链接
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                // 执行查询
                stmt = conn.createStatement();
                String sql = "";
                if (read != "") {
                    switch (read.substring(0, 2)) {//看丟來資料的前兩格決定去向
                        case "re"://丟給紀錄表
                            sql = "INSERT INTO record(C,T,ti) VALUES (?,?,CURRENT_TIME())";
                            server.broadCast(read.substring(2, 4));
                            break;
                        case "op"://丟給設定
                            sql = "INSERT INTO op (C,T) VALUES (?,?)";
                            break;
                        case "ch":

                            jchart();
                            break;
                        case "da":
                            server.broadCast("da");
                            break;
                        case "By":
                            System.out.println("Client send Bye!");
                            clientSocket.close();
                            cliTlk.remove(this);
                            break;
                        case "li":
                            try{
                                URL url = new URL("http://172.20.10.6/gpio/0");
                                URLConnection html = url.openConnection();
                                InputStream in = html.getInputStream();
                                int data = in.read();
                                while (data != -1) {
                                    System.out.print((char) data);
                                    data = in.read();
                                }
                            }
                            catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case "hi":
                            try{
                                URL url = new URL("http://172.20.10.6/gpio/1");
                                URLConnection html = url.openConnection();
                                InputStream in = html.getInputStream();
                                int data = in.read();
                                while (data != -1) {
                                    System.out.print((char) data);
                                    data = in.read();
                                }
                            }
                            catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case "lo":
                            try{
                                URL url = new URL("http://172.20.10.6/gpio/2");
                                URLConnection html = url.openConnection();
                                InputStream in = html.getInputStream();
                                int data = in.read();
                                while (data != -1) {
                                    System.out.print((char) data);
                                    data = in.read();
                                }
                            }
                            catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                    }
                }
                if (sql != "") {
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, read.substring(2, 4));//第一個?要插入的值
                    pstmt.setString(2, read.substring(4));
                    pstmt.executeUpdate();
                    pstmt.close();
                }
                stmt.close();
                conn.close();
            } catch (SQLException se) {
                // 处理 JDBC 错误
                se.printStackTrace();
            } catch (Exception e) {
                // 处理 Class.forName 错误
                e.printStackTrace();
            } finally {
                // 关闭资源
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException se2) {
                }// 什么都不做
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            System.out.println("資料庫儲存完畢");
        }

        public void run() //多執行緒，run
        {
            byte buf[] = new byte[1000];
            // process messages sent from client
            // read message and display it
            while (true) {
                try {
                    String s = "";
                    int len = dis.read(buf);

                    for (int a = 0; a < len; a++) {
                        char c = (char) buf[a];
                        s += c;
                    }

                    System.out.println(s);

                    if (s == "") {
                        break;
                    }
                    DataTransfer(s);
                } // catch problems reading from client
                catch (Exception e) {
                    System.out.println("Client Disconnected!");                   
                    break;
                }
            }

        }
    }

    public static void main(String[] args) {

        Teaservertest server = new Teaservertest();
        server.runServer();

        int count = 0;
        ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
    }
}
