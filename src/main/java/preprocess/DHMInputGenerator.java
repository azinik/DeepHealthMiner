package preprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dhm.NEREngine;
import edu.stanford.nlp.ling.HasWord;
import rainbownlp.preprocess.Preprocess;
import rainbownlp.util.FileUtil;

import rainbownlp.util.StringUtil;

public class DHMInputGenerator {
	public static boolean NormalizeDigits=true;
	public static boolean DoStemming=true;
	public static String corpusName = "ADR_Indication";
	public static String dhmInputFolderPath = System.getProperty("user.dir")+"/DHMInputFiles";
	public static String dhmOutputFolderPath = System.getProperty("user.dir")+"/DHMOutputFiles";
	
	public static void main (String[] args) throws Exception
	{
		corpusName = args.length>1?args[1]:corpusName;
		convertContent2Tokens(args[0],corpusName);

	}
	public static String getDhmInputFolderPath(){
		return dhmInputFolderPath;
	}
	
	
//get a text file and return a list of sentences in a hashmap that has the file 

	
public static void convertContent2Tokens(String filePath,String corpus) throws IOException
{
	String file_name =FileUtil.getFileNameWithoutPath(filePath);
	String content= FileUtil.ReadFileInToString(filePath);
	String DHM_input_test_file = File.createTempFile(file_name + "_", ".txt",
			new File(dhmInputFolderPath+"/"+corpus)).getPath();
	List<String> tokens =getTokens(content,filePath);
	FileUtil.createFile(DHM_input_test_file, tokens);

}
public static List<String>  getTokens(String content, String filePath)
{
	List<String> pre_processed_tokens = new ArrayList<String>();
	
	Preprocess pre_processed_sent = new Preprocess(content);
//	NEREngine.fileProcessedContent.put(filePath, pre_processed_sent);
	
	HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();

	for (Integer sent_index : setences.keySet()) {
		String tokenizedSentence = setences.get(sent_index);

		List<HasWord> tokens = pre_processed_sent.getSentTokensMap().get(sent_index);

		for (int token_index = 0; token_index < tokens.size(); token_index++) {

			String tokenContent = tokens.get(token_index).toString();
			pre_processed_tokens.add(preProcessToken(tokenContent));
		}
		pre_processed_tokens.add("\n");
	}
	

	return pre_processed_tokens;
}
public static String preProcessToken(String token)
{
	if (NormalizeDigits)
	{
		token  = token.replaceAll("\\d", "d");
	}
	if (DoStemming)
	{
		token  = StringUtil.getTermByTermWordnet(token);
	}
	return token;
}


}
