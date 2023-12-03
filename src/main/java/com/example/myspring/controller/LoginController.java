package com.example.myspring.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.myspring.model.LoginResponseModel;

@CrossOrigin(origins = "*" ) // 允許跨網域呼叫API
@RestController  
public class LoginController {
    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String mysqlDriverName;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity Login(String username, String password) { 
        // 登入
        String result = login(username, password);

        LoginResponseModel response = null;

        if(result.length() == 0) {
            // 成功
            response = new LoginResponseModel(0, "登入成功", username);
        } else {
            // 失敗
            response = new LoginResponseModel(1, result, username);
        }

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    // 驗證帳號跟密碼是否存在資料庫
    protected String login(String username, String password)  {
        Connection conn = null;  // 連接資料庫用
        Statement stmt = null;   // 下SQL語法用 
        ResultSet rs = null;     // 取得SQL結果用

        // 1. 註冊驅動程式
        try {
            Class.forName(this.mysqlDriverName);

            // 2. 連接資料庫
            conn = DriverManager.getConnection(this.mysqlUrl + "?user=" + this.mysqlUsername + "&password=" + this.mysqlPassword);

            // 3. 取得statement物件
            stmt = conn.createStatement();

            // 4. 查詢資料庫
            rs = stmt.executeQuery("select count(*) as c from account a where name='" + username + "' and code='" + password + "';");

            // 取得c欄位的資料
            rs.next(); // 將資料指向第一筆
            int count = rs.getInt("c");

            rs.close();
            stmt.close();
            conn.close();

            // 沒有查詢到資料=登入失敗
            // if(count == 0) {
            //     return false;
            // } 
            // else {
            //     return true;
            // }

            return count != 0 ? "" : "帳號錯誤";

        } catch (ClassNotFoundException e) {
            
            return "ClassNotFoundException";
        } catch(SQLException e) {
            return e.getMessage();
        } 
    }
}
