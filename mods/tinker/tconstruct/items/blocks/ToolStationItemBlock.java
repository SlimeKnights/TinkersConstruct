package mods.tinker.tconstruct.items.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ToolStationItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Crafter", "Parts", "Parts", "Parts", "Parts", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternShaper",
            "PatternShaper", "PatternShaper", "PatternShaper", "CastingTable" };

    public ToolStationItemBlock(int id)
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
        return (new StringBuilder()).append("ToolStation.").append(blockType[pos]).toString();
    }
}
