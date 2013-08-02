package test;

import java.util.Random;

//Quantitive Trait Locus
public class Trait
{
    protected int[] alleles;
    static Random rand = new Random();

    public int getAverage ()
    {
        int average = 0;

        if (alleles.length > 0)
        {
            for (byte i = 0; i < alleles.length; i++)
                average += alleles[i];

            average /= alleles.length;
        }

        return average;
    }

    public int[] breed (byte[] other, float[] mutation)
    {
        assert alleles.length == other.length : "Complete trait arrays need to have identical length";

        int[] result = new int[alleles.length > other.length ? alleles.length : other.length];

        for (int i = 0; i < result.length; i++)
        {
            if (rand.nextFloat() < mutation[0])
            {
                result[i] = mutate(alleles[i], other[i], mutation[1]);
            }
            else
            {
                result[i] = rand.nextBoolean() ? alleles[i] : other[i];
            }
        }

        return result;
    }

    //Assumes linear progression from worst to best. Use other methods for other types
    protected int mutate (int allele, int other, float variance)
    {
        int mutator = rand.nextBoolean() ? allele : other;
        double bellcurve = rand.nextGaussian();
        double offset = variance * bellcurve;
        offset -= offset / 2;
        
        if (mutator + offset < 0)
            mutator = 0;
        else if (mutator + offset > Integer.MAX_VALUE)
            mutator = Integer.MAX_VALUE;
        else
            mutator = (int) (mutator + offset);
        
        return mutator;
    }
}
