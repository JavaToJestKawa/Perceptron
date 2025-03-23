import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Perceptron {
    private final List<InputVec> trainingVectors;
    private final List<InputVec> testVectors;
    private final double learningRate;
    private final String objectToClassify;
    private Vec weightVector;

    //    Runs perceptron and defines its behaviours.
    public Perceptron(String trainingFile, String testFile, String objectToClassify, double learningRate) {
        this.learningRate = learningRate;
        this.objectToClassify = objectToClassify;

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
        System.out.println("Entry weights vector:\n" + weightVector);

        train();

        System.out.println("Post-training weights vector:\n" + weightVector);

        test();

        statistics();

        userInput();
    }

    //    Collects data from file
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

    //    Activates weight vector with random numbers.
    public void activateWeightVector() {
        double[] weightVectorComponents = new double[trainingVectors.getFirst().getComponents().length + 1];
//        Arrays.fill(weightVectorComponents, 0);
        for (int i = 0; i < weightVectorComponents.length; i++) {
            weightVectorComponents[i] = Math.random() * 0.01;
        }
        weightVector = new Vec(weightVectorComponents);
    }

    //    Trains perceptron.
    public void train() {
        int maxEpochs = 1000;
        int epochs = 0;
        int maxUpdatesPerSample = 100;
        boolean hasErrors;

        do {
            hasErrors = false;
            for (InputVec trainingVector : trainingVectors) {
                int expected = trainingVector.getLabel().equals(objectToClassify) ? 1 : 0;
                int output;
                int updatesPerSample = 0;

                while ((output = compute(trainingVector)) != expected && updatesPerSample < maxUpdatesPerSample) {
                    updateWeights(trainingVector, expected - output);
                    hasErrors = true;
                    updatesPerSample++;
                }
            }

            epochs++;
        } while (hasErrors && epochs < maxEpochs);

        System.out.println("\nTraining finished after " + epochs + " epochs.\n");
    }

    //    Tests perceptron.
    private void test() {
        for (InputVec testVector : testVectors) {
            int expected = testVector.getLabel().equals(objectToClassify) ? 1 : 0;
            int output = compute(testVector);

            if (output == expected) {
                testVector.setLabelClassified(testVector.getLabel());
            }
        }
    }

    //    Prints perceptron's accuracy.
    private void statistics() {
        int correctlyClassified = 0;
        for (InputVec testVector : testVectors) {
            if (Objects.equals(testVector.getLabel(), testVector.getLabelClassified())) {
                correctlyClassified++;
            }
        }

        System.out.println("\n\n\nPercentage of correct classified objects: "
                + ((double) correctlyClassified / testVectors.size()) * 100);
    }

    //    Enables user to input objects manually.
    private void userInput() {
        boolean continueTesting;

        System.out.println("\nDo you want to continue by inserting your own objects?");
        System.out.println("yes/no");

        Scanner input = new Scanner(System.in);
        String usersAnswer = input.next();
        continueTesting =
                (Objects.equals(usersAnswer, "yes") ||
                        Objects.equals(usersAnswer, "Yes"));

        while (continueTesting) {
            double[] components = new double[trainingVectors.getFirst().getComponents().length];

            for (int i = 0; i < components.length; i++) {
                System.out.print("Insert coordinate " + (char) (i + 97) + ": ");
                components[i] = Double.parseDouble(input.next());
            }

            int result = compute(new InputVec(components, null));
            if (result == 1) {
                System.out.println("Object classified as " + objectToClassify);
            } else {
                System.out.println("Object classified as NONE-" + objectToClassify);
            }

            System.out.println("Do you want to continue by inserting your own objects?");
            System.out.println("yes/no");

            usersAnswer = input.next();
            continueTesting =
                    (Objects.equals(usersAnswer, "yes") ||
                            Objects.equals(usersAnswer, "Yes"));

        }

        input.close();
    }

    //    Computes if input data belongs to class defined by field objectToClassify.
    public int compute(Vec inputVector) {
        double sum = weightVector.getComponents()[0]; //Bias

        for (int i = 0; i < inputVector.getComponents().length; i++) {
            sum += inputVector.getComponents()[i] * weightVector.getComponents()[i + 1];
        }

        return sum >= 0 ? 1 : 0;
    }

    //    Updates weight vector.
    private void updateWeights(InputVec inputVector, int error) {
        double[] weights = weightVector.getComponents();
        double[] input = inputVector.getComponents();

//        Aktualizacja progu (bias)
        weights[0] += learningRate * error;

//        Aktualizacja wag
        for (int i = 0; i < input.length; i++) {
            weights[i + 1] += input[i] * error * learningRate;
        }

        weightVector.setComponents(weights);
    }


    @Override
    public String toString() {
        return "trainingVectors=" + trainingVectors +
                "\n, testVectors=" + testVectors + '}';
    }
}
