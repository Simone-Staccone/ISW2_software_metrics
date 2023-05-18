package control;

import model.Releases;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.util.Arrays;

public class WekaApi {
    private WekaApi(){}

    public static void compute(String projectName, Releases releases) throws Exception {
        for(int i = 1;i<releases.getReleaseList().size();i++){
            String trainUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + projectName.toLowerCase() + File.separator + "Release_" + releases.getReleaseList().get(i).getReleaseNumber() + File.separator + "train";
            String testUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + projectName.toLowerCase() + File.separator + "Release_" + releases.getReleaseList().get(i).getReleaseNumber() + File.separator + "test";

            System.out.println(trainUrl  + File.separator + "DataSet.arff");
            System.out.println(testUrl + File.separator +  "DataSet.arff");

            DataSource source1 = new DataSource(trainUrl  + File.separator + "DataSet.arff");
            DataSource source2 = new DataSource(testUrl + File.separator +  "DataSet.arff");
            Instances trainingDataSet = source1.getDataSet();
            Instances testingDataSet = source2.getDataSet();

            trainingDataSet.setClassIndex(trainingDataSet.numAttributes() - 1);
            testingDataSet.setClassIndex(testingDataSet.numAttributes() - 1);

            System.out.println(trainingDataSet.size());

            RandomForest randomForestClassifier = new RandomForest();
            NaiveBayes naiveBayesClassifier = new NaiveBayes();
            IBk ibkClassifier = new IBk();


            Evaluation eval = new Evaluation(trainingDataSet);

            randomForestClassifier.buildClassifier(trainingDataSet);
            eval.evaluateModel(randomForestClassifier, testingDataSet);


            System.out.println(Arrays.deepToString(eval.confusionMatrix()));
            System.out.println(eval.precision(0) + " " + eval.recall(0) + " " + eval.kappa() + " " + eval.areaUnderROC(0));


            naiveBayesClassifier.buildClassifier(trainingDataSet);
            eval.evaluateModel(naiveBayesClassifier, testingDataSet);

            System.out.println(eval.precision(0) + " " + eval.recall(0) + " " + eval.kappa() + " " + eval.areaUnderROC(0));



            ibkClassifier.buildClassifier(trainingDataSet);
            eval.evaluateModel(ibkClassifier, testingDataSet);

            System.out.println(eval.precision(0) + " " + eval.recall(0) + " " + eval.kappa() + " " + eval.areaUnderROC(0));


        }

    }


}
