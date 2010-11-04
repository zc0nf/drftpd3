/*
 * This file is part of DrFTPD, Distributed FTP Daemon.
 *
 * DrFTPD is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * DrFTPD is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DrFTPD; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.drftpd.plugins.sitebot.announce.archive;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.drftpd.plugins.archive.event.ArchiveFailedEvent;
import org.drftpd.plugins.archive.event.ArchiveStartEvent;
import org.drftpd.plugins.archive.event.ArchiveFinishEvent;
import org.drftpd.plugins.sitebot.AnnounceInterface;
import org.drftpd.plugins.sitebot.AnnounceWriter;
import org.drftpd.plugins.sitebot.OutputWriter;
import org.drftpd.plugins.sitebot.SiteBot;
import org.drftpd.plugins.sitebot.config.AnnounceConfig;
import org.drftpd.util.ReplacerUtils;
import org.tanesha.replacer.ReplacerEnvironment;

/**
 * @author CyBeR
 * @version $Id: ArchiveAnnouncer.java 2072 2010-09-18 22:01:23Z djb61 $
 */
public class ArchiveAnnouncer implements AnnounceInterface {

	private AnnounceConfig _config;

	private ResourceBundle _bundle;

	private String _keyPrefix;

	public void initialise(AnnounceConfig config, ResourceBundle bundle) {
		_config = config;
		_bundle = bundle;
		_keyPrefix = this.getClass().getName();
		// Subscribe to events
		AnnotationProcessor.process(this);
	}

	public void stop() {
		AnnotationProcessor.unprocess(this);
	}

	public String[] getEventTypes() {
		return new String[] { "archivestartevent","archivefinishevent","archivefailedevent" };
	}
	
	public void setResourceBundle(ResourceBundle bundle) {
		_bundle = bundle;
	}

    @EventSubscriber
	public void onArchiveStartEvent(ArchiveStartEvent event) {
		AnnounceWriter writer = _config.getPathWriter("archive", event.getArchiveType().getDirectory());
		if (writer != null) {
			ReplacerEnvironment env = new ReplacerEnvironment(SiteBot.GLOBAL_ENV);
            
			env.add("type", event.getArchiveType().getClass().getSimpleName());
			env.add("rls", event.getArchiveType().getDirectory().getName());
			env.add("files", event.getJobs().size());
			env.add("srcdir", event.getArchiveType().getDirectory().getParent().getPath());

			sayOutput(ReplacerUtils.jprintf(_keyPrefix+".start", env, _bundle), writer);
		}
	}
    
    @EventSubscriber
	public void onArchiveFinishEvent(ArchiveFinishEvent event) {
    	AnnounceWriter writer = _config.getPathWriter("archive", event.getArchiveType().getDirectory());
		if (writer != null) {
			ReplacerEnvironment env = new ReplacerEnvironment(SiteBot.GLOBAL_ENV);
            
			env.add("type", event.getArchiveType().getClass().getSimpleName());
			env.add("rls", event.getArchiveType().getDirectory().getName());
			env.add("time", event.getArchiveTime());
			env.add("srcdir", event.getArchiveType().getDirectory().getParent().getPath());
			
			if (event.getArchiveType().getDestinationDirectory().getPath() != null) {
				env.add("destdir", event.getArchiveType().getDestinationDirectory().getPath());
				sayOutput(ReplacerUtils.jprintf(_keyPrefix+".finish.move", env, _bundle), writer);
			} else {
				sayOutput(ReplacerUtils.jprintf(_keyPrefix+".finish", env, _bundle), writer);
			}
		}
    }
    
    @EventSubscriber
	public void onArchiveFailedEvent(ArchiveFailedEvent event) {
    	AnnounceWriter writer = _config.getPathWriter("archive", event.getArchiveType().getDirectory());
		if (writer != null) {
			ReplacerEnvironment env = new ReplacerEnvironment(SiteBot.GLOBAL_ENV);
            
			env.add("type", event.getArchiveType().getClass().getSimpleName());
			env.add("rls", event.getArchiveType().getDirectory().getName());
			env.add("time", event.getArchiveTime());
			env.add("srcdir", event.getArchiveType().getDirectory().getParent().getPath());
			env.add("reason", event.getFailReason());
			
			sayOutput(ReplacerUtils.jprintf(_keyPrefix+".failed", env, _bundle), writer);
		}
    }

	private void sayOutput(String output, AnnounceWriter writer) {
		StringTokenizer st = new StringTokenizer(output,"\n");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			for (OutputWriter oWriter : writer.getOutputWriters()) {
				oWriter.sendMessage(token);
			}
		}
	}
}
