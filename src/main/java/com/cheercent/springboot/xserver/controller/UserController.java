package com.cheercent.springboot.xserver.controller;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cheercent.springboot.xserver.base.XController;
import com.cheercent.springboot.xserver.base.XLogic;
import com.cheercent.springboot.xserver.logic.user.Info;
import com.cheercent.springboot.xserver.logic.user.Update;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "user")
public class UserController extends XController {


    @Bean
    private XLogic info() {
    	return this.newLogic(Info.class);
    }
	
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String getInfo(HttpServletRequest request) throws IOException {
        return executeLogic(Info.class, request);
    }
    
    @Bean
    private XLogic info2() {
    	return this.newLogic(Update.class);
    }

    @RequestMapping(value = "info2", method = RequestMethod.POST)
    public String getInfo2(HttpServletRequest request) throws IOException {
    	return executeLogic(Update.class, request);
    }
}
