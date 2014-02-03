package tconstruct.items.blocks;

import net.minecraft.block.Block;
import mantle.blocks.abstracts.MultiItemBlock;

public class SearedTableItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Table", "Faucet", "Basin" };

    public SearedTableItemBlock(Block b)
    {
        super(b, "SearedBlock", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
