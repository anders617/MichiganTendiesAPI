/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import com.heroku.sdk.jdbc.DatabaseUrl;
import java.io.StringReader;
import java.sql.*;
import javax.json.*;
import static spark.Spark.*;

/**
 *
 * @author Anders
 */
public class Main {

    public static String mDiningData;
    public static String items;
    public static String diningHalls;
    public static String filterableEntries;

    public static void getMDiningData() {
        Connection connection = null;
        try {
            connection = DatabaseUrl.extract().getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM dining_data");
            result.next();
            connection.close();
            String data = result.getString("data");
            mDiningData = data;
            JsonReader reader = Json.createReader(new StringReader(mDiningData));
            JsonObject allData = reader.readObject();
            MDiningData diningData = new MDiningData(allData);
            items = diningData.getItemsJson().toString();
            diningHalls = diningData.getDiningHallsJson().toString();
            filterableEntries = diningData.getFilterableEntriesJson().toString();
        } catch (Exception e) {
            System.err.println("getMDiningData: " + e);
            mDiningData = "UNABLE TO RETRIEVE DATA";
            items = "UNABLE TO RETRIEVE DATA";
            diningHalls = "UNABLE TO RETRIEVE DATA";
            filterableEntries = "UNABLE TO RETRIEVE DATA";
        }
    }

    public static void main(String args[]) {
        port(Integer.valueOf(System.getenv("PORT")));
        //staticFiles.location("/public");
        getMDiningData();
        System.out.println("Serving data on port " + System.getenv("PORT"));
        get("/", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + mDiningData.length());
            return mDiningData;
        });
        get("/items", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + items.length());
            return items;
        });
        get("/dininghalls", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + diningHalls.length());
            return diningHalls;
        });
        get("/filterableentries", (request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + filterableEntries.length());
            return filterableEntries;
        });
    }
}
