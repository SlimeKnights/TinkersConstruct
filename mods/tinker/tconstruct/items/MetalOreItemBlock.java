package mods.tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MetalOreItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "NetherSlag", "Cobalt", "Ardite", "Copper", "Tin", "Aluminum", "Slag"
    };

    public MetalOreItemBlock(int id)
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
        return (new StringBuilder()).append("MetalOre.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
