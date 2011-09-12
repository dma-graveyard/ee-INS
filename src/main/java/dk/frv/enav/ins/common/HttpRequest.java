package dk.frv.enav.ins.common;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import dk.frv.enav.ins.common.util.Compressor;
import dk.frv.enav.ins.services.shore.ShoreHttp;
import dk.frv.enav.ins.services.shore.ShoreServiceErrorCode;
import dk.frv.enav.ins.services.shore.ShoreServiceException;
import dk.frv.enav.ins.settings.EnavSettings;

public class HttpRequest extends Thread {
	
	private static final Logger LOG = Logger.getLogger(ShoreHttp.class);
	
	private static final String USER_AGENT = "ee-INS";
	
	private String uri;
	private String url;
	private String host;
	private int port = 80;
	private int readTimeout = 60000; // 60 sec
	private int connectionTimeout = 30000; // 30 sec
	
	private HttpClient httpClient;
	private GetMethod method;
	private byte[] responseBody;
	
	public HttpRequest(String uri, EnavSettings enavSettings) {
		this.host = enavSettings.getServerName();
		this.port = enavSettings.getHttpPort();
		this.connectionTimeout = enavSettings.getConnectTimeout();
		this.readTimeout = enavSettings.getReadTimeout();
		setUri(uri);
	}
	
	public void makeRequest() throws ShoreServiceException {
		int statusCode;
		try {
			statusCode = httpClient.executeMethod(method);
		} catch (HttpException e) {
			LOG.error("HTTP request failed with: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
		} catch (IOException e) {
			LOG.error("Failed to make HTTP connection: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
		}
		
		if (statusCode != 200) {
			method.releaseConnection();
			throw new ShoreServiceException(ShoreServiceErrorCode.SERVER_ERROR);
		}
		
		try {
			responseBody = method.getResponseBody();
			
			// Check for GZip content encoding
			Header contentEncoding = method.getResponseHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().toUpperCase().indexOf("GZIP") >= 0) {
				responseBody = Compressor.decompress(responseBody);
			}		
			LOG.debug("Received XML: " + new String(responseBody));
		} catch (IOException e) {
			LOG.error("Failed to read response body: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
		}
		
		method.releaseConnection();
	}
	
	public void init() {
		httpClient = new HttpClient();
		method = new GetMethod(url);
		HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
		params.setSoTimeout(readTimeout);
		params.setConnectionTimeout(connectionTimeout);
		method.setRequestHeader("User-Agent", USER_AGENT);
		method.setRequestHeader("Connection", "close");
		method.addRequestHeader("Accept", "text/*");	
		
		// TODO if compress response
		method.addRequestHeader("Accept-Encoding", "gzip");
	}
	
	public void setUri(String uri) {
		this.uri = uri;
		this.url = "http://" + host;
		if (port != 80) {
			this.url += ":" + port;
		}
		this.url += this.uri;
	}
	
	public byte[] getResponseBody() {
		return responseBody;
	}
	
	public String getUrl() {
		return url;
	}

}
