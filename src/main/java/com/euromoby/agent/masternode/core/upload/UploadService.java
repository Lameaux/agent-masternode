package com.euromoby.agent.masternode.core.upload;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.euromoby.agent.http.HttpClientProvider;
import com.euromoby.agent.masternode.core.status.DatanodeStatus;
import com.euromoby.agent.masternode.core.status.DatanodeStatusService;
import com.euromoby.agent.model.UploadTicket;
import com.google.gson.Gson;

@Component
public class UploadService {

	private static final Logger log = LoggerFactory.getLogger(UploadService.class);

	private static final Gson gson = new Gson();

	@Autowired
	private DatanodeStatusService datanodeStatusService;

	@Autowired
	private HttpClientProvider httpClientProvider;

	public String getUploadUrl() throws Exception {

		DatanodeStatus datanodeStatus = datanodeStatusService.getFreeDatanode();

		if (datanodeStatus != null) {
			try {
				UploadTicket uploadTicket = getUploadTicket(datanodeStatus);
				String uploadTicketId = uploadTicket.getId();
				return "https://" + datanodeStatus.getIp() + ":18080/upload/" + uploadTicketId;
			} catch (Exception e) {
				log.error("Unable to get ticket", e);
				throw e;
			}
		}

		throw new Exception("No data nodes");
	}

	private UploadTicket getUploadTicket(DatanodeStatus datanodeStatus) throws IOException {

		RequestConfig.Builder requestConfigBuilder = httpClientProvider.createRequestConfigBuilder();

		String url = "https://" + datanodeStatus.getIp() + ":18080/upload/ticket";

		HttpUriRequest request = RequestBuilder.get(url).setConfig(requestConfigBuilder.build()).build();

		CloseableHttpResponse response = httpClientProvider.executeRequest(request);
		try {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				EntityUtils.consumeQuietly(response.getEntity());
				throw new IOException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
			}

			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consumeQuietly(entity);
			return gson.fromJson(json, UploadTicket.class);
		} finally {
			response.close();
		}

	}

}
