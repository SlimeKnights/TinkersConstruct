package slimeknights.tconstruct.tools.item.broad;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.small.KamaTool;

public class ScytheTool extends KamaTool {
  /** Tool harvest logic to damage when breaking instant break blocks */
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(3, true) {
    @Override
    public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
      // include depth in boost
      int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
      int sides = (expanded + 1) / 2;
      return RectangleAOEHarvestLogic.calculate(this, tool, stack, world, player, origin, sideHit, 1 + sides, 1 + sides, 3 + (expanded / 2) * 2, matchType);
    }
  };

  public ScytheTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public boolean dealDamage(IModifierToolStack tool, ToolAttackContext context, float damage) {
    boolean hit = super.dealDamage(tool, context, damage);
    // only need fully charged for scythe sweep, easier than sword sweep
    if (context.isFullyCharged()) {
      // basically sword sweep logic, just deals full damage to all entities
      double range = 3 + tool.getModifierLevel(TinkerModifiers.expanded.get());
      LivingEntity attacker = context.getAttacker();
      Entity target = context.getTarget();
      for (LivingEntity aoeTarget : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
        if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
            && (!(aoeTarget instanceof ArmorStandEntity) || !((ArmorStandEntity) aoeTarget).isMarker()) && attacker.distanceToSqr(aoeTarget) < 8.0D + range) {
          aoeTarget.knockback(0.4F, MathHelper.sin(attacker.yRot * ((float) Math.PI / 180F)), -MathHelper.cos(attacker.yRot * ((float) Math.PI / 180F)));
          hit |= ToolAttackUtil.extraEntityAttack(this, tool, attacker, context.getHand(), aoeTarget);
        }
      }

      attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
      if (attacker instanceof PlayerEntity) {
        ((PlayerEntity) attacker).sweepAttack();
      }
    }

    return hit;
  }
}
