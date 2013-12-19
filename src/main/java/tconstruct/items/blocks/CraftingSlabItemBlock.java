package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class CraftingSlabItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "tile.CraftingStation", "Crafter", "Parts", "PatternShaper", "PatternChest", "tile.ToolForge" };

    public CraftingSlabItemBlock(int id)
    {
        super(id, "ToolStation", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, this.blockType.length - 1);
        if (pos == 0 || pos == 5)
        {
            return this.blockType[pos];
        }
        return super.getUnlocalizedName(itemstack);
    }
}
