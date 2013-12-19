package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

public class ToolStationItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Crafter", "Parts", "Parts", "Parts", "Parts", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternChest", "PatternShaper",
            "PatternShaper", "PatternShaper", "PatternShaper", "CastingTable" };

    public ToolStationItemBlock(int id)
    {
        super(id, "ToolStation", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
