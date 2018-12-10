/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.sql.*;
import java.time.Instant;

import java.awt.*; 
import javax.swing.*; 
import java.util.*; 
import java.io.*; 
import java.net.*;

/**
 *
 * @author Wayne
 */
public class Analysis {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/da?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "togo0602";
    static boolean f=true;
    public static void redshot(){
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
    }
    
    
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmttwo=null;
        String r_id,o_id;
        int r_c=0,r_t=0,o_c=0,o_t=0;
        while(true){
            try{
                Thread.sleep(5000);
            }
            catch (Exception e){
            }
            try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");
        
            // 打开链接
            System.out.println("連接數據庫...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
            // 执行查询
            //System.out.println(" 實例化Statement对象...");
            String sql="select C,T from record where r_id=(select max(r_id) from record)";
            String sqltwo="select C,T from op where o_id=(select max(o_id) from op)";
            stmt = conn.createStatement();
            stmttwo=conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//Query結果存在這裡//取得Query資料
            ResultSet rs_two=stmttwo.executeQuery(sqltwo);


            while (rs.next()){//while loop 一筆一筆iterate
                  r_c=rs.getInt("C");
                  r_t=rs.getInt("T");
                  System.out.println("現在溫度 : "+r_c);
            }
            while (rs_two.next()){//while loop 一筆一筆iterate
                  o_c=rs_two.getInt("C");
                  o_t=rs_two.getInt("T");
                  System.out.println("開啟溫度 : "+o_t);
            }
            if(r_c>o_c){
                if(f){
                //System.out.println("阿斯~~~");
                redshot();
                        /*String url="http://192.168.43.177/gpio/1";
                        Runtime.getRuntime().exec("cmd /c start "+url);*/
                    f=false;
                 }   
            }
            if(r_c<=o_t){
                System.out.println(f);
                
                if(f==false){
                redshot();
                }
                f=true;
            }
            sql="delete from record where ti<current_date()";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
            stmt.close();
            stmttwo.close();
            conn.close();
            }catch(SQLException se){
            // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
            // 处理 Class.forName 错误
                e.printStackTrace();
            }finally{
            // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                    if(stmttwo!=null) stmttwo.close();
                }catch(SQLException se2){
                    
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        System.out.println("Done");
        }   
    }
    
}
