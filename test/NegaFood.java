package test;

import net.minecraft.item.ItemFood;

public class NegaFood extends ItemFood
{

	public NegaFood()
	{
		super(10001, -20, 0, false);
		setAlwaysEdible();
	}

}
