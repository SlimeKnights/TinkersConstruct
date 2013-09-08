package biomesoplenty.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;

import com.google.common.base.Optional;

public class Blocks
{
	// Worldgen Blocks
	public static Optional<? extends Block> ash                         = Optional.absent();
	public static Optional<? extends Block> ashStone                    = Optional.absent();
	public static Optional<? extends Block> cragRock                    = Optional.absent();
	public static Optional<? extends Block> driedDirt                   = Optional.absent();
	public static Optional<? extends Block> hardDirt                    = Optional.absent();
	public static Optional<? extends Block> hardIce                     = Optional.absent();
	public static Optional<? extends Block> hardSand                    = Optional.absent();
	public static Optional<? extends Block> holyGrass                   = Optional.absent();
	public static Optional<? extends Block> holyDirt                   	= Optional.absent();
	public static Optional<? extends Block> holyStone                   = Optional.absent();
	public static Optional<? extends Block> mud                         = Optional.absent();
	public static Optional<? extends Block> originGrass                 = Optional.absent();
	public static Optional<? extends Block> longGrass                 = Optional.absent();
	public static Optional<? extends Block> redRock                     = Optional.absent();
	public static Optional<? extends Block> crystal                     = Optional.absent();

	// Planks and logs
	public static Optional<? extends Block> planks                      = Optional.absent();
	public static Optional<? extends Block> logs1                       = Optional.absent();
	public static Optional<? extends Block> logs2                       = Optional.absent();
	public static Optional<? extends Block> logs3                       = Optional.absent();
	public static Optional<? extends Block> logs4                       = Optional.absent();

	// Stairs
	public static Optional<? extends Block> acaciaStairs                = Optional.absent();
	public static Optional<? extends Block> cherryStairs                = Optional.absent();
	public static Optional<? extends Block> darkStairs                  = Optional.absent();
	public static Optional<? extends Block> firStairs                   = Optional.absent();
	public static Optional<? extends Block> holyStairs                  = Optional.absent();
	public static Optional<? extends Block> magicStairs                 = Optional.absent();
	public static Optional<? extends Block> mangroveStairs              = Optional.absent();
	public static Optional<? extends Block> palmStairs                  = Optional.absent();
	public static Optional<? extends Block> redwoodStairs               = Optional.absent();
	public static Optional<? extends Block> willowStairs                = Optional.absent();
	public static Optional<? extends Block> redCobbleStairs             = Optional.absent();
	public static Optional<? extends Block> redBricksStairs             = Optional.absent();
	public static Optional<? extends Block> mudBricksStairs             = Optional.absent();
	public static Optional<? extends Block> holyCobbleStairs            = Optional.absent();
	public static Optional<? extends Block> holyBricksStairs            = Optional.absent();
	public static Optional<? extends Block> pineStairs                  = Optional.absent();
	public static Optional<? extends Block> hellBarkStairs              = Optional.absent();
	public static Optional<? extends Block> jacarandaStairs             = Optional.absent();

	// Slabs
	public static Optional<? extends BlockHalfSlab> woodenSingleSlab1   = Optional.absent();
	public static Optional<? extends BlockHalfSlab> woodenDoubleSlab1   = Optional.absent();
	public static Optional<? extends BlockHalfSlab> woodenSingleSlab2   = Optional.absent();
	public static Optional<? extends BlockHalfSlab> woodenDoubleSlab2   = Optional.absent();
	public static Optional<? extends BlockHalfSlab> stoneSingleSlab     = Optional.absent();
	public static Optional<? extends BlockHalfSlab> stoneDoubleSlab     = Optional.absent();

	// Plants
	public static Optional<? extends Block> flowers                     = Optional.absent();
	public static Optional<? extends Block> mushrooms                   = Optional.absent();
	public static Optional<? extends Block> coral                       = Optional.absent();
	public static Optional<? extends Block> leaves1                     = Optional.absent();
	public static Optional<? extends Block> leaves2                     = Optional.absent();
	public static Optional<? extends Block> leavesColorized             = Optional.absent();
	public static Optional<? extends Block> leavesFruit                 = Optional.absent();
	public static Optional<? extends Block> foliage                     = Optional.absent();
	public static Optional<? extends Block> plants                      = Optional.absent();
	public static Optional<? extends Block> flatPlants                  = Optional.absent();
	public static Optional<? extends Block> saplings                    = Optional.absent();
	public static Optional<? extends Block> colorizedSaplings           = Optional.absent();
	public static Optional<? extends Block> willow                      = Optional.absent();
	public static Optional<? extends Block> ivy                         = Optional.absent();
	public static Optional<? extends Block> treeMoss                    = Optional.absent();
	public static Optional<? extends Block> moss                        = Optional.absent();
	public static Optional<? extends Block> petals                      = Optional.absent();
	public static Optional<? extends Block> bamboo                      = Optional.absent();
	public static Optional<? extends Block> cloud                       = Optional.absent();

	//Nether
	public static Optional<? extends Block> bones                       = Optional.absent();

	public static Optional<? extends Block> amethystOre                 = Optional.absent();

	public static Optional<? extends Block> mudBrick                    = Optional.absent();

	public static Optional<? extends Block> promisedPortal              = Optional.absent();
	public static Optional<? extends Block> glass                       = Optional.absent();
	public static Optional<? extends Block> altar                       = Optional.absent();
	public static Optional<? extends Block> puddle                      = Optional.absent();
	public static Optional<? extends Block> grave                       = Optional.absent();

	/**
	 * Populated by Biomes O Plenty with default Biomes O Plenty leaves. Add additional leaves here (E.g. "Blocks.shearBlockIds.put(acaciaLeaves.blockID, 15.0F);")
	 */
	public static Map shearBlockIds = new HashMap();
}
