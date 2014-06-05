package tconstruct.tools.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class CraftingSlabItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "tile.CraftingStation", "Crafter", "Parts", "PatternShaper", "PatternChest", "tile.ToolForge" };

    public CraftingSlabItemBlock(Block b)
    {
        super(b, "ToolStation", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, this.blockTypes.length - 1);
        if (pos == 0 || pos == 5)
        {
            return this.blockTypes[pos];
        }
        return super.getUnlocalizedName(itemstack);
    }
}
