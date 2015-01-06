/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exterminatorJeff.undergroundBiomes.api;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * This is an interface for the class that can create Underground Biomes versions of arbitary ores
 * It creates three new blocks for each texturized ore.
 * @author Zeno410
 */
public interface UBOreTexturizer {
    // usage: Block is the ore block.
    // Overlay name is the fully qualified name, e.g. modname:overlayName
    // that static vars are fully qualified names for all the textures in the UB pack, just pass as is
    // the event isn't needed per se, but if this is called anytime else, the blocks will not "stick"
    public void setupUBOre(Block oreBlock, String overlayName, FMLPreInitializationEvent event);
    public void setupUBOre(Block oreBlock, int metadata, String overlayName, FMLPreInitializationEvent event);
    public void setupUBOre(Block oreBlock, int metadata, String overlayName, String blockName, FMLPreInitializationEvent event);

    public void requestUBOreSetup(Block oreBlock, String overlayName) throws BlocksAreAlreadySet;
    public void requestUBOreSetup(Block oreBlock, int metadata, String overlayName) throws BlocksAreAlreadySet;
    public void requestUBOreSetup(Block oreBlock, int metadata, String overlayName, String blockName) throws BlocksAreAlreadySet;
    public void redoOres(int xInBlockCoordinates, int zInBlockCoordinates, World serverSideWorld) ;

    public static String amber_overlay = "undergroundbiomes:amber_overlay";
    public static String cinnabar_overlay = "undergroundbiomes:cinnabar_overlay";
    public static String coal_overlay = "undergroundbiomes:coal_overlay";
    public static String copper_overlay = "undergroundbiomes:copper_overlay";
    public static String diamond_overlay = "undergroundbiomes:diamond_overlay";
    public static String emerald_overlay = "undergroundbiomes:emerald_overlay";
    public static String gold_overlay = "undergroundbiomes:gold_overlay";
    public static String iron_overlay = "undergroundbiomes:iron_overlay";
    public static String lapis_overlay = "undergroundbiomes:lapis_overlay";
    public static String lead_overlay = "undergroundbiomes:lead_overlay";
    public static String olivine_peridot_overlay = "undergroundbiomes:olivine-peridot_overlay";
    public static String redstone_overlay = "undergroundbiomes:redstone_overlay";
    public static String ruby_overlay = "undergroundbiomes:ruby_overlay";
    public static String sapphire_overlay = "undergroundbiomes:sapphire_overlay";
    public static String tin_overlay = "undergroundbiomes:tin_overlay";
    public static String uranium_overlay = "undergroundbiomes:uranium_overlay";

    public class BlocksAreAlreadySet extends RuntimeException {
        // this is thrown if UB has already run its pre-initialization step and can no longer register blocks
        public final Block oreBlock;
        public final String overlayName;
        
        public BlocksAreAlreadySet(Block oreBlock, String overlayName) {
            this.oreBlock = oreBlock;
            this.overlayName = overlayName;
        }

        @Override
        public String toString() {
            String blockDescription = "undefined block";
            String overlayDescription = "undefined overlay";
            if (oreBlock != null) blockDescription = oreBlock.getUnlocalizedName();
            if (overlayName != null) overlayDescription = overlayName;
            return "Attempt to create Underground Biomes ore for "+blockDescription+" with "+overlayDescription +
                    " after blocks have already been defined";
        }

    }
}
