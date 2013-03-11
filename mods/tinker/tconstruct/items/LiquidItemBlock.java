package mods.tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
        return (new StringBuilder()).append("LiquidMetal.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
