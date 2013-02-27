package tinker.tconstruct.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SearedTableItemBlock extends ItemBlock
{
    public static final String blockType[] =
    {
        "Table", "Faucet"
    };

    public SearedTableItemBlock(int id)
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
        return (new StringBuilder()).append("SearedTable.").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
