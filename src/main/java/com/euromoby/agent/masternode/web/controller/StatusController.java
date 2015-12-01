package com.euromoby.agent.masternode.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.euromoby.agent.masternode.core.model.DatanodeStatusService;

@Controller
public class StatusController {

	@Autowired
	private DatanodeStatusService datanodeStatusService;	
	
    @RequestMapping("/")
    public String status(ModelMap model) {
    	model.put("pageTitle", "Status");
    	model.put("snapshot", datanodeStatusService.getStatusSnapshot());
        return "status";
    }	
	
}
