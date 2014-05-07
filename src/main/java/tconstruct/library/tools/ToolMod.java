package tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.ItemModifier;

@Deprecated
public abstract class ToolMod extends ItemModifier
{

    public ToolMod(ItemStack[] recipe, int effect, String dataKey)
    {
        super(recipe, effect, dataKey);
    }

}
