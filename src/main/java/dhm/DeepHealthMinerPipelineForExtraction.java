package dhm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import db.DBLayer;
import db.inspireExtractedMentions;
import rainbownlp.core.Artifact;
import rainbownlp.core.Phrase;
import rainbownlp.core.RainbowEngine;
import rainbownlp.core.Setting;
import rainbownlp.core.Artifact.Type;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.preprocess.Preprocess;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;
import edu.asu.diego.dhmevaluation.DLNERMainConceptExtractionEvaluation;
import edu.asu.diego.dhmevaluation.TokenSequenceExampleBuilder;
import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
import edu.asu.diego.extraction.DeepnlInputGenerator;

public class DeepHealthMinerPipelineForExtraction {
	List<Artifact> loadedSentences = new ArrayList<>();
	static String PythonPath = "/usr/local/Cellar/python/2.7.13/bin/python";
	
	static int UserPostCountProcessingThreshold = 15000;


	public static void main(String[] args) throws Exception {

//		PythonPath = args[0];
		String inputCorpora = args[1];

		extractEntities(inputCorpora, false);
		// extractEntities(inputCorpora, true);
	}

	public static void extractEntities(String input_corpora_keywords, boolean is_negative_control) throws Exception {

		InputStream inputStream = DeepHealthMinerPipelineForExtraction.class.getClassLoader()
				.getResourceAsStream(input_corpora_keywords);
		String key_word_file_path = FileUtil.readResourceIntoTempFile(input_corpora_keywords, ".txt", inputStream);

		List<String> input_keywords = FileUtil.loadLineByLine(key_word_file_path);

		for (String input : input_keywords) {
			String[] elements = input.split("\t");

			String corpusName = elements[1];
			if (is_negative_control) {
				corpusName = "no_" + corpusName;
			}
			// g
			// int min_artifact_id
			// =Artifact.getFirstInsertedIdByType(corpusName, Type.Word);
			// int max_artifact_id=Artifact.getLastInsertedIdByType(corpusName,
			// Type.Word);

			String query = "select  artifactId from Artifact where artifactType=" + Type.Sentence.ordinal()
					+ " and corpusName='" + corpusName + "' order by artifactId";
			List<Integer> sent_artifact_ids = (List<Integer>) HibernateUtil.executeReader(query);

			int min_list_index = 0;
			int max_list_index = min_list_index + UserPostCountProcessingThreshold;
			int min = sent_artifact_ids.get(min_list_index);
			int max = sent_artifact_ids.get(max_list_index);

			// we can do this because they are saved in order when loading the
			// posts into artifact
//			while (max_list_index < sent_artifact_ids.size()) {
//				min = sent_artifact_ids.get(min_list_index);
//				max = sent_artifact_ids.get(max_list_index);
//
//				buildExamplesAndExtractEntities(corpusName, min, max);
//
//				min_list_index = max_list_index;
//				max_list_index = max_list_index + UserPostCountProcessingThreshold;
//				HibernateUtil.clearLoaderSession();
//			}
//			// TODO:validate
//			if (min_list_index < sent_artifact_ids.size()) {
//				min = sent_artifact_ids.get(min_list_index);
//				max = sent_artifact_ids.get(sent_artifact_ids.size() - 1);
//				buildExamplesAndExtractEntities(corpusName, min, max);
//				HibernateUtil.clearLoaderSession();
//			}

		}
	}

	public static void buildExamplesAndExtractEntities(RainbowEngine re, String python_path,
			String corpusName, Integer min, Integer max,boolean signSymp,String inputTestFilePath) throws Exception {
		FileUtil.createFolderIfNotExists(System.getProperty("user.dir") + "/DHMInputFiles");
		String deep_nl_script = System.getProperty("user.dir") + "/Deepnl/bin/dl-ner.py";
		String model_file_path = System.getProperty("user.dir") + "/ner-trained_model_inspire.dnn";

//		// Create input format.
////		String DHM_input_test = File.createTempFile(corpusName + "_" + min + "_", ".txt",
////				new File(System.getProperty("user.dir") + "/DHMInputFiles")).getPath();
//		String DHM_input_test = File.createTempFile(corpusName + "_", ".txt",
//				new File(System.getProperty("user.dir") + "/DHMInputFiles")).getPath();
//		DeepnlInputGenerator.writeIOB2(re.getLoader().getWords(), DHM_input_test, corpusName, false, false, false, min, max);

		FileUtil.createFolderIfNotExists(System.getProperty("user.dir") + "/DHMOutputFiles");
		
//		File resul_file = File.createTempFile(corpusName + "_" + min + "_", ".iob",
//				new File(System.getProperty("user.dir") + "/DHMOutputFiles"));
		File resul_file = File.createTempFile(corpusName + "_", ".iob",
				new File(System.getProperty("user.dir") + "/DHMOutputFiles"));

		String shell_command = python_path + " " + deep_nl_script + " " + model_file_path + " < "
				+ new File(inputTestFilePath).getAbsolutePath() + " > " + resul_file.getAbsolutePath();

		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", shell_command);
		builder.directory(new File(System.getProperty("user.dir") + "/Deepnl/deepnl").getAbsoluteFile()); 
		System.out.println("\nRunning the DHM on test examples ... :\n");
		System.out.println("\nRunning the shell command ... :\n");
		
		System.out.println(shell_command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		process.waitFor();
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

		// //updating db
		// DLNERMainConceptExtractionEvaluation.updateNERPredictedClass(
		// DLNERMainConceptExtractionEvaluation.convertPredictionsToNums(
		// DLNERMainConceptExtractionEvaluation.removeSpaceForNewLines(resul_file.getAbsolutePath())),true,corpusName,testExamples);
		// String ner_result_file_path =
		// portability.ConceptextractionOutPutManager.generateNEROutPutByCorpus(TargetSemanticType.ADR,corpusName,testExamples);

//		List<MLExample> testExamples = TokenSequenceExampleBuilder.createTokenSequenceExamplesForTest(false, corpusName,
//				min, max, mysqlConnUserDb);
		String ner_result_file_path = extractPhrasesFromIOBLabels(inputTestFilePath, resul_file.getAbsolutePath(),
				re.getLoader().getWords(), corpusName,signSymp);

//		inspireExtractedMentions.loadDataFromFile(ner_result_file_path);
		System.out.println("NER extraction completed!");
	}


	public static String extractPhrasesFromIOBLabels(String DHMInputFile, String DHMResultFile,
			List<Artifact> tokenArtifacts, String corpusName, boolean SignsSymps) throws IOException {

		List<String> phrase_lines = new ArrayList<>();

		String dhm_input_file_no_space = DLNERMainConceptExtractionEvaluation.removeSpaceForNewLines(DHMInputFile);
		String dhm_output_file_no_space = DLNERMainConceptExtractionEvaluation.removeSpaceForNewLines(DHMResultFile);

		List<String> dhm_input_tokens = FileUtil.loadLineByLine(dhm_input_file_no_space);
		List<String> dhm_labels = FileUtil.loadLineByLine(dhm_output_file_no_space);

		String cur_mention_str = "";
		int counter = 0;
		// while (counter<testExamples.size() && reader.ready()) {
		
		if (SignsSymps)
		{
			while (counter < dhm_labels.size()) {
				String predicted = dhm_labels.get(counter);

				if (predicted.matches("B-ADR")) {
					Artifact curToken = tokenArtifacts.get(counter);
					
					int start_char_index = curToken.getStartIndex();
					int cur_end_char_index = curToken.getEndIndex();
					int line_index = curToken.getLineIndex();
					
					cur_mention_str = dhm_input_tokens.get(counter);
					counter++;
					while (dhm_labels.get(counter).matches("I-ADR")) {
						cur_mention_str += " " + dhm_input_tokens.get(counter);
						cur_end_char_index= tokenArtifacts.get(counter).getEndIndex();
						counter++;
					}
					String text_id = curToken.getAssociatedFilePath();
					// add this to the result phrases
					String phraseLine = text_id + "\t" + cur_mention_str + "\t" + "SIGN_SYMP" + "\t"+
							String.valueOf(line_index) + "\t" +  String.valueOf(start_char_index)+ "\t" +String.valueOf(cur_end_char_index);
	
					phrase_lines.add(phraseLine);
					cur_mention_str = "";
					
				} else if (predicted.matches("B-IND")) {
					Artifact curToken = tokenArtifacts.get(counter);
					int start_char_index = curToken.getStartIndex();
					int cur_end_char_index = curToken.getEndIndex();
					int line_index = curToken.getLineIndex();
					cur_mention_str = dhm_input_tokens.get(counter);
					counter++;
					while (dhm_labels.get(counter).matches("I-IND")) {
						cur_mention_str += " " + dhm_input_tokens.get(counter);
						cur_end_char_index= tokenArtifacts.get(counter).getEndIndex();
						counter++;
					}
					String text_id = curToken.getAssociatedFilePath();
					// add this to the result phrases
					String phraseLine = text_id + "\t" + cur_mention_str + "\t" + "SIGN_SYMP" + "\t"+
							String.valueOf(line_index) + "\t" +  String.valueOf(start_char_index)+ "\t" +String.valueOf(cur_end_char_index);

					phrase_lines.add(phraseLine);
					cur_mention_str = "";
				}
				else {
					counter++;
				}
			}
		}
		else 
		{
			while (counter < dhm_labels.size()) {
				String predicted = dhm_labels.get(counter);

				if (predicted.matches("B-ADR")) {
					Artifact curToken = tokenArtifacts.get(counter);
					cur_mention_str = dhm_input_tokens.get(counter);
					counter++;
					while (dhm_labels.get(counter).matches("I-ADR")) {
						cur_mention_str += " " + dhm_input_tokens.get(counter);
						counter++;
					}
					String text_id = curToken.getAssociatedFilePath();
					// add this to the result phrases
					String phraseLine = text_id + "\t" + cur_mention_str + "\t" + "ADR" + "\t" + corpusName;
					String post_id = StringUtils.substringBetween(text_id, "#");
					String inspire_table = StringUtils.substringBefore(text_id, "#");
					String author_uid = StringUtils.substringAfter(text_id, "@");

					phraseLine += "\t" + inspire_table + "\t" + post_id + "\t" + author_uid;
					phrase_lines.add(phraseLine);
					cur_mention_str = "";
				} 
				else {
					counter++;
				}
			}
		}

		// String path =File.createTempFile("extractedPgrases_"+corpusName,
		// ".tsv").getPath();
		String folder_path = System.getProperty("user.dir") + "/Results";
		FileUtil.createFolderIfNotExists(folder_path);
		String path = File
				.createTempFile("extractedMentions_", ".tsv", new File(folder_path))
				.getPath();
		
		FileUtil.createFile(path, phrase_lines);
		return path;
	}
	
	public static void tagInputSentences(String corpusName) throws IOException, InterruptedException
	{
//		String deep_nl_script = new File("resources/Deepnl/bin/dl-ner.py").getAbsolutePath();
		String deep_nl_script = System.getProperty("user.dir")+"/Deepnl/bin/dl-ner.py";

//		String model_file_path = System.getProperty("user.dir")+"/ner-inspire-onclology-retrain-h100.dnn";
		String model_file_path = System.getProperty("user.dir")+"/ner_inspire_all_min_count40_fix_order_e5_v3.dnn";
		
		String shell_command = PythonPath+ " "+deep_nl_script+" "+ model_file_path+
				" --corpusName "+corpusName+ " --baseDir "+System.getProperty("user.dir")+" --batchTagging 1";
		
	    ProcessBuilder builder = new ProcessBuilder( "/bin/sh","-c",
	    		shell_command
	    		);
//	    builder.directory( new File( "resources/Deepnl/deepnl" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
	    builder.directory(new File(System.getProperty("user.dir")+"/Deepnl/deepnl").getAbsoluteFile()); // this is where you set the root folder for the executable to run with
	    System.out.println("Running the DHM on test examples ... :\n");
	    builder.redirectErrorStream(true);
	    Process process =  builder.start();

	    process.waitFor();
	  
	    BufferedReader stdInput = new BufferedReader(new 
	    InputStreamReader(process.getInputStream()));

	   BufferedReader stdError = new BufferedReader(new 
	        InputStreamReader(process.getErrorStream()));

	   String s = null;
	   while ((s = stdInput.readLine()) != null) {
	       System.out.println(s);
	   }

	   // read any errors from the attempted command
	   while ((s = stdError.readLine()) != null) {
	       System.out.println(s);
	   }
			  

	}
	public static void extractEntities(RainbowEngine re,String python_path,
			String corpusName,boolean signSymp) throws Exception {
		FileUtil.createFolderIfNotExists(System.getProperty("user.dir") + "/DHMInputFiles");
		String deep_nl_script = System.getProperty("user.dir") + "/Deepnl/bin/dl-ner.py";
		String model_file_path = System.getProperty("user.dir") + "/ner-trained_model_inspire.dnn";
		if (python_path==null)
			python_path=PythonPath;
		
//		String shell_command = python_path+ " "+deep_nl_script+" "+ model_file_path+
//				" --corpusName "+corpusName+ 
//				" --baseDir "+System.getProperty("user.dir")+" --batchTagging 1";
//		ProcessBuilder builder = new ProcessBuilder( "/bin/sh","-c",
//	    		shell_command
//	    		);
		
		
		// Create input format.
//		String DHM_input_test = File.createTempFile(corpusName + "_" + min + "_", ".txt",
//				new File(System.getProperty("user.dir") + "/DHMInputFiles")).getPath();
		String DHM_input_test = File.createTempFile(corpusName + "_", ".txt",
				new File(System.getProperty("user.dir") + "/DHMInputFiles")).getPath();
		DeepnlInputGenerator.writeIOB2(re.getLoader().getWords(), DHM_input_test, corpusName, false, false, false, null, null);

		FileUtil.createFolderIfNotExists(System.getProperty("user.dir") + "/DHMOutputFiles");
		
		File resul_file = File.createTempFile(corpusName + "_", ".iob",
				new File(System.getProperty("user.dir") + "/DHMOutputFiles"));

		String shell_command = python_path + " " + deep_nl_script + " " + model_file_path + " < "
				+ new File(DHM_input_test).getAbsolutePath() + " > " + resul_file.getAbsolutePath();

		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", shell_command);
		builder.directory(new File(System.getProperty("user.dir") + "/Deepnl/deepnl").getAbsoluteFile()); 
		System.out.println("\nRunning the DHM on test examples ... :\n");
		System.out.println("\nRunning the shell command ... :\n");
		
		builder.redirectErrorStream(true);
		Process process =  builder.start();

		process.waitFor();
		  
		BufferedReader stdInput = new BufferedReader(new 
		    InputStreamReader(process.getInputStream()));

		BufferedReader stdError = new BufferedReader(new 
		InputStreamReader(process.getErrorStream()));

	    String s = null;
	    while ((s = stdInput.readLine()) != null) {
	       System.out.println(s);
	    }

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
		       System.out.println(s);
		 }
		
//		String ner_result_file_path = extractPhrasesFromIOBLabels(inputTestFilePath, resul_file.getAbsolutePath(),
//				re.getLoader().getWords(), corpusName,signSymp);

		System.out.println("NER extraction completed!");
	}
}
