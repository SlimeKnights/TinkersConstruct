package slimeknights.tconstruct.common.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.Locale;
import java.util.function.Supplier;

/** @deprecated use {@link slimeknights.mantle.registration.deferred.PotionDeferredRegister} */
@Deprecated(forRemoval = true)
public class PotionDeferredRegister extends DeferredRegisterWrapper<Potion> {
  public PotionDeferredRegister(String modID) {
    super(Registries.POTION, modID);
  }

  /** Registers a standalone potion */
  public RegistryObject<Potion> register(String name, Supplier<Potion> potion) {
    return register.register(name, potion);
  }

  /** Registers a group of potions with the same effect */
  public Builder registerTypes(String name, Supplier<? extends MobEffect> effect, int duration, int amplifier) {
    return new Builder(name, effect, duration, amplifier);
  }

  /** Registers a group of potions with the same effect */
  public Builder registerTypes(RegistryObject<? extends MobEffect> effect, int duration, int amplifier) {
    return new Builder(effect.getId().getPath(), effect, duration, amplifier);
  }

  /** Registers a group of potions with the same effect starting at level 1 and a duration of 3 minutes */
  public Builder registerTypes(RegistryObject<? extends MobEffect> effect) {
    return registerTypes(effect, 3 * 60 * 20, 0);
  }

  /** Enum of potion variants for the builder */
  public enum PotionType {
    NORMAL,
    LONG,
    STRONG
  }

  /** Builder to create potion variants */
  public class Builder {
    private final EnumObject.Builder<PotionType,Potion> builder;
    private final String name;
    private final Supplier<? extends MobEffect> effect;
    private final int duration;
    private final int amplifier;

    private Builder(String name, Supplier<? extends MobEffect> effect, int duration, int amplifier) {
      this.builder = new EnumObject.Builder<>(PotionType.class);
      this.name = name;
      this.effect = effect;
      this.duration = duration;
      this.amplifier = amplifier;
      with(PotionType.NORMAL, duration, amplifier);
    }

    /** Adds the given potion type */
    private Builder with(PotionType type, int duration, int amplifier) {
      String prefix = type == PotionType.NORMAL ? "" : type.toString().toLowerCase(Locale.ROOT);
      builder.put(type, register(prefix + '_' + name, () -> new Potion(modID + "." + name, new MobEffectInstance(effect.get(), duration, amplifier))));
      return this;
    }

    /** Adds a strong potion with the given properties */
    public Builder withStrong(int duration, int amplifier) {
      return with(PotionType.STRONG, duration, amplifier);
    }

    /** Adds a strong potion with +1 to the level and half the duration */
    public Builder withStrong() {
      return withStrong(duration / 2, amplifier + 1);
    }

    /** Adds a long potion with the given properties */
    public Builder withLong(int duration, int amplifier) {
      return with(PotionType.LONG, duration, amplifier);
    }

    /** Adds a long potion with 8/3 the duration and same level */
    public Builder withLong() {
      return withLong(duration * 8 / 3, amplifier);
    }

    /** Builds the final object */
    public EnumObject<PotionType,Potion> build() {
      return builder.build();
    }
  }
}
