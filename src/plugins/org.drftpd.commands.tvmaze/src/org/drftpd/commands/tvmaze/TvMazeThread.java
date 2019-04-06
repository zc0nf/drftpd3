/*
 * This file is part of DrFTPD, Distributed FTP Daemon.
 *
 * DrFTPD is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrFTPD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DrFTPD; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.drftpd.commands.tvmaze;

import org.apache.log4j.Logger;
import org.drftpd.vfs.DirectoryHandle;

/**
 * @author scitz0
 */
public class TvMazeThread extends Thread {

	private static final Logger logger = Logger.getLogger(TvMaze.class);

	public TvMazeThread() {
		setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				// Process one item in queue
				DirectoryHandle dir = TvMazeConfig.getInstance().getDirToProcess();
				if (dir != null) {
					logger.debug("Fetching TvMaze data for " + dir.getPath());
					TvMazeUtils.getTvMazeInfo(dir);
				}
				Thread.sleep(TvMazeUtils.randomNumber());
			} catch (InterruptedException ie) {
				logger.info("TvMazeThread interrupted, thread closing");
				break;
			}
		}
	}
}