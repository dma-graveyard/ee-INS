package dk.frv.enav.ins.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

public class Compressor {
	
	private static final Logger LOG = Logger.getLogger(Compressor.class);

	public static byte[] compress(byte[] content) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		gzipOutputStream.write(content);
		gzipOutputStream.close();
		LOG.debug(String.format("Compressed ratio: %.2f percent\n", (double)content.length / (double)byteArrayOutputStream.size()));
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] decompress(byte[] contentBytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(contentBytes));
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] res = out.toByteArray();
		LOG.debug(String.format("Decompress rate: %.2f\n", ((double)res.length) / (double)contentBytes.length));
		return res;
	}

}
