package com.plog.PLOG.controller;

import com.plog.PLOG.entity.Place;
import com.plog.PLOG.service.MyPlaceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Controller
public class controller2 {
    @GetMapping("/my-planner")
    public String planner(){
        return "planner";
    }

}

@RestController
@RequestMapping("/Place")
class PlaceRestController {

    @Autowired
    private MyPlaceService placeService;

    @PostMapping("/save")
    public Place savePlace(@RequestBody Place Place) {
        return placeService.savePlace(Place);
    }
}