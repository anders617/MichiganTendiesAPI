/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import javax.json.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Anders
 */
public class Item {

    private final String name;
    private final List<String> attributes;
    private final Map<String, DiningHallMatch> diningHallMatches;

    public Item(JsonObject menuItem) {
        name = menuItem.getString("name");
        if(menuItem.containsKey("attribute")) {
            attributes = menuItem.getJsonArray("attribute").stream().map(jsonValue -> jsonValue.toString()).collect(Collectors.toList());
            for(int i = 0;i < attributes.size();i++) {
                attributes.set(i, attributes.get(i).replaceAll("\"", ""));
            }
        } else {
            attributes = new ArrayList();
        }
        diningHallMatches = new HashMap();
    }

    public void addDiningHall(String diningHallName, Date date, String formattedDate, String mealName) {
        if (diningHallMatches.containsKey(diningHallName)) {
            diningHallMatches.get(diningHallName).addMealTime(date, formattedDate, mealName);
        } else {
            DiningHallMatch newMatch = new DiningHallMatch(diningHallName);
            newMatch.addMealTime(date, formattedDate, mealName);
            diningHallMatches.put(diningHallName, newMatch);
        }
    }

    public Map<String, DiningHallMatch> getDiningHallMatches() {
        return diningHallMatches;
    }

    public String getName() {
        return name;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public List<DiningHallMatch> getDiningHallMatchesArray() {
        return new ArrayList(diningHallMatches.keySet());
    }

    public JsonObject toJson() {
        JsonArrayBuilder attributesBuilder = Json.createArrayBuilder();
        for (String attribute : attributes) {
            attributesBuilder.add(attribute);
        }
        JsonObjectBuilder diningHallMatchesBuilder = Json.createObjectBuilder();
        JsonArrayBuilder diningHallMatchesArrayBuilder = Json.createArrayBuilder();
        for (String diningHallName : diningHallMatches.keySet()) {
            JsonObject diningHallMatch = diningHallMatches.get(diningHallName).toJson();
            diningHallMatchesBuilder.add(diningHallName, diningHallMatch);
            diningHallMatchesArrayBuilder.add(diningHallMatch);
        }
        return Json.createObjectBuilder()
                .add("name", name)
                .add("attributes", attributesBuilder)
                .add("diningHallMatches", diningHallMatchesBuilder)
                .add("diningHallMatchesArray", diningHallMatchesArrayBuilder)
                .build();
    }
}
