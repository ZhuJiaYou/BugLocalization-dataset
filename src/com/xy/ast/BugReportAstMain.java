package com.xy.ast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BugReportAstMain {
	private final static String TAG = BugReportAstMain.class.getSimpleName();

	public static void main(String[] args) throws Exception {
		// read files
		LogUtils.setOpen(true);

		BufferedWriter bw = null;

		// obtain BugItem
		BR_BugItemGenerator generator = new BR_BugItemGenerator();
		List<BugItem> items = generator.generateBugItemsFromFile(generator.BUG_REPORT_FILE);

		// initialize SourceManager by BugItemList
		LogUtils.log(TAG, "generate all buggy files �����������е�bug�ļ�...");

		SourceCodeManager sourceCodeManager = SourceCodeManager.getInstance();
		sourceCodeManager.initSourFileContext(items);

		// �����Ҫ��github����ȡ�����汾��Դ���룬��򿪿��� if the source code of different versions need to be pulled from github, open this switch
		 if (SourceCodeManager.GIT_CHECKOUT) {
			 sourceCodeManager.gitAllSourceCodeFile(items);
			 return;
		 }

		ArrayList<String> bugFiles = new ArrayList<String>();
		for (BugItem item : items) {
			List<String> files = sourceCodeManager.getLocalSourceFile(item);

			if (files == null) {
				continue;
			}

			for (String filePath : files) {
				File file = new File(filePath);
				if (!file.exists()) {
					LogUtils.log("BugReportAstMain", item.id + " " + item.commit + " " + file.getAbsolutePath());
				} else {
					if(!bugFiles.contains(file.getAbsolutePath())){
						bugFiles.add(file.getAbsolutePath());
					}
				}
			}
		}

		sourceCodeManager.setSourceFiles(bugFiles);
		// sourceCodeManager.initAllSourceFile();

		// ����sourceManager���ɶ�Ӧ��bugLabel bugLabel is generated according to sourceManager
		generator.generateFileLabel(items);

		// ��bugItemд�뵽�ı��С� bugItem is written into txt files
		try {
			bw = new BufferedWriter(new FileWriter(new File(SourceCodeManager.LOCAL_FILE_PREFIX + "bugItem.txt")));

			LogUtils.log(TAG, "�� BugItemд�뵽�ļ� write bugItem into txt files");
			for (BugItem item : items) {
				if (item != null && item.bugFileLable != null && item.bugFileLable.length() > 0) {
					if (item.description != null && item.description.length() > 0) {
						bw.write(item.description);
					} else {
						bw.write(item.summary);
					}
					bw.write("$|$");
					bw.write(item.bugFileLable);
					bw.write("$#$");
					bw.write(item.reportTimes);
					bw.write("\r\n");
				}else{
					LogUtils.log("BugReportAstMain", item.commit + ", " + item.id);
				}
			}
			LogUtils.log(TAG, "BugItemд���ļ��ɹ� bugItem written successifully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// ��ȡԴ����AST Map 
		sourceCodeManager.saveSourceFileList();
		Map<String, String> sourceCodeAstMap = sourceCodeManager.generateSourceCodeAstMap();
		try {
			bw = new BufferedWriter(new FileWriter(new File(SourceCodeManager.LOCAL_FILE_PREFIX + "sourceCodeAst.txt")));

			LogUtils.log(TAG, "��SouceCode AST��д�뵽�ļ�");
			Set<Map.Entry<String, String>> entrySet = sourceCodeAstMap.entrySet();
			for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Map.Entry<String, String> entry = iterator.next();
				bw.write(entry.getKey());
				bw.write("$*$");
				bw.write(entry.getValue());
				bw.write("\r\n");
			}
			LogUtils.log(TAG, "��SouceCode AST��д���ļ��ɹ�");

			bw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		LogUtils.log(TAG, "����ɹ���� !!! enjoy^.^");
	}
}
