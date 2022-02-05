package slimeknights.tconstruct.world.worldgen.islands.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/** Common logic for slime islands, that is islands made of slimy dirt and grass */
@RequiredArgsConstructor
public abstract class AbstractSlimeIslandVariant implements IIslandVariant {
  @Getter
  private final int index;
  protected final SlimeType dirtType;
  protected final SlimeType foliageType;

  @Override
  public BlockState getLakeBottom() {
    return TinkerWorld.slimeGrass.get(dirtType).get(foliageType).defaultBlockState();
  }

  /** Gets the type of congealed slime to place */
  protected abstract SlimeType getCongealedSlimeType(Random random);

  @Override
  public BlockState getCongealedSlime(Random random) {
    return TinkerWorld.congealedSlime.get(getCongealedSlimeType(random)).defaultBlockState();
  }

  @Nullable
  @Override
  public BlockState getPlant(Random random) {
    EnumObject<SlimeType,? extends Block> enumObject = random.nextInt(8) == 0 ? TinkerWorld.slimeFern : TinkerWorld.slimeTallGrass;
    return enumObject.get(foliageType).defaultBlockState();
  }
}
