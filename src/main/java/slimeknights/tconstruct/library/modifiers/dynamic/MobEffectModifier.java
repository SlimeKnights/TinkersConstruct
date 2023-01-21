package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;
import java.util.Objects;

/** Modifier that applies an effect on hit. Supports melee, armor, and ranged */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MobEffectModifier extends IncrementalModifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook {
  private final MobEffect effect;
  private final float levelBase;
  private final float levelMultiplier;
  private final int timeBase;
  private final int timeMultiplierFlat;
  private final int timeMultiplierRandom;

  /** Applies the effect for the given level */
  private void applyEffect(@Nullable LivingEntity target, float scaledLevel) {
    if (target == null) {
      return;
    }
    int level = Math.round(levelBase + scaledLevel * levelMultiplier) - 1;
    if (level < 0) {
      return;
    }
    float duration = timeBase + scaledLevel * timeMultiplierFlat;
    int randomBonus = (int)(timeMultiplierRandom * scaledLevel);
    if (randomBonus > 0) {
      duration += RANDOM.nextInt(randomBonus);
    }
    if (duration > 0) {
      target.addEffect(new MobEffectInstance(effect, (int)duration, level));
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker instanceof LivingEntity living) {
      // 15% chance of working per level
      float scaledLevel = getEffectiveLevel(tool, level);
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        applyEffect(living, scaledLevel);
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    applyEffect(context.getLivingTarget(), getEffectiveLevel(tool, level));
    return 0;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    persistentData.putFloat(getId(), modifier.getEffectiveLevel(tool));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    applyEffect(target, persistentData.getFloat(getId()));
    return false;
  }

  @Override
  protected void registerHooks(ModifierHookMap.Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Loader for this modifier */
  public static final IGenericLoader<MobEffectModifier> LOADER = new IGenericLoader<>() {
    @Override
    public MobEffectModifier deserialize(JsonObject json) {
      MobEffect effect = JsonHelper.getAsEntry(ForgeRegistries.MOB_EFFECTS, json, "effect");
      float levelBase = 1;
      float levelMultiplier = 0;
      if (json.has("level")) {
        JsonObject level = GsonHelper.getAsJsonObject(json, "level");
        levelBase = GsonHelper.getAsFloat(level, "base", 0);
        levelMultiplier = GsonHelper.getAsFloat(level, "multiplier", 0);
      }
      JsonObject time = GsonHelper.getAsJsonObject(json, "time");
      int timeBase = GsonHelper.getAsInt(time, "base", 0);
      int timeMultiplierFlat = GsonHelper.getAsInt(time, "multiplier_flat", 0);
      int timeMultiplierRandom = GsonHelper.getAsInt(time, "multiplier_random", 0);
      return new MobEffectModifier(effect, levelBase, levelMultiplier, timeBase, timeMultiplierFlat, timeMultiplierRandom);
    }

    @Override
    public void serialize(MobEffectModifier object, JsonObject json) {
      json.addProperty("effect", Objects.requireNonNull(object.effect.getRegistryName()).toString());
      JsonObject level = new JsonObject();
      level.addProperty("base", object.levelBase);
      level.addProperty("multiplier", object.levelMultiplier);
      json.add("level", level);
      JsonObject time = new JsonObject();
      time.addProperty("base", object.timeBase);
      time.addProperty("multiplier_flat", object.timeMultiplierFlat);
      time.addProperty("multiplier_random", object.timeMultiplierRandom);
      json.add("time", time);
    }

    @Override
    public MobEffectModifier fromNetwork(FriendlyByteBuf buffer) {
      MobEffect effect = buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
      float levelBase = buffer.readFloat();
      float levelMultiplier = buffer.readFloat();
      int timeBase = buffer.readInt();
      int timeMultiplierFlat = buffer.readInt();
      int timeMultiplierRandom = buffer.readInt();
      return new MobEffectModifier(effect, levelBase, levelMultiplier, timeBase, timeMultiplierFlat, timeMultiplierRandom);
    }

    @Override
    public void toNetwork(MobEffectModifier object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, object.effect);
      buffer.writeFloat(object.levelBase);
      buffer.writeFloat(object.levelMultiplier);
      buffer.writeInt(object.timeBase);
      buffer.writeInt(object.timeMultiplierFlat);
      buffer.writeInt(object.timeMultiplierRandom);
    }
  };

  /** Builder for this modifier in datagen */
  @RequiredArgsConstructor(staticName = "effect")
  @Accessors(fluent = true)
  public static class Builder {
    private final MobEffect effect;
    private float levelBase = 1;
    private float levelMultiplier = 0;
    @Setter
    private int timeBase;
    @Setter
    private int timeMultiplierFlat;
    @Setter
    private int timeMultiplierRandom;

    /**
     * Sets the effect level
     * @param base        Base level granted for the modifier
     * @param multiplier  Bonus granted per level
     * @return  Builder
     */
    public Builder level(float base, float multiplier) {
      levelBase = base;
      levelMultiplier = multiplier;
      return this;
    }

    /** Builds the finished modifier */
    public MobEffectModifier build() {
      return new MobEffectModifier(effect, levelBase, levelMultiplier, timeBase, timeMultiplierFlat, timeMultiplierRandom);
    }
  }
}
