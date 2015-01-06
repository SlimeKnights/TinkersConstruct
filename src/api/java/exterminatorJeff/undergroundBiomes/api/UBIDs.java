package exterminatorJeff.undergroundBiomes.api;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * A shell for various constants
 * @author Zeno410
 */
public class UBIDs {

    public final static int version = 3;

    public final static NamedBlock igneousStoneName = new NamedBlock("igneousStone");
    public final static NamedBlock igneousCobblestoneName = new NamedBlock("igneousCobblestone");
    public final static NamedBlock igneousStoneBrickName= new NamedBlock("igneousStoneBrick");
    public final static NamedBlock metamorphicStoneName= new NamedBlock("metamorphicStone");
    public final static NamedBlock metamorphicCobblestoneName= new NamedBlock("metamorphicCobblestone");
    public final static NamedBlock metamorphicStoneBrickName= new NamedBlock("metamorphicStoneBrick");
    public final static NamedBlock sedimentaryStoneName = new NamedBlock("sedimentaryStone");

    public final static NamedItem ligniteCoalName  = new NamedItem("ligniteCoal");
    public final static NamedItem fossilPieceName = new NamedItem("fossilPiece");

    public final static NamedSlabPair igneousBrickSlabName = new NamedSlabPair(igneousStoneBrickName);
    public final static NamedSlabPair metamorphicBrickSlabName = new NamedSlabPair(metamorphicStoneBrickName);
    public final static NamedSlabPair igneousStoneSlabName = new NamedSlabPair(igneousStoneName);
    public final static NamedSlabPair metamorphicStoneSlabName = new NamedSlabPair(metamorphicStoneName);
    public final static NamedSlabPair igneousCobblestoneSlabName = new NamedSlabPair(igneousCobblestoneName);
    public final static NamedSlabPair metamorphicCobblestoneSlabName = new NamedSlabPair(metamorphicCobblestoneName);
    public final static NamedSlabPair sedimentaryStoneSlabName = new NamedSlabPair(sedimentaryStoneName);
    
    public final static NamedBlock UBButtonName = new NamedBlock("button");
    public final static NamedBlock UBStairsName = new NamedBlock("stairs");
    public final static NamedBlock UBWallsName = new NamedBlock("wall");
    public final static NamedItem UBButtonItemName = new NamedItem(UBButtonName);
    public final static NamedItem UBStairsItemName = new NamedItem(UBStairsName);
    public final static NamedItem UBWallsItemName = new NamedItem(UBWallsName);
    public final static NamedBlock IconTrap = new NamedBlock("iconTrap");


    public static final String ubPrefix() {return "UndergroundBiomes:";}
    public static final String ubIconPrefix() {return "undergroundbiomes:";}

    public static String publicName(String inModName) {
        if (inModName.contains(ubPrefix())) return inModName;
        return ubPrefix()+inModName;
    }

    public static String iconName(String inModName) {
        if (inModName.contains(ubIconPrefix())) return inModName;
        return ubIconPrefix()+inModName;
    }

    public static Item itemNamed(String name) {
        return (Item)(Item.itemRegistry.getObject(name));
    }

    public static Block blockNamed(String name) {
        return Block.getBlockFromName(name);
    }

    public static int itemID(String name) {
        return Item.getIdFromItem(itemNamed(name));
    }

    public static int blockID(String name) {
        return Block.getIdFromBlock(Block.getBlockFromName(name));
    }

    public static NamedBlock slabVersionID(NamedBlock ubStone) {
        if (ubStone == igneousStoneName) return igneousStoneSlabName.half;
        if (ubStone == igneousCobblestoneName) return igneousCobblestoneSlabName.half;
        if (ubStone == igneousStoneBrickName) return igneousBrickSlabName.half;
        if (ubStone == metamorphicStoneName) return metamorphicStoneSlabName.half;
        if (ubStone == metamorphicCobblestoneName) return metamorphicCobblestoneSlabName.half;
        if (ubStone == metamorphicStoneBrickName) return metamorphicBrickSlabName.half;
        if (ubStone == sedimentaryStoneName) return sedimentaryStoneSlabName.half;
        if (ubStone == NamedVanillaBlock.sandstone) return NamedVanillaBlock.stoneSingleSlab;
        if (ubStone == NamedVanillaBlock.stone) return NamedVanillaBlock.stoneSingleSlab;
        if (ubStone == NamedVanillaBlock.cobblestone) return NamedVanillaBlock.stoneSingleSlab;
        if (ubStone == NamedVanillaBlock.sand) return NamedVanillaBlock.stoneSingleSlab;
        throw new RuntimeException(""+ ubStone + " is not not usable as an Underground Biomes stone code");
    }

    public static NamedBlock brickVersionID(NamedBlock ubStone) {
        if (ubStone == igneousStoneName) return igneousStoneBrickName;
        if (ubStone == igneousCobblestoneName) return igneousStoneBrickName;
        if (ubStone == igneousStoneBrickName) return igneousStoneBrickName;
        if (ubStone == metamorphicStoneName) return metamorphicStoneBrickName;
        if (ubStone == metamorphicCobblestoneName) return metamorphicStoneBrickName;
        if (ubStone == metamorphicStoneBrickName) return metamorphicStoneBrickName;
        if (ubStone == sedimentaryStoneName) return sedimentaryStoneName;
        if (ubStone == NamedVanillaBlock.sandstone) return NamedVanillaBlock.smoothSandstone;
        if (ubStone == NamedVanillaBlock.stone) return NamedVanillaBlock.stoneBrick;
        if (ubStone == NamedVanillaBlock.cobblestone) return NamedVanillaBlock.stoneBrick;
        if (ubStone == NamedVanillaBlock.sand) return NamedVanillaBlock.sandstone;
        throw new RuntimeException(""+ ubStone + " is not usable as an Underground Biomes stone code");
    }
}
