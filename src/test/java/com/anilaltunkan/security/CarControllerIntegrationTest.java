package com.anilaltunkan.security;

import com.anilaltunkan.security.Exceptions.ResourceNotFoundException;
import com.anilaltunkan.security.dto.LoginRequest;
import com.anilaltunkan.security.model.Car;
import com.anilaltunkan.security.service.CarService;
import com.anilaltunkan.security.service.importJSONFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarService carService;

    public ArrayList<Car> cars = new importJSONFile().getDataFromJSONFile();

    @Test
    public void testGetAllCars() throws Exception {

        this.mockMvc
                .perform(get("/car/listForSale"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("CarViews/List_Cars_Available"))
                .andExpect(model().attributeExists("searchDto"))
                .andExpect(model().attributeExists("Cars"))
                .andDo(print());
    }

    @Test
    public void testGetCarById() throws Exception {
        Long id = 1L;
        this.mockMvc
                .perform(get("/car/details/"+id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("CarViews/details"))
                .andExpect(model().attributeExists("car"))
                .andDo(print());
    }

    @Test
    public void testAddCar() throws Exception {
        for(int i=0;i<cars.size();i++) {
            Car car = cars.get(i);

            RequestBuilder request = post("/car/save")
                    .param("name", car.getName())
                    .param("price", car.getPrice().toString())
                    .param("numberOfSeats", car.getNumberOfSeats().toString())
                    .param("version", car.getVersion().toString())
                    .param("saleDate", car.getSaleDate())
                    .param("salePrice", car.getSalePrice().toString())
                    .param("buyerName", car.getBuyerName())
                    .param("isAvailable", car.getIsAvailable().toString());

            mockMvc
                    .perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/car/listForSale"));

            String carName = carService.listAll().get(carService.listAll().size() - 1).getName();

            Assert.assertEquals(carName, car.getName());
        }
        Assert.assertEquals(carService.listAll().size(), cars.size());
    }

    @Test
    public void testEditCarById() throws Exception {

        Car car = cars.get(0);
        car.setName("new name");
        car.setId(car.getId());

        RequestBuilder request = post("/car/editCar")
                .param("id", car.getId().toString())
                .param("name", car.getName())
                .param("price", car.getPrice().toString())
                .param("numberOfSeats",car.getNumberOfSeats().toString())
                .param("version",car.getVersion().toString());

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listForSale"));

        String carName = carService.get(1).getName();

        Assert.assertEquals(carName, car.getName());
    }

    @Test
    public void testDeleteCarById() throws Exception {

        Car car = cars.get(0);
        long id = car.getId();
        cars.remove(id-1);
        this.mockMvc
                .perform(delete("/car/delete/"+id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listForSale"))
                .andDo(print());

        Assert.assertEquals(carService.isExist(1), cars.contains(car));
    }

    @Test
    public void testBuyCar() throws Exception {
        Car car = cars.get(1);
        car.setId(2L);
        car.setBuyerName("feras");
        car.setSalePrice(250);
        car.setSaleDate("2008-10-12");


        RequestBuilder request = post("/car/saveBuyCar")
                .param("id", car.getId().toString())
                .param("buyerName", car.getBuyerName())
                .param("saleDate", car.getSaleDate())
                .param("salePrice", car.getSalePrice().toString());

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(redirectedUrl("/car/listForSale"));

        Assert.assertFalse(carService.get(2).getIsAvailable());
    }

}
