package dk.frv.enav.ins.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

public class OneInstanceGuard {
	
	private static final Logger LOG = Logger.getLogger(OneInstanceGuard.class);

	private File lockFile;
	private FileChannel channel;
	private FileLock lock;
	private boolean alreadyRunning = false;

	public OneInstanceGuard(String lockFileName) {
		lockFile = new File(lockFileName);
		if (lockFile.exists()) {
			lockFile.delete();
		}
		try {
			channel = new RandomAccessFile(lockFile, "rw").getChannel();
		} catch (FileNotFoundException e) {
			// Not running
			LOG.info("File not found: " + e);
			return;
		}
		try {
			lock = channel.tryLock();
			if (lock == null) {
				// File is lock by other application
				channel.close();
				throw new IOException("Instance already active");
			}
		} catch (IOException e) {
			// Running
			LOG.info("Instance already running");
			alreadyRunning = true;
			return;
		}
		ShutdownHook shutdownHook = new ShutdownHook(this);
		Runtime.getRuntime().addShutdownHook(shutdownHook);

	}

	public void unlockFile() {
		// release and delete file lock
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				lockFile.delete();
			}
		} catch (IOException e) {
			LOG.error("Failed to unlock lock file");
		}
	}

	public boolean isAlreadyRunning() {
		return alreadyRunning;
	}

	static class ShutdownHook extends Thread {

		private OneInstanceGuard guard;

		public ShutdownHook(OneInstanceGuard guard) {
			setDaemon(true);
			this.guard = guard;
		}

		public void run() {
			guard.unlockFile();
		}
	}

}