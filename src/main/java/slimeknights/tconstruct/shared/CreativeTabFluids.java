package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import slimeknights.mantle.client.CreativeTab;

import java.util.List;

public class CreativeTabFluids extends CreativeTab
{
	public CreativeTabFluids()
	{
		super("TinkerFluids", ForgeModContainer.getInstance().universalBucket.getEmpty());
	}

	@Override
	public void displayAllRelevantItems(List<ItemStack> itemList)
	{
		super.displayAllRelevantItems(itemList);

		UniversalBucket bucket = ForgeModContainer.getInstance().universalBucket;

		for (Fluid fluid : TinkerFluids.fluids)
		{
			FluidStack fs = new FluidStack(fluid, bucket.getCapacity());
			ItemStack stack = new ItemStack(bucket);
			boolean canAdd = true; // See Note below.
			if (bucket.fill(stack, fs, true) == fs.amount)
			{
				// Note: It looks like you are adding some Fluids to TinkerFluids.fluids twice.
				for(ItemStack is : itemList)
				{
					if(bucket.getFluid(is).isFluidEqual(stack))
					{
						canAdd = false;
						break;
					}
				}
				if (canAdd || itemList.size() < 1) itemList.add(stack);
				// End Note
			}
		}
	}
}
