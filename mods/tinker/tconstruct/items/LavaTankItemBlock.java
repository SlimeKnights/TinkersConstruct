package mods.tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LavaTankItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Tank", "Gague", "Window"
    };

    public LavaTankItemBlock(int id)
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
        return (new StringBuilder()).append("LavaTank.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
