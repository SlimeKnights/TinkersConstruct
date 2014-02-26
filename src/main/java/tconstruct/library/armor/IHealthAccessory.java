package tconstruct.library.armor;

import net.minecraft.item.ItemStack;

public interface IHealthAccessory
{
    public boolean canEquipItem (ItemStack item, int slot);

    public int getHealthBoost (ItemStack item);
}