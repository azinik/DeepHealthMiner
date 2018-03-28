package(default_visibility = ["//visibility:public"])

java_runtime(
    name = "jdk-9-ea+153",
    srcs = glob(["jdk9-ea+153/**"]),
    java_home = "jdk9-ea+153",
)
java_binary(
    name = "DeepHealthMiner",
    srcs = glob(["**/*.java"]),
	deps = ["DHM_jars","@RainbowNlp//:rnlp","@stanford_core_nlp//jar"],
	data = ["DHM_jars","@RainbowNlp//:exported_data"],

    main_class = "dhm.NEREngine",
)
java_import(
        name = "DHM_jars",
        jars = [
			"libs/commons-io-2.5.jar",
			"libs/commons-lang3-3.5.jar",
			"libs/lucene-core-3.6.2.jar",
			"libs/lucene-spellchecker-3.6.1.jar",
			"libs/mysql-connector-java-5.1.21.jar",
			"libs/semanticvectors-5.4.jar",
			"libs/stanford-parser-2012-05-22-models.jar",
			"libs/stanford-parser.jar",
			"libs/stanford-postagger.jar",
            ],
)

