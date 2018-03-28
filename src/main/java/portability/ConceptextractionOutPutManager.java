//package portability;
//
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.lang3.StringUtils;
//
//import LuceneManagerADR.ADRDicIndex.LexiconSource;
//import drugExtraction.DrugNER;
//import edu.asu.diego.adrminecore.ADRLexiconEntry;
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils;
//import edu.asu.diego.dhmevaluation.CRFBasedExtractionUtils.TargetSemanticType;
//import edu.asu.diego.normalization.SemanticVectorLexiconCandidateSVMExampleBuilder;
//import edu.asu.diego.dhmevaluation.MainConceptExtractionEvaluation;
//import edu.asu.diego.dhmutils.Negation;
//import rainbownlp.core.Artifact;
//import rainbownlp.core.Artifact.Type;
//import rainbownlp.core.Phrase;
//import rainbownlp.machinelearning.MLExample;
//import rainbownlp.util.FileUtil;
//import rainbownlp.util.HibernateUtil;
//
//
//
//public class ConceptextractionOutPutManager {
//	public static String experimentgroup = MainConceptExtractionEvaluation.experimentgroup;
//	
//	
////	public enum ExtractionMethod {
////		LuceneSearchLexicon, SemVecSVM, CRF, SVMCRF,MetaMap,CRFSemanticType,SVMSemanticType,SemVecCRF
////	}
//
//	public static void main (String args[]) throws Exception
//	{
//		generateBratTextAnnInput(TargetSemanticType.ADR, "dhm-oncology-cancer-groups-train", null);
////		generateAnnotationFormatOutPut(TargetSemanticType.ADR_Indication);
////		generateAnnotationFormatOutPut(TargetSemanticType.ADR);
////		generateBratTextAnnInput(TargetSemanticType.ADR, "dhm-lung-cancer-pd1", 100);
////		generateBratTextAnnInput(TargetSemanticType.ADR, "dhm-lung-cancer-tarceva", 100);
//		
//	}
//	public static void generateAnnotationFormatOutPut(TargetSemanticType targetSemanticType, String expermentGroup) throws IOException
//	
//	{
//		System.out.println("Generating output results ... ");
//		HibernateUtil.clearLoaderSession();
//		List<String> output_extracted_ann_format_lines = new ArrayList<>();
//		
////		List<MLExample> testExamples = 
////				MLExample.getAllExamples(expermentGroup, false);
////		List<Artifact> testSentences = new ArrayList<>();
//		//generating the test sentences
//		//just get all as
////		for (MLExample test_e:testExamples)
////		{
////			Artifact related_sent = test_e.getRelatedArtifact().getParentArtifact();
////			if (!testSentences.contains(related_sent))
////			{
////				testSentences.add(related_sent);
////			}
////	
////		}
//		List<Artifact> testSentences = Artifact.listByTypeByForTrain(Type.Sentence, false);
//		String resultFileTemp =  File.createTempFile("ADRMineOutput_", ".tsv").getPath();
//		System.out.println("Extracted mentions are saved in: "+resultFileTemp);
//		int count=0;
//		for (Artifact sent:testSentences)
//		{
//			System.out.println("printing the output ADRs "+count+"/"+testSentences.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,expermentGroup,targetSemanticType,false);
//			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
//				 String line= text_id+"\t"+extracted_p.getStartCharOffset()+"\t"+
//						 extracted_p.getEndCharOffset()+"\t"+
//						 "ADR\t"+
//						 extracted_p.getPhraseContent()+"\t"+sent.getContent();
////				 output_extracted_ann_format_lines.add(line);
//				 FileUtil.appendLine(resultFileTemp, line);
//			 }
////			 HibernateUtil.clearLoaderSession();
//		
//		}
//		
//		
//		FileUtil.createFile(resultFileTemp, output_extracted_ann_format_lines);
//		
//	}
//	public static void generateAnnotationFormatOutPutByCorpus(TargetSemanticType targetSemanticType, String corpusName) throws IOException
//	
//	{
//		System.out.println("Generating output results ... ");
////		HibernateUtil.clearLoaderSession();
//
//		List<MLExample> examples = MLExample.getExamplesWithLabels(corpusName,null);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		String resultFileTemp =  File.createTempFile("DHM_Output_"+corpusName+"_", ".tsv").getPath();
//		
//		System.out.println("Extracted mentions are saved in: "+resultFileTemp);
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			
//			System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
////			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 //TODO
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
////				 String line= text_id+"\t"+extracted_p.getStartCharOffset()+"\t"+
////						 extracted_p.getEndCharOffset()+"\t"+
////						 "ADR\t"+
////						 extracted_p.getPhraseContent()+"\t"+sent.getContent();
//				 
//				 String line= text_id+"\t"+
//						 extracted_p.getPhraseContent();
////				 output_extracted_ann_format_lines.add(line);
//				 FileUtil.appendLine(resultFileTemp, line);
//			 }
////			 HibernateUtil.clearLoaderSession();
//		
//		}
//		
//		
////		FileUtil.createFile(resultFileTemp, output_extracted_ann_format_lines);
//		
//	}
//	public static String generateNEROutPutByCorpus(TargetSemanticType targetSemanticType, String corpusName) throws IOException
//	
//	{
//		System.out.println("Generating output results ... ");
////		HibernateUtil.clearLoaderSession();
//
//		List<MLExample> examples = MLExample.getExamplesWithLabels(corpusName,null);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		String resultFileTemp =  File.createTempFile("DHM_Output_"+corpusName+"_", ".tsv").getPath();
//		
//		System.out.println("Extracted mentions are saved in: "+resultFileTemp);
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			
//			System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
//
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
//				 String line= text_id+"\t"+
//						 extracted_p.getPhraseContent()+"\t"+targetSemanticType.toString()+"\t"+corpusName;
////				 disc_text#40932#zometa_cg@91615
//				 String post_id = StringUtils.substringBetween(text_id, "#");
//				 String inspire_table = StringUtils.substringBefore(text_id, "#");
//				 String author_uid = StringUtils.substringAfter(text_id,"@");
//					
//				 line+="\t"+inspire_table+"\t"+post_id+"\t"+author_uid;
//
//				 FileUtil.appendLine(resultFileTemp, line);
//			 }
//			 HibernateUtil.clearLoaderSession();
//		
//		}
//
//		return resultFileTemp;
//	}
//public static String generateNEROutPutByCorpus
//	(TargetSemanticType targetSemanticType, String corpusName, List<MLExample> examples) throws IOException
//	
//	{
//		System.out.println("Generating the file saving the extracted mentions ... ");
////		HibernateUtil.clearLoaderSession();
//
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		String resultFileTemp =  File.createTempFile("DHM_Output_"+corpusName+"_", ".tsv").getPath();
//		List<String> result_lines = new ArrayList<>();
//		
//		System.out.println("Extracted mentions are saved in: "+resultFileTemp);
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			if (count%1000==0)
//				System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
//
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
//				 String line= text_id+"\t"+
//						 extracted_p.getPhraseContent()+"\t"+targetSemanticType.toString()+"\t"+corpusName;
////				 disc_text#40932#zometa_cg@91615
//				 String post_id = StringUtils.substringBetween(text_id, "#");
//				 String inspire_table = StringUtils.substringBefore(text_id, "#");
//				 String author_uid = StringUtils.substringAfter(text_id,"@");
//					
//				 line+="\t"+inspire_table+"\t"+post_id+"\t"+author_uid;
//
////				 FileUtil.appendLine(resultFileTemp, line);
//				 result_lines.add(line);
//			 }
//			 HibernateUtil.clearLoaderSession();
//		
//		}
//		FileUtil.createFile(resultFileTemp, result_lines);
//		return resultFileTemp;
//	}
// public static void generateBratFormatOutPutByCorpus(TargetSemanticType targetSemanticType, String corpusName, String resultFile, int limit) throws IOException
//	
//	{
//		System.out.println("Generating output results ... ");
////		HibernateUtil.clearLoaderSession();
//
//		List<MLExample> examples = MLExample.getRandomExamplesWithLabels(corpusName,limit);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		
//		
//		System.out.println("Extracted mentions are saved in: "+resultFile);
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			
//			System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
////			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 //TODO
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
////				 String line= text_id+"\t"+extracted_p.getStartCharOffset()+"\t"+
////						 extracted_p.getEndCharOffset()+"\t"+
////						 "ADR\t"+
////						 extracted_p.getPhraseContent()+"\t"+sent.getContent();
//				 
//				 String line= text_id+"\t"+
//						 extracted_p.getPhraseContent();
////				 output_extracted_ann_format_lines.add(line);
//				 FileUtil.appendLine(resultFile, line);
//			 }
////			 HibernateUtil.clearLoaderSession();
//		
//		}
//		
//		
////		FileUtil.createFile(resultFileTemp, output_extracted_ann_format_lines);
//		
//	}
// public static void generateBratTextAnnInput(TargetSemanticType targetSemanticType, String corpusName, Integer limit) throws Exception
//	
//	{
//	 	String dir_path = "/Users/azadehn/Desktop/"+corpusName+"-train-set-an";
//	 	String log_file_path = "/Users/azadehn/Desktop/"+corpusName+"-train-posts.txt";
//	 	FileUtil.createFolderIfNotExists(dir_path);
//	 	List<String> log_applied_paths = new ArrayList<>();
////		HibernateUtil.clearLoaderSession();
//	 	List<String> target_paths = new ArrayList<>();
//	 	List<String> paths = updateDrugExamplesAndReturnPaths(corpusName,limit);
//	 	HibernateUtil.clearLoaderSession();
//	 	
//		List<MLExample> updated_examples =  MLExample.getExamplesByPaths(corpusName,paths );
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,updated_examples);
//		List<Artifact> updated_docs = getRelatedDocs(sent_example_map.keySet());
//		
//		int count = 0;
//		int entity_count = 0;
//		
//		for (Artifact doc: updated_docs)
//		{
//			entity_count=0;
//			
//			List<Artifact> rel_sents = doc.getChildsArtifact();
//			
//			List<String> text_file_lines = new ArrayList<>();
//			List<String> ann_file_lines = new ArrayList<>();
//			
//			for (Artifact sent:rel_sents)
//			{
//				String sent_content = sent.getContent();
//				sent_content = sent_content.replaceAll("\\\\","");
//				
//				text_file_lines.add(sent_content);
//				
//				//for annotation file
//				List<MLExample> rel_examples = sent_example_map.get(sent);
//								
//				List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//						(sent,rel_examples,corpusName,targetSemanticType,false,true);
//				List<Phrase> extracted_drugs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//							(sent,rel_examples,corpusName,TargetSemanticType.Drug,false,true);
//
//				 
//				
//				 for (Phrase e:extracted_adrs)
//				 {
//					 Integer start = e.getStartArtifact().getStartIndex();
//					 int end= e.getEndArtifact().getEndIndex()+1;
//					 String p_content = e.getPhraseContent().replaceAll("\\\\","");
////					 T1	Organization 0 4	Sony
//					 String ann_line = "T"+entity_count+"\t"+targetSemanticType.ADR.toString()+" "+
//							 start+" "+end+"\t"+p_content;
//					 ann_file_lines.add(ann_line);
//					 entity_count++;
//				 }
//				 for (Phrase e:extracted_drugs)
//				 {
//					 Integer start = e.getStartArtifact().getStartIndex();
//					 int end= e.getEndArtifact().getEndIndex()+1;
//					 String p_content = e.getPhraseContent().replaceAll("\\\\","");
////					 T1	Organization 0 4	Sony
//					 String ann_line = "T"+entity_count+"\t"+targetSemanticType.Drug.toString()+
//							 " "+start+" "+end+"\t"+p_content;
//					 ann_file_lines.add(ann_line);
//					 entity_count++;
//				 }
//			}
//			FileUtil.createFile(dir_path+"/"+doc.getAssociatedFilePath()+".txt", text_file_lines);
//			FileUtil.createFile(dir_path+"/"+doc.getAssociatedFilePath()+".ann", ann_file_lines);
//			log_applied_paths.add(doc.getAssociatedFilePath());
//			
//			count++;
//			System.out.println(count+"/"+updated_docs.size());
//						
//		} 
//		FileUtil.createFile(log_file_path, log_applied_paths);
//				
//	}
// 
//	 public static List<String> updateDrugExamplesAndReturnPaths(String corpusName, Integer limit) throws Exception{
//		 	List<MLExample> examples = new ArrayList();
//		 	if (limit==null)
//		 	{
//		 		examples = MLExample.getAllExamples(corpusName, false);
//		 	}
//		 	else
//		 	{
//		 		examples = MLExample.getRandomExamplesWithLabels(corpusName,limit);
//		 	}
//			
//			
////			HashMap<String, List<Artifact>> doc_sent_map = getDocumentSentenceMap(sent_example_map.keySet());
//			//This may not be optimized -- will improve 
//			List<Artifact> rel_docs =getRelatedDocs(examples);
//			List<String> file_paths = new ArrayList<>();
//			
//			for (Artifact doc: rel_docs)
//			{
//				
//				List<Artifact> rel_sents = doc.getChildsArtifact();
//				
//				for (Artifact sent:rel_sents)
//				{
//					DrugNER.extractDrugsFromIndexAndUpdate(sent);				
//				}
//				file_paths.add(doc.getAssociatedFilePath());			
//			} 
//			HibernateUtil.clearLoaderSession();
//			return file_paths;
//	 }
//	 public static HashMap<String, List<Artifact>> getDocumentSentenceMap(Set<Artifact> sents) {
//		HashMap<String, List<Artifact>> doc_sent_map = new HashMap<>();
//		for (Artifact sent:sents )
//		{
//			String file_path = sent.getAssociatedFilePath();
//			
//			List<Artifact> rel_sents = (List<Artifact>) ((doc_sent_map.get(file_path))==null?new ArrayList<>():doc_sent_map.get(file_path));
//			rel_sents.add(sent);
//			doc_sent_map.put(file_path, rel_sents);
//		}
//		return doc_sent_map;
//	}
//	 public static List<Artifact> getRelatedDocs(Set<Artifact> sents) {
//		List<Artifact> docs = new ArrayList<>();
//		
//		for (Artifact sent:sents )
//		{
//			Artifact rel_sent = sent.getParentArtifact();
//			
//			if (!docs.contains(rel_sent))
//				docs.add(rel_sent);
//		}
//		return docs;
//	}
//	 public static List<Artifact> getRelatedDocs(List<MLExample> examples) {
//		List<Artifact> docs = new ArrayList<>();
//		
//		for (MLExample e:examples )
//		{
//			Artifact rel_doc = e.getRelatedArtifact().getParentArtifact().getParentArtifact();
//			
//			if (!docs.contains(rel_doc))
//				docs.add(rel_doc);
//		}
//		return docs;
//	}
////get examples with labels, form the sentences, create a map of filepath and list of sentences
// //create a folder
// //crete the text file and the annotation filescp to server and ask him to do so
//	public static List<Phrase> filterMentionsForRelationshipExtraction(Artifact sent,List<Phrase> extracted_mentions,
//			List<String> target_drugs) throws Exception
//	{
//		if (extracted_mentions.isEmpty())
//			return extracted_mentions;
//		
//		List<Phrase> filtered_mentions = new ArrayList<>();
//		
//		//get drugs for in the sentece
//		List<ADRLexiconEntry> found_drugs = SemanticVectorLexiconCandidateSVMExampleBuilder.getExtractedLexEntriesFromTextSpan
//				(sent.getContent(), 60, LexiconSource.ADRLex, DrugNER.luceneIndexPath, false,true);
//		boolean filter_out_sent = false;
//		if (found_drugs!=null && !found_drugs.isEmpty())
//		{
//			boolean has_target_drug = false;
//			for (ADRLexiconEntry drug: found_drugs)
//			{
//				String drug_name = drug.getContent();
//				if (target_drugs.contains(drug_name))
//				{
//					has_target_drug=true;
//					break;
//				}
//				
//			}
//			for (ADRLexiconEntry drug: found_drugs)
//			{
//				String drug_name = drug.getContent();
//				if (!target_drugs.contains(drug_name) 
//						&& !has_target_drug)
//				{
//					filter_out_sent=true;
//					break;
//				}
//				
//			}
//		}
//		if (filter_out_sent)
//		{
//			return new ArrayList<Phrase>();
//		}
//		//for negation
//		for (Phrase p: extracted_mentions)
//		{
//			boolean is_negated = Negation.isWordNegatedForNER(p.getStartArtifact(),sent);
//			if (is_negated){
//				continue;
//			}
//			else
//			{
//				filtered_mentions.add(p);
//			}
//		}
//		
//		return filtered_mentions;
//	}
//public static void generateAnnotationFormatOutPutByCorpus(TargetSemanticType targetSemanticType, String corpusName,
//		boolean filterForRelations,List<String> targetDrugs ) throws Exception
//	
//	{
//		System.out.println("Generating output results ... ");
//
//		List<MLExample> examples = MLExample.getExamplesWithLabels(corpusName, null);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		String resultFileTemp =  File.createTempFile("DHM_Output_"+corpusName+"_Filter"+filterForRelations, ".tsv").getPath();
//		
//		System.out.println("Extracted mentions are saved in: "+resultFileTemp);
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			
//			System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
//			 if (filterForRelations)
//			 {
//				 extracted_adrs = filterMentionsForRelationshipExtraction
//						 (sent, extracted_adrs, targetDrugs);
//			 }
////			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 //TODO
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
////				 String line= text_id+"\t"+extracted_p.getStartCharOffset()+"\t"+
////						 extracted_p.getEndCharOffset()+"\t"+
////						 "ADR\t"+
////						 extracted_p.getPhraseContent()+"\t"+sent.getContent();
//				 
//				 String line= text_id+"\t"+
//						 extracted_p.getPhraseContent();
////				 output_extracted_ann_format_lines.add(line);
//				 FileUtil.appendLine(resultFileTemp, line);
//			 }
////			 HibernateUtil.clearLoaderSession();
//		
//		}
//		
//		
////		FileUtil.createFile(resultFileTemp, output_extracted_ann_format_lines);
//		
//	}
//
//	//This is just for report and manual review of extractions
//	public static void generateDetailedOutPutByCorpus(TargetSemanticType targetSemanticType, String corpusName, boolean limitMentions) throws IOException
//	
//	{
//		String resultFile = "/Users/azadehn/Desktop/extraction-evaluation-pd1.tsv";
//		List<MLExample> examples = MLExample.getExamplesWithLabels(corpusName,null);
//		HashMap<Artifact, List<MLExample>> sent_example_map =  getSentenceExampleMap(corpusName,examples);
//		
//		HashMap<String,String> path_doc_map = new HashMap<>();
//	
//		
//		int count=0;
//		for (Artifact sent:sent_example_map.keySet())
//		{
//			
////			String doc = path_doc_map.get(sent.getAssociatedFilePath());
////			if (doc==null)
////			{
//////				List<Artifact> all_sents =  Artifact.listByTypeByFilePath(Type.Sentence,corpusName,sent.getAssociatedFilePath());
////				String getSentences = "from Artifact where artifactType = 1 "
////				+" and corpusName='"+corpusName+"' and associatedFilePath='"+sent.getAssociatedFilePath()+"' order by artifactId";
////			    List<Artifact> sent_in_doc = (List<Artifact>) HibernateUtil.executeReader(getSentences);
////			    
////				StringBuilder doc_content = new StringBuilder();
////				for (Artifact rel_sent:sent_in_doc)
////				{
////					doc_content.append(rel_sent.getContent());
////				}
////				doc= doc_content.toString();
////				path_doc_map.put(sent.getAssociatedFilePath(), doc);
////				
////			}
//			
//			
//			List<MLExample> rel_examples = sent_example_map.get(sent);
//			
//			System.out.println("printing the output ADRs "+count+"/"+sent_example_map.size());
//			count++;
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,rel_examples,corpusName,targetSemanticType,false,true);
////			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 //TODO
//			 String text_id = sent.getAssociatedFilePath();
//			 
//			 for (Phrase extracted_p:extracted_adrs)
//			 { 
//				 String p_content = extracted_p.getPhraseContent();
//				 if (limitMentions)
//					{
//						
//						if (!(p_content.matches(".*acne.*")
//							|| p_content.matches(".*finger.*")
//							|| p_content.matches(".*nail.*")
//							|| p_content.matches(".*paronychia.*")
//							//for Pd1 study
//							||p_content.matches(".*itch.*")
//							|| p_content.matches(".*prurit.*")
//							|| p_content.matches(".*xerosis.*")
//							|| (p_content.matches(".*dry.*") && p_content.matches(".*skin.*"))
//							|| p_content.matches(".*rash.*")
//							|| (p_content.matches(".*sweat.*") && !p_content.matches(".*night.*"))
//							|| p_content.matches(".*blister.*")
//							|| p_content.matches(".*bullous.*")
//							|| p_content.matches(".*vesicle.*")
//							|| p_content.matches(".*basal.*")
//								
//								))
//						{
//							continue;
//						}
//
//					}
//				 String line= "correct\t"+text_id+"\t"+
//						 p_content+"\t"+sent.getContent();
////				 output_extracted_ann_format_lines.add(line);
//				 FileUtil.appendLine(resultFile, line);
//			 }
//		
//		}
//		
//		
//		
//	}
//	public static HashMap<Artifact, List<MLExample>> getSentenceExampleMap(String corpusName,List<MLExample> examples)
//	{
//		HashMap<Artifact, List<MLExample>> sent_example_map = new HashMap<>();
//		int count = 0;
//		System.out.println("exmple sentence map "+count);
//		for (MLExample e:examples)
//		{
//			Artifact rel_artifact = e.getRelatedArtifact();
//			Artifact rel_sent = rel_artifact.getParentArtifact();
//			
//			List<MLExample> rel_examples = sent_example_map.get(rel_sent);
//			if (rel_examples==null)
//			{
//				rel_examples = new ArrayList<>();
//			}
//			
//			rel_examples.add(e);
//			sent_example_map.put(rel_sent, rel_examples);
//			
//			
////			if (rel_sent!=cur_sent)
////			{
////				List<MLExample> sent_examples =new ArrayList<>() ;
////				sent_examples.addAll(cur_sent_examples);
////				sent_example_map.put(cur_sent, sent_examples);
////				cur_sent_examples.clear();
////				cur_sent_examples.add(e);
////				cur_sent = rel_sent;
////			}
////			else
////			{
//// 				cur_sent_examples.add(e);
////			}
////			count++;
//////			HibernateUtil.clearLoaderSession();
//		}
//		return sent_example_map;
//	}
//
//
//	public static void generateAnnotationFormatOutPut(TargetSemanticType targetSemanticType)
//	{
//		HibernateUtil.clearLoaderSession();
//		List<String> output_extracted_ann_format_lines = new ArrayList<>();
//		
//		List<MLExample> testExamples = 
//				MLExample.getAllExamples(experimentgroup, false);
//		List<Artifact> testSentences = new ArrayList<>();
//		//generating the test sentences
//		for (MLExample test_e:testExamples)
//		{
//			Artifact related_sent = test_e.getRelatedArtifact().getParentArtifact();
//			if (!testSentences.contains(related_sent))
//			{
//				testSentences.add(related_sent);
//			}
//	
//		}
//		
//		for (Artifact sent:testSentences)
//		{
//			 List<Phrase> extracted_adrs = CRFBasedExtractionUtils.getCRFExtractedConceptPhrasesForSent
//					(sent,experimentgroup,targetSemanticType,false);
//			 String text_id = getTextIdFromFilePath(sent.getAssociatedFilePath());
//			 for (Phrase extracted_p:extracted_adrs)
//			 {
//				 String line= text_id+"\t"+extracted_p.getStartCharOffset()+"\t"+
//						 extracted_p.getEndCharOffset()+"\t"+
//						 "ADR\t"+
//						 extracted_p.getPhraseContent();
//				 output_extracted_ann_format_lines.add(line);
//			 }
//		
//		}
//		FileUtil.createFile("/tmp/ADRMineOutput.tsv", output_extracted_ann_format_lines);
//			
//	}
//	public static String getTextIdFromFilePath(String file_path)
//	{
//		
//		String text_id = "";
//		if (file_path.endsWith("txt"))
//		{
////			/home/azadeh/projects/java/drug-effect-ext/data/off-label-IndivTextFiles-zyprexa/287.txt
//			String pattern = ".*\\/(.*)\\.txt$";
//		    Pattern p = Pattern.compile(pattern);
//		    Matcher m = p.matcher(file_path);
//		    if (m.matches())
//		    {
//		    	text_id = m.group(1);
//		    }
//		}
//		else
//			text_id=file_path;
//
//		return text_id;
//	}
//}
