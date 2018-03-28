package edu.asu.diego.extraction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import db.DiscCmntText;
import db.DiscText;
import edu.asu.diego.dhmevaluation.TokenSequenceExampleBuilder;
import edu.stanford.nlp.ling.HasWord;
import rainbownlp.core.Artifact;
import rainbownlp.core.Artifact.Type;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.preprocess.Preprocess;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.StringUtil;
import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.FileUtil;


public class DeepnlInputGenerator  {
	public static final String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
	static boolean DoStemming = true;
	static boolean normalizeDidit = true;
	public enum TokenLabelIOB2 {
		O(0),
		B_ADR(1),
		I_ADR(2),
		B_IND(3),
		I_IND(4),
		B_BEN(5),
		I_BEN(6);
		private static final Map<Integer,TokenLabelIOB2> lookup = 
			new HashMap<Integer,TokenLabelIOB2>();
		
		static {
	          for(TokenLabelIOB2 l : EnumSet.allOf(TokenLabelIOB2.class))
	               lookup.put(l.getCode(), l);
	     }
		
		private int code;

	     private TokenLabelIOB2(int code) {
	          this.code = code;
	     }

	     public int getCode() { return code; }

	     public static TokenLabelIOB2 getEnum(int code) { 
	          return lookup.get(code); 
	     }	
	     
	}
	public static void main(String[] args) throws Exception
	{
		String file_path = args[0];

//		List<MLExample> trainExamples = 
//				MLExample.getAllExamples(experimentgroup, true);
	
		List<MLExample> testExamples = 
				MLExample.getAllExamples(experimentgroup, false);
	
		List<Integer> test_example_ids = new ArrayList<Integer>();
		for(MLExample example : testExamples)
		{
			test_example_ids.add(example.getExampleId());
		}
		
		writeIOB2(test_example_ids, file_path,false,false,false);

	}


	public static void writeIOB2(List<Integer> exampleIdsToWrite, String filePath,
			boolean forTrain,boolean replaceUnknown, boolean healthRelated) 
		throws IOException
	{
//		if(new File(filePath).exists()) return;
//		List<String> vocabs = FileUtil.loadLineByLine("/tmp/vocabs.txt");
//		FileWriter file_writer = new FileWriter(filePath);
		List<String> outputLines =new ArrayList<>();
		
		int count=0;
		for(Integer example_id : exampleIdsToWrite) {
			
			MLExample example = MLExample.getExampleById(example_id);
			Artifact relatedArtifact = example.getRelatedArtifact();
			Artifact next = relatedArtifact.getNextArtifact();
			Artifact previous = relatedArtifact.getPreviousArtifact();
			

			String deep_nl_line="";
			
//			Integer expected = Integer.parseInt(example.getExpectedClass());
//			if (healthRelated)
//			{
//				if (expected==3) expected=1;
//				if (expected==4) expected=2; 
//			}
//			TokenLabelIOB2 label_enum = TokenLabelIOB2.getEnum(expected);
//			
//			String label = label_enum.toString().replaceAll("_", "-");
			String token_content = relatedArtifact.getContent().toLowerCase();
			
			if (previous != null &&
					previous.getContent().matches("@"))
			{
				token_content = "username";
			}
			if (normalizeDidit)
			{
				token_content  = token_content.replaceAll("\\d", "d");
			}
			if (DoStemming)
			{
				token_content  = rainbownlp.util.StringUtil.getTermByTermWordnet(token_content).toLowerCase();
			}
////			if (replaceUnknown)
////			{
////				token_content =Word2VecManager.replaceUnknownWords(token_content, vocabs);
////			}
//			if (forTrain)
//			{
//				deep_nl_line = token_content+"\t"+label;
//			}
//			else
				deep_nl_line = token_content;

		
			deep_nl_line = deep_nl_line.replaceAll("\\t+", "\t").replaceAll("\t$", "");
			 
			System.out.println("**************** count"+count+"/"+exampleIdsToWrite.size());
//			file_writer.write( deep_nl_line+ "\n");
			outputLines.add(deep_nl_line);
			
			if (next==null)
			{
//				file_writer.write( "\n");
				outputLines.add("\n");
			}
//			file_writer.flush();
			count++;
			
			HibernateUtil.clearLoaderSession();
		}
		FileUtil.createFile(filePath, outputLines);
//		file_writer.flush();
//		file_writer.close();
	}
	//this a method for quickly generating the input
	public static void writeIOB2( List<Artifact> tokenArtifacts, String filePath, String corpusName,
			boolean forTrain,boolean replaceUnknown, 
			boolean healthRelated, 
			Integer minArtifactId, Integer maxArtifactId) 
		throws IOException
	{
		List<String> outputLines = new ArrayList<>();
		if (!ConfigurationUtil.DBDetachedMode) {
			if (minArtifactId!=null )
			{
				tokenArtifacts = Artifact.listByType(Type.Word, corpusName,forTrain,minArtifactId,maxArtifactId);
			}
			else
			{
				tokenArtifacts = Artifact.listByType(Type.Word, corpusName,forTrain);
			}
		}
		
		boolean is_user_name=false;
		int  total= tokenArtifacts.size();
		int count=0;
		int token_index=0;
//		for(int token_index = 0; token_index< tokenArtifacts.size();token_index++){
		for (Artifact curArtifact:tokenArtifacts)	
		{
//			Artifact curArtifact = tokenArtifacts.get(token_index);
			
			String token_content = curArtifact.getContent().toLowerCase();
			if (is_user_name)
			{
				token_content = "username";
				is_user_name=false;
				
			}
		
			else if (token_content.matches("@"))
			{
				is_user_name=true;
			}
		
			if (normalizeDidit)
			{
				token_content  = token_content.replaceAll("\\d", "d");
			}
			if (DoStemming)
			{
				token_content  = StringUtil.getTermByTermWordnet(token_content);
			}
			outputLines.add(token_content);
			if (curArtifact.getNextArtifact()==null)
			{
				outputLines.add("\n");
			}
			count++;
			if (count%5000==0)
			{
				System.out.println("Generating NN inputs ...  count "+count+"/"+total);
			}
			token_index++;
		}
	
		FileUtil.createFile(filePath, outputLines);

		
	}
//	public static void writeIOB2( String filePath,
//			boolean forTrain,boolean replaceUnknown, boolean healthRelated) 
//		throws IOException
//	{
//		List<String> outputLines = new ArrayList<>();
//		
//		List<Artifact> sentences =Artifact.listByTypeByForTrain(Type.Sentence, forTrain);
//		List<String> drugs = new ArrayList<>();
//		drugs.add("Infliximab");
//		drugs.add("remicade");
//		HashMap<String, String> posts = DiscText.getContents(drugs,200139);
//			posts.putAll(DiscCmntText.getContents(drugs,200139));
//		int count =0;
//		for (String  post:posts.values())
//		{
//			String post_content = post;
//			
//			Preprocess pre_processed_sent = new Preprocess(post);
//			
//			HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();
//			boolean is_userName= false;
//			for (Integer sent_index: setences.keySet())
//			{	
////				String tokenizedSentence = setences.get(sent_index);
//				List<HasWord> tokens = pre_processed_sent.getSentTokensMap().get(sent_index);
//				
//				for(int token_index = 0; token_index< tokens.size();token_index++){
//					
//					String token_content = tokens.get(token_index).toString().toLowerCase();
//					if (is_userName)
//					{
//						token_content = "username";
//						is_userName=false;
//						continue;
//					}
//				
//					if (token_content.matches("@"))
//					{
//						is_userName=true;
//					}
//				
//					if (normalizeDidit)
//					{
//						token_content  = token_content.replaceAll("\\d", "d");
//					}
//					if (DoStemming)
//					{
//						token_content  = rainbownlp.util.StringUtil.getTermByTermWordnet(token_content);
//					}
//					outputLines.add(token_content);
//				}
//
//				
//			}
//			outputLines.add(String.format("%n"));
//			
//			System.out.println("**************** count"+count+"/"+sentences.size());
//			count++;
//		}
//		FileUtil.createFile(filePath, outputLines);
//
//		
//	}
	public static void writeIOB2AppendExisting(List<Integer> exampleIdsToWrite, String filePath,
			boolean forTrain,boolean replaceUnknown, boolean healthRelated) 
		throws IOException
	{

		List<String> outputLines =new ArrayList<>();
		
		int count=0;
		for(Integer example_id : exampleIdsToWrite) {
			
			MLExample example = MLExample.getExampleById(example_id);
			Artifact relatedArtifact = example.getRelatedArtifact();
			Artifact next = relatedArtifact.getNextArtifact();
			Artifact previous = relatedArtifact.getPreviousArtifact();
			

			String deep_nl_line="";
			
			Integer expected = Integer.parseInt(example.getExpectedClass());
			if (healthRelated)
			{
				if (expected==3) expected=1;
				if (expected==4) expected=2; 
			}
			TokenLabelIOB2 label_enum = TokenLabelIOB2.getEnum(expected);
			
			String label = label_enum.toString().replaceAll("_", "-");
			String token_content = relatedArtifact.getContent().toLowerCase();
			
			if (previous != null &&
					previous.getContent().matches("@"))
			{
				token_content = "username";
			}
			if (normalizeDidit)
			{
				token_content  = token_content.replaceAll("\\d", "d");
			}
			if (DoStemming)
			{
				token_content  = rainbownlp.util.StringUtil.getTermByTermWordnet(token_content).toLowerCase();
			}
//			if (replaceUnknown)
//			{
//				token_content =Word2VecManager.replaceUnknownWords(token_content, vocabs);
//			}
			if (forTrain)
			{
				deep_nl_line = token_content+"\t"+label;
			}
			else
				deep_nl_line = token_content;

		
			deep_nl_line = deep_nl_line.replaceAll("\\t+", "\t").replaceAll("\t$", "");
			 
			System.out.println("**************** count"+count+"/"+exampleIdsToWrite.size());
			outputLines.add(deep_nl_line);
			
			if (next==null)
			{
				outputLines.add("\n");
			}
			count++;
			
			HibernateUtil.clearLoaderSession();
		}
		FileUtil.appendLines(filePath, outputLines);

	}
}
