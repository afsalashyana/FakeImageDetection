package training_phase_1;

import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.split.FileSplit;
import org.deeplearning4j.datasets.canova.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.weights.HistogramIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;


public class base_file {

    private static Logger log = LoggerFactory.getLogger(base_file.class);
    public static String FILENAME = "TrainingData.txt";

    public static void main(String[] args) throws Exception {

        int numLinesToSkip = 0;                 //No need to skip any lines from the CSV
        String delimiter = ",";                 //Delimiter is ','

        RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
        recordReader.initialize(new FileSplit(new ClassPathResource(FILENAME).getFile()));

        int labelIndex = 16384;             //16384 values in each row of the Training.txt CSV: 0-16383 input features followed by an integer label (class) index.
        int numClasses = 2;             //2 classes (Fake or Real) in the data set. Classes have integer values 0 or 1;
        int batchSize = 120;        //Total 20 Data sets. In this case all of them are taken at once as a batch
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
        DataSet next = iterator.next();

        final int numInputs = 16384;        //Total 16384 Inputs.
        int outputNum = 2;                 //True or False ; Fake or Real
        int iterations = 10;             //Iterations set to 10 since data set is small - Possible Overfitting
        long seed = 6;


        //Neural Network is made with 2 Layers since the problem basically is similar to 2 variable linear regression.
        //Testing with 3 layers(2 Dense Layers and one output layer) produced 40% lower efficiency than 2 layers.
        //Sticking with 2 layer model
        log.info("Building model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .activation("tanh")
                .weightInit(WeightInit.XAVIER)
                .learningRate(0.1)
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(100)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation("softmax")
                        .nIn(100).nOut(outputNum).build())
                .backprop(true).pretrain(false)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(5));
//        model.setListeners(new HistogramIterationListener(1));    //Get visual output on browser

        next.normalizeZeroMeanZeroUnitVariance();
        next.shuffle();

        //split test and train
        SplitTestAndTrain testAndTrain = next.splitTestAndTrain(0.65);  //Use 65% of data for training

        log.info("Training............");
        DataSet trainingData = testAndTrain.getTrain();
        model.fit(trainingData);


        Evaluation eval = new Evaluation(numClasses);   //Instantiating Evaluation Model
        DataSet test = testAndTrain.getTest();
        INDArray output = model.output(test.getFeatureMatrix());
        log.info("Evaluating............");
        eval.eval(test.getLabels(), output);
        log.info(eval.stats());
    }
}
