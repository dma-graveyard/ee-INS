package dk.frv.enav.ins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class ExceptionHandler implements UncaughtExceptionHandler {
	
	private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOG.error("Uncaught exception from thread " + t.getName());
		LOG.error(e.getMessage());
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		LOG.error(result.toString());
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "An error has occured! Please contact administrator.", "Application error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

}
