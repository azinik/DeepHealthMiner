//package relationships;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import LuceneManagerADR.ADRDicIndex.LexiconSource;
//import drugExtraction.DrugNER;
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils;
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
//import portability.ConceptextractionOutPutManager;
//import rainbownlp.core.Artifact;
//import rainbownlp.core.Phrase;
//import rainbownlp.core.PhraseLink;
//import rainbownlp.core.Artifact.Type;
//import rainbownlp.machinelearning.MLExample;
//import rainbownlp.util.HibernateUtil;
//
//public class DrugADRRelations {
//	
//	public static void main(String[] args) throws Exception
//	{
//		createDrugADRRelations("dhm-lung-cancer-pd1", false);
//	}
//	public static void createDrugADRRelations(String corpusName, boolean forTrain) throws Exception
//	{
//		
//		//get all posts sentences
//		//get all ADRs and add to the posts-ADR map
//		//get all drugs and add to post drug map
//		//then for each post in th epostADR map, find the drugs in the post and create a relationship for each
//		
//		//get all sentences, from there list all ADRs and list have a map for drugs and for ADRs
//		//TO
//		HashMap<String, List<Phrase>> post_drug_map = new HashMap<>();
//		HashMap<String, List<Phrase>> post_adr_map = new HashMap<>();
//		
//		List<MLExample> examples = MLExample.getExamplesWithLabels(corpusName, null);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  ConceptextractionOutPutManager.getSentenceExampleMap(corpusName,examples);
//		
//		int count=0;
//		
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//		
//			List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,TargetSemanticType.ADR,false,true);
//			 //get related drugs and add to map
//			List<Phrase> extracted_drugs = DrugNER.getDrugPhrasesFromSentence(sent, 30, LexiconSource.ADRLex, DrugNER.luceneIndexPath);
//			List<Phrase> post_drugs = post_drug_map.get(sent.getAssociatedFilePath());
//			if (post_drugs==null)
//				post_drugs = new ArrayList<>();
//			
//			List<Phrase> post_adrs = post_adr_map.get(sent.getAssociatedFilePath());
//			if (post_adrs==null)
//				post_adrs = new ArrayList<>();
//			post_adrs.addAll(extracted_adrs);
//			post_drugs.addAll(extracted_drugs);
//			post_adr_map.put(sent.getAssociatedFilePath(), post_adrs);
//			post_drug_map.put(sent.getAssociatedFilePath(), post_drugs);
//			count++;
//			System.out.println(count+"/"+sent_example_map.size());
//			
//		}
//		List<Artifact> train_posts = Artifact.listByType(Type.Document,corpusName,forTrain);
//		for (Artifact doc:train_posts)
//		{
//			List<Phrase> adrs = post_adr_map.get(doc.getAssociatedFilePath());
//			List<Phrase> drugs = post_drug_map.get(doc.getAssociatedFilePath());
//			if (adrs==null || drugs==null)
//				continue;
//			HashMap<String, List<Phrase>> drug_map = new HashMap<>();
//			//we want to build the examples with the closest mention of the drug, for each drug mention, loop through the phrases and choose the one that makes the distance
//			for (Phrase drug:drugs)
//			{
//				List<Phrase> related_phrases = drug_map.get(drug.getPhraseContent());
//				if (related_phrases==null)
//					related_phrases = new ArrayList<>();
//				related_phrases.add(drug);
//				drug_map.put(drug.getPhraseContent(), related_phrases);
//				
//			}
//			//check for duplicate
//			for (Phrase adr:adrs)
//			{
//				for (String drug: drug_map.keySet())
//				{
//					//select the closet drug
//					List<Phrase> drug_phrases_in_post = drug_map.get(drug);
//					Phrase drug_target_phrase =drug_phrases_in_post.get(0);
//					
//					if (drug_phrases_in_post.size()>1)
//					{
//						int distance= Math.abs( adr.getStartArtifact().getStartIndex() - drug_target_phrase.getStartArtifact().getStartIndex());
//						
//						for (int i=1;i<drug_phrases_in_post.size();i++)
//						{
//							Phrase drug_mention = drug_phrases_in_post.get(i);
//							
//							int cur_distance =Math.abs( adr.getStartArtifact().getStartIndex() - drug_mention.getStartArtifact().getStartIndex());
//							if (cur_distance<distance)
//							{
//								distance=cur_distance;
//								drug_target_phrase = drug_mention;
//							}
//						}
//					}
//					
//					
//					//build example and save
//					//build for phrase 
//					PhraseLink phrase_link = 
//							PhraseLink.getInstance(adr, drug_target_phrase);
//					Integer expected_class = 0;
//
////					ConfigurationUtil.SaveInGetInstance = false;
//
//					MLExample link_example = 
//							MLExample.getInstanceForLink(phrase_link, corpusName+"_Link");
//					link_example.setExpectedClass(expected_class);
//					link_example.setRelatedPhraseLink(phrase_link);
//
//					link_example.setPredictedClass(-1);
//
//					link_example.setForTrain(forTrain);
//
//					MLExample.saveExample(link_example);
//
////					ConfigurationUtil.SaveInGetInstance = true;
//
////					link_example.calculateFeatures(featureCalculators);
////					FileUtil.logLine("debug.log","example processed: "+example_counter);
//					HibernateUtil.clearLoaderSession();	
//				}
//			}
//		}
//
//	}
//
//}
