package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Locale;

public enum SlimeIslandVariant implements IStringSerializable {
  BLUE(0, TinkerWorld.blueSlimeGrass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), TinkerFluids.blueSlime.getBlock(),
       createArray(TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.BLUE).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.GREEN).getDefaultState()),
       TinkerWorld.blueSlimeVine.get().getDefaultState(), createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState())),
  GREEN(1, TinkerWorld.greenSlimeGrass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), TinkerFluids.blueSlime.getBlock(),
        createArray(TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.BLUE).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.GREEN).getDefaultState()),
        TinkerWorld.blueSlimeVine.get().getDefaultState(), createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState())),
  PURPLE(2, TinkerWorld.purpleSlimeGrass.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(), TinkerFluids.purpleSlime.getBlock(),
         createArray(TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.PURPLE).getDefaultState()),
         TinkerWorld.purpleSlimeVine.get().getDefaultState(), createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState())),
  MAGMA(3, TinkerWorld.magmaSlimeGrass.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(), Blocks.LAVA,
        createArray(TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.MAGMA).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.BLOOD).getDefaultState()),
        null, createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState()));

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
