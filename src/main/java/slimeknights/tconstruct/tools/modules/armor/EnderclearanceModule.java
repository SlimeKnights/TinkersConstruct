package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.events.teleport.ModifierTeleportEvent;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.TeleportHelper;

import javax.annotation.Nullable;
import java.util.List;

/** Module making the target teleport */
public record EnderclearanceModule(LevelingValue chance, LevelingInt diameter, LevelingInt teleportChances) implements ModifierModule, ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, OnAttackedModifierHook {
  private static final LevelingInt DEFAULT = LevelingInt.flat(16);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EnderclearanceModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.ON_ATTACKED);
  /** @deprecated use {@link #EnderclearanceModule(LevelingValue)} */
  @Deprecated(forRemoval = true)
  public static final EnderclearanceModule INSTANCE = new EnderclearanceModule(LevelingValue.eachLevel(0.25f));
  public static final RecordLoadable<EnderclearanceModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("chance", EnderclearanceModule::chance),
    LevelingInt.LOADABLE.defaultField("diameter", DEFAULT, true, EnderclearanceModule::diameter),
    LevelingInt.LOADABLE.defaultField("teleport_chances", DEFAULT, true, EnderclearanceModule::teleportChances),
    EnderclearanceModule::new);

  /** @deprecated use {@link #EnderclearanceModule(LevelingValue, LevelingInt, LevelingInt)} */
  @Deprecated(forRemoval = true)
  public EnderclearanceModule(LevelingValue chance) {
    this(chance, DEFAULT, DEFAULT);
  }

  @Override
  public RecordLoadable<EnderclearanceModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Teleports the target */
  private void teleport(ModifierEntry modifier, @Nullable LivingEntity target) {
    if (target != null) {
      float level = modifier.getEffectiveLevel();
      TeleportHelper.randomNearbyTeleport(target, (e, x, y, z) -> new ModifierTeleportEvent(e, x, y, z, modifier), diameter.compute(level), teleportChances.compute(level));
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    teleport(modifier, context.getLivingTarget());
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    if (isDirectDamage && source.getEntity() instanceof LivingEntity attacker) {
      float level = CounterModule.getLevel(tool, modifier, slotType, context.getEntity());
      if (attacker.getRandom().nextFloat() < chance.compute(level)) {
        teleport(modifier, attacker);
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    teleport(modifier, target);
    return false;
  }
}
