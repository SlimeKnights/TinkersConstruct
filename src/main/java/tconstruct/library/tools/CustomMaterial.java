package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public abstract class CustomMaterial
{
    public final int materialID;
    public final int value;
    public final ItemStack input;
    public final ItemStack craftingItem;

    public CustomMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem)
    {
        this.materialID = materialID;
        this.value = value;
        this.input = input;
        this.craftingItem = craftingItem;
    }

    /*
     * public boolean matches(ItemStack input, ItemStack pattern) { if
     * (ItemStack.areItemStacksEqual(this.input, input) &&
     * ItemStack.areItemStacksEqual(this.craftingPattern, pattern)) return true;
     * return false; }
     */
}
