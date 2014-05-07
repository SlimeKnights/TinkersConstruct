package tconstruct.library.accessory;

import net.minecraft.item.ItemStack;

public interface IHealthAccessory extends IAccessory
{
    public int getHealthBoost (ItemStack item);
}
