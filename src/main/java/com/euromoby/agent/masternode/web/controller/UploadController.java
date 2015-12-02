package com.euromoby.agent.masternode.web.controller;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.euromoby.agent.masternode.core.model.UploadedFile;
import com.euromoby.agent.masternode.core.upload.UploadService;

@Controller
public class UploadController {

	@Autowired
	private UploadService uploadService;
	
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String uploadGet(ModelMap model) {
    	model.put("pageTitle", "Upload");
        return "upload";
    }	
	
    @RequestMapping(value="/upload", method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody UploadedFile uploadPost(MultipartHttpServletRequest request) throws Exception {
    	Iterator<String> fileNamesIterator = request.getFileNames();
    	MultipartFile mpf = request.getFile(fileNamesIterator.next());
    	return uploadService.uploadFile(mpf.getOriginalFilename(), mpf.getContentType(), mpf.getBytes());
    }
	
}
