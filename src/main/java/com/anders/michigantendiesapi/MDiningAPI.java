/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import java.util.function.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.json.*;

/**
 *
 * @author Anders
 */
public class MDiningAPI {

    private static final String DINING_HALL_MENU_DETAILS_BASE_URL = "https://mobile.its.umich.edu/michigan/services/dining/menusByDiningHall?_type=json&diningHall=";
    private static final String DINING_HALL_MENU_BASE_URL = "https://mobile.its.umich.edu/michigan/services/dining/shallowMenusByDiningHall?_type=json&diningHall=";
    private static final String DINING_HALL_LIST_URL = "https://mobile.its.umich.edu/michigan/services/dining/shallowDiningHallGroups?_type=json";
    private static final String DINING_HALL_GROUP_NAME = "DINING HALLS";

    private static final DateFormat MENU_DETAILS_URL_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private JsonStructure parseJson(String json) {
        JsonReader reader = Json.createReader(new StringReader(json));
        return reader.read();
    }

    private void sendHttpGetRequest(String url, Consumer<String> onSuccess, Consumer<Integer> onFailure) {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().reduce((first, second) -> first + second).get();
                Optional.ofNullable(onSuccess).ifPresent(callback -> callback.accept(response));
            } else {
                Optional.ofNullable(onFailure).ifPresent(callback -> callback.accept(responseCode));
            }
        } catch (Exception e) {
            System.err.println("sendHttpGetRequest: " + e);
            Optional.ofNullable(onFailure).ifPresent(callback -> callback.accept(-1));
        }
    }

    public void requestDiningHallList(Consumer<JsonArray> onSuccess, Consumer<Integer> onFailure) {
        sendHttpGetRequest(DINING_HALL_LIST_URL, response -> {

            Optional<JsonValue> diningHallList = ((JsonObject) parseJson(response))
                    .getJsonArray("diningHallGroup")
                    .stream()
                    .filter(v -> ((JsonObject) v).getString("name").equals(DINING_HALL_GROUP_NAME))
                    .findAny();
            if (diningHallList.isPresent()) {
                JsonObject list = (JsonObject) diningHallList.get();
                Optional.ofNullable(onSuccess).ifPresent(callback -> callback.accept(list.getJsonArray("diningHall")));
            } else {
                Optional.ofNullable(onFailure).ifPresent(callback -> callback.accept(-1));
            }
        }, onFailure);
    }

    public void requestDiningHallMenu(String diningHallName, Consumer<JsonArray> onSuccess, Consumer<Integer> onFailure) {
        try {
            String url = DINING_HALL_MENU_BASE_URL + URLEncoder.encode(diningHallName, "UTF-8");
            sendHttpGetRequest(url, response -> {
                JsonArray menu = ((JsonObject) parseJson(response)).getJsonArray("menu");
                Optional.ofNullable(onSuccess).ifPresent(callback -> callback.accept(menu));
            }, onFailure);
        } catch (Exception e) {
            System.err.println("requestDiningHallMenu: " + e);
        }
    }

    public void requestDiningHallMenuDetails(
            String diningHallName,
            String mealName,
            Date date,
            Consumer<JsonObject> onSuccess,
            Consumer<Integer> onFailure) {
        try {
            String url = DINING_HALL_MENU_DETAILS_BASE_URL
                    + URLEncoder.encode(diningHallName, "UTF-8")
                    + "&menu=" + URLEncoder.encode(mealName, "UTF-8")
                    + "&date=" + MENU_DETAILS_URL_DATE_FORMAT.format(date);
            sendHttpGetRequest(url, response -> {
                JsonObject details = ((JsonObject) parseJson(response)).getJsonArray("menu").getJsonObject(0);
                Optional.ofNullable(onSuccess).ifPresent(callback -> callback.accept(details));
            }, onFailure);
        } catch (Exception e) {
            System.err.println("requestDiningHallMenuDetails: " + e);
        }
    }

    private Consumer<JsonArray> menuHandler(String diningHallName) {
        return menu -> {
            for (JsonObject meal : menu.getValuesAs(JsonObject.class)) {
                try {
                    Date date = MDiningData.DATE_FORMAT.parse(meal.getString("date"));
                    requestDiningHallMenuDetails(
                            diningHallName,
                            meal.getString("name"),
                            date,
                            menuDetailsHandler(diningHallName, date),
                            null
                    );
                } catch (Exception e) {
                    System.err.println("menuHandler: " + e);
                }
            }
        };
    }

    private Consumer<JsonObject> menuDetailsHandler(String diningHallName, Date date) {
        return details -> {
            if (details.getBoolean("hasCategories")) {
                JsonArray categories = details.getJsonArray("category");
                for (JsonObject category : categories.getValuesAs(JsonObject.class)) {
                    JsonArray menuItems = category.getJsonArray("menuItem");
                    for (JsonObject menuItem : menuItems.getValuesAs(JsonObject.class)) {
                        MDiningData.M_DINING_DATA.addItem(
                                menuItem,
                                diningHallName,
                                date,
                                details.getString("formattedDate"),
                                details.getString("name"));
                    }
                }
            }
        };
    }

    public void requestDiningData() {
        requestDiningHallList(diningHalls -> {
            for (JsonObject diningHall : diningHalls.getValuesAs(JsonObject.class)) {
                MDiningData.M_DINING_DATA.addDiningHall(diningHall.getString("name"), diningHall);
            }
        }, errorCode -> {
            System.out.println("Error: " + errorCode);
        });
        for (String diningHallName : MDiningData.M_DINING_DATA.getDiningHalls().keySet()) {
            requestDiningHallMenu(
                    diningHallName, 
                    menuHandler(diningHallName), 
                    null
            );
        }
    }
}
