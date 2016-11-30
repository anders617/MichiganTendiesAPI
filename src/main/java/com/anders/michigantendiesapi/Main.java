/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import com.heroku.sdk.jdbc.DatabaseUrl;
import java.sql.*;
import static spark.Spark.*;
import java.util.Scanner;

/**
 *
 * @author Anders
 */
public class Main {
    
    public static String mDiningData = getMDiningData();
    
    public static String getMDiningData() {
        Connection connection = null;
        try {
            connection = DatabaseUrl.extract().getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM dining_data");
            result.next();
            connection.close();
            return result.getString("data");
        } catch (Exception e) {
            System.err.println("getMDiningData: " + e);
        }
        return "UNABLE TO RETRIEVE DATA";
    }

    public static void main(String args[]) {
        port(Integer.valueOf(System.getenv("PORT")));
        //staticFiles.location("/public");
        System.out.println("Serving data on port " + System.getenv("PORT"));
        get("/", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + mDiningData.length());
            return mDiningData;
        });
    }
}
