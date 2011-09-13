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