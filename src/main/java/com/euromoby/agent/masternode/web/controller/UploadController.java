package com.euromoby.agent.masternode.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.euromoby.agent.masternode.core.upload.UploadService;

@Controller
public class UploadController {

	@Autowired
	private UploadService uploadService;
	
    @RequestMapping("/upload")
    public String upload(ModelMap model) {
    	model.put("pageTitle", "Upload");
    	try {
    		String uploadUrl = uploadService.getUploadUrl();    	
    		model.put("uploadUrl", uploadUrl);
    	} catch (Exception e) {
    		model.put("error", "error");
    	}
    	
        return "upload";
    }	
	

	
}
