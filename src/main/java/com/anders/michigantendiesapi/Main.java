/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import com.heroku.sdk.jdbc.DatabaseUrl;
import java.io.StringReader;
import java.sql.*;
import static spark.Spark.*;
import java.util.Scanner;
import javax.json.*;

/**
 *
 * @author Anders
 */
public class Main {

    static class WaitAndQuit implements Runnable {

        @Override
        public void run() {
            boolean running = true;
            Scanner s = new Scanner(System.in);
            while (running) {
                running = !(s.next(".+").toLowerCase().charAt(0) == 'q');
            }
            System.out.println("Closing Down Server...");
            stop();
        }

    }
    public static String mDiningData = getMDiningData();
    
    public static String getMDiningData() {
        Connection connection;
        try {
            connection = DatabaseUrl.extract().getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM dining_data");
            result.next();
            return result.getString("data");
        } catch (Exception e) {
            System.err.println("getMDiningData: " + e);
        }
        return "UNABLE TO RETRIEVE DATA";
    }

    public static void main(String args[]) {
        //port(Integer.valueOf(System.getenv("PORT")));
        //staticFiles.location("/public");
        Thread waitAndQuit = new Thread(new WaitAndQuit());
        waitAndQuit.start();
        get("/", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            return mDiningData;
        });
    }
}
