package mods.tinker.tconstruct.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class BattleaxeRenderer implements IItemRenderer
{

    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        return false;
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data)
    {
        
    }

}
