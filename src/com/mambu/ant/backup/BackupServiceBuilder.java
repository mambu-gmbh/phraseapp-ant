package com.mambu.ant.backup;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.mambu.ant.backup.local.LocalBackupService;

/**
 * Builder used to create backup services.
 *
 * @author fgavrilescu
 */
public final class BackupServiceBuilder {

	public enum BackupServiceProvider {
		LOCAL
	}

	private BaseBackupService theBackupService;

	public static BackupServiceBuilder create(BackupServiceProvider backupType) {

		return new BackupServiceBuilder(backupType);
	}

	private BackupServiceBuilder(BackupServiceProvider backupType) {

		theBackupService = newBackupService(backupType);
	}

	private BaseBackupService newBackupService(BackupServiceProvider backupServiceProvider) {

		switch (backupServiceProvider) {
			case LOCAL:
				return new LocalBackupService();
			default:
				throw new IllegalArgumentException(format("Unsupported backup provider: %s. Allowed values: %s",
						backupServiceProvider, asList(BackupServiceProvider.values())));
		}
	}

	public BackupServiceBuilder withProperty(String key, String value) {

		theBackupService.setProperty(key, value);
		return this;
	}

	public BackupService build() {

		return theBackupService;
	}

}
