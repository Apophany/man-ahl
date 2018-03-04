package com.man.ahl.random;

import com.google.common.base.Strings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.when;

/**
 * Unit tests for generation of a sample from a discrete
 * probability distribution. The unit tests do not test
 * the statistical integrity of the resultant distribution,
 * although some basic tests are performed to ensure that
 * expected values are returned for a given PRNG.
 *
 * If one wished to perform a statistical test against
 * the generated observations, then a chi square goodness of
 * fit could be performed.
 *
 * A single test is also provided which outputs a visual
 * representation of the distribution of the resultant
 * observations.
 */
@RunWith(Enclosed.class)
public class RandomGenTest {

    @RunWith(Parameterized.class)
    public static final class InputValidationTests {

        @Parameterized.Parameter()
        public Random random;

        @Parameterized.Parameter(1)
        public int[] population;

        @Parameterized.Parameter(2)
        public float[] distribution;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    new Object[][]{
                            //Non matching sizes
                            {new Random(), new int[]{1, 2}, new float[]{1f}},

                            //Null Random
                            {null, new int[]{1, 2}, new float[]{1f}},

                            //Null population
                            {new Random(), null, new float[]{1f}},

                            //Null distribution
                            {new Random(), new int[]{1, 2}, null},

                            //Empty inputs
                            {new Random(), new int[]{}, new float[]{}},

                            //Probability sum > 1
                            {new Random(), new int[]{1, 2, 3}, new float[]{0.25f, 0.5f, 0.3f}}
                    }
            );
        }

        @Test (expected = IllegalArgumentException.class)
        public void test_validate_inputs() {
            new RandomGen(random, population, distribution);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static final class DistributionValidationTests {

        @Mock
        private Random random;

        @Test
        public void test_prng_equals_distribution_point() {
            final int[] populationValues = new int[]{1, 4, 5, 2};
            final float[] distribution = new float[]{0.1f, 0.5f, 0.35f, 0.05f};

            when(random.nextFloat()).thenReturn(0.6f);

            final RandomGen gen = new RandomGen(random, populationValues, distribution);
            final int res = gen.nextNum();

            Assert.assertEquals(4, res);
        }

        @Test
        public void test_prng_below_distribution_point() {
            final int[] populationValues = new int[]{1, 4, 5, 2};
            final float[] distribution = new float[]{0.1f, 0.5f, 0.35f, 0.05f};

            when(random.nextFloat()).thenReturn(0.3f);

            final RandomGen gen = new RandomGen(random, populationValues, distribution);
            final int res = gen.nextNum();

            Assert.assertEquals(4, res);
        }


        @Test
        public void test_prng_above_distribution_point() {
            final int[] populationValues = new int[]{1, 4, 5, 2};
            final float[] distribution = new float[]{0.1f, 0.5f, 0.35f, 0.05f};

            when(random.nextFloat()).thenReturn(0.61f);

            final RandomGen gen = new RandomGen(random, populationValues, distribution);
            final int res = gen.nextNum();

            Assert.assertEquals(5, res);
        }

        @Test
        public void test_distribution_with_zeros() {
            final int[] populationValues = new int[]{1, 4, 5, 2};
            final float[] distribution = new float[]{0.1f, 0.0f, 0.85f, 0.05f};

            final RandomGen gen = new RandomGen(random, populationValues, distribution);

            when(random.nextFloat()).thenReturn(0.11f);

            int res;
            res = gen.nextNum();
            Assert.assertEquals(5, res);

            when(random.nextFloat()).thenReturn(0.95f);
            res = gen.nextNum();
            Assert.assertEquals(5, res);

            when(random.nextFloat()).thenReturn(0.97f);
            res = gen.nextNum();
            Assert.assertEquals(2, res);
        }

        @Test
        public void visualise_distribution() {
            final int[] populationValues = new int[]{1, 4, 5, 2};
            final float[] distribution = new float[]{0.1f, 0.5f, 0.35f, 0.05f};

            RandomGen gen = new RandomGen(new Random(), populationValues, distribution);

            Map<Integer, Integer> observations = new HashMap<>();
            for (int i = 0; i < 100; i++) {
                int observation = gen.nextNum();
                observations.merge(observation, 1, (o, n) -> o + 1);
            }

            for (int sample : populationValues) {
                System.out.print(String.format("%d:", sample));
                Integer count = observations.get(sample);
                if (count != null) {
                    System.out.print(Strings.repeat("*", count));
                }
                System.out.print("\n");
            }
        }
    }
}
