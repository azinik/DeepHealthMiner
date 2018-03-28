package edu.asu.diego.dhmevaluation.deep;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import rainbownlp.util.FileUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class deepLUtils {
	static boolean  DoStemming =true;
	
	public static void main(String args[]) throws Exception
	{	
//		writeIOBFromTextSentences("/home/azadeh/Downloads/twitter_PSB_testset.tsv",
//				"/tmp/test.txt",false,false);
	}
//	public static void writeIOBFromTextSentences(String input_test_sents_file, String filePath,
//			boolean forTrain,boolean replaceUnknown) 
//		throws IOException
//	{
//		FileWriter file_writer = new FileWriter(filePath);		
//		List<String> input_lines = FileUtil.loadLineByLine(input_test_sents_file);
//		
//		for (String test_line: input_lines)
//		{
//			String content = test_line.split("\\t")[1];
//			content = content.replaceAll("@USERNAME", "@ username");
//			content = content.replaceAll("#", "# ").replaceAll(" +", " ");
//			Preprocess pre_processed_sent = new Preprocess(content);
//			
//			HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();
//			for (Integer sent:setences.keySet())
//			{
//
//				List<HasWord> tokens = pre_processed_sent.getSentTokensMap().get(sent);
//				
//				for(int token_index = 0; token_index< tokens.size();token_index++){
//					
//					String token_content = tokens.get(token_index).toString();
//					String deep_nl_line = "";
//					if (DoStemming)
//					{
//						token_content  = rainbownlp.util.StringUtil.getTermByTermWordnet(token_content).toLowerCase();
//					}
////					if (replaceUnknown)
////					{
////						token_content =Word2VecManager.replaceUnknownWords(token_content, vocabs);
////					}
////					if (forTrain)
////					{
////						deep_nl_line = token_content+"\t"+label;
////					}
////					else
//					
//					String prev = "";
//					
//					if (token_index-1>=0)
//					{
//						prev = tokens.get(token_index-1).toString();
//						if (prev.matches("@"))
//							token_content = "username";
//					}
//			
//					deep_nl_line = token_content;
//					file_writer.write( deep_nl_line+ "\n");
//				}
//				
//				file_writer.write( "\n");
//				file_writer.flush();
//					
//			    
//			}
//		}
//		file_writer.flush();
//		file_writer.close();
//	}
}
