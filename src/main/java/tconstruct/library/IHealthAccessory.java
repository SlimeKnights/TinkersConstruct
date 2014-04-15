package tconstruct.library;

import net.minecraft.item.ItemStack;

public interface IHealthAccessory extends IAccessory
{
    public int getHealthBoost (ItemStack item);
}
