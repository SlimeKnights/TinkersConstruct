package tconstruct.smeltery.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

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
