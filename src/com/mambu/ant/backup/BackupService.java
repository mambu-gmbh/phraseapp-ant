package com.mambu.ant.backup;

import java.io.File;
import java.util.List;

/**
 * Service used to backup files to an external storage (AWS, local etc.)
 *
 * @author fgavrilescu
 */
public interface BackupService {

	/**
	 * Backup the provided files list.
	 *
	 * @param files list of files to be backed up
	 */
	void backup(List<File> files) throws BackupException;

}
