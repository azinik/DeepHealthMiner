package edu.asu.diego.dhmevaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.asu.diego.dhmevaluation.TokenSequenceExampleBuilder.TokenLabel;
import edu.asu.diego.extraction.DeepnlInputGenerator.TokenLabelIOB2;
import rainbownlp.analyzer.evaluation.classification.Evaluator;
import rainbownlp.core.Artifact;
import rainbownlp.core.Phrase;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.StringUtil;

public class DLNERMainConceptExtractionEvaluation {
	public static String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
//	public static String experimentgroupLexiconCand = LexiconCandidateSVMExampleBuilder.ExperimentGroupADRConceptsLexiconCandidates;
	private boolean conbineWithLexiconCandClasses = false;
	public static  int  countFP=0;
	public static int  countFN=0;
	public static boolean applyPostProcessingRules= true;
	
	public enum ExtractionMethod {
		LuceneSearchLexicon, SemVecSVM, CRF, SVMCRF,MetaMap
	}
	

	public static void main (String args[]) throws Exception
	{
////		updateDBByResultFileForLargeVol();
//		evaluatePyLearn2(convertPredictionsToNums(removeSpaceForNewLines("/home/azadeh/projects/deepnl/results.iob")),true);
////		evaluatePyLearn2(convertPredictionsToNums(removeSpaceForNewLines("/home/azadeh/projects/deepnl/results.iob")),true);

//		evaluatePyLearn2("/home/azadeh/tf-predictions.txt",false);
//		evaluatePyLearn2("/home/azadeh/Documents/assignment2_dev/q2_test.predicted",false);
//		evaluatePyLearn2("/home/azadeh/projects/deepnl-withchange/results.iob",false);
//		evaluatePyLearn2("/home/azadeh/projects/dl-ner/output");
		//This is for deepnl
		
		
//		portability.ConceptextractionOutPutManager.generateAnnotationFormatOutPut(TargetSemanticType.ADR);
//		core.ConceptExtractionEvaluation.evaluateConceptExtractionUsingAnnFiles
//		("/home/azadeh/projects/drug-effect-ext/data/PSB-sharedTask2016/TestGoldStandard/v3-reviewed/gold-s.tsv",
//				"/tmp/ADRMineOutputTwitter",TargetSemanticType.ADR,true);
		
	}
	
	
	public static void evaluatePyLearn2(String resultFilePath, boolean deduct_one,String experimentGroup) throws Exception
	{
		//get test examples 
		List<MLExample> testExamples = 
				MLExample.getAllExamples(experimentGroup, false);
//		List<MLExample> testExamples = 
//			       MLExample.getLimitedPreSelectedExamples(experimentgroup, false,"tempIsSelectedForTrain=1");

		String exampleids = "";
		List<Integer> test_example_ids = new ArrayList<>();
		
		for(MLExample example : testExamples)
		{
			exampleids = exampleids.concat(","+example.getExampleId());
			test_example_ids.add(example.getExampleId());
		}
			
		exampleids = exampleids.replaceFirst(",", "");
		String resetQuery = "update MLExample set predictedClass = -1 where exampleId in ("+ exampleids +")";
		HibernateUtil.executeNonReader(resetQuery);
		
		FileReader fileR = new FileReader(resultFilePath);
		BufferedReader reader = new BufferedReader(fileR);
		
		int counter = 0;
		while (counter<testExamples.size() && reader.ready()) {
			String line = reader.readLine();
			int classNum = Integer.parseInt(line.split(" ")[0]);
			int predicted = classNum;
			if (deduct_one)
				predicted=classNum-1;
			testExamples.get(counter).setPredictedClass(predicted);//convert to index (e.g. 1 -> 0)
//			pTestExamples.get(counter).setPredictionWeight(maxWeight);
			
//			MLExample.saveExample(pTestExamples.get(counter));
			MLExample test = testExamples.get(counter);
			
			System.out.println("Class number "+ predicted +" predicted class: "+test.getPredictedClass());
			String savePredictedQuery = "update MLExample set predictedClass ="+test.getPredictedClass()+" where exampleId="+test.getExampleId();
			HibernateUtil.executeNonReader(savePredictedQuery);
			
			counter++;
		}

		assert !reader.ready() : "Something wrong file remained, updated rows:"+counter;
		assert counter==testExamples.size() : "Something wrong resultset remained, updated rows:"+counter;
		
		reader.close();
		
		
		Evaluator.getEvaluationResult(testExamples).printResult();
		
		MainConceptExtractionEvaluation ev = new MainConceptExtractionEvaluation();
		
//		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR",false);
////		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"Indication",false);
////		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR&Indication",false);
	}
	
	public static void updateNERPredictedClass(String resultFilePath, boolean deduct_one,String experimentGroup,List<MLExample> testExamples) throws Exception
	{
//		//get test examples 
//		List<MLExample> testExamples = 
//				MLExample.getAllExamples(experimentGroup, false);
	
//		List<MLExample> testExamples = 
//			       MLExample.getLimitedPreSelectedExamples(experimentgroup, false,"tempIsSelectedForTrain=1");
		
		FileReader fileR = new FileReader(resultFilePath);
		BufferedReader reader = new BufferedReader(fileR);
		
		
//		//temp
//		FileReader fileR2 = new FileReader(DLNERMainConceptExtractionEvaluation.removeSpaceForNewLines("/tmp/nn-test.txt"));
//		
//		BufferedReader reader2 = new BufferedReader(fileR2);
//		
		int counter = 0;
		int examples_size =  testExamples.size();
//		while (counter<testExamples.size() && reader.ready()) {
		for(MLExample curExample:testExamples )
		{
			String line = reader.readLine();
			int classNum = Integer.parseInt(line.split(" ")[0]);
			int predicted = classNum;
			if (deduct_one)
				predicted=classNum-1;
//			MLExample curExample = testExamples.get(counter);
					
			curExample.setPredictedClass(predicted);//convert to index (e.g. 1 -> 0)
//			String line2=reader2.readLine();
//			if (!StringUtil.getTermByTermWordnet(curExample.getRelatedArtifact().getContent()).equals(line2)
//					&& !line2.matches("d+") && !curExample.getRelatedArtifact().getContent().matches(".*\\d+.*"))
//			{
//				System.out.println(curExample);
//				System.out.println(curExample.getRelatedArtifact().getContent()+"=======>"+line2);
//				
//			}
			
			MLExample.addUpdateQuery(curExample);
			try {
				
				if(counter%400==0){
					
					MLExample.FlushUpdateQueryBuffer(null);
				}	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
//			pTestExamples.get(counter).setPredictionWeight(maxWeight);
			
//			MLExample.saveExample(pTestExamples.get(counter));
//			MLExample test = testExamples.get(counter);
			if (counter%5000==0)
				
				System.out.println("updating predicated class  "+counter+"/"+examples_size);
////			String savePredictedQuery = "update MLExample set predictedClass ="+test.getPredictedClass()+" where exampleId="+test.getExampleId();
////			HibernateUtil.executeNonReader(savePredictedQuery);
			
			counter++;
		}
		MLExample.FlushUpdateQueryBuffer(null);

		assert !reader.ready() : "Something wrong file remained, updated rows:"+counter;
		assert counter==testExamples.size() : "Something wrong resultset remained, updated rows:"+counter;
		
		reader.close();
//		reader2.close();
		
		
//		Evaluator.getEvaluationResult(testExamples).printResult();
//		
//		MainConceptExtractionEvaluation ev = new MainConceptExtractionEvaluation();
//		
////		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR",false);
//////		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"Indication",false);
//////		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR&Indication",false);
	}
	public static String removeSpaceForNewLines(String resultFile) throws IOException
	{
		List<String> new_lines = new ArrayList<>();
		File new_result_file =  File.createTempFile("NN_Output_no_space_", ".tsv");
		new_result_file.deleteOnExit();

		List<String> result_lines =  FileUtil.loadLineByLine(resultFile);
		for(String line:result_lines)
		{
			if (!line.isEmpty())
			{
				new_lines.add(line);
			}
		}
		
		FileUtil.writeToExistingFile(new_result_file, new_lines);
		return new_result_file.getPath();
	}
	public static String convertPredictionsToNums(String resultFile) throws IOException
	{
		List<String> new_lines = new ArrayList<>();
		File new_result_file =  File.createTempFile("NN_numeric_output_", ".tsv");
		new_result_file.deleteOnExit();
		
		List<String> result_lines =  FileUtil.loadLineByLine(resultFile);
		for(String line:result_lines)
		{
			line = line.replaceAll("-", "_");
			TokenLabelIOB2 num_val_label = TokenLabelIOB2.valueOf(line);
			Integer code = num_val_label.getCode()+1;
			new_lines.add(code.toString());
		}
		
		FileUtil.writeToExistingFile(new_result_file, new_lines);
		return new_result_file.getPath();
	}
	//TODO
	public static void updateDBByResultFileForLargeVol() throws Exception
	{
		//TODO automate this for a given list
		List<String> drug_sets = new ArrayList<>();
		
//		drug_sets.add("'quetiapine','nexium','suboxone','latuda','lamictal','cipro','enbrel','venlafaxine','xarelto','olanzapine','pregabalin'");
//		drug_sets.add("'tysabri','zyprexa','baclofen','lamotrigine','nasonex','eliquis','paroxetine','victoza'");
//		drug_sets.add("'pristiq','metoprolol','boniva','pradaxa','geodon','duloxetine','levaquin','valsartan'");
//		drug_sets.add("'rivaroxaban','dabigatran','namenda','apixaban','januvia','spiriva','invokana'");
//		
//		drug_sets.add("'liraglutide','fosamax','memantine','viibryd','saphris','denosumab','etanercept','effient','vimpat','zometa','avelox'");
//		drug_sets.add("'ofloxacin','bystolic','factive','alendronate','linagliptin','sabril',"
//				+ "'synthroid','lurasidone','ziprasidone','actonel','onglyza','canagliflozin','fycompa','nesina','dronedarone','livalo','floxin'");
		
		//larger sets
		drug_sets.add("'vyvanse'");
		drug_sets.add("'prozac'");
		drug_sets.add("'lyrica','seroquel','humira','paxil'");
		drug_sets.add("'cymbalta','tamiflu'");
		drug_sets.add("'albuterol','trazodone'");
		drug_sets.add("'fluoxetine','effexor'");
		int set_num = 6;
		for (String drug_set:drug_sets)
		{
			//get related file
			String result_file_path = "/home/azadeh/projects/thesis-data-experiments/deepnl/Twitter/ADRMine-on-all-tweets/drug-sets/set-"+set_num+"-results.iob"; 
			
			evaluateDeepNLForLimitedSets(convertPredictionsToNums(removeSpaceForNewLines(result_file_path)),true,drug_set);
			System.out.println("done with updating set"+set_num+" "+drug_set);
			set_num++;
		}
	}
	public static void evaluateDeepNLForLimitedSets(String resultFilePath, boolean deduct_one,String drugSet) throws Exception
	{
		//get test examples 
		
		List<MLExample> testExamples = 
				MLExample.getLimitedPreSelectedExamples(experimentgroup, false, 
						" relatedDrug in ("+drugSet+")");
		
		System.out.println("Test exaples retrieved ...");
		String exampleids = "";
//		List<Integer> test_example_ids = new ArrayList<>();
//		
//		for(MLExample example : testExamples)
//		{
//			exampleids = exampleids.concat(","+example.getExampleId());
//			test_example_ids.add(example.getExampleId());
//		}
//			
//		exampleids = exampleids.replaceFirst(",", "");
//		String resetQuery = "update MLExample set predictedClass = -1 where exampleId in ("+ exampleids +")";
//		HibernateUtil.executeNonReader(resetQuery);
		
		FileReader fileR = new FileReader(resultFilePath);
		BufferedReader reader = new BufferedReader(fileR);
		
		int counter = 0;
		while (counter<testExamples.size() && reader.ready()) {
			String line = reader.readLine();
			int classNum = Integer.parseInt(line.split(" ")[0]);
			int predicted = classNum;
			if (deduct_one)
				predicted=classNum-1;
			testExamples.get(counter).setPredictedClass(predicted);//convert to index (e.g. 1 -> 0)
//			pTestExamples.get(counter).setPredictionWeight(maxWeight);
			
//			MLExample.saveExample(pTestExamples.get(counter));
			MLExample test = testExamples.get(counter);
			
			System.out.println("Class number "+ predicted +" predicted class: "+test.getPredictedClass());
			String savePredictedQuery = "update MLExample set predictedClass ="+test.getPredictedClass()+" where exampleId="+test.getExampleId();
			HibernateUtil.executeNonReader(savePredictedQuery);
			HibernateUtil.clearLoaderSession();
			counter++;
		}

		assert !reader.ready() : "Something wrong file remained, updated rows:"+counter;
		assert counter==testExamples.size() : "Something wrong resultset remained, updated rows:"+counter;
		
		reader.close();
		HibernateUtil.clearLoaderSession();
		
//		Evaluator.getEvaluationResult(testExamples).printResult();
//		
//		MainConceptExtractionEvaluation ev = new MainConceptExtractionEvaluation();
//		
//		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR",false);
//		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR&Indication",false);
	}
	
}
