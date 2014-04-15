package tconstruct.library;

import net.minecraft.item.ItemStack;

public interface IAccessory
{
    public boolean canEquipItem (ItemStack item, int slot);
}
