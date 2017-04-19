package com.xy.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ����BugReport����AST���������������ı���
 * 
 * @author hz
 *
 */
public class BR_BugItemGenerator {
	public final String TAG = BR_BugItemGenerator.class.getSimpleName();
//	public final String BUG_REPORT_FILE = "dataset/SWT.txt";
//	public final String BUG_REPORT_FILE = "dataset/JDT.txt";
	public final String BUG_REPORT_FILE = "dataset/AspectJ.txt";
//	public final String BUG_REPORT_FILE = "dataset/Eclipse_Platform_UI.txt";
//	public final String BUG_REPORT_FILE = "dataset/Tomcat.txt";



	public SourceCodeManager sourceCodeManager;

	public BR_BugItemGenerator() {
		sourceCodeManager = SourceCodeManager.getInstance();
	}

	/**
	 * 
	 * @param file
	 *            ��Ҫ������bug report
	 * @return �������bugItem �б�
	 */
	public List<BugItem> generateBugItemsFromFile(String file) {
		if (file == null || file.length() < 0) {
			return null;
		}

		List<BugItem> items = new ArrayList<BugItem>();

		BufferedReader br = null;
		try {
			LogUtils.log(TAG, "���Դ�BugReport�ļ���" + file);
			br = new BufferedReader(new FileReader(new File(file)));
			LogUtils.log(TAG, "��BugReport�ļ��ɹ�!");

			LogUtils.log(TAG, "��ʼ����BugReport�ļ�...");
			String line = null;
			while ((line = br.readLine()) != null) {
				// ������ͷ����������
				char firstChar = line.charAt(0);
				if (firstChar > '9' || firstChar < '0') {
					LogUtils.log(TAG, "Skip line��" + line);
					continue;
				}

				items.add(new BugItem(line));
			}
			LogUtils.log(TAG, "����BugReport�ļ����!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

	// ƥ���Ӧ�ļ�ӳ���ע����ø÷���֮ǰ��SouceManager��Ҫ�ȳ�ʼ���б�
	public void generateFileLabel(List<BugItem> items) {
		LogUtils.log(TAG, "��ʼ����BugReport��ǩ");
		for (BugItem bugItem : items) {
			List<String> files = bugItem.files;
			if (files != null && files.size() > 0) {
				bugItem.bugFileLable = sourceCodeManager.getBugReportLabel(files);
			}
		}
		LogUtils.log(TAG, "���ɱ�ǩ���");
	}
}
