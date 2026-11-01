package slimeknights.tconstruct.plugin.jsonthings.block;

import dev.gigaherz.jsonthings.things.blocks.FlexLiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

/** Json Things version of {@link slimeknights.mantle.block.fluid.MobEffectLiquidBlock} */
public class FlexMobEffectLiquidBlock extends FlexLiquidBlock {
  private final Supplier<MobEffectInstance> effect;
  public FlexMobEffectLiquidBlock(Properties properties, Map<Property<?>,Comparable<?>> propertyDefaultValues, Supplier<FlowingFluid> fluidSupplier, Supplier<MobEffectInstance> effect) {
    super(properties, propertyDefaultValues, fluidSupplier);
    this.effect = effect;
  }

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (entity.getFluidTypeHeight(getFluid().getFluidType()) > 0 && entity instanceof LivingEntity living) {
      MobEffectInstance effect = this.effect.get();
      effect.setCurativeItems(new ArrayList<>());
      living.addEffect(effect);
    }
  }
}
