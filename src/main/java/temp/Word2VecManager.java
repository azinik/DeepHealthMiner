package temp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.omg.CORBA.TRANSACTION_MODE;

import db.DiscCmntText;
import db.DiscText;
import rainbownlp.preprocess.Preprocess;
import rainbownlp.util.FileUtil;
import rainbownlp.util.StringUtil;

public class Word2VecManager {
	public static boolean stem=true;
	public static boolean NormalizeDigits=true;
	
	public static void main (String[] args) throws SQLException, IOException
	{
		generateWord2VecInputFileForInspire();

	}
	
	
	public static void generateWord2VecInputFileForInspire(
			) throws SQLException, IOException
	{
		List<String> input_text_lines = new ArrayList<String>();
//		String word2vec_input = tools.Configuration.getValue("ProjectDataFilesRoot")+"/text8-InHouseTwitterTrainTestExcluded-Sep2214";

//		String word2vec_input = "/Users/azadehn/Documents/text8-DS-dhm-tarceva";
		String word2vec_input = "/Users/azadehn/Documents/text8-DS-dhm-oncology.txt";
		
		//TODO: remove this so that I can run for all dhm
		//from dermatologists: nivo*|keytruda|pembro*|opdivo |PD*1
		List<String> drugs = new ArrayList<>();
		drugs.add("Tarceva");
		drugs.add("Erlotinib");
		drugs.add(" nivo");
		drugs.add("keytruda");
		drugs.add(" pembro");
		drugs.add("opdivo");
		drugs.add("PD-1");
		drugs.add("PD_1");
		drugs.add("PD1");
		drugs.add("PD 1");
		//discussion texts
		//we have to break this since the size of the list will be large
		List<String> posts = new ArrayList<>();
		
		
		int min=1;
		int max = min+100000;
		
		while (max<800000)
		{
			posts =DiscText.getTextsBody(min,max,drugs);
			processInspirePostsW2vec(posts,word2vec_input);
			min = max+1;
			max=min+100000;
			System.out.println("retreving discussions from id "+min);
		}
		
		//discussion comment text 
		posts = new ArrayList<>();
		min=1;
		max = min+100000;
		
		while (max<7300000)
		{
			posts =DiscCmntText.getTextsBody(min,max,drugs);
			processInspirePostsW2vec(posts,word2vec_input);
			min = max+1;
			max=min+100000;
			System.out.println("retreving disc comments from id "+min);
		}

	}
	
public static void processInspirePostsW2vec(List<String> posts, String w2vecTextFile) throws IOException
{
	int count=0;
	 File file = new File(w2vecTextFile);
	 Writer writer = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(file, true), "UTF-8"));
		
	
	for (String post_text_body:posts)
	{
		Preprocess pre_processed_sent = new Preprocess(post_text_body);
		
		HashMap<Integer, String> setences = pre_processed_sent.getSentenceIndexMap();
		
		for (Integer sent_index: setences.keySet())
		{	
			String tokenizedSentence = setences.get(sent_index);
			if (tokenizedSentence.matches(".*http.*"))
			{
				count++;
				continue;
			}
			tokenizedSentence = StringUtil.getTermByTermWordnet(tokenizedSentence);
			
			if (NormalizeDigits)
			{				
				tokenizedSentence = tokenizedSentence.replaceAll("\\d", "d");	
			}
//			input_text_lines.add(tokenizedSentence.toLowerCase());
//			FileUtil.appendLine(w2vecTextFile, tokenizedSentence.toLowerCase());
			writer.write(tokenizedSentence.toLowerCase()+"\n");
//			System.out.printsln("post text"+count+"/"+posts.size());
			count++;
		}
		
		
	}
	writer.flush();
	writer.close();

}
	
	
	
}
