package edu.asu.diego.dhmevaluation;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.asu.diego.dhmevaluation.TokenSequenceExampleBuilder.TokenLabel;
import rainbownlp.core.Artifact;
import rainbownlp.core.Phrase;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;
import rainbownlp.util.StringUtil;


public class MainConceptExtractionEvaluation {
	public static String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
	private boolean conbineWithLexiconCandClasses = false;
	public static  int  countFP=0;
	public static int  countFN=0;
	public static boolean applyPostProcessingRules= false;
	public static boolean tokenizeExpectedADRs = true;
	public static StanfordParser parser = new StanfordParser();
	
	public enum ExtractionMethod {
		LuceneSearchLexicon, SemVecSVM, CRF, SVMCRF,MetaMap
	}

	public static void main (String args[]) throws Exception
	{
		// This includes all tokens
		List<MLExample> testExamples = 
				MLExample.getAllExamples(experimentgroup, false);
		

		MainConceptExtractionEvaluation ev = new MainConceptExtractionEvaluation();
		
//		ev.evaluateExtractedPhrases(testExamples,ExtractionMethod.CRF,"ADR");
		ev.evaluateExtractedPhrases(testExamples,ExtractionMethod.CRF,"Indication");
	}
	
	public static void evaluateExtractedPhrases(List<MLExample> testExamples,ExtractionMethod method,String semanticType) throws Exception
	{
		List<String> logFileLinesFp = new ArrayList<>();
		List<String> logFileLinesFpFN = new ArrayList<>();
		List<String> logFileLinestp = new ArrayList<>();
		List<String> logFileLinesfn = new ArrayList<>();

				
		List<Artifact> testSentences = new ArrayList<>();
//		List<Artifact> testSentences = Artifact.listByType(Artifact.Type.Sentence);
		Evaluation eval = new Evaluation();
		int tp;
		//generating the test sentences
		for (MLExample test_e:testExamples)
		{
			Artifact related_sent = test_e.getRelatedArtifact().getParentArtifact();
			if (!testSentences.contains(related_sent))
			{
				testSentences.add(related_sent);
			}
			if (semanticType.equals("ADR") &&
					(Integer.parseInt(test_e.getExpectedClass()) != TokenLabel.BADR.ordinal() 
					&& Integer.parseInt(test_e.getExpectedClass() )!= TokenLabel.IADR.ordinal())
					&& Integer.parseInt(test_e.getPredictedClass() )!= TokenLabel.BADR.ordinal()
					&& Integer.parseInt(test_e.getPredictedClass() )!= TokenLabel.IADR.ordinal())
			{
				eval.addTN();
			}
		}
		
		//Then we need to group them as phrases
		//Then compare with gold standartd
		for (Artifact sent:testSentences)
		{
			ArrayList<String> tps = new ArrayList<String>();
			ArrayList<String> fps = new ArrayList<String>();
			ArrayList<String> fns = new ArrayList<String>();
//			if (!sent.getContent().startsWith("But I had to get off of"))
//			{
//				continue;
//			}
			List<String> expected_adrs= new ArrayList<>();
			List<String> expected_indications = new ArrayList<>();
			
			//This is the gold standard
			if (semanticType.equals("ADR"))
				expected_adrs = getExpectedADRs(sent);
			else if (semanticType.equals("Indication"))
			{
				expected_indications = getExpectedIndications(sent);
			}
			List<String> found_adrs = new ArrayList<>();
			List<String> found_indications = new ArrayList<>();
			
			if (method.equals(ExtractionMethod.CRF))
			{
				
				if (semanticType.equals("ADR"))
				{
					// This list keeps what the CRF has extracted for the sent
					found_adrs = CRFBasedExtractionUtils.getCRFExtractedADRsForSent(sent,TokenSequenceExampleBuilder.ExperimentGroupADRConcepts);
				}
//				else if (semanticType.equals("Indication"))
//				{
//					found_indications = CRFBasedExtractionUtils.getCRFExtractedIndicationsForSent(sent,TokenSequenceExampleBuilder.ExperimentGroupADRConcepts);
//				}
			}
//			else if (method.equals(ExtractionMethod.SVMCRF))
//			{
//				if (semanticType.equals("ADR"))
//				{
//					found_adrs = MainHybridADRConceptClassifier.getMergedSVMCRFExtractedADRs(sent);
//				}
//				else
//				{
//					//TODO
//				}
//			}
//			else if (method.equals(ExtractionMethod.LuceneSearchLexicon))
//			{
//				if (semanticType.equals("ADR"))
//				{
//					found_adrs = SemanticVectorLexiconCandidateSVMExampleBuilder.getLexiconBasedSemanticVectorExtractedConcepts(sent,applyPostProcessingRules);
//				}
//				else
//				{
//					//TODO
//				}
//			}
//			else if (method.equals(ExtractionMethod.SemVecSVM))
//			{
//				if (semanticType.equals("ADR"))
//				{
//					List<MLExample> positive_examples = MLExample.getPhraseExamplesByPredictedClass(sent,SemanticVectorLexiconCandidateSVMExampleBuilder.ExperimentGroupSVCandidates,false);
//					for (MLExample example: positive_examples)
//					{
//						found_adrs.add(example.getRelatedPhrase().getPhraseContent());
//					}
//				}
//				
//		
//			}
//			else if (method.equals(ExtractionMethod.MetaMap))
//			{
//				
////				found_adrs = MetaMap.getFoundADRsByMetaMap(sent,"lexicon");
//				found_adrs = MetaMap.getFoundADRsByMetaMap(sent,"semantic_type");
//				
//			}
			if (semanticType.equals("ADR"))
				
				eval = evaluateConcetpExtraction(sent, expected_adrs, found_adrs, tps, fps, fns, eval);
			else if (semanticType.equals("Indication"))
			{
				eval = evaluateConcetpExtraction(sent, expected_indications, found_indications, tps, fps, fns, eval);
			}
			
			if (!fps.isEmpty())
			{
				logFileLinesFp.add(sent.getContent());
				logFileLinesFp.add("TPs:" + Arrays.asList(tps.toArray()).toString());
				logFileLinesFp.add("FPs:" + Arrays.asList(fps.toArray()).toString());
				logFileLinesFp.add("FNs:" + Arrays.asList(fns.toArray()).toString());
			}
			if (!fns.isEmpty())
			{
				logFileLinesfn.add(sent.getContent());
				logFileLinesfn.add("TPs:" + Arrays.asList(tps.toArray()).toString());
				logFileLinesfn.add("FPs:" + Arrays.asList(fps.toArray()).toString());
				logFileLinesfn.add("FNs:" + Arrays.asList(fns.toArray()).toString());
			}
			if (!tps.isEmpty())
			{
				logFileLinestp.add(sent.getContent());
				logFileLinestp.add("TPs:" + Arrays.asList(tps.toArray()).toString());
				logFileLinestp.add("FPs:" + Arrays.asList(fps.toArray()).toString());
				logFileLinestp.add("FNs:" + Arrays.asList(fns.toArray()).toString());
			}
			if (!fns.isEmpty() || !fps.isEmpty())
			{
				logFileLinesFpFN.add(sent.getContent());
				logFileLinesFpFN.add("TPs:" + Arrays.asList(tps.toArray()).toString());
				logFileLinesFpFN.add("FPs:" + Arrays.asList(fps.toArray()).toString());
				logFileLinesFpFN.add("FNs:" + Arrays.asList(fns.toArray()).toString());
			}

			System.out.println(sent.getContent());
		}
		eval.getEvaluation();
		File tempTruePositives = File.createTempFile("logTruePositives-", Long.toString(System.currentTimeMillis()));
		File tempFalsePositives = File.createTempFile("logFalsePositives-", Long.toString(System.currentTimeMillis()));
		File tempFalseNegatives = File.createTempFile("logFalseNegatives-", Long.toString(System.currentTimeMillis()));
		File tempFalsePosNeg = File.createTempFile("logFalsePosNeg-", Long.toString(System.currentTimeMillis()));
		
		FileUtil.writeToFile(tempTruePositives, logFileLinestp);
		FileUtil.writeToFile(tempFalsePositives, logFileLinesFp);
		FileUtil.writeToFile(tempFalseNegatives, logFileLinesfn);
		FileUtil.writeToFile(tempFalsePosNeg, logFileLinesFpFN);
	}
	public static int CRFCount=0;
	public static Evaluation evaluateConcetpExtraction
				(Artifact sent, List<String> expectedPhraseCont, List<String> extractedPhraseCont,ArrayList<String> tps,
						ArrayList<String> fps,ArrayList<String> fns,Evaluation eval) throws UnsupportedEncodingException, FileNotFoundException
	{
		
		List<String> temp_found_adrs = extractedPhraseCont;
		List<String> tokenizedExpected = new ArrayList<>();
		
		//since we parsed the tweets after annotattion, correctly  extracted can be considered FP
		if (tokenizeExpectedADRs)
		{
			for (String expected: expectedPhraseCont)
			{
				tokenizedExpected.add(parser.getTokenizedSentence(expected));
			}
			
		}
		if (tokenizedExpected.isEmpty())
		{
			tokenizedExpected.addAll(expectedPhraseCont);
		}
		for (String expected:tokenizedExpected)
		{
			boolean expedted_found = false;
			System.out.println();
			for (String extracted:extractedPhraseCont)
			{
				String expected_clean = StringUtil.cleanString(expected);
				String extracted_clean = StringUtil.cleanString(extracted);
				if (expected_clean.matches(".*"+extracted_clean+".*")
						|| extracted_clean.matches(".*"+expected_clean+".*"))
				{
					eval.addTP();
					tps.add(sent.getArtifactId()+" expected:"+expected_clean +"====> extracted: "+extracted);
					temp_found_adrs.remove(extracted);
					expedted_found =true;
//					if (MainHybridADRConceptClassifier.isFoundByCRFExclusively(extracted,sent))
//					{
//						CRFCount++;
//						
//						FileUtil.appendLine("/tmp/justCRFTP",CRFCount+" "+ sent.getArtifactId()+" "+ sent.getContent());
//						FileUtil.appendLine("/tmp/justCRFTP", extracted);
//					}
//					else
//					{
//						FileUtil.appendLine("/tmp/SVMCRFTP", sent.getArtifactId()+" "+ sent.getContent());
//						FileUtil.appendLine("/tmp/SVMCRFTP", extracted);
//					}
					break;
				}
			}
			if (!expedted_found)
			{
				eval.addFN();
				countFN++;
//				fns.add(sent.getAssociatedFilePath()+"$$$$ "+expected);
				fns.add(countFN+": "+ expected);
			}
		}
		//up to here the tps are removed from the list and the rest are false positives
		for (String found:temp_found_adrs )
		{
			countFP++;
//			fps.add(sent.getAssociatedFilePath()+"$$$$  "+found);
			fps.add(countFP+": "+found);
			
			eval.addFP();
		}
		
		return eval;


	}
	 public static List<String> getExpectedADRs(Artifact sent) {
		List<String> expected= new ArrayList<>();
		ArrayList<Artifact> alreadyAdded = new ArrayList<>();
		 
		List<Phrase> annotations = Phrase.getPhrasesInSentence(sent);
		//TODO CHANGE
		for (Phrase p:annotations)
		{
			if(alreadyAdded.contains(p.getStartArtifact()) || alreadyAdded.contains(p.getEndArtifact()))
			{
				continue;
			}
			if (p.getPhraseEntityType().equals("ADR")
					|| p.getPhraseEntityType().toLowerCase().startsWith("int")
//					|| p.getPhraseEntityType().toLowerCase().startsWith("ind")
//					|| p.getPhraseEntityType().toLowerCase().startsWith("bene")
					)
			{
				String acceptedStringForApproximateMatch = p.getPhraseContent();
				//for now we try to aaccept two word after and two word before
				String next ="";
//				Artifact nextArtifact = p.getEndArtifact().getNextArtifact();
//				if (nextArtifact != null)
//				{
//					next = nextArtifact.getContent();
////					Artifact secondNext =nextArtifact.getNextArtifact();
////					if (secondNext != null)
////					{
////						next+=" "+secondNext.getContent();
////					}
//				}
				
//				String prev ="";
//				Artifact prevArtifact = p.getStartArtifact().getPreviousArtifact();
//				if (prevArtifact != null)
//				{
//					if (prevArtifact.getContent().matches("\\w+"))
//					{
//						prev = prevArtifact.getContent();
//					}
//					
////					Artifact secondPrev =prevArtifact.getPreviousArtifact();
////					
////					if (secondPrev != null)
////					{
////						prev = secondPrev.getContent()+" "+prev;
////					}
//				}
//				acceptedStringForApproximateMatch = prev+" " +acceptedStringForApproximateMatch+" "+next;
				expected.add(acceptedStringForApproximateMatch.trim());
				
//				Artifact cur = p.getStartArtifact();
//				while (!cur.equals(p.getEndArtifact()))
//				{
//					alreadyAdded.add(cur);
//					cur = cur.getNextArtifact();
//				}
//				alreadyAdded.add(p.getEndArtifact());
			}
		}
		return expected;
	}
	public static List<String> getExpectedIndications(Artifact sent) {
		List<String> expected= new ArrayList<>();
		ArrayList<Artifact> alreadyAdded = new ArrayList<>();
		 
		List<Phrase> annotations = Phrase.getPhrasesInSentence(sent);
		
		for (Phrase p:annotations)
		{
			if(alreadyAdded.contains(p.getStartArtifact()) || alreadyAdded.contains(p.getEndArtifact()))
			{
				continue;
			}
			if ( p.getPhraseEntityType().toLowerCase().startsWith("ind")
					|| p.getPhraseEntityType().toLowerCase().startsWith("bene")
					)
			{
				expected.add(p.getPhraseContent().trim());

			}
		}
		return expected;
	}

}
