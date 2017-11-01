# DeepHealthMiner
DeepHealthMiner is a Named Entity Recognition tool which is trained for extraction of health related entities, particularly drug safety related mentions (Adverse drug reaction and Indications) from patient posts in social media.

How to run DeepHelthMiner

Please download the folder that contains the DeepHealthMiner_deploy.jar
 file and make sure to download the trained NER model file, Deepnl and lemmatiser folders and place them in the same folder as the jar file.

Usage example:
java -jar DeepHealthMiner_deploy.jar testPatientPost.txt /usr/local/Cellar/python/2.7.13/bin/python

Parameters:
The path of the the file that contains the target post content.
The path of the directory that the python is installed on the machine (e.g. /usr/local/Cellar/python/2.7.13/bin/python)

Output:

The result file will be saved in the “Results” folder. The result file lists the details about the extracted entities. The information about each extracted entity is in one line in tab-separated format:
[input text file name]	[extracted entity]	[entity type]	[line index]	[start char index]	[end char index]

Input text file is the path of the file that contains the patient post (an example file is provided).
Extracted entity is the span of text that is identified as a health-related entity.
Entity type is set to be Sign_Symp for all extracted mentions, but we can change the setting to distinguish Adverse drug reactions (ADRs) from Indications.
Line index is the related line of the entity in text.
Start char index and end char index are the offset of the first and last characters of the extracted mention.

Notes:

The current version of DeepHealthMiner is using Deepnl (https://github.com/attardi/deepnl) for training the NER neural network model. The provided Deepnl folder contains the source code of the neural network training. 

Please note that, Deepnl takes 1-2 minutes for loading the trained NER model for the first time, but after the model is loaded, the tagging of the entities in the test sentences is very fast. 



Citation

Please cite the following publication:
Nikfarjam, Azadeh. Health Information Extraction from Social Media. Diss. Arizona State University, 2016.

