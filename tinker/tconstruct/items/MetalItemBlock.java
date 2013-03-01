package tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append("StorageMetals.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
