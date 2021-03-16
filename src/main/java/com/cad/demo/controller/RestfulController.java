package com.cad.demo.controller;

import com.cad.demo.entity.*;
import com.cad.demo.entity.vo.PathBetweenTwoNode;
import com.cad.demo.service.HugegraphService;
import com.cad.demo.service.RestfulService;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class RestfulController {

    @Autowired
    private RestfulService restfulService;


    @GetMapping(value = "/getAlledgelabel")
    public String FindAllPath(){
        return restfulService.getAllEdgeLabel();
    }

    @PostMapping(value = "/transver/path")
    public HugeGraphPath FindAllPath(@RequestBody FindPath findPath){
        String id1 = findPath.getSId();
        String id2 = findPath.getEId();
        int maxdepth = findPath.getMaxdepth();
        System.out.println("id1: "+id1);

        HugeGraphPath p = restfulService.getAllpath(id1,id2,maxdepth);
        return p;
    }

    @PostMapping(value = "/transver/neighbour")
    public HugegraphNeighbor Neighbour(@RequestBody KneighbourParams request) {
        HugegraphNeighbor p = restfulService.getNeighbour(request);
        return p;
    }


}
