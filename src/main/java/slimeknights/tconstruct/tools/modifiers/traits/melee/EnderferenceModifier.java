package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerEffects;

import javax.annotation.Nullable;

/** @deprecated use {@link MobEffectModule} and {@link TinkerEffects#ENDERFERENCE_KEY} */
@Deprecated(forRemoval = true)
public class EnderferenceModifier extends Modifier implements ProjectileLaunchModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN);
    hookBuilder.addModule(MobEffectModule.builder(TinkerEffects.enderference).applyBeforeMelee(true).time(RandomLevelingValue.flat(100)).buildWeapon());
    hookBuilder.addModule(MobEffectModule.builder(TinkerEffects.enderference).time(RandomLevelingValue.flat(100)).toolTag(TinkerTags.Items.ARMOR).chance(LevelingValue.eachLevel(0.25f)).buildCounter());
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    persistentData.putBoolean(TinkerEffects.ENDERFERENCE_KEY, true);
  }
}
