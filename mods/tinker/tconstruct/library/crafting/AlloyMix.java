package mods.tinker.tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.liquids.LiquidStack;

public class AlloyMix
{
	public final LiquidStack result;
	public final List<LiquidStack> mixers;

	public AlloyMix(LiquidStack output, List<LiquidStack> inputs)
	{
		result = output;
		mixers = inputs;
	}

	/*public boolean matches(List liquids)
	{
		ArrayList list = new ArrayList(mixers);
		return false;
	}*/

	public LiquidStack mix (ArrayList<LiquidStack> liquids)
	{
		ArrayList<LiquidStack> copyMix = new ArrayList(mixers);
		ArrayList effectiveAmount = new ArrayList();

		for (int i = 0; i < liquids.size(); i++)
		{
			LiquidStack liquid = liquids.get(i);
			Iterator iter = copyMix.iterator();
			while (iter.hasNext())
			{
				LiquidStack mixer = (LiquidStack) iter.next();
				if (mixer.itemID == liquid.itemID && mixer.itemMeta == liquid.itemMeta)
				{
					int eAmt = liquid.amount / mixer.amount;
					effectiveAmount.add(eAmt);
					copyMix.remove(mixer);
					//inputs.add(liquid);
					break;
				}
			}
		}
		//}

		if (copyMix.size() > 0)
			return null;

		//Remove old liquids
		int low = getLowestAmount(effectiveAmount);
		ArrayList<LiquidStack> copyMix2 = new ArrayList(mixers);

		for (int i = 0; i < liquids.size(); i++)
		{
			LiquidStack liquid = liquids.get(i);
			Iterator iter = copyMix2.iterator();
			while (iter.hasNext())
			{
				LiquidStack mixer = (LiquidStack) iter.next();
				if (mixer.itemID == liquid.itemID && mixer.itemMeta == liquid.itemMeta)
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

		return new LiquidStack(result.itemID, result.amount * low, result.itemMeta);
	}

	int getLowestAmount (ArrayList list)
	{
		int frist = (Integer) list.get(0); //FRIST!!!
		for (int i = 1; i < list.size(); i++)
		{
			int compare = (Integer) list.get(i);
			if (frist > compare)
				frist = compare;
		}
		return frist;
	}
}
