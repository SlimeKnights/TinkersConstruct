package mods.tinker.tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class CraftingSlabItemBlock extends ItemBlock
{
    public static final String blockType[] = { "tile.CraftingStation", "Crafter", "Parts", "PatternShaper", "PatternChest", "tile.ToolForge" };

    public CraftingSlabItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        if (pos == 0 || pos == 5)
        {
            return blockType[pos];
        }
        return (new StringBuilder()).append("ToolStation.").append(blockType[pos]).toString();
    }
}
