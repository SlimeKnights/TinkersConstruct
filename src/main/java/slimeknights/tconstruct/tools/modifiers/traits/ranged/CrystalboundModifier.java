package slimeknights.tconstruct.tools.modifiers.traits.ranged;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

public class CrystalboundModifier extends Modifier implements ProjectileLaunchModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.PROJECTILE_LAUNCH);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.VELOCITY.add(builder, level * 0.1f);
  }

  @SuppressWarnings("SuspiciousNameCombination") // mojang uses the angle between X and Z, but parchment named atan2 as the angle between Y and X, makes IDEA mad as it things parameters should swap
  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    Vec3 direction = projectile.getDeltaMovement();
    double oldAngle = Mth.atan2(direction.x, direction.z);
    int possibleDirections = (int)Math.pow(2, 6 - modifier.getLevel());
    double radianIncrements = 2 * Math.PI / possibleDirections;
    double newAngle = Math.round(oldAngle / radianIncrements) * radianIncrements;
    projectile.setDeltaMovement(direction.yRot((float)(newAngle - oldAngle)));
    projectile.setYRot((float)(newAngle * 180f / Math.PI));
  }
}
