package edu.asu.diego.dhmevaluation;
//package edu.asu.diego.dhmevaluation;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
//import portability.ConceptextractionOutPutManager;
//import rainbownlp.machinelearning.CRFSuite;
//import rainbownlp.machinelearning.LearnerEngine;
//import rainbownlp.machinelearning.MLExample;
//import rainbownlp.util.FileUtil;
//import rainbownlp.util.HibernateUtil;
//
//
//
//public class MainCRFADRConceptClassifier  extends LearnerEngine  {
////	public static String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
//	public static String experimentgroup = "ADRConceptTokenSeq";
//	
//	
//	public static void main(String[] args) throws Exception
//	{
////		FeatureValuePair.resetIndexes();
//		
//////		String eval_method=args[0];
//		String train_file = args[0];
//		String test_file = args[1];
////		
////		String train_ids_file = args[2];
////		String test_ids_file = args[3];
//		
//		String database = args[2];
//		String user = args[3];
//		String pass = args[4];
//		
//		 
//		String CRFSuite_file_path = args[5];
//		// "jdbc:mysql://localhost/deextTwitter"
//		HibernateUtil.changeConfigurationDatabase(args[2], args[3], args[4]);
//		
//		CRFSuite learner_engine = (CRFSuite) CRFSuite.getLearnerEngine(experimentgroup);
//	
//		
//		learner_engine.setTrainFilePath(train_file);
//		learner_engine.setTestFilePath(test_file);
//		learner_engine.CRFSuiteExecutable_arg = CRFSuite_file_path;
//
//		
//		List<MLExample> trainExamples = MLExample.getAllExamples(experimentgroup, true);;	
//		List<MLExample> testExamples = MLExample.getAllExamples(experimentgroup, false);
//		
//
//		learner_engine.train(trainExamples);
//		learner_engine.test(testExamples);
//		
//		//Evaluator.getEvaluationResult(testExamples).printResult();
//		
//		MainConceptExtractionEvaluation ev = new MainConceptExtractionEvaluation();
//		ev.evaluateExtractedPhrases(testExamples,MainConceptExtractionEvaluation.ExtractionMethod.CRF,"ADR");
//		
////		List<String> expected_predicted_lines = new ArrayList<>();
////		for (MLExample example:testExamples)
////		{
////			String line=example.getRelatedArtifact().getContent()+"\t"+example.getExpectedClass()+"\t"+example.getPredictedClass();
////			expected_predicted_lines.add(line);
////			
////		}
////		File temp_token_expected_predicted = File.createTempFile("token-expected-predicted-class-", Long.toString(System.currentTimeMillis()));
////		FileUtil.writeToFile(temp_token_expected_predicted, expected_predicted_lines);
//		
//		//generate concepts in annotation format
//		ConceptextractionOutPutManager.generateAnnotationFormatOutPut(TargetSemanticType.ADR);
//
//		
//	}
//public static void saveTrainAndTestExamples()
//{
//	List<MLExample> trainExamples = MLExample.getAllExamples(experimentgroup, true);	
//	List<MLExample> testExamples = MLExample.getAllExamples(experimentgroup, false);
//	
//	List<String> train_ids = new ArrayList<>();
//	List<String> test_ids = new ArrayList<>();
//	
//	
//	for (MLExample e:trainExamples)
//	{
//		train_ids.add(String.valueOf(e.getExampleId()));
//	}
//	for (MLExample e:testExamples)
//	{
//		test_ids.add(String.valueOf(e.getExampleId()));
//	}
//	//create train file
//	FileUtil.createFile("/tmp/train-ids.txt", train_ids);
//	FileUtil.createFile("/tmp/test-ids.txt", test_ids);
//	
//			
//}
//public static List<MLExample> getExamplesFromIds(String exampleidsFile)
//{
//	List<MLExample> examples = new ArrayList<>();
//	
//	List<String> example_ids = FileUtil.loadLineByLine(exampleidsFile);
//	
//	for (String id:example_ids)
//	{
//		examples.add(MLExample.getExampleById(Integer.parseInt(id)));
//	}
//	return examples;
//	
//			
//}
//	
////	 @Override
//	public void train(List<MLExample> exampleForTrain) throws IOException
//	 {
//
//	 }
//
////	@Override
//	public void test(List<MLExample> pTestExamples) throws Exception {
//
//	}
//}
