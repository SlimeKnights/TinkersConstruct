package slimeknights.tconstruct.library.data.tinkering;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.OffsetBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ConditionalFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static slimeknights.mantle.Mantle.commonResource;

/** Data provider for spilling fluids */
@SuppressWarnings("deprecation")  // fluid registry is ours to use, not yours forge
public abstract class AbstractFluidEffectProvider extends GenericDataProvider {
  private final String modId;
  private final Map<ResourceLocation,Builder> entries = new HashMap<>();

  public AbstractFluidEffectProvider(PackOutput packOutput, String modId) {
    super(packOutput, Target.DATA_PACK, FluidEffectManager.FOLDER);
    this.modId = modId;
  }

  /** Adds the fluids to the map */
  protected abstract void addFluids();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addFluids();
    return allOf(entries.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().build(entry.getKey()))));
  }

  /* Helpers */

  /** Creates a new fluid builder for the given location */
  protected Builder addFluid(ResourceLocation id, FluidIngredient fluid) {
    Builder newBuilder = new Builder(fluid);
    Builder original = entries.put(id, newBuilder);
    if (original != null) {
      throw new IllegalArgumentException("Duplicate spilling fluid " + id);
    }
    return newBuilder;
  }

  /** Creates a new fluid builder for the given mod ID */
  protected Builder addFluid(String name, FluidIngredient fluid) {
    return addFluid(new ResourceLocation(modId, name), fluid);
  }

  /** Creates a builder for a fluid stack */
  protected Builder addFluid(FluidStack fluid) {
    return addFluid(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getPath(), FluidIngredient.of(fluid));
  }

  /** Creates a builder for a fluid and amount */
  protected Builder addFluid(Fluid fluid, int amount) {
    return addFluid(BuiltInRegistries.FLUID.getKey(fluid).getPath(), FluidIngredient.of(fluid, amount));
  }

  /** Creates a builder for a tag and amount */
  protected Builder addFluid(String name, TagKey<Fluid> fluid, int amount) {
    return addFluid(name, FluidIngredient.of(fluid, amount));
  }

  /** Creates a builder for a tag and amount */
  protected Builder addFluid(TagKey<Fluid> fluid, int amount) {
    return addFluid(fluid.location().getPath(), fluid, amount);
  }

  /** Creates a builder for a fluid object */
  protected Builder addFluid(FluidObject<?> fluid, int amount) {
    return addFluid(fluid.getId().getPath(), fluid.ingredient(amount));
  }


  /* Common units */

  /** Builder for a gem based fluid */
  protected Builder addGem(FluidObject<?> fluid) {
    return addFluid(fluid, FluidValues.GEM_SHARD);
  }

  /** Builder for a metal based fluid */
  protected Builder addMetal(FluidObject<?> fluid) {
    return addFluid(fluid, FluidValues.NUGGET);
  }

  /** Adds a conditional fluid effect */
  protected Builder compatFluid(TagKey<Fluid> fluid, int amount) {
    return addFluid(fluid, amount).addCondition(new TagFilledCondition<>(fluid));
  }

  /** Adds a conditional fluid effect */
  protected Builder compatFluid(String name, int amount) {
    return compatFluid(FluidTags.create(commonResource(name)), amount);
  }

  /** Adds a conditional fluid effect */
  protected Builder compatFluid(String modId, TagKey<Fluid> fluid, int amount) {
    return addFluid(fluid, amount).addCondition(new ModLoadedCondition(modId));
  }

  /** Adds a conditional fluid effect */
  protected Builder compatFluid(String modId, String name, int amount) {
    return compatFluid(modId, FluidTags.create(commonResource(name)), amount);
  }

  /** Builder for a metal based fluid */
  protected Builder compatMetal(FluidObject<?> fluid, String... extraIngots) {
    Builder builder = addMetal(fluid);
    // automatically add ourself
    String ourself = fluid.getId().getPath().substring("molten_".length());
    if (extraIngots.length == 0) {
      builder.metalCondition(ourself);
    } else {
      // also add extra ingots if requested
      builder.metalCondition(Stream.concat(Stream.of(ourself), Stream.of(extraIngots)).toArray(String[]::new));
    }
    return builder;
  }

  /** Builder for a clay based fluid */
  protected Builder addClay(FluidObject<?> fluid) {
    return addFluid(fluid, FluidValues.BRICK);
  }

  /** Builder for a glass based fluid */
  protected Builder addGlass(FluidObject<?> fluid) {
    return addFluid(fluid, FluidValues.GLASS_PANE);
  }

  /** Builder for a glass based fluid */
  protected Builder addSlime(FluidObject<?> fluid) {
    return addFluid(fluid, FluidValues.SLIME_DROP);
  }


  /* Deprecated */

  /** Use {@link #addMetal(FluidObject)} with {@link Builder#fireDamage(float)}, {@link FireFluidEffect} and {@link Builder#placeFire()} */
  @Deprecated(forRemoval = true)
  protected Builder burningFluid(TagKey<Fluid> tag, float damage, int time) {
    return burningFluid(tag.location().getPath(), tag, FluidValues.NUGGET, damage, time);
  }

  /** Use {@link #addFluid(TagKey, int)} with {@link Builder#fireDamage(float)}, {@link FireFluidEffect} and {@link Builder#placeFire()} */
  @Deprecated(forRemoval = true)
  protected Builder burningFluid(String name, TagKey<Fluid> tag, int amount, float damage, int time) {
    Builder builder = addFluid(name, tag, amount).fireDamage(damage);
    if (time > 0) {
      builder.addEntityEffect(new FireFluidEffect(TimeAction.SET, time)).placeFire();
    }
    return builder;
  }

  /** Builder class */
  @RequiredArgsConstructor
  @CanIgnoreReturnValue
  protected static class Builder {
    private final List<ICondition> conditions = new ArrayList<>();
    private final FluidIngredient ingredient;
    private final List<FluidEffect<? super FluidEffectContext.Block>> blockEffects = new ArrayList<>();
    private final List<FluidEffect<? super FluidEffectContext.Entity>> entityEffects = new ArrayList<>();
    private boolean hidden = false;

    /** Hides this effect from the book */
    public Builder hidden() {
      this.hidden = true;
      return this;
    }

    /** Adds a condition to the builder */
    public Builder addCondition(ICondition condition) {
      this.conditions.add(condition);
      return this;
    }

    /** Adds conditions for a metal fluid based on any of the given list of ingots being present */
    public Builder metalCondition(String... names) {
      ICondition[] conditions = new ICondition[names.length + 1];
      conditions[0] = ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS;
      for (int i = 0; i < names.length; i++) {
        conditions[i+1] = new TagFilledCondition<>(ItemTags.create(commonResource("ingots/" + names[i])));
      }
      return addCondition(new OrCondition(conditions));
    }

    /** Adds an effect to the given fluid */
    public Builder addBlockEffect(FluidEffect<? super FluidEffectContext.Block> effect) {
      blockEffects.add(effect);
      return this;
    }

    /** Adds an offset effect to the given fluid */
    public Builder offsetBlockEffect(FluidEffect<? super FluidEffectContext.Block> effect) {
      return addBlockEffect(new OffsetBlockFluidEffect(effect));
    }

    /** Adds an effect to the given fluid */
    public Builder addEntityEffect(FluidEffect<? super FluidEffectContext.Entity> effect) {
      entityEffects.add(effect);
      return this;
    }

    public Builder addEntityEffects(List<? extends FluidEffect<? super FluidEffectContext.Entity>> effects) {
      for (FluidEffect<? super FluidEffectContext.Entity> effect : effects) {
        addEntityEffect(effect);
      }
      return this;
    }

    /** Adds an effect to the given fluid */
    public Builder addEffect(FluidMobEffect.Builder builder, TimeAction action) {
      addBlockEffect(builder.buildCloud());
      addEntityEffects(builder.buildEntity(action));
      return this;
    }

    /** @deprecated use {@link #addEffect(FluidMobEffect.Builder, TimeAction)}, parameter order was swapped for parity with {@link #addEntityEffects(List)} with {@link FluidMobEffect.Builder#buildEntity(TimeAction)} */
    @Deprecated(forRemoval = true)
    public Builder addEffect(TimeAction action, FluidMobEffect.Builder builder) {
      return addEffect(builder, action);
    }

    /** Adds an effect to the given fluid */
    public Builder addEffect(FluidEffect<FluidEffectContext> effect) {
      addBlockEffect(effect);
      addEntityEffect(effect);
      return this;
    }

    /** Adds an effect to the given fluid that only matches if the block matches the predicate */
    public Builder addBlockEffect(IJsonPredicate<BlockState> predicate, FluidEffect<? super FluidEffectContext.Block> effect) {
      return addBlockEffect(new ConditionalFluidEffect.Block(predicate, effect));
    }

    /** Adds an offset effect to the given fluid that only matches if the block matches the offset predicate */
    public Builder offsetBlockEffect(IJsonPredicate<BlockState> predicate, FluidEffect<? super FluidEffectContext.Block> effect) {
      return offsetBlockEffect(new ConditionalFluidEffect.Block(predicate, effect));
    }

    /** Adds an effect to the given fluid that only matches if the entity matches the predicate */
    public Builder addEntityEffect(IJsonPredicate<LivingEntity> predicate, FluidEffect<? super FluidEffectContext.Entity> effect) {
      return addEntityEffect(new ConditionalFluidEffect.Entity(predicate, effect));
    }


    /* Damage helpers */

    /** Adds a fire block to the block effects */
    public Builder placeFire() {
      return addBlockEffect(new PlaceBlockFluidEffect(Blocks.FIRE, SoundEvents.FIRECHARGE_USE));
    }

    /** Adds a damage effect to the builder */
    public Builder addDamage(float amount, DamageTypePair type) {
      return addEntityEffect(new DamageFluidEffect(amount, type));
    }

    /** Adds a damage effect to the builder */
    public Builder addDamage(IJsonPredicate<LivingEntity> predicate, float amount, DamageTypePair type) {
      return addEntityEffect(predicate, new DamageFluidEffect(amount, type));
    }

    /** Adds fire damage to the builder */
    public Builder impactDamage(float amount) {
      return addDamage(amount, TinkerDamageTypes.FLUID_IMPACT);
    }

    /** Adds fire damage to the builder */
    public Builder fireDamage(float amount) {
      return addDamage(LivingEntityPredicate.FIRE_IMMUNE.inverted(), amount, TinkerDamageTypes.FLUID_FIRE);
    }

    /** Adds fire damage to the builder */
    public Builder coldDamage(float amount) {
      return addDamage(LivingEntityPredicate.CAN_FREEZE, amount, TinkerDamageTypes.FLUID_COLD);
    }

    /** Adds fire damage to the builder */
    public Builder magicDamage(float amount) {
      return addDamage(amount, TinkerDamageTypes.FLUID_MAGIC);
    }

    /** Adds fire damage to the builder */
    public Builder spikeDamage(float amount) {
      return addDamage(amount, TinkerDamageTypes.FLUID_SPIKE);
    }

    /** Builds the instance */
    @CheckReturnValue
    private JsonObject build(ResourceLocation id) {
      JsonObject json = new JsonObject();
      if (!conditions.isEmpty()) {
        json.add("conditions", CraftingHelper.serialize(conditions.toArray(new ICondition[0])));
      }
      if (blockEffects.isEmpty() && entityEffects.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 effect");
      }
      FluidEffects effects = new FluidEffects(ingredient, blockEffects, entityEffects, hidden);
      try {
        FluidEffects.LOADABLE.serialize(effects, json);
      } catch (Exception e) {
        throw new RuntimeException("Error serializing fluid effect ID " + id + " with value " + effects, e);
      }
      return json;
    }
  }
}
