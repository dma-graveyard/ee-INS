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
		LOG.debug("XML request: " + sw.toString());
		setRequestBody(sw.toString().getBytes(ENCODING), ENCODING);
	}

	public void setRequestBody(byte[] body, String contentType) {
		// TODO if Gzip Compress request
//		try {			
//			byte[] compressed = Compressor.compress(body);						
//			body = compressed;
//			method.addRequestHeader("Content-Encoding", "gzip");
//		} catch (IOException e) {
//			LOG.error("Failed to GZip request: " + e.getMessage());
//		}
		
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
