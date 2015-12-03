package com.euromoby.agent.masternode.core.upload;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import com.euromoby.agent.Constants;
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

	public UploadedFile uploadFile(String fileName, String contentType, MultipartFile mpf) throws Exception {

		String fileId = idGeneratorService.generateId();
		String uploadUrl = getDataNodeUrl() + Constants.NODE_URL_UPLOAD + "/" + fileId;
		String getUrl = getDataNodeUrl() + Constants.NODE_URL_GET + "/" + fileId;		

		UploadedFile uploadedFile = new UploadedFile();
		uploadedFile.setId(fileId);
		uploadedFile.setContentType(contentType);
		uploadedFile.setFileName(fileName);
		uploadedFile.setUrl(getUrl);

		// do real upload
		uploadFile(uploadedFile, uploadUrl, mpf);

		return uploadedFile;
	}

	public String getDataNodeUrl() throws Exception {
		DatanodeStatus datanodeStatus = datanodeStatusService.getFreeDatanode();
		if (datanodeStatus != null) {
			return "https://" + datanodeStatus.getIp() + ":18080";
		}
		throw new Exception("No data nodes");
	}

	private void uploadFile(UploadedFile uploadedFile, String uploadUrl, MultipartFile mpf) throws IOException {

		RequestConfig.Builder requestConfigBuilder = httpClientProvider.createRequestConfigBuilder();

		HttpPost request = new HttpPost(uploadUrl);
		request.setConfig(requestConfigBuilder.build());

		InputStream mpfInputStream = mpf.getInputStream();
		try {
			HttpEntity requestMultipartEntity = MultipartEntityBuilder.create()
					.addBinaryBody("file", mpfInputStream, ContentType.parse(uploadedFile.getContentType()), uploadedFile.getFileName())
					.build();
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
				DatanodeFile datanodeFile = gson.fromJson(json, DatanodeFile.class);
				uploadedFile.setSize(datanodeFile.getSize());
			} finally {
				response.close();
			}
		} finally {
			IOUtils.closeQuietly(mpfInputStream);
		}

	}

}
