package com.man.ahl.random;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Float.max;
import static java.lang.Math.abs;

public class RandomGen {
    private static final float EPSILON = 1e-7f;

    // Values that may be returned by nextNum()
    private final int[] randomNums;

    // Probability of the occurrence of randomNums
    private final float[] cumulativeDistribution;

    private final Random random;

    public RandomGen(Random prngGenerator, int[] population, float[] probabilities) {
        validateInput(prngGenerator, population, probabilities);

        this.random = prngGenerator;
        this.randomNums = population;
        this.cumulativeDistribution = convertToCumulative(probabilities);

        final float totalProbability = cumulativeDistribution[cumulativeDistribution.length - 1];
        if (!fpEquals(totalProbability, 1.0f)) {
            throw new IllegalArgumentException("Probabilities must sum to 1");
        }
    }

    /**
     * Provides the next sample from the population according to
     * the specified discrete probability distribution.
     * <p>
     * Calculated using a simple form of inverse transformation
     * sampling. The discrete cumulative probability distribution
     * is calculated and a uniformly distributed pseudo random
     * number is used to define the point along the cumulative
     * distribution function from which to sample.
     * <p>
     * Algorithm runs in O(log n) time where n is the number of
     * points in the discrete probability distribution
     */
    public int nextNum() {
        final float prng = random.nextFloat();

        int index = Arrays.binarySearch(cumulativeDistribution, prng);
        if (Math.abs(index) > randomNums.length) {
            throw new IllegalStateException();
        }

        if (index < 0) {
            index = Math.abs(index + 1);
        }
        return randomNums[Math.abs(index)];
    }

    private void validateInput(Random prngGenerator, int[] population, float[] probabilities) {
        if (prngGenerator == null) {
            throw new IllegalArgumentException("prngGenerator cannot be null");
        }
        if (population == null || population.length < 1 || probabilities == null || probabilities.length < 1) {
            throw new IllegalArgumentException("Inputs must not be null or empty");
        }
        if (population.length != probabilities.length) {
            throw new IllegalArgumentException("Discrete distribution size must equal population size");
        }
    }

    /**
     * Convert the probability distribution to a
     * sorted cumulative probability distribution
     */
    private float[] convertToCumulative(float[] probabilities) {
        float[] cumulative = new float[probabilities.length];

        for (int i = 0; i < probabilities.length; i++) {
            cumulative[i] = probabilities[i];
            if (i > 0) {
                cumulative[i] += cumulative[i - 1];
            }
        }
        Arrays.sort(cumulative);
        return cumulative;
    }

    /**
     * Floating point equality using relative epsilon
     */
    private boolean fpEquals(float a, float b) {
        return abs(a - b) < EPSILON * max(abs(a), abs(b));
    }
}
