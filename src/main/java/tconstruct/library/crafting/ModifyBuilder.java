package tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModifyBuilder
{
    public static ModifyBuilder instance = new ModifyBuilder();
    public List<ItemModifier> itemModifiers = new ArrayList<ItemModifier>();
    
    public ItemStack modifyItem(ItemStack input, ItemStack[] modifiers)
    {
        ItemStack copy = input.copy(); //Prevent modifying the original
        if (copy.getItem() instanceof IModifyable)
        {
            boolean built = false;
            for (ItemModifier mod : itemModifiers)
            {
                if (mod.matches(modifiers, copy))
                {
                    built = true;
                    mod.addMatchingEffect(copy); //Order matters here
                    mod.modify(modifiers, copy);
                }
            }
            if (built)
                return copy;
        }
        return null;
    }
    
    public static void registerModifier(ItemModifier mod)
    {
        if (mod == null)
            throw new NullPointerException("Modifier cannot be null.");
        instance.itemModifiers.add(mod);
    }
}
