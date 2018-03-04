package com.man.ahl.random;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RandomGenTest {
    @Test
    public void test_graphical_distribution() {
        final int[] population = new int[]{1, 4, 5, 2};
        final float[] distribution = new float[]{0.1f, 0.5f, 0.35f, 0.05f};

        RandomGen gen = new RandomGen(population, distribution);

        Map<Integer, Integer> observations = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            int observation = gen.nextNum();
            observations.merge(observation, 1, (o, n) -> o + 1);
        }

        for (int i = 0; i < population.length; i++) {
            System.out.print(String.format("%d:", population[i]));
            Integer count = observations.get(population[i]);
            if (count != null) {
                System.out.print(Strings.repeat("*", count));
            }
            System.out.print("\n");
        }
    }
}
