package com.mambu.ant.backup;

/**
 * Exception thrown when backup cannot be executed. It is checked because the backup could be a sensitive operation where an
 * action could be taken (like retry or fallback to a different backup provider).
 *
 * @author fgavrilescu
 */
public class BackupException extends Exception {

	public BackupException(Exception cause) {

		super(cause);
	}

}
