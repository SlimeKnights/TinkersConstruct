package mods.tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CraftedSoilItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Slime", "Grout"
    };

    public CraftedSoilItemBlock(int id)
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
        return (new StringBuilder()).append("CraftedSoil.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
