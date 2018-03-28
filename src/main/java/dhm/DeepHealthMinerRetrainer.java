package dhm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.asu.diego.extraction.DeepnlInputGenerator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;

public class DeepHealthMinerRetrainer {
	public static void main (String[] args) throws IOException
	{
		generateDHMInput("/Users/azadehn/Documents/projects/projects-data/dhm-dermatology-ADRs/annotations/DHM_retrain/python-deepnl-Train-stemmedDSAndInspire200.csv",
				"/Users/azadehn/Documents/projects/projects-data/dhm-dermatology-ADRs/annotations/DHM_retrain/DHMRetrainCorpora.txt");
	}
public static String generateDHMInput(String filePath, String corposNamesFile) throws IOException
{
	//get existing sentences
	List<String> corpora = FileUtil.loadLineByLine(corposNamesFile);
	
	for (String corpusName: corpora)
	{
		//TODO:make sure the or is correct
		List<MLExample> trainExamples = 
		MLExample.getAllExamples(corpusName, true);

		List<Integer> train_example_ids = new ArrayList<Integer>();
		for(MLExample example : trainExamples)
		{
			train_example_ids.add(example.getExampleId());
		}
		DeepnlInputGenerator.writeIOB2AppendExisting(train_example_ids, filePath,
				true,false, false);
	}
	
	return filePath;
}
}
