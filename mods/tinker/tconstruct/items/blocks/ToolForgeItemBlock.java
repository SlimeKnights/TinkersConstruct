package mods.tinker.tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;

public class ToolForgeItemBlock extends ItemBlock
{
    public static final String blockType[] = { "iron", "gold", "diamond", "emerald", "cobalt", "ardite", "manyullyn", "copper", "bronze", "tin", "aluminum", "alubrass", "alumite", "steel" };

    public ToolForgeItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    /*@Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.ToolForge.").append(blockType[pos]).toString();
    }*/
}
