import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Perceptron {
    private List<InputVec> trainingVectors;
    private List<InputVec> testVectors;
    private Vec weightVector;
    private double threshold;
    private double learningRate;
    private int maxEpochs = 1000;

    public Perceptron(String trainingFile, String testFile, double threshold, double learningRate) {
        this.threshold = threshold;
        this.learningRate = learningRate;

        try {
            trainingVectors = collectFileData(trainingFile);
        } catch (IOException e) {
            throw new RuntimeException(e + "TRAINING FILE ERROR.");
        }

        try {
            testVectors = collectFileData(testFile);
        } catch (IOException e) {
            throw new RuntimeException(e + "TEST FILE ERROR.");
        }

        activateWeightVector();
        System.out.println("Wektor wag początkowy: " + weightVector);

        train();
        System.out.println("Wektor wag po treningu: " + weightVector);

//        test();

//        System.out.println(this.toString());
    }

    public List<InputVec> collectFileData(String file) throws IOException {

        return Files.lines(Paths.get(file))
                .map(line -> line.trim().split("\\s+"))
                .map(arr -> {
                    double[] coordinates = Arrays.stream(arr, 0, arr.length - 1)
                            .map(txt -> txt.replaceAll(",", "."))
                            .mapToDouble(Double::parseDouble)
                            .toArray();
                    String name = arr[arr.length - 1];

                    return new InputVec(coordinates, name);
                }).toList();
    }

    public void activateWeightVector(){
        double[] weightVectorComponents = new double[trainingVectors.getFirst().getComponents().length+1];
//        Arrays.fill(weightVectorComponents, 0);
        for (int i = 0; i < weightVectorComponents.length; i++) {
            weightVectorComponents[i] = Math.random() * 0.01;
        }
        weightVector = new Vec(weightVectorComponents);
    }

    public int  compute(Vec inputVector){
        double sum = weightVector.getComponents()[0];

        for(int i=0; i<inputVector.getComponents().length; i++){
            sum += inputVector.getComponents()[i]*weightVector.getComponents()[i+1];
//            System.out.println(inputVector.getComponents()[i]*weightVector.getComponents()[i]);
//            System.out.println(inputVector.getComponents()[i]);
//            System.out.println(weightVector.getComponents()[i+1]);
        }

        System.out.println("Compute sum: " + sum);

        return sum >= 0 ? 1 : 0;
    }

    public void train(){
        boolean hasErrors;
        int epochs = 0;

        do {
            hasErrors = false;
            for (InputVec trainingVector : trainingVectors) {
                int expected = trainingVector.getLabel().equals("Iris-setosa") ? 1 : 0;
                int output = compute(trainingVector);

                if (output != expected) {
                    updateWeights(trainingVector, expected - output);
                    hasErrors = true;
                }
            }
            epochs++;
        } while (hasErrors && epochs < maxEpochs);

        System.out.println("Trening zakończony po " + epochs + " epokach.");
    }

    private void updateWeights(InputVec inputVector, int error) {
        double[] weights = weightVector.getComponents();
        double[] input = inputVector.getComponents();

//        Aktualizacja progu (bias)
        weights[0] += learningRate*error;

//        Aktualizacja wag
        for(int i=0; i<input.length; i++){
            weights[i +1] += input[i]*error*learningRate;
        }

        System.out.println("Nowe wagi: " + Arrays.toString(weights));

        weightVector.setComponents(weights);
    }



    @Override
    public String toString() {
        return "trainingVectors=" + trainingVectors +
                "\n, testVectors=" + testVectors + '}';
    }
}
