package main;

import java.io.Serializable;

public class NeuralNetwork implements Serializable {
	public int[] layers;
	public int networkSize;
	int inputSize, outputSize;

	public double[][] activation;
	public double[][][] weights;
	double[][] bias;
	double[][] errors;
	double[][] derivative;

	public NeuralNetwork(int... layers) {
		this.layers = layers;
		networkSize = layers.length;
		inputSize = layers[0];
		outputSize = layers[layers.length - 1];
		activation = new double[networkSize][];
		errors = new double[networkSize][];
		derivative = new double[networkSize][];
		bias = new double[networkSize][];
		weights = new double[networkSize][][];
		for (int i = 0; i < networkSize; i++) {
			activation[i] = new double[layers[i]];
			errors[i] = new double[layers[i]];
			derivative[i] = new double[layers[i]];

			bias[i] = NetworkTools.createRandomArray(layers[i], -0.5, 0.5);

			if (i > 0) {
				weights[i] = NetworkTools.createRandomArray(layers[i], layers[i - 1], -0.5, 0.5);
			}
		}
	}

	public double[] run(double... input) {
		if (input.length != inputSize)
			return null;
		activation[0] = input;
		for (int layer = 1; layer < networkSize; layer++) {
			for (int neuron = 0; neuron < layers[layer]; neuron++) {
				double sum = bias[layer][neuron];
				for (int prev = 0; prev < layers[layer - 1]; prev++) {
					sum += activation[layer - 1][prev] * weights[layer][neuron][prev];
				}
				activation[layer][neuron] = sigmoid(sum);
				derivative[layer][neuron] = activation[layer][neuron] * (1 - activation[layer][neuron]);
			}
		}
		return activation[networkSize - 1].clone();
	}

	public static double sigmoid(double x) {
		return 1d / (1 + Math.exp(-x));
	}

	public void train(double[] input, double[] target, double eta) {
		if (input.length != inputSize || target.length != outputSize)
			return;
		run(input);
		backProp(target);
		updateWeights(eta);
	}

	public void train(TrainSet set, int loops, int batch_size) {
		if (set.INPUT_SIZE != inputSize || set.OUTPUT_SIZE != outputSize)
			return;
		for (int i = 0; i < loops; i++) {
			TrainSet batch = set.extractBatch(batch_size);
			for (int j = 0; j < batch_size; j++) {
				this.train(batch.getInput(j), batch.getOutput(j), 0.3);
			}
		}
	}

	public void backProp(double[] target) {
		for (int neuron = 0; neuron < layers[networkSize - 1]; neuron++) {
			errors[networkSize - 1][neuron] = (activation[networkSize - 1][neuron] - target[neuron])
					* derivative[networkSize - 1][neuron];
		}
		for (int layer = networkSize - 2; layer > 0; layer--) {
			for (int neuron = 0; neuron < layers[layer]; neuron++) {
				double sum = 0;
				for (int next = 0; next < layers[layer + 1]; next++) {
					sum += weights[layer + 1][next][neuron] * errors[layer + 1][next];
				}
				errors[layer][neuron] = sum * derivative[layer][neuron];
			}
		}
	}

	public void updateWeights(double eta) {
		for (int layer = 1; layer < networkSize; layer++) {
			for (int neuron = 0; neuron < layers[layer]; neuron++) {
				for (int prev = 0; prev < layers[layer - 1]; prev++) {
					weights[layer][neuron][prev] -= eta * errors[layer][neuron] * activation[layer - 1][prev];

				}
				bias[layer][neuron] -= eta * errors[layer][neuron];

			}
		}
	}

}
