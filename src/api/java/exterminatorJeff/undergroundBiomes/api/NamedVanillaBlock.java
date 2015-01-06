package exterminatorJeff.undergroundBiomes.api;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class NamedVanillaBlock extends NamedBlock {

    public final static NamedBlock cobblestone = new NamedVanillaBlock(Blocks.cobblestone);
    public final static NamedBlock cobblestone_wall = new NamedVanillaBlock(Blocks.cobblestone_wall);
    public final static NamedBlock dispenser= new NamedVanillaBlock(Blocks.dispenser);
    public final static NamedBlock furnace= new NamedVanillaBlock(Blocks.furnace);

    public final static NamedBlock lever= new NamedVanillaBlock(Blocks.lever);
    public final static NamedBlock piston= new NamedVanillaBlock(Blocks.piston);
    public final static NamedBlock planks= new NamedVanillaBlock(Blocks.planks);
    public final static NamedBlock stone_pressure_plate= new NamedVanillaBlock(Blocks.stone_pressure_plate);

    public final static NamedBlock sand= new NamedVanillaBlock(Blocks.sand);
    public final static NamedBlock sandstone= new NamedVanillaBlock(Blocks.sandstone);
    public final static NamedBlock smoothSandstone= new NamedVanillaBlock(Blocks.sandstone);
    public final static NamedBlock stairsCobblestone= new NamedVanillaBlock(Blocks.stone_stairs);
    public final static NamedBlock stairsStoneBrick= new NamedVanillaBlock(Blocks.stone_brick_stairs);
    public final static NamedBlock stone= new NamedVanillaBlock(Blocks.stone);
    public final static NamedBlock stoneBrick= new NamedVanillaBlock(Blocks.stonebrick);
    public final static NamedBlock stoneButton= new NamedVanillaBlock(Blocks.stone_button);
    public final static NamedBlock stoneSingleSlab= new NamedVanillaBlock(Blocks.stone_slab);
    public final static NamedBlock torchRedstoneActive= new NamedVanillaBlock(Blocks.lit_redstone_lamp);

    public NamedVanillaBlock(String name) {
        super(name);
        id = UBIDs.blockID(name);
        block = UBIDs.blockNamed(name);
    }

    public NamedVanillaBlock(Block _block) {
        super(_block.getUnlocalizedName());
        id = Block.getIdFromBlock(_block);
        block = _block;
    }

    public Block block() {
        if (block == null) {
           block = UBIDs.blockNamed(this.internal());
           //if (block == null) throw new RuntimeException();
        }
        return block;
    }
}
