package com.mambu.ant.backup;

import java.util.HashMap;
import java.util.Map;

/**
 * A base backup service which provides some extra functionality around handling properties for the backup services, which are
 * hidden away by {@link BackupService} users.
 *
 * @author fgavrilescu
 */
public abstract class BaseBackupService implements BackupService {

	private Map<String, String> properties;

	protected BaseBackupService() {

		properties = new HashMap<>();
	}

	void setProperty(String key, String value) {

		properties.put(key, value);
	}

	protected String getProperty(String key) {

		return properties.get(key);
	}

}
