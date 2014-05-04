package tconstruct.library;

import net.minecraft.item.ItemStack;

public interface IAccessory
{
    public boolean canEquipAccessory (ItemStack item, int slot);
}
