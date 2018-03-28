//package edu.asu.diego.loader;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.commons.io.FileUtils;
//
//import rainbownlp.core.Artifact;
//import rainbownlp.core.Setting;
//import rainbownlp.preprocess.DocumentAnalyzer;
//import rainbownlp.util.FileUtil;
//import rainbownlp.util.HibernateUtil;
//import edu.asu.diego.dhmevaluation.deep.Preprocess;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.process.PTBTokenizer;
//
//
//
//
//public class DAnalyzer extends DocumentAnalyzer {
//	public boolean isForDemo =false;
//	List<Artifact> loadedSentences = new ArrayList<>();
//	
//	public static void main(String[] args)
//	{
//		
//		String text_files_folder = args[0];
//		String corpus_name = args[1];
//
//
//		Setting.TrainingMode = false;
//		FileUtil.logLine(null,"loading documents ...");
//		
//		DAnalyzer doc_proc = new DAnalyzer();
//		doc_proc.processDocuments(text_files_folder,corpus_name);
//		
//		
//	}
//	
//
//
//
//	private  void loadSentences(Artifact parentDoc,String corpusName) throws IOException {
////		Tokenizer docTokenizer = new Tokenizer(parentDoc.getAssociatedFilePath());
//		
//		String file_content = FileUtil.ReadFileInToString(parentDoc.getAssociatedFilePath());
//		Preprocess pre_processed_sent = new Preprocess(file_content);
//		
//		HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();
//		
//		List<Artifact> setencesArtifacts = new ArrayList<Artifact>();
//		Artifact previous_sentence = null;
//		
//		for (Integer sent_index: setences.keySet())
//		{	
//			String tokenizedSentence = setences.get(sent_index);
//
//			Artifact new_sentence = Artifact.getInstance(Artifact.Type.Sentence,
//					parentDoc.getAssociatedFilePath(), sent_index,corpusName);
//			
//			new_sentence.setParentArtifact(parentDoc);
//			new_sentence.setLineIndex(sent_index);
//			new_sentence.setContent(tokenizedSentence);
//			
//			if (previous_sentence != null) {
//				new_sentence.setPreviousArtifact(previous_sentence);
//				previous_sentence.setNextArtifact(new_sentence);
//				HibernateUtil.save(previous_sentence);
//			}
//			
//			HibernateUtil.save(new_sentence);
//		
//		
//			loadWords(new_sentence,sent_index,pre_processed_sent,corpusName);
//
//			setencesArtifacts.add(new_sentence);
//			
//			previous_sentence = new_sentence;
//			HibernateUtil.clearLoaderSession();
//		}
////		parentDoc.setChildsArtifact(setencesArtifacts);
//
//	}
//	private void loadWords(Artifact parentSentence, Integer sentIndex, Preprocess pre_processed_sent,String corpusName ) {
//		
//		List<Artifact> tokensArtifacts = new ArrayList<Artifact>();
//		Artifact previous_word = null;
//		
//		String textContent = "";
//		Artifact new_word = null;
//			
//		List<HasWord> tokens = pre_processed_sent.getSentTokensMap().get(sentIndex);
//		
//		for(int token_index = 0; token_index< tokens.size();token_index++){
//			
//			textContent = tokens.get(token_index).toString();
//			int start_char = pre_processed_sent.getTokenStartCharIndex(sentIndex, token_index);
////			new_word = Artifact.getInstance(
////					Artifact.Type.Word, parentSentence.getAssociatedFilePath(),start_char);
//	
//			new_word = Artifact.getInstance(Artifact.Type.Word);
//			new_word.setStartIndex(start_char);
//			new_word.setAssociatedFilePath(parentSentence.getAssociatedFilePath());
//			new_word.setArtifactType(Artifact.Type.Word);
//			
//			new_word.setContent(textContent);
//			new_word.setParentArtifact(parentSentence);
//			new_word.setLineIndex(sentIndex);
//			new_word.setEndIndex(pre_processed_sent.getTokenEndCharIndex(sentIndex, token_index));
//			new_word.setWordIndex(token_index);
//			new_word.setCorpusName(corpusName);
//			
//			HibernateUtil.save(new_word);
//			if (previous_word != null) {
//				new_word.setPreviousArtifact(previous_word);
//				previous_word.setNextArtifact(new_word);
//				HibernateUtil.save(previous_word);
//			}
//			
//				
//			tokensArtifacts.add(new_word);
//			previous_word = new_word;
//			
//		}
////		parentSentence.setChildsArtifact(tokensArtifacts);
//	}
//
//	
//	public int processDocuments(String rootPath, String corpusName){
//		int numberOfInstances = 0;
//		
//		loadDocuments(rootPath,corpusName);
//		
////		Tokenizer.fixDashSplitted();
//		
//		return numberOfInstances;
//	}
//	public void loadDocuments(String input_text_files, String corpus_name) {
//		
//		List<Artifact> loaded_documents = new ArrayList<Artifact>();
//		
//		Iterator<File> file_iterator = 
//              FileUtils.iterateFiles(new File(input_text_files), null,false);      
//	      
//	    int counter = 0;
//	
//	    while (file_iterator.hasNext()) {
//    	  File f = file_iterator.next();
//    	  String file_path = f.getPath();
//    	  String file_name = f.getName();
//    	  file_name = file_name.replaceAll(".txt", "");
//
//          Artifact new_doc = 
//                      Artifact.getInstance(Artifact.Type.Document, file_path, 0,corpus_name);
//         
//              
//          loaded_documents.add(new_doc);
//          counter++;
//	    }
//	      
//
//		int count=0;
//		
//		for(Artifact doc:loaded_documents){
//			count++;
//			
//			System.out.println(count+" / "+loaded_documents.size());
//			try {
//				loadSentences(doc,corpus_name);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}
//	public void loadDocuments(HashMap<String, String> doc_id_map, String corpus_name) {
//		
//		List<Artifact> loaded_documents = new ArrayList<Artifact>();    
//	      
//	    int counter = 0;
//	    for (String doc_id: doc_id_map.keySet())
//	    {
//	    	Artifact new_doc = 
//                    Artifact.getInstance(Artifact.Type.Document, doc_id, 0,corpus_name);
//
//            loaded_documents.add(new_doc);
//            try {
//				loadSentences(new_doc,corpus_name,doc_id_map.get(doc_id));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            System.out.println(counter+" / "+doc_id_map.keySet().size());
//            counter++;
//			HibernateUtil.clearLoaderSession();
//
//	    }
//
//
//	}
//
//	private  void loadSentences(Artifact parentDoc,String corpusName,String docContent) throws IOException {
////		Tokenizer docTokenizer = new Tokenizer(parentDoc.getAssociatedFilePath());
//		
//		Preprocess pre_processed_sent = new Preprocess(docContent);
//		
//		HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();
//		
//		List<Artifact> setencesArtifacts = new ArrayList<Artifact>();
//		Artifact previous_sentence = null;
//		
//		for (Integer sent_index: setences.keySet())
//		{	
//			String tokenizedSentence = setences.get(sent_index);
//			Artifact new_sentence  =  Artifact.getInstance(Artifact.Type.Sentence);
//			new_sentence.setArtifactType(Artifact.Type.Sentence);
//			new_sentence.setAssociatedFilePath(parentDoc.getAssociatedFilePath());
//			
//			new_sentence.setParentArtifact(parentDoc);
//			new_sentence.setLineIndex(sent_index);
//			new_sentence.setContent(tokenizedSentence);
//			HibernateUtil.save(new_sentence);
//			
//			if (previous_sentence != null) {
//				new_sentence.setPreviousArtifact(previous_sentence);
//				previous_sentence.setNextArtifact(new_sentence);
//				HibernateUtil.save(previous_sentence);
//			}
//			loadWords(new_sentence,sent_index,pre_processed_sent,corpusName);
//
//			setencesArtifacts.add(new_sentence);
//			
//			previous_sentence = new_sentence;
//			HibernateUtil.clearLoaderSession();
//		}
////		parentDoc.setChildsArtifact(setencesArtifacts);
//
//	}
//
//
//
//
//	@Override
//	public List<Artifact> processDocuments(String rootPath) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	
//
//}
