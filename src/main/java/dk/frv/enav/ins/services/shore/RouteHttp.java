/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.services.shore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

/**
 * Encapsulation of HTTP connection to shore. 
 */
public class RouteHttp {

	private static final Logger LOG = Logger.getLogger(RouteHttp.class);

	private static final String USER_AGENT = "ee-INS";
	private static final String ENCODING = "UTF-8";

	private String uri;
	private String url;
	private String host;
	private int port = 80;
	private int readTimeout = 60000; // 60 sec
	private int connectionTimeout = 30000; // 30 sec

	private HttpClient httpClient;
	private PostMethod method;
	private String responseBody;
//	private byte[] responseBody;

	public RouteHttp() {
		this.host = "80.217.206.47";
//		this.host = "localhost";
		this.port = 8080;

		setUri(uri);
	}

	public void makeRequest() throws Exception {
		// Make the request
		int resCode = -1;
		try {
			System.out.println("Trying to connect to server");
			resCode = httpClient.executeMethod(method);
			System.out.println("Connected!");
		} catch (HttpException e) {
			LOG.error("Failed to make HTTP connection: " + e.getMessage());
			LOG.error("HTTP request failed with: " + e.getMessage());
//			throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
		} catch (IOException e) {
			LOG.error("Failed to make HTTP connection: " + e.getMessage());
			System.out.println("Failed: " + e.getMessage());
//			throw new ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
		}

		System.out.println(resCode);
		
		if (resCode != 200) {
			method.releaseConnection();
//			throw new ShoreServiceException(ShoreServiceErrorCode.SERVER_ERROR);
		}

		try {
			System.out.println("Message recieved:");
			responseBody = method.getResponseBodyAsString();
			System.out.println(responseBody);
		} catch (IOException e) {
			LOG.error("Failed to read response body: " + e.getMessage());
//			throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
		}

		method.releaseConnection();
	}

	public void init() {
		httpClient = new HttpClient();
		method = new PostMethod(url);
		HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
		params.setSoTimeout(readTimeout);
		params.setConnectionTimeout(connectionTimeout);
		method.setRequestHeader("User-Agent", USER_AGENT);
		method.setRequestHeader("Connection", "close");
		method.addRequestHeader("Accept", "text/*");	
		method.addRequestHeader("Content-Type", "text/xml");
		
		// TODO if compress response
//		method.addRequestHeader("Accept-Encoding", "gzip");
	}

	public void setRequestBody(String route) {
		try {
			method.setRequestEntity(new StringRequestEntity(route, null, null));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void setUri(String uri) {
		this.uri = uri;
		this.url = "http://" + host;
		if (port != 80) {
			this.url += ":" + port;
		}
//		this.url += this.uri;
	}


	public String getUri() {
		return uri;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getResponseBody() {
		return responseBody;
	}

}
