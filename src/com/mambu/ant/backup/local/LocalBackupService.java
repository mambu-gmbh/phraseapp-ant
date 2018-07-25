package com.mambu.ant.backup.local;

import static com.mambu.ant.backup.Constants.DESTINATION_DIR;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.mambu.ant.backup.BackupException;
import com.mambu.ant.backup.BaseBackupService;
import com.mambu.ant.backup.Constants;

/**
 * Stores the files locally, on the current machine.
 */
public class LocalBackupService extends BaseBackupService {

	private static final String BACKUP_NAME_FORMAT = "%stranslations_%s.zip";

	/**
	 * Crates an archive to local drive containing the provided files.
	 *
	 * @param files the files to backup
	 */
	@Override
	public void backup(List<File> files) throws BackupException {

		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(createBackupName()))) {
			zipFiles(zipOut, files);
		} catch (IOException e) {
			throw new BackupException(e);
		}

	}

	private String createBackupName() {

		String currentDate = BASIC_ISO_DATE.format(LocalDate.now());
		String destinationPath = getProperty(Constants.DESTINATION_DIR);
		if (isBlank(destinationPath)) {
			destinationPath = "";
		} else if (!destinationPath.endsWith(File.separator)) {
			destinationPath += File.separator;
		}
		return String.format(BACKUP_NAME_FORMAT, destinationPath, currentDate);
	}

	private void zipFiles(ZipOutputStream zipOut, List<File> filesToZip) throws IOException {

		for (File file : filesToZip) {
			addFileEntry(zipOut, file);
			addFileContent(zipOut, file);
		}
	}

	private void addFileEntry(ZipOutputStream zipOut, File fileToZip) throws IOException {

		ZipEntry zipEntry = new ZipEntry(getRelativePath(fileToZip));
		zipOut.putNextEntry(zipEntry);
	}

	private void addFileContent(ZipOutputStream zipOut, File fileToZip) throws IOException {

		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		}
	}

	private String getRelativePath(File file) {

		String sourceDir = getProperty(DESTINATION_DIR);
		String filePath = file.getPath().substring(sourceDir.length());
		if (filePath.startsWith(File.separator)) {
			filePath = filePath.substring(1);
		}
		return filePath;
	}

}
