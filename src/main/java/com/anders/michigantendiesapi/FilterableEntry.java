/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import java.util.*;
import javax.json.*;

/**
 *
 * @author Anders
 */
public class FilterableEntry {
    private final Date date;
    private final List<String> mealNames;
    private final String itemName;
    private final String diningHallName;
    private final List<String> attributes;
    

    public FilterableEntry(Date date, String itemName, String diningHallName, List<String> mealNames, List<String> attributes) {
        this.date = date;
        this.mealNames = mealNames;
        this.itemName = itemName;
        this.diningHallName = diningHallName;
        this.attributes = attributes;
    }

    public JsonObject toJson() {
        JsonArrayBuilder mealNamesBuilder = Json.createArrayBuilder();
        for (String mealName : mealNames) {
            mealNamesBuilder.add(mealName);
        }
        JsonArrayBuilder attributesBuilder = Json.createArrayBuilder();
        for(String attribute : attributes) {
            attributesBuilder.add(attribute);
        }
        return Json.createObjectBuilder()
                .add("date", MDiningData.DATE_FORMAT.format(date))//TODO:Correct date formatting
                .add("mealNames", mealNamesBuilder)
                .add("itemName", itemName)
                .add("diningHallName", diningHallName)
                .add("attributes", attributesBuilder)
                .build();
    }
}
