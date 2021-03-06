package com.euromoby.agent.masternode.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.euromoby.agent.Constants;
import com.euromoby.agent.masternode.core.model.DatanodeStatusService;
import com.euromoby.agent.model.PingRequest;
import com.euromoby.agent.model.PingResponse;

@Controller
public class PingController {

	@Autowired
	private DatanodeStatusService datanodeStatusService;
	
	@RequestMapping(value = Constants.MASTER_URL_PING, method = RequestMethod.POST, consumes = "application/json; charset=utf-8", produces = "application/json; charset=utf-8")
	public @ResponseBody
	PingResponse ping(@RequestBody(required = true) PingRequest pingRequest, HttpServletRequest request) {
		
		String remoteAddr = request.getRemoteAddr();
		datanodeStatusService.updateStatus(remoteAddr, pingRequest);
		
		PingResponse response = new PingResponse();
		response.setUpdateTime(datanodeStatusService.getUpdateTime());
		return response;
	}

}
