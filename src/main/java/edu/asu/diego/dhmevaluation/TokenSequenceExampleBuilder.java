package edu.asu.diego.dhmevaluation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBLayer;
import edu.stanford.nlp.ling.HasWord;
//
//import machineLearning.WordRepresentation;
//import machineLearning.chunk.ChunkBinaryExampleBuilder;
//import machineLearning.chunk.ChunkEmbeddingsFeatures;
//import machineLearning.chunk.SentenceChunkFeatures;
import rainbownlp.core.Artifact;
import rainbownlp.core.Artifact.Type;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.preprocess.Preprocess;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.StringUtil;



public class TokenSequenceExampleBuilder {
	//This classifies a chunk as having ADR or not
	public static String ExperimentGroupADRConcepts = "ADRConceptTokenSeq";

	public enum TokenLabel {
		OUT(0),
		BADR(1),
		IADR(2),
		BIND(3),
		IIND(4),
		BBEN(5),
		IBEN(6),
		BDRUG(7);
		private static final Map<Integer,TokenLabel> lookup = 
			new HashMap<Integer,TokenLabel>();
		
		static {
	          for(TokenLabel l : EnumSet.allOf(TokenLabel.class))
	               lookup.put(l.getCode(), l);
	     }
		
		private int code;

	     private TokenLabel(int code) {
	          this.code = code;
	     }

	     public int getCode() { return code; }

	     public static TokenLabel getEnum(int code) { 
	          return lookup.get(code); 
	     }	
	     
	}
	public static void main(String[] args)
	{
		createTokenSequenceExamples(false,"dhm",null);
	}
	public static void createTokenSequenceExamples(boolean forTrain,String corpusName, Connection mysqlConn)
	{
		MLExample.batchAddUpdate = true;
		//get all child artifacts and check if they are in ADR concepts
//		List<Artifact> all_sentences = Artifact.listByTypeByForTrainByCorpus(Type.Sentence,forTrain,corpusName);
		List<Artifact> all_sentences = Artifact.listByTypeByForTrain(Type.Sentence,forTrain);
		
		int count=0;
		int count_sentences =0;
		int all =all_sentences.size();
		for (Artifact sent:all_sentences)
		{
		
			// build one example for each
//			HashMap<Artifact, Integer> token_label_map = new HashMap<Artifact, Integer>();
			
			List<Artifact> tokens = sent.getChildsArtifact();
			
//			for (Artifact token:tokens)
//			{
//				token_label_map.put(token, TokenLabel.OUT.ordinal());
//			}

			for (Artifact token: tokens)
//			for (Artifact token: token_label_map.keySet())
			{
				MLExample token_seq_example = 
						MLExample.getInstanceForArtifact(token, corpusName);
				
//				Integer label = token_label_map.get(token);
				
//				token_seq_example.setExpectedClass(label);
				token_seq_example.setExpectedClass(TokenLabel.OUT.ordinal());
					
				token_seq_example.setPredictedClass(-1);
				
				token_seq_example.setForTrain(forTrain);
				token_seq_example.setAssociatedFilePath(token.getAssociatedFilePath());
				if (MLExample.batchAddUpdate)
				{
					MLExample.addInsertQuery(token_seq_example);
				}
				else
				{
					MLExample.saveExample(token_seq_example);
				}
				if(count%200==0){
					MLExample.FlushInsertQueryBuffer(mysqlConn);
					MLExample.FlushUpdateQueryBuffer(mysqlConn);
				}
				count++;

			}
//			if(MLExample.batchAddUpdate && count%50==0){
//				MLExample.FlushInsertQueryBuffer();
//				MLExample.FlushUpdateQueryBuffer();
//			}
			count_sentences++;
			System.out.println("Building classification candidates... count: "+count_sentences+"/"+all);
			HibernateUtil.clearLoaderSession();
		}
		if(MLExample.batchAddUpdate)
		{
			MLExample.FlushInsertQueryBuffer(mysqlConn);
			MLExample.FlushUpdateQueryBuffer(mysqlConn);
		}
		

	
	}

	public static void createTokenSequenceExamplesForTest(boolean forTrain,String corpusName,Connection mysqlConn) throws SQLException
	{
		MLExample.batchAddUpdate = true;
//		int min_artifatId = Artifact.getFirstInsertedIdByType(corpusName, Type.Word);
//		int max = Artifact.getLastInsertedIdByType(corpusName, Type.Word);
		 
		List<Artifact> tokenArtifacts = Artifact.listByType(Type.Word, corpusName,forTrain);
//		List<Artifact> tokenArtifacts = listArtifactsByType(Type.Word, corpusName,forTrain);
		int count=0;


		for (Artifact token: tokenArtifacts)
//    			for (Artifact token: token_label_map.keySet())
		{
			MLExample token_seq_example = 
					MLExample.getInstanceForArtifact(token, corpusName);
			
//    				Integer label = token_label_map.get(token);
			
//    				token_seq_example.setExpectedClass(label);
			token_seq_example.setExpectedClass(TokenLabel.OUT.ordinal());
				
			token_seq_example.setPredictedClass(-1);
			
			token_seq_example.setForTrain(forTrain);
			token_seq_example.setAssociatedFilePath(token.getAssociatedFilePath());
			if (MLExample.batchAddUpdate)
			{
				MLExample.addInsertQuery(token_seq_example);
			}
			else
			{
				MLExample.saveExample(token_seq_example);
			}
			if(count%200==0){
				MLExample.FlushInsertQueryBuffer(mysqlConn);
				MLExample.FlushUpdateQueryBuffer(mysqlConn);
			}
			count++;
			System.out.println(count+"/"+tokenArtifacts.size());
		}
		if(MLExample.batchAddUpdate)
		{
			MLExample.FlushInsertQueryBuffer(mysqlConn);
			MLExample.FlushUpdateQueryBuffer(mysqlConn);
		}

	
	}
	public static List<MLExample> createTokenSequenceExamplesForTest(boolean forTrain,String corpusName,Integer minArtifactId,Integer maxArtifactId, Connection mysqlConn) throws SQLException
	{
		List<MLExample> examples = new ArrayList<>();
		
		MLExample.batchAddUpdate = true;
		
		List<Artifact> tokenArtifacts = new ArrayList<>();
		
		if (maxArtifactId ==null && minArtifactId==null)
		{
			tokenArtifacts = Artifact.listByType(Type.Word, corpusName,forTrain);
		}
		else
		{
			tokenArtifacts = Artifact.listByType(Type.Word, corpusName,forTrain,minArtifactId,maxArtifactId);
		}
		int count=0;

		System.out.println("Building the classification examples:"+corpusName+"# Size: "+tokenArtifacts.size());
		for (Artifact token: tokenArtifacts)
//    			for (Artifact token: token_label_map.keySet())
		{
			MLExample token_seq_example = 
					MLExample.getInstanceForArtifact(token, corpusName);
			
//    				Integer label = token_label_map.get(token);
			
//    				token_seq_example.setExpectedClass(label);
			token_seq_example.setExpectedClass(TokenLabel.OUT.ordinal());
				
			token_seq_example.setPredictedClass(-1);
			
			token_seq_example.setForTrain(forTrain);
			token_seq_example.setAssociatedFilePath(token.getAssociatedFilePath());
			if (MLExample.batchAddUpdate)
			{
				MLExample.addInsertQuery(token_seq_example);
			}
			else
			{
				MLExample.saveExample(token_seq_example);
			}
			if(count%200==0){
				MLExample.FlushInsertQueryBuffer(mysqlConn);
				MLExample.FlushUpdateQueryBuffer(mysqlConn);
			}
			count++;
			
			examples.add(token_seq_example);
			
		}
		if(MLExample.batchAddUpdate)
		{
			MLExample.FlushInsertQueryBuffer(mysqlConn);
			MLExample.FlushUpdateQueryBuffer(mysqlConn);
		}

		return examples;
	}


//	//This method is used due to very slow performance of Hibernate
//	public static List<Artifact> listArtifactsByType(Type pSentenceType, String corpusName, boolean forTrain) throws SQLException {
//		List<Artifact> artifacts = new ArrayList<>();
//		
//		Connection conn = DBLayer.getConnection("user_azadehn");
//		Statement st = (Statement) conn.createStatement();
//		
//		String query = "select * from Artifact where artifactType = "+pSentenceType.ordinal()
//		+"  and corpusName='"+corpusName+"' order by artifactId";
//			
//		
//	      
//	      // execute the query, and get a java resultset
//	      ResultSet rs = st.executeQuery(query.toString());
//	      int count=0;
//	      while (rs.next())
//	      {
//	//	        String title = rs.getString("body");
//	    	  int artifactid = rs.getInt("artifactId");
//	    	  
//	    	  artifacts.add(Artifact.findInstance(artifactid));
//	    	  count++;
//	    	  System.out.println("id counter "+count+"/"+rs.getFetchSize());
//	      }
//	      st.close();
//		return artifacts;
//	}
}
