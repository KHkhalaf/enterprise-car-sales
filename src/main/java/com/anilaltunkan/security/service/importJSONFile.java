package com.anilaltunkan.security.service;

import com.anilaltunkan.security.model.Car;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class importJSONFile {

    public ArrayList<Car> getDataFromJSONFile(){

        ArrayList<Car> cars = new ArrayList<>();
        try
        {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new FileReader("C:\\Users\\hp\\Desktop\\MOCK_DATA.json"));

            for (Object obj : array) {
                JSONObject car = (JSONObject) obj;

                Long id = (Long) car.get("id");
                String name = (String) car.get("name");
                Integer price = ((Long)car.get("price")).intValue();
                Integer numberOfSeats = ((Long)car.get("numberOfSeats")).intValue();
                Integer salePrice = ((Long)car.get("salePrice")).intValue();
                String saleDate = (String)car.get("saleDate");
                String buyerName = (String) car.get("buyerName");
                Boolean isAvailable = (Boolean) car.get("isAvailable");

                if(!isAvailable){
                    salePrice = 0;
                    saleDate = "";
                    buyerName = "";
                }
                Car _car = new Car(name, price, numberOfSeats, salePrice, saleDate, buyerName, isAvailable);
                _car.setId(id);
                _car.setVersion(0L);

                cars.add(_car);
            }
        }
        catch(FileNotFoundException ex){
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return cars;
    }
}
