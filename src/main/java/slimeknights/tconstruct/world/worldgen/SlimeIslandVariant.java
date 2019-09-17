package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;

public enum SlimeIslandVariant implements IStringSerializable {
  BLUE(0, TinkerWorld.blue_blue_slime_grass.getDefaultState(), TinkerFluids.blue_slime_fluid_block.get(),
          createArray(TinkerCommons.congealed_blue_slime.getDefaultState(), TinkerCommons.congealed_green_slime.getDefaultState()),
          TinkerWorld.blue_slime_vine.getDefaultState(), createArray(TinkerWorld.purple_slime_fern.getDefaultState(), TinkerWorld.purple_slime_tall_grass.getDefaultState())),
  GREEN(1, TinkerWorld.blue_green_slime_grass.getDefaultState(), TinkerFluids.blue_slime_fluid_block.get(),
          createArray(TinkerCommons.congealed_blue_slime.getDefaultState(), TinkerCommons.congealed_green_slime.getDefaultState()),
          TinkerWorld.blue_slime_vine.getDefaultState(), createArray(TinkerWorld.purple_slime_fern.getDefaultState(), TinkerWorld.purple_slime_tall_grass.getDefaultState())),
  PURPLE(2, TinkerWorld.purple_purple_slime_grass.getDefaultState(), TinkerFluids.purple_slime_fluid_block.get(),
          createArray(TinkerCommons.congealed_purple_slime.getDefaultState()),
          TinkerWorld.purple_slime_vine.getDefaultState(), createArray(TinkerWorld.blue_slime_fern.getDefaultState(), TinkerWorld.blue_slime_tall_grass.getDefaultState())),
  MAGMA(3, TinkerWorld.orange_magma_slime_grass.getDefaultState(), Blocks.LAVA,
          createArray(TinkerCommons.congealed_magma_slime.getDefaultState(), TinkerCommons.congealed_blood_slime.getDefaultState()),
          null, createArray(TinkerWorld.orange_slime_fern.getDefaultState(), TinkerWorld.orange_slime_tall_grass.getDefaultState()));

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
    }
    else {
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
