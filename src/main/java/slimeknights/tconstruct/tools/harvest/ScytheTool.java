package slimeknights.tconstruct.tools.harvest;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ScytheTool extends KamaTool {
  /** Tool harvest logic to damage when breaking instant break blocks */
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(3, 3, 3);

  public ScytheTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  protected boolean isShears(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.get()) > 0;
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity living, Entity targetEntity, float damage, boolean isCritical, boolean fullyCharged) {
    boolean hit = super.dealDamage(tool, living, targetEntity, damage, isCritical, fullyCharged);
    // only need fully charged for scythe sweep, easier than sword sweep
    if (fullyCharged) {
      // basically sword sweep logic, just deals full damage to all entities
      double range = 3 + tool.getModifierLevel(TinkerModifiers.expanded.get());
      for (LivingEntity sideEntity : living.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(range, 0.25D, range))) {
        if (sideEntity != living && sideEntity != targetEntity && !living.isOnSameTeam(sideEntity)
            && (!(sideEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) sideEntity).hasMarker()) && living.getDistanceSq(sideEntity) < 8.0D + range) {
          sideEntity.applyKnockback(0.4F, MathHelper.sin(living.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(living.rotationYaw * ((float) Math.PI / 180F)));
          hit |= ToolAttackUtil.extraEntityAttack(this, tool, living, sideEntity);
        }
      }

      living.world.playSound(null, living.getPosX(), living.getPosY(), living.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, living.getSoundCategory(), 1.0F, 1.0F);
      if (living instanceof PlayerEntity) {
        ((PlayerEntity) living).spawnSweepParticles();
      }
    }

    return hit;
  }
}
