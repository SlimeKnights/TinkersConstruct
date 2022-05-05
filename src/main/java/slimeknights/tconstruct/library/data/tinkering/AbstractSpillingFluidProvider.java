package slimeknights.tconstruct.library.data.tinkering;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.spilling.effects.ConditionalSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect.DamageType;
import slimeknights.tconstruct.library.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Data provider for spilling fluids */
public abstract class AbstractSpillingFluidProvider extends GenericDataProvider {
  private final String modId;
  private final Map<ResourceLocation,Builder> entries = new HashMap<>();

  public AbstractSpillingFluidProvider(DataGenerator generator, String modId) {
    super(generator, PackType.SERVER_DATA, SpillingFluidManager.FOLDER, SpillingFluidManager.GSON);
    this.modId = modId;
  }

  /** Adds the fluids to the map */
  protected abstract void addFluids();

  @Override
  public void run(HashCache cache) throws IOException {
    addFluids();
    entries.forEach((id, data) -> saveThing(cache, id, data.build()));
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
    return addFluid(Objects.requireNonNull(fluid.getFluid().getRegistryName()).getPath(), FluidIngredient.of(fluid));
  }

  /** Creates a builder for a fluid and amount */
  protected Builder addFluid(Fluid fluid, int amount) {
    return addFluid(Objects.requireNonNull(fluid.getRegistryName()).getPath(), FluidIngredient.of(fluid, amount));
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
  protected Builder addFluid(FluidObject<?> fluid, boolean forgeTag, int amount) {
    return addFluid(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), amount);
  }

  /** Adds a builder for burning with a nugget amount */
  protected Builder burningFluid(TagKey<Fluid> tag, float damage, int time) {
    return burningFluid(tag.location().getPath(), tag, FluidValues.NUGGET, damage, time);
  }

  /** Adds a builder for burning */
  protected Builder burningFluid(String name, TagKey<Fluid> tag, int amount, float damage, int time) {
    return addFluid(name, tag, amount)
      .addEffect(LivingEntityPredicate.FIRE_IMMUNE.inverted(), new DamageSpillingEffect(DamageType.FIRE, damage))
      .addEffect(new SetFireSpillingEffect(time));
  }

  /** Builder class */
  @RequiredArgsConstructor
  protected static class Builder {
    @Setter @Accessors(fluent = true)
    private ICondition condition = null;
    private final FluidIngredient ingredient;
    private final ImmutableList.Builder<ISpillingEffect> effects = ImmutableList.builder();

    /** Adds an effect to the given fluid */
    public Builder addEffect(ISpillingEffect effect) {
      effects.add(effect);
      return this;
    }

    /** Adds a effect to the given fluid that only matches if the entity matches the predicate */
    public Builder addEffect(IJsonPredicate<LivingEntity> predicate, ISpillingEffect effect) {
      return addEffect(new ConditionalSpillingEffect(predicate, effect));
    }

    /** Builds the instance */
    private SpillingFluidJson build() {
      List<ISpillingEffect> effects = this.effects.build();
      if (effects.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 effect");
      }
      return new SpillingFluidJson(condition, ingredient, effects);
    }
  }

  /** Class of built effect instance */
  @SuppressWarnings({"ClassCanBeRecord", "unused"}) // breaks GSON
  @RequiredArgsConstructor
  private static class SpillingFluidJson {
    @Nullable
    private final ICondition condition;
    private final FluidIngredient fluid;
    private final List<ISpillingEffect> effects;
  }
}
