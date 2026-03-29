package org.farhan.mbt.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SourceFileRepository {

	private final String BASEDIR;

	public SourceFileRepository() {
		BASEDIR = "target/src-gen/";
	}

	public SourceFileRepository(String baseDir) {
		BASEDIR = baseDir;
	}

	public void clear(String tags) {
		deleteDir(new File(BASEDIR));
	}

	public boolean contains(String tags, String path) {
		path = BASEDIR + "/" + path;
		return new File(path).exists();
	}

	public void delete(String tags, String path) {
		new File(BASEDIR + path).delete();
	}

	public void deleteDir(File aDir) {
		if (aDir.exists()) {
			for (String s : aDir.list()) {
				File f = new File(aDir.getAbsolutePath() + File.separator + s);
				if (f.isDirectory()) {
					deleteDir(f);
				}
				f.delete();
			}
		}
	}

	public String get(String tags, String path) throws Exception {
		path = BASEDIR + "/" + path;
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}

	public ArrayList<String> list(String tags, String path, String extension) {
		String root = BASEDIR;
		ArrayList<String> files = new ArrayList<String>();
		File aDir = new File(root + path);
		if (aDir.exists()) {
			for (String s : aDir.list()) {
				String aDirObjPath = (path + s);
				File aDirObj = new File(root + aDirObjPath);
				if (aDirObj.isDirectory()) {
					files.addAll(list("", aDirObjPath + "/", extension));
				} else if (aDirObjPath.toLowerCase().endsWith(extension.toLowerCase())) {
					files.add(aDirObjPath);
				}
			}
		}
		return files;
	}

	public void put(String tags, String path, String content) throws Exception {
		Path filePath = Paths.get(BASEDIR, path);
		Files.createDirectories(filePath.getParent());
		Files.writeString(filePath, content, StandardCharsets.UTF_8);
	}
}
