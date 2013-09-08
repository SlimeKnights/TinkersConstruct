package biomesoplenty.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.google.common.base.Optional;

public class BlockReferences {

	public static enum EnumBlocks
	{
		acaciaLog (Blocks.logs1, 0),
		cherryLog (Blocks.logs1, 1),
		darkLog (Blocks.logs1, 2),
		firLog (Blocks.logs1, 3),
		holyLog (Blocks.logs2, 0),
		magicLog (Blocks.logs2, 1),
		mangroveLog (Blocks.logs2, 2),
		palmLog (Blocks.logs2, 3),
		redwoodLog (Blocks.logs3, 0),
		willowLog (Blocks.logs3, 1),
		deadLog (Blocks.logs3, 2),
		bigFlowerStem (Blocks.logs3, 3),
		pineLog (Blocks.logs4, 0),
		hellBarkLog (Blocks.logs4, 1),
		jacarandaLog (Blocks.logs4, 2),

		acaciaPlank (Blocks.planks, 0),
		cherryPlank (Blocks.planks, 1),
		darkPlank (Blocks.planks, 2),
		firPlank (Blocks.planks, 3),
		holyPlank (Blocks.planks, 4),
		magicPlank (Blocks.planks, 5),
		mangrovePlank (Blocks.planks, 6),
		palmPlank (Blocks.planks, 7),
		redwoodPlank (Blocks.planks, 8),
		willowPlank (Blocks.planks, 9),
		bambooThatching (Blocks.planks, 10),
		pinePlank (Blocks.planks, 11),
		hellBarkPlank (Blocks.planks, 12),
		jacarandaPlank (Blocks.planks, 13),

		acaciaLeaves (Blocks.leavesColorized, 0),
		mangroveLeaves (Blocks.leavesColorized, 1),
		palmLeaves (Blocks.leavesColorized, 2),
		redwoodLeaves (Blocks.leavesColorized, 3),
		willowLeaves (Blocks.leavesColorized, 4),
		pineLeaves (Blocks.leavesColorized, 5),

		yellowAutumnLeaves (Blocks.leaves1, 0),
		bambooLeaves (Blocks.leaves1, 1),
		magicLeaves (Blocks.leaves1, 2),
		darkLeaves (Blocks.leaves1, 3),
		deadLeaves (Blocks.leaves1, 4),
		firLeaves (Blocks.leaves1, 5),
		holyLeaves (Blocks.leaves1, 6),
		orangeAutumnLeaves (Blocks.leaves1, 7),
		originLeaves (Blocks.leaves2, 0),
		pinkCherryLeaves (Blocks.leaves2, 1),
		mapleLeaves (Blocks.leaves2, 2),
		whiteCherryLeaves (Blocks.leaves2, 3),
		hellBarkLeaves (Blocks.leaves2, 4),
		jacarandaLeaves (Blocks.leaves2, 5),

		appleLeaves (Blocks.leavesFruit, 3),
		appleLeavesFruitless (Blocks.leavesFruit, 0),

		bamboo (Blocks.bamboo, 0),

		poisonIvyItem (Blocks.foliage, 7),
		sproutItem (Blocks.foliage, 5),
		bushItem (Blocks.foliage, 4),
		highGrassItem (Blocks.foliage, 3),
		mediumGrassItem (Blocks.foliage, 2),
		shortGrassItem (Blocks.foliage, 1),
		algae (Blocks.foliage, 0),

		holySapling (Blocks.saplings, 7),
		magicSapling (Blocks.saplings, 3),
		darkSapling (Blocks.saplings, 4),
		deadSapling (Blocks.saplings, 5),
		acaciaSapling (Blocks.colorizedSaplings, 0),
		firSapling (Blocks.saplings, 6),
		mangroveSapling (Blocks.colorizedSaplings, 1),
		palmSapling (Blocks.colorizedSaplings, 2),
		redwoodSapling (Blocks.colorizedSaplings, 3),
		willowSapling (Blocks.colorizedSaplings, 4),
		pineSapling (Blocks.colorizedSaplings, 5),
		mapleSapling (Blocks.saplings, 11),
		orangeAutumnSapling (Blocks.saplings, 8),
		pinkCherrySapling (Blocks.saplings, 10),
		whiteCherrySapling (Blocks.saplings, 12),
		hellBarkSapling (Blocks.saplings, 13),
		jacarandaSapling (Blocks.saplings, 14),
		appleSapling (Blocks.saplings, 0),
		originSapling (Blocks.saplings, 9),
		yellowAutumnSapling (Blocks.saplings, 1),
		bambooSapling (Blocks.saplings, 2),

		mud (Blocks.mud, 0),
		driedDirt (Blocks.driedDirt, 0),
		redRock (Blocks.redRock, 0),
		ash (Blocks.ash, 0),
		ashStone (Blocks.ashStone, 0),
		hardIce (Blocks.hardIce, 0),
		originGrass (Blocks.originGrass, 0),
		longGrass (Blocks.longGrass, 0),
		hardSand (Blocks.hardSand, 0),
		hardDirt (Blocks.hardDirt, 0),
		holyGrass (Blocks.holyGrass, 0),
		holyDirt (Blocks.holyDirt, 0),
		holyStone (Blocks.holyStone, 0),
		holyStoneCobble (Blocks.holyStone, 1),
		crystal (Blocks.crystal, 0),
		cragRock (Blocks.cragRock, 0),
		quicksand (Blocks.mud, 1),
		cloud (Blocks.cloud, 0),
		smolderingGrass (Blocks.holyGrass, 1),
		redRockCobble (Blocks.redRock, 1),
		giantFlowerRed (Blocks.petals, 0),
		giantFlowerYellow (Blocks.petals, 1),
		
		amethystOre (Blocks.amethystOre, 0),
		amethystBlock (Blocks.amethystOre, 1),
		rubyOre (Blocks.amethystOre, 2),
		rubyBlock (Blocks.amethystOre, 3),
		peridotOre (Blocks.amethystOre, 4),
		peridotBlock (Blocks.amethystOre, 5),
		topazOre (Blocks.amethystOre, 6),
		topazBlock (Blocks.amethystOre, 7),
		tanzaniteOre (Blocks.amethystOre, 8),
		tanzaniteBlock (Blocks.amethystOre, 9),
		apatiteOre (Blocks.amethystOre, 10),
		apatiteBlock (Blocks.amethystOre, 11),
		sapphireOre (Blocks.amethystOre, 12),
		sapphireBlock (Blocks.amethystOre, 13),

		smallBoneSegment (Blocks.bones, 0),
		mediumBoneSegment (Blocks.bones, 1),
		largeBoneSegment (Blocks.bones, 2),

		kelp (Blocks.coral, 0),

		toadstool (Blocks.mushrooms, 0),
		portobello (Blocks.mushrooms, 1),
		bluemilk (Blocks.mushrooms, 2),
		glowshroom (Blocks.mushrooms, 3),

		deadGrass (Blocks.plants, 0),
		desertGrass (Blocks.plants, 1),
		desertSprouts (Blocks.plants, 2),
		duneGrass (Blocks.plants, 3),
		holyTallGrass (Blocks.plants, 4),
		thorn (Blocks.plants, 5),
		barley (Blocks.plants, 6),
		cattail (Blocks.plants, 7),
		reed (Blocks.plants, 8),
		tinyCactus (Blocks.plants, 12),

		treeMoss (Blocks.treeMoss, 0),
		moss (Blocks.moss, 0),
		willow (Blocks.willow, 0),
		ivy (Blocks.ivy, 0),

		clover (Blocks.flowers, 0),
		swampFlower (Blocks.flowers, 1),
		deathbloom (Blocks.flowers, 2),
		glowFlower (Blocks.flowers, 3),
		hydrangea (Blocks.flowers, 4),
		daisy (Blocks.flowers, 5),
		tulip (Blocks.flowers, 6),
		wildFlower (Blocks.flowers, 7),
		violet (Blocks.flowers, 8),
		anenome (Blocks.flowers, 9),
		lilyflower (Blocks.flowers, 10),
		rainbowflower (Blocks.flowers, 11),
		aloe (Blocks.flowers, 12),
		sunflower (Blocks.flowers, 13),
		dandelion (Blocks.flowers, 15),
		;

		public Optional<? extends Block> block;
		public int meta;

		private EnumBlocks(Optional<? extends Block> block, int meta) {
			this.block = block;
			this.meta = meta;
		}

		public Optional<? extends Block> getBlock() {
			return block;
		}

		public int getMeta() {
			return meta;
		}
	}

	public static ItemStack getBlockItemStack(String string)
	{
		Optional<? extends Block> stackblock = EnumBlocks.valueOf(string).block;
		int stackmeta = EnumBlocks.valueOf(string).meta;

		if (stackmeta != 0)
		{
			return new ItemStack(stackblock.get(), 1, stackmeta);
		}
		
		return new ItemStack(stackblock.get(), 1);
	}

	public static int getBlockID(String string)
	{
		Optional<? extends Block> stackblock = EnumBlocks.valueOf(string).block;

		return stackblock.get().blockID;
	}

	public static int getBlockMeta(String string)
	{
		int stackmeta = EnumBlocks.valueOf(string).meta;

		return stackmeta;
	}
}
