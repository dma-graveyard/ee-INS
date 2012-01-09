/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.services.shore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import dk.frv.enav.ins.common.util.Compressor;
import dk.frv.enav.ins.settings.EnavSettings;

/**
 * Encapsulation of HTTP connection to shore. 
 */
public class ShoreHttp {

	private static final Logger LOG = Logger.getLogger(ShoreHttp.class);

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
	private byte[] responseBody;

	public ShoreHttp() {

	}

	public ShoreHttp(String uri, EnavSettings enavSettings) {
		this();
		this.host = enavSettings.getServerName();
		this.port = enavSettings.getHttpPort();
		this.connectionTimeout = enavSettings.getConnectTimeout();
		this.readTimeout = enavSettings.getReadTimeout();
		setUri(uri);
	}

	public void makeRequest() throws ShoreServiceException {
		// Make the request
		int resCode = -1;
		try {
			resCode = httpClient.executeMethod(method);
		} catch (HttpException e) {
			LOG.error("HTTP request failed with: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
		} catch (IOException e) {
			LOG.error("Failed to make HTTP connection: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.NO_CONNECTION_TO_SERVER);
		}

		if (resCode != 200) {
			method.releaseConnection();
			throw new ShoreServiceException(ShoreServiceErrorCode.SERVER_ERROR);
		}

		try {
			responseBody = method.getResponseBody();
			int rawResSize = responseBody.length;

			// Check for GZip content encoding
			Header contentEncoding = method.getResponseHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().toUpperCase().indexOf("GZIP") >= 0) {
				responseBody = Compressor.decompress(responseBody);
			}		
			LOG.debug("Received XML: " + new String(responseBody));
			LOG.debug("Received XML size    : " + responseBody.length);
			LOG.debug("Received raw XML size: " + rawResSize);
		} catch (IOException e) {
			LOG.error("Failed to read response body: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
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
		
		// TODO if compress response
		method.addRequestHeader("Accept-Encoding", "gzip");
	}

	public Object getXmlUnmarshalledContent(String contextPath) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(contextPath);
		Unmarshaller u = jc.createUnmarshaller();
		return u.unmarshal(new ByteArrayInputStream(responseBody));
	}

	public void setXmlMarshalContent(String contextPath, Object obj) throws JAXBException, UnsupportedEncodingException {
		JAXBContext jc = JAXBContext.newInstance(contextPath);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
		StringWriter sw = new StringWriter();		
		m.marshal(obj, sw);
		String req = sw.toString();
		LOG.debug("XML request: " + req);
		setRequestBody(sw.toString().getBytes(ENCODING), ENCODING);
	}

	public void setRequestBody(byte[] body, String contentType) {
		// TODO if Gzip Compress request
		byte[] compressed = {};
		try {			
			compressed = Compressor.compress(body);						
			//body = compressed;
			//method.addRequestHeader("Content-Encoding", "gzip");
		} catch (IOException e) {
			LOG.error("Failed to GZip request: " + e.getMessage());
		}		
		LOG.debug("XML req size           : " + body.length);
		LOG.debug("XML req compressed size: " + compressed.length);
		ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(body, contentType);
		method.setRequestEntity(requestEntity);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
		this.url = "http://" + host;
		if (port != 80) {
			this.url += ":" + port;
		}
		this.url += this.uri;
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

}
