package mods.tinker.tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class MetalItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Tin", "Aluminum", "AlBrass", "Alumite", "Steel"
    };

    public MetalItemBlock(int id)
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
        return (new StringBuilder()).append("StorageMetals.").append(blockType[pos]).toString();
    }
}
