package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class GravelOreItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "iron", "gold", "copper", "tin", "aluminum", "cobalt" };

    public GravelOreItem(Block b)
    {
        super(b, "block.ore.gravel", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
