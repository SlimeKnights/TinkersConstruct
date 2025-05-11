package slimeknights.tconstruct.library.modifiers.fluid;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Common logic for effects between {@link slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect} and {@link slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect}
 * @param effect  Effect to apply
 * @param level   Potion level starting at 1. Fixed with respect to fluid amount.
 * @param time    Potion time in ticks, scales with fluid amount. Set to {@link MobEffectInstance#INFINITE_DURATION} for infinite.
 * @param curativeItems  Items allowed to cure the effect
 */
public record FluidMobEffect(MobEffect effect, int time, int level, @Nullable List<Item> curativeItems) {
  private static final String TRANSLATION_ROOT = TConstruct.makeTranslationKey("fluid_effect", "mob_effect.");
  public static final RecordLoadable<FluidMobEffect> LOADABLE = RecordLoadable.create(
    Loadables.MOB_EFFECT.requiredField("effect", e -> e.effect),
    IntLoadable.FROM_ONE.defaultField("time", -1, false, e -> e.time),
    IntLoadable.FROM_ONE.defaultField("level", 1, true, e -> e.level),
    Loadables.ITEM.list(0).nullableField("curative_items", e -> e.curativeItems),
    FluidMobEffect::new);

  public FluidMobEffect(MobEffect effect, int time, int level) {
    this(effect, time, level, null);
  }

  /** Gets the amplifier for a mob effect */
  public int amplifier() {
    return level - 1;
  }

  /** Checks if the duration is infinite */
  public boolean isInfinite() {
    return time == MobEffectInstance.INFINITE_DURATION;
  }

  /** Creates the final effect */
  public MobEffectInstance effectWithTime(int time) {
    MobEffectInstance instance = new MobEffectInstance(effect, time, this.level - 1);
    if (curativeItems != null) {
      instance.setCurativeItems(curativeItems.stream().map(ItemStack::new).collect(Collectors.toList()));
    }
    return instance;
  }

  /** Creates the final effect */
  public MobEffectInstance makeEffect(float scale) {
    return effectWithTime((int)(this.time * scale));
  }

  /** Applies the effect to the target entity */
  public float apply(LivingEntity target, EffectLevel scale, TimeAction timeAction, FluidAction action) {
    // infinite requires a full level
    if (isInfinite() && !scale.isFull()) {
      return 0;
    }
    float used;
    int time;
    // infinite costs 1 and sets the full time
    if (isInfinite()) {
      time = this.time;
      used = 1;
    } else {
      // add and set both have distinct behavior under an existing effect, same otherwise
      MobEffectInstance existingInstance = target.getEffect(effect);
      int amplifier = amplifier();
      if (existingInstance != null && existingInstance.getAmplifier() >= amplifier) {
        // if the existing level is larger, just skip, would be a cheese to increase said level
        // lower levels we treat as not having the effect, must be exact match to extend
        if (existingInstance.getAmplifier() > amplifier) {
          return 0;
        }
        if (timeAction == TimeAction.ADD) {
          int extraTime = (int) (this.time * scale.value());
          if (extraTime <= 0) {
            return 0;
          }
          // add simply sums existing time with the desired time. If the level is higher than ours, it produces a hidden effect
          time = existingInstance.getDuration() + extraTime;
          used = scale.value();
        } else {
          // set can produce time up to the maximum allowed by scale,
          float existing = existingInstance.getDuration() / (float) this.time;
          float effective = scale.effective(existing);
          // no change? means we skip applying entirely
          if (effective < existing) {
            return 0;
          }
          used = effective - existing;
          time = (int) (this.time * effective);
        }
      } else {
        // if no existing, time and set behave the same way, use maximum amount to compute time
        time = (int) (this.time * scale.value());
        used = scale.value();
      }
    }
    // if we got time, add the effect
    if (time != 0) {
      MobEffectInstance newInstance = effectWithTime(time);
      // if simulating, just ask if it's applicable. Won't handle event canceling, but thats acceptable.
      if (action.simulate()) {
        return target.canBeAffected(newInstance) ? used : 0;
      }
      return target.addEffect(newInstance) ? used : 0;
    }
    return 0;
  }

  /** Gets the display name for this effect */
  public Component getDisplayName(TimeAction action) {
    // level display based on PotionUtils#addPotionTooltip
    Component component = effect.getDisplayName();
    // add level if above 1
    if (level > 1) {
      component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + (level - 1)));
    }
    if (isInfinite()) {
      return component;
    }
    return Component.translatable(TRANSLATION_ROOT + action.name().toLowerCase(Locale.ROOT), time / 20, component);
  }

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final ImmutableList.Builder<FluidMobEffect> effects = ImmutableList.builder();

    private Builder() {}

    /** Adds an effect to the builder with the passed cures. If none are passed, effect will have no cure*/
    public Builder effectCure(MobEffect effect, int time, int level, Item... curativeItems) {
      effects.add(new FluidMobEffect(effect, time, level, List.of(curativeItems)));
      return this;
    }

    /** Adds an effect to the builder with default cures */
    public Builder effect(MobEffect effect, int time, int level) {
      effects.add(new FluidMobEffect(effect, time, level, null));
      return this;
    }

    /** Adds an effect to the builder */
    public Builder effect(MobEffect effect, int time) {
      return effect(effect, time, 1);
    }

    private List<FluidMobEffect> getEffects() {
      List<FluidMobEffect> effects = this.effects.build();
      if (effects.isEmpty()) {
        throw new IllegalStateException("Must have at least one effect");
      }
      return effects;
    }

    /** Builds the cloud effect for blocks */
    public MobEffectCloudFluidEffect buildCloud() {
      return new MobEffectCloudFluidEffect(getEffects());
    }

    public List<MobEffectFluidEffect> buildEntity(TimeAction action) {
      return getEffects().stream().map(effect -> new MobEffectFluidEffect(effect, action)).toList();
    }
  }
}
