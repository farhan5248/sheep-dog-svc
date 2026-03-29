package org.farhan.impl;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.farhan.dsl.grammar.IResourceRepository;

public class SourceFileRepositoryImpl implements IResourceRepository {

	private final String BASEDIR;

	public SourceFileRepositoryImpl() {
		BASEDIR = "target/src-gen/";
	}

	public SourceFileRepositoryImpl(String baseDir) {
		BASEDIR = baseDir;
	}

	@Override
	public void clear(String tags) {
		deleteDir(new File(BASEDIR));
	}

	@Override
	public boolean contains(String tags, String path) {
		path = BASEDIR + path;
		return new File(path).exists();
	}

	@Override
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

	@Override
	public String get(String tags, String path) throws Exception {
		path = BASEDIR + path;
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}

	@Override
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

	@Override
	public void put(String tags, String path, String content) throws Exception {
		path = BASEDIR + path;
		new File(path).getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8);
		pw.write(content);
		pw.flush();
		pw.close();
	}
}
