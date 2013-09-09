package biomesoplenty.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;

import com.google.common.base.Optional;

public class Fluids
{
	public static Optional<? extends Item> 	bopBucket         		        = Optional.absent();

	public static Optional<? extends Block> springWater                     = Optional.absent();
	public static Optional<? extends Block> liquidPoison                    = Optional.absent();

	public static Optional<? extends Fluid> springWaterFluid                = Optional.absent();
	public static Optional<? extends Fluid> liquidPoisonFluid               = Optional.absent();
}
