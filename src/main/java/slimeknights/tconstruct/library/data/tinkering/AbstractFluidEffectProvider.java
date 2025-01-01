package slimeknights.tconstruct.library.data.tinkering;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.modifiers.fluid.ConditionalFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    return allOf(entries.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().build())));
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
  protected Builder addFluid(FluidObject<?> fluid, boolean commonTag, int amount) {
    return addFluid(fluid.getId().getPath(), fluid.ingredient(amount, commonTag));
  }

  /** Adds a builder for burning with a nugget amount */
  protected Builder burningFluid(TagKey<Fluid> tag, float damage, int time) {
    return burningFluid(tag.location().getPath(), tag, FluidValues.NUGGET, damage, time);
  }

  /** Adds a builder for burning */
  protected Builder burningFluid(String name, TagKey<Fluid> tag, int amount, float damage, int time) {
    Builder builder = addFluid(name, tag, amount).addEntityEffect(LivingEntityPredicate.FIRE_IMMUNE.inverted(), new DamageFluidEffect(damage, TinkerDamageTypes.FLUID_FIRE));
    if (time > 0) {
      builder.addEntityEffect(new FireFluidEffect(TimeAction.SET, time)).addBlockEffect(new PlaceBlockFluidEffect(Blocks.FIRE));
    }
    return builder;
  }

  /** Builder class */
  @RequiredArgsConstructor
  protected static class Builder {
    private final List<ICondition> conditions = new ArrayList<>();
    private final FluidIngredient ingredient;
    private final List<FluidEffect<? super FluidEffectContext.Block>> blockEffects = new ArrayList<>();
    private final List<FluidEffect<? super FluidEffectContext.Entity>> entityEffects = new ArrayList<>();

    /** Adds a condition to the builder */
    public Builder addCondition(ICondition condition) {
      this.conditions.add(condition);
      return this;
    }

    /** Adds an effect to the given fluid */
    @CanIgnoreReturnValue
    public Builder addBlockEffect(FluidEffect<? super FluidEffectContext.Block> effect) {
      blockEffects.add(effect);
      return this;
    }

    /** Adds an effect to the given fluid */
    @CanIgnoreReturnValue
    public Builder addEntityEffect(FluidEffect<? super FluidEffectContext.Entity> effect) {
      entityEffects.add(effect);
      return this;
    }

    /** Adds an effect to the given fluid */
    public Builder addEffect(TimeAction action, FluidMobEffect.Builder builder) {
      addBlockEffect(builder.buildCloud());
      for (MobEffectFluidEffect effect : builder.buildEntity(action)) {
        addEntityEffect(effect);
      }
      return this;
    }

    /** Adds an effect to the given fluid */
    public Builder addEffect(FluidEffect<FluidEffectContext> effect) {
      addBlockEffect(effect);
      addEntityEffect(effect);
      return this;
    }

    /** Adds an effect to the given fluid that only matches if the entity matches the predicate */
    public Builder addBlockEffect(IJsonPredicate<BlockState> predicate, FluidEffect<? super FluidEffectContext.Block> effect) {
      return addBlockEffect(new ConditionalFluidEffect.Block(predicate, effect));
    }

    /** Adds an effect to the given fluid that only matches if the entity matches the predicate */
    public Builder addEntityEffect(IJsonPredicate<LivingEntity> predicate, FluidEffect<? super FluidEffectContext.Entity> effect) {
      return addEntityEffect(new ConditionalFluidEffect.Entity(predicate, effect));
    }

    /** Builds the instance */
    private JsonObject build() {
      JsonObject json = new JsonObject();
      if (!conditions.isEmpty()) {
        json.add("conditions", CraftingHelper.serialize(conditions.toArray(new ICondition[0])));
      }
      if (blockEffects.isEmpty() && entityEffects.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 effect");
      }
      FluidEffects.LOADABLE.serialize(new FluidEffects(ingredient, blockEffects, entityEffects), json);
      return json;
    }
  }
}
