package tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SearedTableItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Table", "Faucet", "Basin" };

    public SearedTableItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("SearedBlock.").append(blockType[pos]).toString();
    }
}
