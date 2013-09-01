package tconstruct.library.util;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

//Quantitive Trait Locus
public class Trait
{
    protected int[] alleles;
    static Random rand = new Random();
    String traitName;

    public Trait(String name, NBTTagCompound tags)
    {
        traitName = name;
        loadFromNBT(tags);
    }

    public Trait(int[] alleles)
    {
        this.alleles = alleles;
    }

    public Trait(int minimum, int maximum, int quantity, boolean bellCurve)
    {
        this.alleles = new int[quantity];
        if (bellCurve)
        {
            for (int i = 0; i < alleles.length; i++)
            {
                int value = (int) (rand.nextGaussian() * (maximum + 1) + minimum);
                alleles[i] = value;
            }
        }
        else
        {
            for (int i = 0; i < alleles.length; i++)
            {
                int value = rand.nextInt(maximum + 1) + minimum;
                alleles[i] = value;
            }
        }
    }

    public Trait setName (String name)
    {
        this.traitName = name;
        return this;
    }

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

    public Trait breed (byte[] other, float mutationChance, float mutationVariance)
    {
        int[] newAlleles = breedNumbers(other, mutationChance, mutationVariance);
        Trait trait = new Trait(newAlleles).setName(this.traitName);
        return trait;
    }

    public int[] breedNumbers (byte[] other, float mutationChance, float mutationVariance)
    {
        assert alleles.length == other.length : "Complete trait arrays need to have identical length";

        int[] result = new int[alleles.length > other.length ? alleles.length : other.length];

        for (int i = 0; i < result.length; i++)
        {
            if (rand.nextFloat() < mutationChance)
            {
                result[i] = mutate(alleles[i], other[i], mutationVariance);
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

    public void saveToNBT (NBTTagCompound tags)
    {
        tags.setIntArray(traitName + ".Alleles", alleles);
    }

    public void loadFromNBT (NBTTagCompound tags)
    {
        alleles = tags.getIntArray(traitName + ".Alleles");
    }
}
