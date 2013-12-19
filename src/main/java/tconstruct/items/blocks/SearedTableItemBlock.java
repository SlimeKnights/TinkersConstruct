package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class SearedTableItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Table", "Faucet", "Basin" };

    public SearedTableItemBlock(int id)
    {
        super(id, "SearedBlock", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
