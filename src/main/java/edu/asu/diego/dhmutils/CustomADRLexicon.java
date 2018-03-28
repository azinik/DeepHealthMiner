package edu.asu.diego.dhmutils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rainbownlp.core.Setting;
import rainbownlp.util.FileUtil;
import rainbownlp.util.StringUtil;

public class CustomADRLexicon {
	
	
	
	public static void main (String [] args)
	{
		String ADR_combined_lexicon_path = args[0];
		String chv_origina_cnames = args[1];
		String limited_chv_conceptNames_file = args[2];
		
//		createADRLimitedLexicon(ADR_combined_lexicon_path,chv_origina_cnames);
		

		groupConceptNames(limited_chv_conceptNames_file);
	}
	public static void createADRLimitedLexicon(String ADR_combined_lexicon_path, String chv_origina_cnames)
	{
		List<String> original_concept_names = getConceptNamesFromOriginalADRLexicon(ADR_combined_lexicon_path);
		List<String> original_umls_ids = 
				getUniqueUMLSIdsFromOriginalADRLexicon(ADR_combined_lexicon_path);
		
		createADRConceptsFromConceptNames(chv_origina_cnames,original_umls_ids,original_concept_names);
	}

	private static List<String> getUniqueUMLSIdsFromOriginalADRLexicon(
			String ADR_combined_lexicon_path) {
		List<String> initial_umls_concepts =  new ArrayList<String>();
		
		List<String> l_lines = FileUtil.loadLineByLine(ADR_combined_lexicon_path);
		
		for (String line: l_lines)
		{
			String[] concepts = line.split("\\t");
			for (String concept:concepts)
			{
				if (concept.matches("[cC]\\d+"))
				{
					initial_umls_concepts.add(concept);
				}
			}
				
		}
		return initial_umls_concepts;
	}
	// This will return the wordnet root of the original ADR dic by Dr. Leamen et al
	private static List<String> getConceptNamesFromOriginalADRLexicon(
			String ADR_combined_lexicon_path) {
		List<String> original_conceptNames_wnroot =  new ArrayList<String>();
		
		List<String> l_lines = FileUtil.loadLineByLine(ADR_combined_lexicon_path);
		
		for (String line: l_lines)
		{
			String[] concepts = line.split("\\t");
			for (String concept:concepts)
			{
				if (!concept.matches("[cC]\\d+"))
				{
					String root_cn = StringUtil.getTermByTermWordnet(concept);
					original_conceptNames_wnroot.add(root_cn);
				}
			}
				
		}
		return original_conceptNames_wnroot;
	}
	// This method check the new CHV lexicon and will create a new lexicon based on the original 
//	ADR lexicon that we had
	public static void createADRConceptsFromConceptNames(String cn_file_path, 
			List<String> ADR_umls_concepts, List<String> combined_ADR_concept_names)
	{
		List<String> cn_lines = FileUtil.loadLineByLine(cn_file_path);
		
		List<String> ADR_concept_names = new ArrayList<String>();
		List<String> nonADR_concept_names = new ArrayList<String>();
		
		for (String cn_line:cn_lines)
		{
			String umls_id = cn_line.split("\\t")[0];
			String chv_concept_name = cn_line.split("\\t")[1];
			String chv_root = StringUtil.getTermByTermWordnet(chv_concept_name);
			
			if (ADR_umls_concepts.contains(umls_id)
					|| combined_ADR_concept_names.contains(chv_root)
					)
			{
				cn_line = cn_line.replaceAll("\\(.*\\)$", "");
				ADR_concept_names.add(cn_line.trim());
			}
			else
			{
				nonADR_concept_names.add(cn_line);
			}
					
		}
		FileUtil.createFile(Setting.getValue("ProjectDataFilesRoot")+"/newADRConcepNames.tsv", ADR_concept_names);
		FileUtil.createFile(Setting.getValue("ProjectDataFilesRoot")+"/nonADRConcepNames.tsv", nonADR_concept_names);
	}
	public static void groupConceptNames(String cn_file_path)
	{
		HashMap<String, List<String>> id_concepts_map = new HashMap<String, List<String>>();
		
		id_concepts_map = loadConceptNames(cn_file_path);
		List<String> grouped_cn_lines = new ArrayList<String>();
		
		for (String umls_id:id_concepts_map.keySet())
		{
			String line = umls_id;
			List<String> concepts = id_concepts_map.get(umls_id);
			for (String concept: concepts)
			{
				line += "\t"+concept;
			}
			grouped_cn_lines.add(line);
		}
		FileUtil.createFile(Setting.getValue("ProjectDataFilesRoot")+"/groupedConceptNames.tsv", grouped_cn_lines);
	}


	public static HashMap<String, List<String>> loadGroupedConceptNames() {
		
		HashMap<String, List<String>> id_concepts_map = new HashMap<String, List<String>>();
		
		List<String> grouped_lines = FileUtil.loadLineByLine(Setting.getValue("ProjectDataFilesRoot")+"/groupedConceptNames.tsv");
		
		 
		for (String cn_line:grouped_lines)
		{
			List<String> relatedConcepts = new ArrayList<String>(); 
			String[] concepts = cn_line.split("\\t");
			String umls_id = concepts[0];
			
			for (int i=1;i<concepts.length;i++)
				relatedConcepts.add(concepts[i]);
			id_concepts_map.put(umls_id,relatedConcepts);		
		}
		return id_concepts_map;
	}
	public static HashMap<String, List<String>> loadConceptNames(String cn_file_path)
	{
		HashMap<String, List<String>> id_concepts_map = new HashMap<String, List<String>>();
		
		
		List<String> cn_lines = FileUtil.loadLineByLine(cn_file_path);
		
		for (String cn_line:cn_lines)
		{
			String[] concepts = cn_line.split("\\t");
			String umls_id = concepts[0];
			
			List<String> related_concepts = id_concepts_map.get(umls_id);
			
			if (related_concepts == null)
				related_concepts = new ArrayList<String>();
			
			related_concepts.add(concepts[1]);
			id_concepts_map.put(umls_id, related_concepts);
					
		}

		return id_concepts_map;
	}
}
