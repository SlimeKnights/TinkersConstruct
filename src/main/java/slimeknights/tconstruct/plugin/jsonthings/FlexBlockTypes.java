package slimeknights.tconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.serializers.FlexBlockType;
import dev.gigaherz.jsonthings.things.serializers.IBlockSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.plugin.jsonthings.block.FlexBurningLiquidBlock;
import slimeknights.tconstruct.plugin.jsonthings.block.FlexMobEffectLiquidBlock;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** Collection of custom block types added by Tinkers */
public class FlexBlockTypes {
  /** Creates the supplier for a fluid in a fluid block */
  private static Supplier<FlowingFluid> fluidSupplier(ResourceLocation name) {
    return Lazy.of(() -> {
      if (Loadables.FLUID.fromKey(name, "fluid") instanceof FlowingFluid flowing) {
        return flowing;
      } else {
        throw new RuntimeException("LiquidBlock requires a flowing fluid");
      }
    });
  }

  /** Initializes the block types */
  public static void init() {
    register("burning_liquid", data -> {
      ResourceLocation fluidField = Loadables.RESOURCE_LOCATION.getOrDefault(data, "fluid", null);
      int burnTime = GsonHelper.getAsInt(data, "burn_time");
      float damage = GsonHelper.getAsFloat(data, "damage");
      return (props, builder) -> {
        final List<Property<?>> _properties = builder.getProperties();
        return new FlexBurningLiquidBlock(props, builder.getPropertyDefaultValues(), fluidSupplier(Objects.requireNonNullElse(fluidField, builder.getRegistryName())), burnTime, damage) {
          @Override
          protected void createBlockStateDefinition(Builder<Block,BlockState> stateBuilder) {
            super.createBlockStateDefinition(stateBuilder);
            _properties.forEach(stateBuilder::add);
          }
        };
      };
    });
    register("mob_effect_liquid", data -> {
      ResourceLocation fluidField = Loadables.RESOURCE_LOCATION.getOrDefault(data, "fluid", null);
      ResourceLocation effectName = Loadables.RESOURCE_LOCATION.getIfPresent(data, "effect");
      int effectLevel = GsonHelper.getAsInt(data, "burn_time");
      return (props, builder) -> {
        final List<Property<?>> _properties = builder.getProperties();
        Lazy<MobEffect> effect = Lazy.of(() -> Loadables.MOB_EFFECT.fromKey(effectName, "effect"));
        return new FlexMobEffectLiquidBlock(props, builder.getPropertyDefaultValues(), fluidSupplier(Objects.requireNonNullElse(fluidField, builder.getRegistryName())), () -> new MobEffectInstance(effect.get(), 5*20, effectLevel - 1)) {
          @Override
          protected void createBlockStateDefinition(Builder<Block,BlockState> stateBuilder) {
            super.createBlockStateDefinition(stateBuilder);
            _properties.forEach(stateBuilder::add);
          }
        };
      };
    });
  }

  /** Local helper to register our stuff */
  private static <T extends Block & IFlexBlock> void register(String name, IBlockSerializer<T> factory) {
    FlexBlockType.register(TConstruct.resourceString(name), factory, "translucent", true, false, true);
  }
}
