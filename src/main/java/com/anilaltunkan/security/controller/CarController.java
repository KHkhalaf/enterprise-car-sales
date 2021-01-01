package com.anilaltunkan.security.controller;

import com.anilaltunkan.security.Exceptions.ResourceNotFoundException;
import com.anilaltunkan.security.batchMQ.sender;
import com.anilaltunkan.security.dto.InventoryOrder;
import com.anilaltunkan.security.dto.ParametersDTO;
import com.anilaltunkan.security.dto.SearchDTO;
import com.anilaltunkan.security.model.Car;
import com.anilaltunkan.security.model.Parameters;
import com.anilaltunkan.security.service.CarService;
import com.anilaltunkan.security.service.ParametersService;
import com.anilaltunkan.security.service.loggingService;
import com.anilaltunkan.security.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/car")
public class CarController {

    @Autowired
    private loggingService loggService;

    @Autowired
    private CarService carService;
    @Autowired
    private ParametersService parametersService;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    public CarController() {
    }

    @RequestMapping("/")
    public String showIndex(){
        ServiceInstance instance = loadBalancerClient.choose("authentication-test");
        System.out.println(instance.getHost()+"         "+instance.getPort());
        return "Shared/index";
    }

    @RequestMapping("/listForSale")
    public String  viewCarsListForSalePage(Model model) {

        List<Car> Cars = carService.listAllByStatus(true);

        if(Cars.size() == 0)
            return "Shared/NoContent";
        else
            model.addAttribute("Cars", Cars);

        SearchDTO searchDto = new SearchDTO();
        model.addAttribute("searchDto",searchDto);
        return "CarViews/List_Cars_Available";
    }

    @RequestMapping("/listNotForSale")
    public String  viewCarsListNotForSalePage(Model model) {
        List<Car> Cars = carService.listAllByStatus(false);

        if(Cars.size() == 0)
            return "Shared/NoContent";
        else
            model.addAttribute("Cars", Cars);

        return "CarViews/List_Cars_UnAvailable";
    }

    @RequestMapping("/details/{id}")
    public String getCarById(@PathVariable(name = "id")int id, Model model) throws ResourceNotFoundException{
        Car car = carService.get(id);
        model.addAttribute("car",car);

        loggService.writeLogging("Get details car by id="+id);
        return "CarViews/details";
    }
    @RequestMapping("/create")
    public String showNewCarPage(Model model) {
        Car car = new Car();
        model.addAttribute("car",car);

        return "CarViews/create";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String AddCar(@ModelAttribute("car") Car car) {

        car.setIsAvailable(true);
        if(car.getNumberOfSeats() == null || car.getNumberOfSeats() == 0)
            car.setNumberOfSeats(parametersService.GetNumberOfSeats());
        else
            car.setNumberOfSeats(car.getNumberOfSeats());
        carService.save(car);

        loggService.writeLogging("Add new car , id="+car.getId());

        return "redirect:/car/listForSale";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditCarPage( @PathVariable(name = "id")int id) throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("CarViews/edit");
        Car car = carService.get(id);
        mav.addObject("car", car);

        return mav;
    }

    @RequestMapping(value = "/editCar", method = RequestMethod.POST)
    public String updateCar(@ModelAttribute("car") Car car) throws ResourceNotFoundException {

        Car _car = carService.get(car.getId());
        if(car.getNumberOfSeats()!=null)
            if(car.getNumberOfSeats() == 0)
                _car.setNumberOfSeats(parametersService.GetNumberOfSeats());
            else
                _car.setNumberOfSeats(car.getNumberOfSeats());

        _car.setPrice(car.getPrice());
        _car.setName(car.getName());

        carService.save(_car);

        loggService.writeLogging("Edit a car by id="+car.getId());

        return "redirect:/car/listForSale";
    }

    @RequestMapping("/buy/{id}")
    public ModelAndView buyCarPage( @PathVariable(name = "id")int id) throws ResourceNotFoundException, ParseException {
        ModelAndView mav = new ModelAndView("CarViews/buy");
        Car car = carService.get(id);
        mav.addObject("car", car);

        return mav;
    }

    @RequestMapping(value = "/saveBuyCar", method = RequestMethod.POST)
    public String saveBuyCar(@ModelAttribute("car") Car car, Model model) throws ResourceNotFoundException {

        System.out.println(car.getSaleDate());
        Long carId = car.getId();
        // Start Transaction
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Car _car = session.get(Car.class, carId);
            if(_car != null){
                tx = session.beginTransaction();

                Integer finalSalePrice = Math.round(car.getPrice() +
                                         (car.getPrice() * parametersService.GetProfitRatio()));
                _car.setSalePrice(finalSalePrice);
                _car.setSaleDate(car.getSaleDate());
                _car.setBuyerName(car.getBuyerName());
                _car.setIsAvailable(false);

                session.update(_car);

                loggService.writeLogging("buy a car , id="+carId);

                tx.commit();

            }else{
                System.out.println("Car details not found with ID: "+ carId);

                model.addAttribute("message","Unfortunately !!! Car was updated or deleted by another User ");
                return "Shared/error";
            }
        }catch(Exception e){
            e.printStackTrace();
            if(tx != null){
                tx.rollback();
            }
        }
        // End Transaction
        SearchDTO searchDto = new SearchDTO();
        model.addAttribute("searchDto",searchDto);

        return "redirect:/car/listForSale";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchCarByName(@ModelAttribute("searchDto") SearchDTO searchDto) {

        List<Car> Cars = carService.search(searchDto.getSearchKey());
        ModelAndView mav = new ModelAndView("CarViews/List_Cars_Available");

        if(Cars.size() == 0)
            mav.setViewName("Shared/NoContent");
        else
            mav.addObject("Cars", Cars);
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteCar(@PathVariable(name = "id") int id) {
        carService.delete(id);

        loggService.writeLogging("delete a car by id="+id);
        return "redirect:/car/listForSale";
    }
    @RequestMapping("/addInventoryOrder")
    public ModelAndView addInventoryOrder() throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("CarViews/inventoryOrder");
        InventoryOrder inventoryOrder = new InventoryOrder();
        mav.addObject("inventoryOrder",inventoryOrder);
        return mav;
    }

    @RequestMapping(value = "/saveInventoryOrder", method = RequestMethod.POST)
    public String addInventoryOrder(@ModelAttribute("inventoryOrder") InventoryOrder inventoryOrder) throws ResourceNotFoundException {
        sender _sender = new sender();

        _sender.send(inventoryOrder.getContent(), inventoryOrder.getEmail(), inventoryOrder.getDate());

        return "redirect:/car/listForSale";
    }

    @RequestMapping("/updateParameters")
    public ModelAndView updateParametersView(){
        ModelAndView mav = new ModelAndView("CarViews/updateParameters");
        ParametersDTO parametersDto = new ParametersDTO();
        mav.addObject("parametersDto",parametersDto);
        return mav;
    }

    @RequestMapping("/saveParameters")
    public String saveParameters(@ModelAttribute("parametersDto") ParametersDTO parametersDto){

        Parameters parameters = new Parameters();
        parameters.setId(1);
        parameters.setNumberOfSeats(parametersDto.getNumberOfSeats());
        parameters.setProfitRatio(Float.parseFloat(parametersDto.getProfitRatio()));
        parametersService.saveParameters(parameters);

        loggService.writeLogging("update parameters , NumberOfSeats="+parameters.getNumberOfSeats()
                                    +" , ProfitRatio="+parameters.getProfitRatio());

        return "redirect:/car/listForSale";
    }
}