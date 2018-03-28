package dhm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import preprocess.DHMInputGenerator;
import rainbownlp.core.Artifact;
import rainbownlp.core.RainbowEngine;
import rainbownlp.core.RainbowEngine.DatasetType;
import rainbownlp.preprocess.DocumentAnalyzer.InputType;
import rainbownlp.util.ConfigurationUtil;

public class NEREngine {
	 public static String projectDataFolder =  "/Users/azadehn/Documents/projects/projects-data/dhm-dermatology-ADRs/";

	List<Artifact> loadedSentences = new ArrayList<>();
	static int UserPostCountProcessingThreshold = 10000;
	
	
	public static void main(String[] args) throws Exception
	{
		//loading the data into rnlp
		ConfigurationUtil.DBDetachedMode=true;
		
		RainbowEngine re= new RainbowEngine();
		re.readInput(args[0], InputType.SingleTextFile, DatasetType.TEST_SET);

		String python_path = args[1];
		DeepHealthMinerPipelineForExtraction.
			extractEntities(re,python_path,"SingleFile",true);

        System.exit(0);
	}

}
