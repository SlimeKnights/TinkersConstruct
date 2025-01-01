package slimeknights.tconstruct.world.worldgen.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import slimeknights.tconstruct.world.TinkerStructures;

/**
 * Recreation of {@link net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator} with variable vine type.
 */
@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class LeaveVineDecorator extends TreeDecorator {
  public static final Codec<LeaveVineDecorator> CODEC = RecordCodecBuilder.create(inst ->
    inst.group(
      BuiltInRegistries.BLOCK.byNameCodec().fieldOf("vines").forGetter(d -> d.vines),
      Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability)
    ).apply(inst, LeaveVineDecorator::new));

  private final Block vines;
  private final float probability;

  @Override
  protected TreeDecoratorType<?> type() {
    return TinkerStructures.leaveVineDecorator.get();
  }

  @Override
  public void place(TreeDecorator.Context context) {
    RandomSource random = context.random();
    context.leaves().forEach((pos) -> {
      for (Direction direction : Plane.HORIZONTAL) {
        if (random.nextFloat() < this.probability) {
          BlockPos offset = pos.relative(direction);
          if (context.isAir(offset)) {
            addHangingVine(offset, VineBlock.getPropertyForFace(direction.getOpposite()), context);
          }
        }
      }
    });
  }

  /** Places vines at the given position */
  private void placeVine(BlockPos pos, BooleanProperty property, TreeDecorator.Context context) {
    context.setBlock(pos, vines.defaultBlockState().setValue(property, Boolean.TRUE));
  }

  /** Adds a hanging vine around the given position */
  private void addHangingVine(BlockPos pos, BooleanProperty property, TreeDecorator.Context context) {
    placeVine(pos, property, context);
    int i = 4;

    for(BlockPos target = pos.below(); context.isAir(target) && i > 0; --i) {
      placeVine(target, property, context);
      target = target.below();
    }
  }
}
