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
    public static JsonObject itemsJson;

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
            itemsJson = allData.getJsonObject("items");
            items = allData.getJsonObject("items").toString();
            diningHalls = allData.getJsonObject("diningHalls").toString();
            filterableEntries = allData.getJsonArray("filterableEntries").toString();
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
        get("/item", (request, response) -> {
            String name = request.queryParams("name").toLowerCase();
            String itemString = "Item Not Found :(";
            JsonObject item = itemsJson.getJsonObject(name);
            if(item == null) {
                for(String itemName : itemsJson.keySet()) {
                    if(itemName.contains(name)) {
                        item = itemsJson.getJsonObject(itemName);
                        itemString = item.toString();
                        break;
                    }
                }
            } else {
                itemString = item.toString();
            }
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Content-Length", "" + itemString.length());
            return itemString;
        });
    }
}
