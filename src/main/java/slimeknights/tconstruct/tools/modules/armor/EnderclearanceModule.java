package slimeknights.tconstruct.tools.modules.armor;

import lombok.Getter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.events.teleport.EnderclearanceTeleportEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TeleportHelper;

import javax.annotation.Nullable;
import java.util.List;

/** Module making the target teleport */
public enum EnderclearanceModule implements ModifierModule, ProjectileHitModifierHook, MeleeHitModifierHook, OnAttackedModifierHook {
  INSTANCE;

  @Getter
  private final List<ModuleHook<?>> defaultHooks = HookProvider.<EnderclearanceModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.ON_ATTACKED);
  @Getter
  private final SingletonLoader<EnderclearanceModule> loader = new SingletonLoader<>(this);


  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      TeleportHelper.randomNearbyTeleport(entity, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    if (isDirectDamage && source.getEntity() instanceof LivingEntity attacker) {
      // 15% chance of working per level, doubled bonus on shields
      int level = modifier.getLevel();
      if (slotType.getType() == Type.HAND) {
        level *= 2;
      }
      if (TConstruct.RANDOM.nextFloat() < (level * 0.25f)) {
        TeleportHelper.randomNearbyTeleport(attacker, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null) {
      TeleportHelper.randomNearbyTeleport(target, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
    }
    return false;
  }
}
