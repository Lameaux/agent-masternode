package com.euromoby.agent.masternode.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.euromoby.agent.model.PingRequest;

@Component
public class DatanodeStatusService {

	private ConcurrentHashMap<String, DatanodeStatus> status = new ConcurrentHashMap<>();
	private long updateTime = System.currentTimeMillis();

	public List<DatanodeStatus> getStatusSnapshot() {
		return new ArrayList<DatanodeStatus>(status.values());
	}
	
	public void updateStatus(String ip, PingRequest pingRequest) {

		DatanodeStatus datanodeStatus = new DatanodeStatus();
		datanodeStatus.setIp(ip);
		datanodeStatus.setCurrentTime(pingRequest.getCurrentTime());
		datanodeStatus.setFreeSpace(pingRequest.getFreeSpace());

		status.put(ip, datanodeStatus);
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public DatanodeStatus getFreeDatanode() {
		List<DatanodeStatus> snapshot = getStatusSnapshot();
		if (snapshot.isEmpty()) {
			return null;
		}
		return snapshot.get(0);
	}
	
}
