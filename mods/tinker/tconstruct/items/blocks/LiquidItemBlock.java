package mods.tinker.tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class LiquidItemBlock extends ItemBlock
{
    public static final String blockType[] =
    	{ "Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "Brass", "Manyullyn", "Alumite", "Obsidian", "Steel" };

    public LiquidItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata(int meta)
    {
        return meta;
    }

    public String getUnlocalizedName(ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length-1);
        return (new StringBuilder()).append("LiquidMetal.").append(blockType[pos]).toString();
    }
}
