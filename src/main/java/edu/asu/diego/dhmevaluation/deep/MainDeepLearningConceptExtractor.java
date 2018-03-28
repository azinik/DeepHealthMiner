package edu.asu.diego.dhmevaluation.deep;
//package edu.asu.diego.dhmevaluation.deep;
//
//import java.io.IOException;
//import java.util.List;
//
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
//import portability.ConceptextractionOutPutManager;
//import rainbownlp.machinelearning.LearnerEngine;
//import rainbownlp.machinelearning.MLExample;
//import rainbownlp.util.HibernateUtil;
//
//
//
//public class MainDeepLearningConceptExtractor  extends LearnerEngine  {
////	public static String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
//	public static String experimentgroup = "ADRConceptTokenSeq";
//	
//	
//	public static void main(String[] args) throws Exception
//	{
////		FeatureValuePair.resetIndexes();
//		
//		//test/train options
//		//test 1 train 2 train and test 3 
//		String test_train= args[0];
//		
//		String test_file = args[1];
//
//		String database = args[2];
//		String user_name = args[3];
//		String password = args[4];
//		
//		
//		
//		// "jdbc:mysql://localhost/deextTwitter"
//		HibernateUtil.changeConfigurationDatabase(args[2], args[3], args[4]);
// 
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
//
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
