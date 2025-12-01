package slimeknights.tconstruct.library.modifiers.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RandomTeleportFluidEffect;
import slimeknights.tconstruct.library.utils.Util;

import java.util.function.BinaryOperator;

/** Represents an effect applied by a fluid to an entity or block */
public interface FluidEffect<C extends FluidEffectContext> extends IHaveLoader, UnloadableFluidEffect<C> {
  /** Effect that does nothing, useful for conditionals. */
  FluidEffect<FluidEffectContext> EMPTY = simple(((fluid, scale, context, action) -> 0));

  /** Registry for fluid effect loaders */
  GenericLoaderRegistry<FluidEffect<? super FluidEffectContext.Block>> BLOCK_EFFECTS = new GenericLoaderRegistry<>("Fluid block effect", EMPTY, false);
  /** Registry for fluid effect loaders */
  GenericLoaderRegistry<FluidEffect<? super FluidEffectContext.Entity>> ENTITY_EFFECTS = new GenericLoaderRegistry<>("Fluid entity effect", EMPTY, false);

  /** Registers an effect to both blocks and entities */
  static void registerGeneral(ResourceLocation id, RecordLoadable<? extends FluidEffect<FluidEffectContext>> loader) {
    BLOCK_EFFECTS.register(id, loader);
    ENTITY_EFFECTS.register(id, loader);
  }

  @Override
  RecordLoadable<? extends FluidEffect<C>> getLoader();

  /** Gets a description of this effect for display in the book */
  default Component getDescription(RegistryAccess registryAccess) {
    return Component.translatable(getTranslationKey(getLoader()));
  }


  /* Singletons */


  /** Effect which extinguishes fire from the entity */
  FluidEffect<FluidEffectContext.Entity> EXTINGUISH_FIRE = simple((fluid, level, context, action) -> {
    Entity target = context.getTarget();
    if (target.isOnFire() && level.isFull()) {
      if (action.execute()) {
        context.getTarget().clearFire();
      }
      return 1;
    }
    return 0;
  });

  /** @deprecated use {@link RandomTeleportFluidEffect} */
  @Deprecated(forRemoval = true)
  FluidEffect<FluidEffectContext.Entity> TELEPORT = new RandomTeleportFluidEffect(LevelingInt.flat(16), LevelingInt.flat(16));

  /** Weathers the targeted copper block */
  FluidEffect<FluidEffectContext.Block> WEATHER = simple((fluid, level, context, action) -> {
    BlockState state = context.getBlockState();
    if (level.isFull() && state.getBlock() instanceof WeatheringCopper copper) {
      if (action.execute() && context.getLevel() instanceof ServerLevel world) {
        copper.applyChangeOverTime(state, world, context.getBlockPos(), world.getRandom());
      }
      return 1;
    }
    return 0;
  });

  /** Removes the block at the location */
  FluidEffect<FluidEffectContext.Block> REMOVE_BLOCK = simple((fluid, level, context, action) -> {
    if (level.isFull()) {
      Level world = context.getLevel();
      BlockPos pos = context.getBlockPos();
      BlockState original = world.getBlockState(pos);
      BlockState replacement = world.getFluidState(pos).createLegacyBlock();
      if (original != replacement) {
        if (action.execute() && !world.isClientSide) {
          if (world.setBlockAndUpdate(pos, replacement)) {
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(original));
          }
        }
        return 1;
      }
    }
    return 0;
  });


  /** Creates a simple fluid effect with no JSON parameters */
  static <C extends FluidEffectContext> FluidEffect<C> simple(UnloadableFluidEffect<C> effect) {
    return SingletonLoader.<FluidEffect<C>>singleton(loader -> new FluidEffect<>() {
      private Component description;

      @Override
      public RecordLoadable<? extends FluidEffect<C>> getLoader() {
        return loader;
      }

      @Override
      public Component getDescription(RegistryAccess registryAccess) {
        // cache the description in the effect to save lookup time
        if (description == null) {
          description = FluidEffect.super.getDescription(registryAccess);
        }
        return description;
      }

      @Override
      public float apply(FluidStack fluid, EffectLevel level, C context, FluidAction action) {
        return effect.apply(fluid, level, context, action);
      }
    });
  }


  /* Description helpers */
  /** Separator for the binary operator to reduce effect lists */
  String KEY_SEPARATOR = TConstruct.makeTranslationKey("fluid_effect", "separator");
  /** Stream reducer to merge a list into a single value */
  BinaryOperator<Component> MERGE_COMPONENT_LIST = (left, right) -> Component.translatable(KEY_SEPARATOR, left, right);

  /** Gets the registry name for the given loader */
  @SuppressWarnings("unchecked")
  static ResourceLocation getLoaderName(RecordLoadable<? extends FluidEffect<?>> loader) {
    ResourceLocation loaderId = ENTITY_EFFECTS.getName((RecordLoadable<? extends FluidEffect<? super FluidEffectContext.Entity>>)loader);
    if (loaderId != null) {
      return loaderId;
    }
    loaderId = BLOCK_EFFECTS.getName((RecordLoadable<? extends FluidEffect<? super FluidEffectContext.Block>>)loader);
    if (loaderId != null) {
      return loaderId;
    }
    Mantle.logger.error("Failed to get default description for unregistered fluid effect loader {}", loader);
    return new ResourceLocation("missingno");
  }

  /** Gets the string key for the given loader */
  static String getTranslationKey(RecordLoadable<? extends FluidEffect<?>> loader) {
    return Util.makeTranslationKey("fluid_effect", getLoaderName(loader));
  }

  /** Translates the key for the loader with the given arguments */
  static Component makeTranslation(RecordLoadable<? extends FluidEffect<?>> loader, Object... arguments) {
    return Component.translatable(getTranslationKey(loader), arguments);
  }
}
