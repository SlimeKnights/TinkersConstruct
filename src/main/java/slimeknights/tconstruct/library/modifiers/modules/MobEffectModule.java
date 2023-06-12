package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
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
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageTakenModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.utils.ScalingValue;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.TConstruct.RANDOM;

/**
 * Module that applies a mob effect on melee attack, projectile hit, and counterattack
 */
public record MobEffectModule(
  IJsonPredicate<LivingEntity> predicate,
  MobEffect effect,
  ScalingValue level,
  ScalingValue time
) implements DamageTakenModifierHook, MeleeHitModifierHook, ProjectileLaunchModifierHook, ProjectileHitModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.DAMAGE_TAKEN, TinkerHooks.MELEE_HIT, TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);

  /** Creates a builder instance */
  public static MobEffectModule.Builder builder(MobEffect effect) {
    return new Builder(effect);
  }

  /** Applies the effect for the given level */
  private void applyEffect(@Nullable LivingEntity target, float scaledLevel) {
    if (target == null || !predicate.matches(target)) {
      return;
    }
    int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
    if (level < 0) {
      return;
    }
    float duration = this.time.computeValue(scaledLevel);
    if (duration > 0) {
      target.addEffect(new MobEffectInstance(effect, (int)duration, level));
    }
  }

  @Override
  public void onDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker instanceof LivingEntity living) {
      // 15% chance of working per level
      float scaledLevel = modifier.getEffectiveLevel(tool);
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        applyEffect(living, scaledLevel);
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    applyEffect(context.getLivingTarget(), modifier.getEffectiveLevel(tool));
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    persistentData.putFloat(modifier.getId(), modifier.getEffectiveLevel(tool));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    applyEffect(target, persistentData.getFloat(modifier.getId()));
    return false;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  /** Builder for this modifier in datagen */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  public static class Builder {
    private final MobEffect effect;
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;
    private ScalingValue level = ScalingValue.flat(1);
    private ScalingValue time = ScalingValue.flat(0);

    /** Builds the finished modifier */
    public MobEffectModule build() {
      return new MobEffectModule(entity, effect, level, time);
    }
  }

  public static final IGenericLoader<MobEffectModule> LOADER = new IGenericLoader<>() {
    @Override
    public MobEffectModule deserialize(JsonObject json) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity");
      MobEffect effect = JsonHelper.getAsEntry(ForgeRegistries.MOB_EFFECTS, json, "effect");
      ScalingValue level = ScalingValue.get(json, "level");
      ScalingValue time = ScalingValue.get(json, "time");
      return new MobEffectModule(predicate, effect, level, time);
    }

    @Override
    public void serialize(MobEffectModule object, JsonObject json) {
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.predicate));
      json.addProperty("effect", Objects.requireNonNull(object.effect.getRegistryName()).toString());
      json.add("level", object.level.serialize());
      json.add("time", object.time.serialize());
    }

    @Override
    public MobEffectModule fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.fromNetwork(buffer);
      MobEffect effect = buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
      ScalingValue level = ScalingValue.fromNetwork(buffer);
      ScalingValue time = ScalingValue.fromNetwork(buffer);
      return new MobEffectModule(predicate, effect, level, time);
    }

    @Override
    public void toNetwork(MobEffectModule object, FriendlyByteBuf buffer) {
      LivingEntityPredicate.LOADER.toNetwork(object.predicate, buffer);
      buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, object.effect);
      object.level.toNetwork(buffer);
      object.time.toNetwork(buffer);
    }
  };
}
