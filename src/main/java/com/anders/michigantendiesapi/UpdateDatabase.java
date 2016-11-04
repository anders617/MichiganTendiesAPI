/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anders.michigantendiesapi;

import java.sql.*;
import javax.json.*;
import java.io.*;

import com.heroku.sdk.jdbc.DatabaseUrl;

/**
 *
 * @author Anders
 */
public class UpdateDatabase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        MDiningAPI m = new MDiningAPI();
        m.requestDiningData();
        System.out.print(MDiningData.M_DINING_DATA.toJson().toString());
        Connection connection = null;
        try {
            connection = DatabaseUrl.extract().getConnection();
            System.out.println("Connected...");
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE dining_data");
            statement.executeUpdate("CREATE TABLE dining_data (id INTEGER, data TEXT)");
            statement.executeUpdate("INSERT INTO dining_data (id, data) VALUES (0, '" + 
                    MDiningData.M_DINING_DATA.toJson().toString().replaceAll("'", "''")
                    + "')");
            System.out.println("Executed statements...");
            ResultSet result = statement.executeQuery("SELECT * FROM dining_data");
            result.next();
            JsonReader reader
                    = Json.createReader(
                            new StringReader(
                                    result.getString("data")
                            )
                    );
            JsonObject j = reader.readObject();
            System.out.println(j);
            connection.close();
        } catch (Exception e) {
            System.err.println("THERE WAS AN ERROR");
            System.err.println(e);
        }
    }

}
