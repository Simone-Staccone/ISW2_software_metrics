package control;

import model.Releases;
import utils.IO;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

import java.io.File;

public class WekaApi {
    private WekaApi(){}

    public static void compute(String projectName, Releases releases) throws Exception {
        IO fileWriter = new IO(projectName,true);

        for(int i = 0;i<releases.getReleaseList().size();i++){

            String trainUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + projectName.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "train";
            String testUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + projectName.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "test";


            DataSource trainSource = new DataSource(trainUrl  + File.separator + "DataSet.arff");
            DataSource testSource = new DataSource(testUrl + File.separator +  "DataSet.arff");

            Instances trainData = trainSource.getDataSet();
            Instances testData = testSource.getDataSet();

            trainData.setClassIndex(trainData.numAttributes() - 1);
            testData.setClassIndex(trainData.numAttributes() - 1);

            Evaluation eval = new Evaluation(trainData);
            nbClassification(i,trainData, testData, eval,fileWriter);
            eval = new Evaluation(trainData);
            ibkClassifier(i,trainData,testData,eval,fileWriter);
            eval = new Evaluation(trainData);
            randomForestClassifier(i,trainData,testData,eval,fileWriter);

            AttributeSelection filter = new AttributeSelection();
            CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();


            BestFirst bestFirst = new BestFirst();
            bestFirst.setOptions(Utils.splitOptions("-D 0")); //0 backward, 2 bidirectional, 1 forward
            filter.setEvaluator(cfsSubsetEval);
            filter.setSearch(bestFirst);
            filter.setInputFormat(trainData);


            Instances filteredTrainingData = Filter.useFilter(trainData, filter);
            filteredTrainingData.setClassIndex(filteredTrainingData.numAttributes() - 1);
            Instances filteredTestingData = Filter.useFilter(testData, filter);
            filteredTestingData.setClassIndex(filteredTestingData.numAttributes() - 1);

            eval = new Evaluation(filteredTrainingData);
            nbClassification(i,filteredTrainingData, filteredTestingData, eval,fileWriter);
            eval = new Evaluation(filteredTrainingData);
            ibkClassifier(i,filteredTrainingData,filteredTestingData,eval,fileWriter);
            eval = new Evaluation(filteredTrainingData);
            randomForestClassifier(i,filteredTrainingData,filteredTestingData,eval,fileWriter);

            Filter resampleUnder = new Resample();
            resampleUnder.setOptions(Utils.splitOptions("-B 1.0 -Z 130.3")); //options for under-sampling
            resampleUnder.setInputFormat(filteredTrainingData);

            Instances filteredTrainingDataU = Filter.useFilter(filteredTrainingData, resampleUnder);
            filteredTrainingDataU.setClassIndex(filteredTrainingData.numAttributes() - 1);
            Instances filteredTestingDataU = Filter.useFilter(filteredTestingData, resampleUnder);
            filteredTestingDataU.setClassIndex(filteredTestingData.numAttributes() - 1);

            eval = new Evaluation(filteredTrainingDataU);
            nbClassification(i,filteredTrainingDataU, filteredTestingDataU, eval,fileWriter);
            eval = new Evaluation(filteredTrainingDataU);
            ibkClassifier(i,filteredTrainingDataU,filteredTestingDataU,eval,fileWriter);
            eval = new Evaluation(filteredTrainingDataU);
            randomForestClassifier(i,filteredTrainingDataU,filteredTestingDataU,eval,fileWriter);

            SpreadSubsample resampleOver = new SpreadSubsample ();
            resampleOver.setOptions(Utils.splitOptions("-M 1.0")); //options for over-sampling
            resampleOver.setInputFormat(filteredTrainingData);

            Instances filteredTrainingDataO = Filter.useFilter(filteredTrainingData, resampleOver);
            filteredTrainingDataO.setClassIndex(filteredTrainingData.numAttributes() - 1);
            Instances filteredTestingDataO = Filter.useFilter(filteredTestingData, resampleOver);
            filteredTestingDataO.setClassIndex(filteredTestingData.numAttributes() - 1);

            eval = new Evaluation(filteredTrainingDataO);
            nbClassification(i,filteredTrainingDataO, filteredTestingDataO, eval,fileWriter);
            eval = new Evaluation(filteredTrainingDataO);
            ibkClassifier(i,filteredTrainingDataO,filteredTestingDataO,eval,fileWriter);
            eval = new Evaluation(filteredTrainingDataO);
            randomForestClassifier(i,filteredTrainingDataO,filteredTestingDataO,eval,fileWriter);


            /*SMOTE*/
            SMOTE smote = new SMOTE();
            smote.setInputFormat(filteredTrainingData);
            Instances smoteTrainingData = Filter.useFilter(filteredTrainingData, smote);
            Instances smoteTestingData = Filter.useFilter(filteredTestingData, smote);

            eval = new Evaluation(smoteTrainingData);
            nbClassification(i,smoteTrainingData, smoteTestingData, eval,fileWriter);
            eval = new Evaluation(smoteTrainingData);
            ibkClassifier(i,smoteTrainingData,smoteTestingData,eval,fileWriter);
            eval = new Evaluation(smoteTrainingData);
            randomForestClassifier(i,smoteTrainingData,smoteTestingData,eval,fileWriter);
        }

    }

    private static void randomForestClassifier(int i, Instances trainData, Instances testData, Evaluation eval, IO fileWriter) throws Exception {
        RandomForest randomForest = new RandomForest();
        randomForest.buildClassifier(trainData);

        // evaluation
        eval.evaluateModel(randomForest, testData);
        fileWriter.serializeDataSetOnCsv(i,"Random Forest",eval.precision(0),eval.recall(0),eval.areaUnderROC(0),eval.kappa());
    }

    private static void nbClassification(int i, Instances train, Instances test, Evaluation eval, IO fileWriter) throws Exception {
        NaiveBayes naiveBayes = new NaiveBayes();
        naiveBayes.buildClassifier(train);

        // evaluation
        eval.evaluateModel(naiveBayes, test);
        fileWriter.serializeDataSetOnCsv(i,"Naive Bayess",eval.precision(0),eval.recall(0),eval.areaUnderROC(0),eval.kappa());
    }

    private static void ibkClassifier(int i, Instances train, Instances test, Evaluation eval, IO fileWriter) throws Exception {
        IBk iBk = new IBk();
        iBk.buildClassifier(train);
        iBk.setKNN(1);
        iBk.setBatchSize(String.valueOf(100));
        iBk.setCrossValidate(false);
        iBk.setDebug(false);
        iBk.setDoNotCheckCapabilities(false);
        iBk.setMeanSquared(false);
        iBk.setNumDecimalPlaces(2);
        iBk.setWindowSize(0);

        // evaluation
        eval.evaluateModel(iBk, test);
        fileWriter.serializeDataSetOnCsv(i,"IBK",eval.precision(0),eval.recall(0),eval.areaUnderROC(0),eval.kappa());
    }

}
