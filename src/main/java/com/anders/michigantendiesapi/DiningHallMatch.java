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
public class DiningHallMatch {

    private final String name;
    private final Map<Date, MealTime> mealTimes;

    public DiningHallMatch(String name) {
        this.name = name;
        this.mealTimes = new HashMap();
    }

    public void addMealTime(Date date, String formattedDate, String mealName) {
        if (mealTimes.containsKey(date)) {
            mealTimes.get(date).addMealName(mealName);
        } else {
            try {
                MealTime newMealTime = new MealTime(date, formattedDate);
                newMealTime.addMealName(mealName);
                mealTimes.put(date, newMealTime);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    public JsonObject toJson() {
        JsonObjectBuilder mealTimesBuilder = Json.createObjectBuilder();
        JsonArrayBuilder mealTimesArrayBuilder = Json.createArrayBuilder();
        for (Date date : mealTimes.keySet()) {
            JsonObject mealTime = mealTimes.get(date).toJson();
            mealTimesBuilder.add(MDiningData.DATE_FORMAT.format(date), mealTime);//TODO:Correct date formatting
            mealTimesArrayBuilder.add(mealTime);
        }
        return Json.createObjectBuilder()
                .add("name", name)
                .add("mealTimes", mealTimesBuilder)
                .add("mealTimesArray", mealTimesArrayBuilder)
                .build();
    }

}
