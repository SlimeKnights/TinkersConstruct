package tconstruct.tools.itemblocks;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;

public class ToolStationItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Crafter", "Parts", "Parts", "Parts", "Parts", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternShaper", "PatternShaper", "PatternShaper", "PatternShaper", "CastingTable" };

    public ToolStationItemBlock(Block b)
    {
        super(b, "ToolStation", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
