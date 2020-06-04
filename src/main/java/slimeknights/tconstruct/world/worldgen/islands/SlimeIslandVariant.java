package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Locale;

public enum SlimeIslandVariant implements IStringSerializable {
  BLUE(0, WorldBlocks.blue_slime_grass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), TinkerFluids.blue_slime.getBlock(),
    createArray(WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.BLUE).getDefaultState(), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState()),
    WorldBlocks.blue_slime_vine.get().getDefaultState(), createArray(WorldBlocks.slime_fern.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), WorldBlocks.slime_tall_grass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState())),
  GREEN(1, WorldBlocks.green_slime_grass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), TinkerFluids.blue_slime.getBlock(),
    createArray(WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.BLUE).getDefaultState(), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState()),
    WorldBlocks.blue_slime_vine.get().getDefaultState(), createArray(WorldBlocks.slime_fern.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), WorldBlocks.slime_tall_grass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState())),
  PURPLE(2, WorldBlocks.purple_slime_grass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), TinkerFluids.purple_slime.getBlock(),
    createArray(WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.PURPLE).getDefaultState()),
    WorldBlocks.purple_slime_vine.get().getDefaultState(), createArray(WorldBlocks.slime_fern.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), WorldBlocks.slime_tall_grass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState())),
  MAGMA(3, WorldBlocks.magma_slime_grass.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(), Blocks.LAVA,
    createArray(WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.MAGMA).getDefaultState(), WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.BLOOD).getDefaultState()),
    null, createArray(WorldBlocks.slime_fern.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(), WorldBlocks.slime_tall_grass.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState()));

  private final int index;
  private final BlockState lakeBottom;
  private final BlockState lakeFluid;
  private final BlockState[] congealedSlime;
  private final BlockState vine;
  private final BlockState[] tallGrass;

  SlimeIslandVariant(int index, BlockState lakeBottom, Block lakeFluid, BlockState[] congealedSlime, BlockState vine, BlockState[] tallGrass) {
    this.index = index;
    this.lakeBottom = lakeBottom;
    if (lakeFluid != null) {
      this.lakeFluid = lakeFluid.getDefaultState();
    } else {
      this.lakeFluid = Blocks.WATER.getDefaultState();
    }
    this.congealedSlime = congealedSlime;
    this.vine = vine;
    this.tallGrass = tallGrass;
  }

  private static BlockState[] createArray(BlockState... states) {
    return states;
  }

  public BlockState getLakeBottom() {
    return this.lakeBottom;
  }

  public BlockState getLakeFluid() {
    return this.lakeFluid;
  }

  public BlockState[] getCongealedSlime() {
    return this.congealedSlime;
  }

  public BlockState getVine() {
    return this.vine;
  }

  public BlockState[] getTallGrass() {
    return this.tallGrass;
  }

  @Override
  public String getName() {
    return this.toString().toLowerCase(Locale.US);
  }

  public int getIndex() {
    return this.index;
  }

  public static SlimeIslandVariant getVariantFromIndex(int index) {
    switch (index) {
      case 0:
        return BLUE;
      case 1:
        return GREEN;
      case 2:
        return PURPLE;
      case 3:
        return MAGMA;
      default:
        throw new IllegalStateException("Unexpected variant: " + index);
    }
  }
}
