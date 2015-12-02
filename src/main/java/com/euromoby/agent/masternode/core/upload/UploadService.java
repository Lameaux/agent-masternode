package com.euromoby.agent.masternode.core.upload;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.euromoby.agent.http.HttpClientProvider;
import com.euromoby.agent.masternode.core.model.DatanodeStatus;
import com.euromoby.agent.masternode.core.model.DatanodeStatusService;
import com.euromoby.agent.masternode.core.model.UploadedFile;
import com.euromoby.agent.model.DatanodeFile;
import com.google.gson.Gson;

@Component
public class UploadService {

	private static final Logger log = LoggerFactory.getLogger(UploadService.class);

	private static final Gson gson = new Gson();

	@Autowired
	private DatanodeStatusService datanodeStatusService;

	@Autowired
	private HttpClientProvider httpClientProvider;

	@Autowired
	private IdGeneratorService idGeneratorService;

	public UploadedFile uploadFile(String fileName, String contentType, byte[] content) throws Exception {

		String fileId = idGeneratorService.generateId();
		String uploadUrl = getUploadUrl(fileId); 

		UploadedFile uploadedFile = new UploadedFile();
		uploadedFile.setId(fileId);
		uploadedFile.setContentType(contentType);
		uploadedFile.setFileName(fileName);
		uploadedFile.setSize(content.length);
		uploadedFile.setUrl(uploadUrl);		
		
		// do real upload
		DatanodeFile datanodeFile = uploadFile(uploadedFile, content);

		return uploadedFile;
	}

	public String getUploadUrl(String fileId) throws Exception {
		DatanodeStatus datanodeStatus = datanodeStatusService.getFreeDatanode();
		if (datanodeStatus != null) {
			return "https://" + datanodeStatus.getIp() + ":18080/upload/" + fileId;
		}
		throw new Exception("No data nodes");
	}

	private DatanodeFile uploadFile(UploadedFile uploadedFile, byte[] content) throws IOException {

		RequestConfig.Builder requestConfigBuilder = httpClientProvider.createRequestConfigBuilder();

		HttpPost request = new HttpPost(uploadedFile.getUrl());
		request.setConfig(requestConfigBuilder.build());

		HttpEntity requestMultipartEntity = MultipartEntityBuilder.create()
				.addBinaryBody("file", content, ContentType.parse(uploadedFile.getContentType()), uploadedFile.getFileName()).build();
		request.setEntity(requestMultipartEntity);		
		
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
			return gson.fromJson(json, DatanodeFile.class);
		} finally {
			response.close();
		}

	}

}
