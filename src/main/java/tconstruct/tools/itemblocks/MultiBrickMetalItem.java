package tconstruct.tools.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class MultiBrickMetalItem extends MultiItemBlock {
    static String blockTypes[] = { "brick.alumite", "brick.ardite", "brick.cobalt", "brick.manyullyn", "fancybrick.alumite", "fancybrick.ardite", "fancybrick.cobalt", "fancybrick.manyullyn" };

    public MultiBrickMetalItem(Block b)
    {
        super(b, "block", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
