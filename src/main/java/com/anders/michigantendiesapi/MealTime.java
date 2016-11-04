/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import java.util.*;
import javax.json.*;
import java.text.*;

/**
 *
 * @author Anders
 */
public class MealTime {

    private final Date date;
    private final String formattedDate;
    private final List<String> mealNames;

    public MealTime(Date date, String formattedDate) throws ParseException {
        this.date = date;
        this.formattedDate = formattedDate;
        this.mealNames = new ArrayList();
    }

    public void addMealName(String mealName) {
        if (!mealNames.contains(mealName)) {
            mealNames.add(mealName);
        }
    }

    public JsonObject toJson() {
        JsonArrayBuilder mealNamesBuilder = Json.createArrayBuilder();
        for (String mealName : mealNames) {
            mealNamesBuilder.add(mealName);
        }
        return Json.createObjectBuilder()
                .add("date", MDiningData.DATE_FORMAT.format(date))//TODO:Correct date formatting
                .add("formattedDate", formattedDate)
                .add("mealNames", mealNamesBuilder)
                .build();
    }
}
