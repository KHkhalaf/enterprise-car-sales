package com.anilaltunkan.security.service;

import java.util.ArrayList;
import java.util.List;

import com.anilaltunkan.security.AuthenticationTestApplication;
import com.anilaltunkan.security.Exceptions.ResourceNotFoundException;
import com.anilaltunkan.security.repository.CarRepository;
import com.anilaltunkan.security.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarService {

    @Autowired
    private CarRepository repo;

    public List<Car> listAllByStatus(boolean isAvailable){
        List<Car> _cars = new ArrayList<>();
        List<Car> cars;
        cars = repo.findAll();
        if(isAvailable){
            for (Car car : cars)
                if (car.getIsAvailable())
                    _cars.add(car);
        }
        else{
            for (Car car : cars)
                if (!car.getIsAvailable())
                    _cars.add(car);
        }
        return _cars;
    }

    public List<Car> listAll(){
        return repo.findAll();
    }

    public List<Car> search(String searchKey){

        List<Car> _cars = new ArrayList<>();
        List<Car> cars;System.out.println(searchKey);
        cars = repo.findAll();
            for (Car car : cars)
                if (car.getName().toUpperCase().contains(searchKey.toUpperCase()) && car.getIsAvailable())
                _cars.add(car);
        return _cars;

    }
    public void save(Car car) {

        repo.save(car);
    }

    public Car get(long id) throws ResourceNotFoundException {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + id));
    }

    public void delete(long id) {
        repo.deleteById(id);
    }

    public boolean isExist(long id){
        List<Car> cars = listAll();
        for(int i=0;i<cars.size();i++)
            if(id == cars.get(i).getId())
                return true;

        return false;
    }
}