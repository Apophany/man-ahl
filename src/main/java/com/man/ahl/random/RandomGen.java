package com.man.ahl.random;

import java.util.Arrays;
import java.util.Random;

public class RandomGen {
    // Values that may be returned by nextNum()
    private final int[] randomNums;
    // Probability of the occurence of randomNums
    private final float[] cumulativeDistribution;
    private Random random;

    public RandomGen(int[] population, float[] probabilities) {
        if (population.length != probabilities.length) {
            throw new IllegalArgumentException("Discrete distribution size must equal population");
        }
        this.random = new Random();
        this.randomNums = population;
        this.cumulativeDistribution = convertToCumulative(probabilities);
    }

    /**
     Returns one of the randomNums. When this method is called
     multiple times over a long period, it should return the
     numbers roughly with the initialized probabilities.
     */
    public int nextNum() {
        final float prng = random.nextFloat();
        for (int i = 0; i < cumulativeDistribution.length; i++) {
            if (prng <= cumulativeDistribution[i]) {
                return randomNums[i];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Convert the probability distribution to a
     * sorted cumulative probability distribution
     */
    private float[] convertToCumulative(float[] probabilities) {
        float[] cumulative = new float[probabilities.length];
        cumulative[0] = probabilities[0];

        for (int i = 1; i < probabilities.length; i++) {
            cumulative[i] = cumulative[i - 1] + probabilities[i];
        }
        Arrays.sort(cumulative);
        return cumulative;
    }
}
