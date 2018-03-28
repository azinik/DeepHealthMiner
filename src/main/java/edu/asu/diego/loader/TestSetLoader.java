//package edu.asu.diego.loader;
//
//import rainbownlp.core.Setting;
//import rainbownlp.util.FileUtil;
//
//public class TestSetLoader {
//	
//    static boolean forTrain = false;
//	
//	public static void main(String[] args) throws Exception
//	{
//		//instead of separet text files, you can use a text file and list each document in one line
////		String test_text_file = args[0];
//		String text_files_folder = args[0];
//
//		Setting.TrainingMode = false;
//		FileUtil.logLine(null,"loading documents ...");
//		
//		DAnalyzer doc_proc = new DAnalyzer();
//		doc_proc.processDocuments(text_files_folder);
//		
//		//if you are using a single flat file
////		doc_proc.loadDocumentsFromFlatFile(test_text_file, "\\t");
//		
//////		FileUtil.logLine(null,"Parsing sentences ...");
//////		Parser.parseSentences(forTrain);
////		
////		FileUtil.logLine(null,"Building machine learning candidates ...");
////		TokenSequenceExampleBuilder.createTokenSequenceExamples(forTrain);
//
//	}
//	
//	
//
//public static String separateTagsInContent(String content)
//{
//	String new_content = content.replaceAll("@([^\\s])", "@ $1").replaceAll("#([^\\s])", "# $1");
//	return new_content;
//}
//}
