/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import javax.json.*;
import java.util.*;
import java.text.*;

/**
 *
 * @author Anders
 */
public class MDiningData {

    public static final MDiningData M_DINING_DATA = new MDiningData();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") {
        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, java.text.FieldPosition pos) {
            StringBuffer toFix = super.format(date, toAppendTo, pos);
            return toFix.insert(toFix.length() - 2, ':');
        }

        @Override
        public Date parse(String source) throws java.text.ParseException {
            final int split = source.length() - 2;
            return super.parse(source.substring(0, split - 1) + source.substring(split)); // replace ":" du TimeZone
        }
    };
    
    private final Map<String, JsonObject> diningHalls;
    private final Map<String, Item> items;

    public MDiningData() {
        diningHalls = new HashMap();
        items = new HashMap();
    }

    public void addItem(JsonObject menuItem, String diningHallName, Date date, String formattedDate, String mealName) {
        String trimmedName = menuItem.getString("name").trim().toLowerCase();
        if (items.containsKey(trimmedName)) {
            items.get(trimmedName).addDiningHall(diningHallName, date, formattedDate, mealName);
        } else {
            Item newItem = new Item(menuItem);
            newItem.addDiningHall(diningHallName, date, formattedDate, mealName);
            items.put(trimmedName, newItem);
        }
    }

    public void addDiningHall(String name, JsonObject diningHall) {
        diningHalls.put(name, diningHall);
    }

    public Map<String, JsonObject> getDiningHalls() {
        return diningHalls;
    }

    public JsonObject toJson() {
        JsonObjectBuilder itemsBuilder = Json.createObjectBuilder();
        for (String itemName : items.keySet()) {
            itemsBuilder.add(itemName, items.get(itemName).toJson());
        }
        JsonObjectBuilder diningHallsBuilder = Json.createObjectBuilder();
        for (String diningHallName : diningHalls.keySet()) {
            diningHallsBuilder.add(diningHallName, diningHalls.get(diningHallName));
        }
        return Json.createObjectBuilder()
                .add("items", itemsBuilder)
                .add("diningHalls", diningHallsBuilder)
                .build();
    }
}
