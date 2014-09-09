package tconstruct.library.tools;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class CustomMaterial
{
    public final int materialID;
    public final int value;
    public final ItemStack input;
    public final ItemStack craftingItem;
    public final String oredict;

    public CustomMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem)
    {
        this.materialID = materialID;
        this.value = value;
        this.input = input;
        this.craftingItem = craftingItem;
        this.oredict = null;
    }

    public CustomMaterial(int materialID, int value, String oredict, ItemStack craftingItem)
    {
        this.materialID = materialID;
        this.value = value;
        this.input = null;
        this.craftingItem = craftingItem;
        this.oredict = oredict;
    }

    /**
     * Wether an itemstack is a stack of this custom material or not.
     */
    public boolean matches (ItemStack stack)
    {
        if (this.oredict != null)
        {
            List<ItemStack> items = OreDictionary.getOres(oredict);
            for (ItemStack item : items)
                if (OreDictionary.itemMatches(item, stack, false))
                    return true;
            return false;
        }
        return stack.isItemEqual(input);
    }

}
