package tconstruct.library.crafting;

import java.util.*;
import net.minecraftforge.fluids.FluidStack;

public class AlloyMix
{
    public final FluidStack result;
    public final List<FluidStack> mixers;

    public AlloyMix(FluidStack output, List<FluidStack> inputs)
    {
        result = output;
        mixers = inputs;
    }

    /*
     * public boolean matches(List liquids) { ArrayList list = new
     * ArrayList(mixers); return false; }
     */

    public FluidStack mix (ArrayList<FluidStack> liquids)
    {
        ArrayList<FluidStack> copyMix = new ArrayList(mixers);
        ArrayList effectiveAmount = new ArrayList();

        for (int i = 0; i < liquids.size(); i++)
        {
            FluidStack liquid = liquids.get(i);
            Iterator iter = copyMix.iterator();
            while (iter.hasNext())
            {
                FluidStack mixer = (FluidStack) iter.next();
                // if (mixer.itemID == liquid.itemID && mixer.itemMeta ==
                // liquid.itemMeta)
                if (mixer.isFluidEqual(liquid))
                {
                    int eAmt = liquid.amount / mixer.amount;
                    effectiveAmount.add(eAmt);
                    copyMix.remove(mixer);
                    // inputs.add(liquid);
                    break;
                }
            }
        }
        // }

        if (copyMix.size() > 0)
            return null;

        // Remove old liquids
        int low = getLowestAmount(effectiveAmount);
        ArrayList<FluidStack> copyMix2 = new ArrayList(mixers);

        for (int i = 0; i < liquids.size(); i++)
        {
            FluidStack liquid = liquids.get(i);
            Iterator iter = copyMix2.iterator();
            while (iter.hasNext())
            {
                FluidStack mixer = (FluidStack) iter.next();
                // if (mixer.itemID == liquid.itemID && mixer.itemMeta ==
                // liquid.itemMeta)
                if (mixer.isFluidEqual(liquid))
                {
                    int eAmt = low * mixer.amount;
                    liquid.amount -= eAmt;
                    if (liquid.amount <= 0)
                    {
                        liquids.remove(liquid);
                        i--;
                    }
                    copyMix2.remove(mixer);
                    break;
                }
            }
        }

        FluidStack ret = result.copy();
        ret.amount *= low;
        return ret;
    }

    int getLowestAmount (ArrayList list)
    {
        int frist = (Integer) list.get(0); // FRIST!!!
        for (int i = 1; i < list.size(); i++)
        {
            int compare = (Integer) list.get(i);
            if (frist > compare)
                frist = compare;
        }
        return frist;
    }
}
