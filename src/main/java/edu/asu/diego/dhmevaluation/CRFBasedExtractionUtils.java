package edu.asu.diego.dhmevaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
import rainbownlp.core.Artifact;
import rainbownlp.core.Phrase;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.FileUtil;
import rainbownlp.util.StringUtil;

public class CRFBasedExtractionUtils {
	public static String experimentgroup = TokenSequenceExampleBuilder.ExperimentGroupADRConcepts;
//	public statiSc String experimentgroupLexiconCand = LexiconCandidateSVMExampleBuilder.ExperimentGroupADRConceptsLexiconCandidates;
	public enum TargetSemanticType{
		ADR,
		Indication,
		//healthRelated
		ADR_Indication,
		Drug
	}

	public static void main (String args[])
	{

	}
	
	public static List<String> getCRFExtractedConceptsForSent(Artifact sent,List<MLExample> relatedExamples, String experimentGroup, TargetSemanticType targetSemanticType,
			boolean forTrain)
	{
		List<String> extracted = new ArrayList<String>();
		
//		List<Phrase> extractedPhrases =getCRFExtractedConceptPhrasesForSent(sent, experimentGroup, targetSemanticType,forTrain);
		List<Phrase> extractedPhrases = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
				(sent,relatedExamples,experimentGroup,targetSemanticType,false);
		for (Phrase p:extractedPhrases)
		{
			extracted.add(p.getPhraseContent());
		}
		
		return extracted;
	}
	public static List<String> getCRFExtractedADRsForSent(Artifact sent, String experimentGroup)
	 {
		List<String> found_phrases = new ArrayList<>();
		List<MLExample> sent_related_examples = MLExample.getTokenExamplesBySent(sent,experimentGroup,false); 
		
		if (sent_related_examples == null || sent_related_examples.isEmpty())
		{
			return found_phrases;
		}
		HashMap<Artifact, Integer> token_predicted_class_map = new HashMap<>();

		for (MLExample example:sent_related_examples)
		{
			Integer predicted = Integer.parseInt(example.getPredictedClass());
			token_predicted_class_map.put(example.getRelatedArtifact(), predicted);
		}
		
		List<Artifact> sent_child_tokens = sent.getChildsArtifact();
		List<Artifact> analysed_tokens = new ArrayList<>();
		
		for(Artifact child_token :sent_child_tokens)
		{
			if (analysed_tokens.contains(child_token)) continue;
			
			Integer predicted = token_predicted_class_map.get(child_token);
			if (predicted ==null)
			{
				analysed_tokens.add(child_token);
				continue;
			}
			//the commented section is for health related
//			if (predicted ==1 || predicted ==3)
			if (predicted ==1)
			{
				analysed_tokens.add(child_token);
				// build the phrase
				Artifact start_token = child_token;
				Artifact end_token = child_token;
				
				Artifact current = start_token;
				
				Artifact nextArtifact = start_token.getNextArtifact();
				Integer predicted_next = token_predicted_class_map.get(nextArtifact);
				
//				while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
				while (predicted_next != null && predicted_next ==2)
				{
					current = nextArtifact;
					analysed_tokens.add(current);
					nextArtifact = current.getNextArtifact();
					predicted_next = token_predicted_class_map.get(nextArtifact);
				}
				end_token = current;
				String found_adr ="";
				Artifact cur_found = start_token;
				while (!cur_found.equals(end_token))
				{
					found_adr += " "+cur_found.getContent();
					cur_found = cur_found.getNextArtifact();
				}
				found_adr += " "+end_token.getContent();
				found_adr= found_adr.trim();
				found_phrases.add(found_adr);
			}
			// It reaches here if p==2 but previous is not 1 or 2
//			if (predicted ==2 || predicted ==4)
			if (predicted ==2 )
			{
				Artifact previous = child_token.getPreviousArtifact();
				if (previous != null)
				{
					Artifact start_token=child_token;
					Integer predicted_prev = token_predicted_class_map.get(previous);
					if (predicted_prev ==null)
					{
						start_token = previous;
					}
				}
				Artifact start_token = child_token;
				Artifact end_token = start_token;
				Artifact current = start_token;
				Artifact nextArtifact = child_token.getNextArtifact();
				Integer predicted_next = token_predicted_class_map.get(nextArtifact);
				//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
				while (predicted_next != null && (predicted_next ==2))
				{
					current = nextArtifact;
					analysed_tokens.add(current);
					nextArtifact = current.getNextArtifact();
					predicted_next = token_predicted_class_map.get(nextArtifact);
				}
				end_token = current;
				
				String found_adr ="";
				Artifact cur_found = start_token;
				while (cur_found != end_token)
				{
					found_adr += " "+cur_found.getContent();
					cur_found = cur_found.getNextArtifact();
				}
				found_adr += " "+end_token.getContent();
				found_adr= found_adr.trim();
				found_phrases.add(found_adr);
			}
		}

		
		return found_phrases;
		 
	 }
	public static List<Phrase> getCRFExtractedConceptPhrasesForSent(Artifact sent, List<MLExample> relatedExamples,String experimentGroup, TargetSemanticType targetSemanticType,
			boolean forTrain)
	 {
		List<Phrase> found_phrases = new ArrayList<Phrase>();

		
		if (relatedExamples == null || relatedExamples.isEmpty())
		{
			return found_phrases;
		}
		List<MLExample> analysed_examples = new ArrayList<MLExample>();
		
		for(int count=0;count<relatedExamples.size();count++)
		{
			MLExample child_example= relatedExamples.get(count);
			
			if (analysed_examples.contains(child_example)) continue;
			
			Integer predicted = Integer.parseInt(child_example.getPredictedClass());
			
			if (predicted ==-1 || predicted ==0)
			{
				analysed_examples.add(child_example);
				continue;
			}
			String found_adr ="";
			if (targetSemanticType.equals(TargetSemanticType.ADR))
			{
				if (predicted ==1 )
				{
					
					
					// build the phrase
					Artifact start_token = child_example.getRelatedArtifact();
					Artifact end_token = start_token;
					
					MLExample current = child_example;
					found_adr += " "+current.getRelatedArtifact().getContent();
					analysed_examples.add(child_example);
					int phrase_token_index=count+1;
					
					if (phrase_token_index<relatedExamples.size())
					{
						MLExample nextExample = relatedExamples.get(phrase_token_index);
						Integer predicted_next = Integer.parseInt(nextExample.getPredictedClass());
						
						while (predicted_next != null && predicted_next ==2)
						{
							current = nextExample;
							
							found_adr += " "+current.getRelatedArtifact().getContent();
							analysed_examples.add(current);
							phrase_token_index++;
							if (phrase_token_index<relatedExamples.size())
							{
								nextExample = relatedExamples.get(phrase_token_index);
								predicted_next = Integer.parseInt(nextExample.getPredictedClass());
							}
							else
								break;		
						}
						end_token = current.getRelatedArtifact();
				
					}		
					
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
				//TODO: test this
				// It reaches here if p==2 but previous is not 1 or 2
//				else if (predicted ==2 )
//				{
//					Artifact previous = child_token.getPreviousArtifact();
//					if (previous != null)
//					{
//						Artifact start_token=child_token;
//						Integer predicted_prev = token_predicted_class_map.get(previous);
////						TODO//What is this?
//						if (predicted_prev ==null)
//						{
//							start_token = previous;
//						}
//					}
//					Artifact start_token = child_token;
//					Artifact end_token = start_token;
//					Artifact current = start_token;
//					Artifact nextArtifact = child_token.getNextArtifact();
//					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
//					//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
//					while (predicted_next != null && (predicted_next ==2))
//					{
//						current = nextArtifact;
//						analysed_tokens.add(current);
//						nextArtifact = current.getNextArtifact();
//						predicted_next = token_predicted_class_map.get(nextArtifact);
//					}
//					end_token = current;
//					
//					String found_adr ="";
//					Artifact cur_found = start_token;
//					while (cur_found != end_token)
//					{
//						found_adr += " "+cur_found.getContent();
//						cur_found = cur_found.getNextArtifact();
//					}
//					found_adr += " "+end_token.getContent();
//					found_adr= found_adr.trim();
//					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
//					
//					found_phrases.add(extracted_phrase);
//				}
			}
		}
		
		
		
		//TODO: complete for indication and helathrelated
		return found_phrases;
		 
	 }
	public static List<Phrase> getCRFExtractedConceptPhrasesForSent(Artifact sent, List<MLExample> relatedExamples,String experimentGroup, TargetSemanticType targetSemanticType,
			boolean forTrain,boolean InsideEExamplesFetched)
	 {
		List<Phrase> found_phrases = new ArrayList<Phrase>();

		
		if (relatedExamples == null || relatedExamples.isEmpty())
		{
			return found_phrases;
		}
		List<MLExample> analysed_examples = new ArrayList<MLExample>();
		
		for(int count=0;count<relatedExamples.size();count++)
		{
			MLExample child_example= relatedExamples.get(count);
			
			if (analysed_examples.contains(child_example)) continue;
			
			Integer predicted = Integer.parseInt(child_example.getPredictedClass());
			
			if (predicted ==-1 || predicted ==0)
			{
				analysed_examples.add(child_example);
				continue;
			}
			String found_adr ="";
			if (targetSemanticType.equals(TargetSemanticType.ADR))
			{
				if (predicted ==1 )
				{
					
					
					// build the phrase
					Artifact start_token = child_example.getRelatedArtifact();
					Artifact end_token = start_token;
					
					MLExample current = child_example;
					found_adr += " "+current.getRelatedArtifact().getContent();
					analysed_examples.add(child_example);
					int phrase_token_index=count+1;
					
					
					if (phrase_token_index<relatedExamples.size())
					{
						MLExample nextExample = relatedExamples.get(phrase_token_index);
						Integer predicted_next = Integer.parseInt(nextExample.getPredictedClass());
						Artifact nextToken = nextExample.getRelatedArtifact();
						Artifact cur_token =current.getRelatedArtifact();
						
						while (predicted_next != null && predicted_next ==2 && cur_token.getNextArtifact()==nextToken)
						{
							current = nextExample;
							
							found_adr += " "+current.getRelatedArtifact().getContent();
							analysed_examples.add(current);
							phrase_token_index++;
							if (phrase_token_index<relatedExamples.size())
							{
								nextExample = relatedExamples.get(phrase_token_index);
								predicted_next = Integer.parseInt(nextExample.getPredictedClass());
							}
							else
								break;		
						}
						end_token = current.getRelatedArtifact();
				
					}		
					
					found_adr= found_adr.trim();
					ConfigurationUtil.DBDetachedMode = false;
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
				//TODO: complete this for the examples that are just inside entity
				// It reaches here if p==2 but previous is not 1 or 2
				else if (predicted ==2 )
				{
					System.out.println("we are here");
				}
//					Artifact previous = child_token.getPreviousArtifact();
//					if (previous != null)
//					{
//						Artifact start_token=child_token;
//						Integer predicted_prev = token_predicted_class_map.get(previous);
////						TODO//What is this?
//						if (predicted_prev ==null)
//						{
//							start_token = previous;
//						}
//					}
//					Artifact start_token = child_token;
//					Artifact end_token = start_token;
//					Artifact current = start_token;
//					Artifact nextArtifact = child_token.getNextArtifact();
//					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
//					//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
//					while (predicted_next != null && (predicted_next ==2))
//					{
//						current = nextArtifact;
//						analysed_tokens.add(current);
//						nextArtifact = current.getNextArtifact();
//						predicted_next = token_predicted_class_map.get(nextArtifact);
//					}
//					end_token = current;
//					
//					String found_adr ="";
//					Artifact cur_found = start_token;
//					while (cur_found != end_token)
//					{
//						found_adr += " "+cur_found.getContent();
//						cur_found = cur_found.getNextArtifact();
//					}
//					found_adr += " "+end_token.getContent();
//					found_adr= found_adr.trim();
//					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
//					
//					found_phrases.add(extracted_phrase);
//				}
			}
			//TODO: handle multi token drugs
			else if (targetSemanticType.equals(TargetSemanticType.Drug))
			{
				if (predicted ==TokenSequenceExampleBuilder.TokenLabel.BDRUG.ordinal() )
				{
					
					
					// build the phrase
					Artifact start_token = child_example.getRelatedArtifact();
					Artifact end_token = start_token;
					ConfigurationUtil.DBDetachedMode = false;
					Phrase extracted_phrase =Phrase.getInstance(start_token.getContent(), start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
					
			}
		}
		
		
		
		//TODO: complete for indication and helathrelated
		return found_phrases;
		 
	 }

	public static List<Phrase> getCRFExtractedConceptPhrasesForSent(Artifact sent, String experimentGroup, TargetSemanticType targetSemanticType,
			boolean forTrain)
	 {
		List<Phrase> found_phrases = new ArrayList<Phrase>();
		List<MLExample> sent_related_examples = MLExample.getTokenExamplesBySent(sent,experimentGroup,forTrain); 
		
		if (sent_related_examples == null || sent_related_examples.isEmpty())
		{
			return found_phrases;
		}
		HashMap<Artifact, Integer> token_predicted_class_map = new HashMap<Artifact, Integer>();

		for (MLExample example:sent_related_examples)
		{
			Integer predicted = Integer.parseInt(example.getPredictedClass());
			token_predicted_class_map.put(example.getRelatedArtifact(), predicted);
		}
		
		List<Artifact> sent_child_tokens = sent.getChildsArtifact();
		List<Artifact> analysed_tokens = new ArrayList<Artifact>();
		
		for(Artifact child_token :sent_child_tokens)
		{
			if (analysed_tokens.contains(child_token)) continue;
			
			Integer predicted = token_predicted_class_map.get(child_token);
			if (predicted ==null)
			{
				analysed_tokens.add(child_token);
				continue;
			}
			if (targetSemanticType.equals(TargetSemanticType.ADR))
			{
				if (predicted ==1)
				{
					
					analysed_tokens.add(child_token);
					// build the phrase
					Artifact start_token = child_token;
					Artifact end_token = child_token;
					
					Artifact current = start_token;
					
					Artifact nextArtifact = start_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					
					while (predicted_next != null && predicted_next ==2)
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;

					String found_adr ="";
					Artifact cur_found = start_token;
					while (!cur_found.equals(end_token))
					{
						found_adr += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_adr += " "+end_token.getContent();
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
				// It reaches here if p==2 but previous is not 1 or 2
				else if (predicted ==2 )
				{
					Artifact previous = child_token.getPreviousArtifact();
					if (previous != null)
					{
						Artifact start_token=child_token;
						Integer predicted_prev = token_predicted_class_map.get(previous);
//						TODO//What is this?
						if (predicted_prev ==null)
						{
							start_token = previous;
						}
					}
					Artifact start_token = child_token;
					Artifact end_token = start_token;
					Artifact current = start_token;
					Artifact nextArtifact = child_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
					while (predicted_next != null && (predicted_next ==2))
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;
					
					String found_adr ="";
					Artifact cur_found = start_token;
					while (cur_found != end_token)
					{
						found_adr += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_adr += " "+end_token.getContent();
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
			}
			//health related
			else if (targetSemanticType.equals(TargetSemanticType.ADR_Indication))
			{
				//the commented section is for health related
				if (predicted ==1 || predicted ==3)
				{
					analysed_tokens.add(child_token);
					// build the phrase
					Artifact start_token = child_token;
					Artifact end_token = child_token;
					
					Artifact current = start_token;
					
					Artifact nextArtifact = start_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					
					while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;
					String found_adr ="";
					Artifact cur_found = start_token;
					while (!cur_found.equals(end_token))
					{
						found_adr += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_adr += " "+end_token.getContent();
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);

				}
				// It reaches here if p==2 but previous is not 1 or 2
				if (predicted ==2 || predicted ==4)
				{
					Artifact previous = child_token.getPreviousArtifact();
					if (previous != null)
					{
						Artifact start_token=child_token;
						Integer predicted_prev = token_predicted_class_map.get(previous);
						if (predicted_prev ==null)
						{
							start_token = previous;
						}
					}
					Artifact start_token = child_token;
					Artifact end_token = start_token;
					Artifact current = start_token;
					Artifact nextArtifact = child_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
					while (predicted_next != null && (predicted_next ==2))
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;
					
					String found_adr ="";
					Artifact cur_found = start_token;
					while (cur_found != end_token)
					{
						found_adr += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_adr += " "+end_token.getContent();
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
			}
			else if (targetSemanticType.equals(TargetSemanticType.Indication))
			{
				//the commented section is for health related
//				if (predicted ==1 || predicted ==3)
				if (predicted ==3)
				{
					analysed_tokens.add(child_token);
					// build the phrase
					Artifact start_token = child_token;
					Artifact end_token = child_token;
					
					Artifact current = start_token;
					
					Artifact nextArtifact = start_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					
//					while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
					while (predicted_next != null && predicted_next ==4)
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;
					String found_adr ="";
					Artifact cur_found = start_token;
					while (!cur_found.equals(end_token))
					{
						found_adr += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_adr += " "+end_token.getContent();
					found_adr= found_adr.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_adr, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
				// It reaches here if p==2 but previous is not 1 or 2
//				if (predicted ==2 || predicted ==4)
				if (predicted ==4 )
				{
					Artifact previous = child_token.getPreviousArtifact();
					if (previous != null)
					{
						Artifact start_token=child_token;
						Integer predicted_prev = token_predicted_class_map.get(previous);
						if (predicted_prev ==null)
						{
							start_token = previous;
						}
					}
					Artifact start_token = child_token;
					Artifact end_token = start_token;
					Artifact current = start_token;
					Artifact nextArtifact = child_token.getNextArtifact();
					Integer predicted_next = token_predicted_class_map.get(nextArtifact);
					//while (predicted_next != null && (predicted_next ==2 || predicted_next ==4))
					while (predicted_next != null && ( predicted_next ==4))
					{
						current = nextArtifact;
						analysed_tokens.add(current);
						nextArtifact = current.getNextArtifact();
						predicted_next = token_predicted_class_map.get(nextArtifact);
					}
					end_token = current;
					
					String found_indication ="";
					Artifact cur_found = start_token;
					while (cur_found != end_token)
					{
						found_indication += " "+cur_found.getContent();
						cur_found = cur_found.getNextArtifact();
					}
					found_indication += " "+end_token.getContent();
					found_indication= found_indication.trim();
					Phrase extracted_phrase =Phrase.getInstance(found_indication, start_token, end_token,"Extracted");
					
					found_phrases.add(extracted_phrase);
				}
			}

		}	
		
		return found_phrases;
		 
	 }


	public static String getTextIdFromFilePath(String file_path)
	{
		String text_id = "";
//		/home/azadeh/projects/java/drug-effect-ext/data/off-label-IndivTextFiles-zyprexa/287.txt
		String pattern = ".*\\/(.*)\\.txt$";
	    Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(file_path);
	    if (m.matches())
	    {
	    	text_id = m.group(1);
	    }
		return text_id;
	}

}
