package org.farhan.dsl.asciidoc.impl;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.farhan.dsl.grammar.IResourceRepository;

public class VsCodeFileRepository implements IResourceRepository {

	@Override
	public void clear(String tags) {
		throw new UnsupportedOperationException("clear(String tags) is not implemented");
	}

	@Override
	public boolean contains(String tags, String path) {
		path = path.replace("/", File.separator);
		return new File(path).exists();
	}

	@Override
	public void delete(String tags, String path) {
		path = path.replace("/", File.separator);
		new File(path).delete();
	}

	@Override
	public String get(String tags, String path) throws Exception {
		path = path.replace("/", File.separator);
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}

	@Override
	public ArrayList<String> list(String tags, String subDir, String extension) {
		ArrayList<String> files = new ArrayList<String>();
		File aDir = new File(subDir.replace("/", File.separator));
		if (aDir.exists()) {
			for (String f : aDir.list()) {
				String aDirObjPath = subDir.replace("/", File.separator) + File.separator + f;
				if ((new File(aDirObjPath)).isDirectory()) {
					files.addAll(list("", aDirObjPath, extension));
				} else if (aDirObjPath.toLowerCase().endsWith(extension.toLowerCase())) {
					files.add(aDirObjPath.replace(File.separator, "/"));
				}
			}
		}
		return files;
	}

	@Override
	public void put(String tags, String path, String content) throws Exception {
		path = path.replace("/", File.separator);
		new File(path).getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8);
		pw.write(content);
		pw.flush();
		pw.close();
	}
}
