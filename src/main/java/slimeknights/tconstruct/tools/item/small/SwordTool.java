package slimeknights.tconstruct.tools.item.small;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SwordTool extends ToolCore {
  public static final ToolType TOOL_TYPE = ToolType.get("sword");
  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.WEB, Material.TALL_PLANTS, Material.CORAL, Material.GOURD, Material.LEAVES);
  public static final ToolHarvestLogic HARVEST_LOGIC = new HarvestLogic();

  public SwordTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  /** Gets the bonus area of the sweep attack */
  protected double getSweepRange(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded.get()) + 1;
  }

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
    return !player.isCreative();
  }

  // sword sweep attack
  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity living, Entity targetEntity, float damage, boolean isCritical, boolean fullyCharged) {
    // deal damage first
    boolean hit = super.dealDamage(tool, living, targetEntity, damage, isCritical, fullyCharged);

    // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
    // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
    if (hit && fullyCharged && !living.isSprinting() && !isCritical && living.isOnGround() && (living.distanceWalkedModified - living.prevDistanceWalkedModified) < living.getAIMoveSpeed()) {
      // loop through all nearby entities
      double range = getSweepRange(tool);
      // if the modifier is missing, sweeping damage will be 0, so easiest to let it fully control this
      float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, damage);
      for (LivingEntity livingEntity : living.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(range, 0.25D, range))) {
        if (livingEntity != living && livingEntity != targetEntity && !living.isOnSameTeam(livingEntity)
            && (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity).hasMarker()) && living.getDistanceSq(livingEntity) < 10.0D + range) {
          livingEntity.applyKnockback(0.4F, MathHelper.sin(living.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(living.rotationYaw * ((float) Math.PI / 180F)));
          super.dealDamage(tool, living, livingEntity, sweepDamage, false, true);
        }
      }

      living.world.playSound(null, living.getPosX(), living.getPosY(), living.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, living.getSoundCategory(), 1.0F, 1.0F);
      if (living instanceof PlayerEntity) {
        ((PlayerEntity) living).spawnSweepParticles();
      }
    }

    return hit;
  }

  /** Harvest logic for swords */
  public static class HarvestLogic extends ToolHarvestLogic {
    @Override
    public boolean isEffectiveAgainst(IModifierToolStack tool, ItemStack stack, BlockState state) {
      // no sword tool type by default, so augment with vanilla list
      return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
      // webs are slow
      float speed = super.getDestroySpeed(stack, state);
      if (state.getMaterial() == Material.WEB) {
        speed *= 7.5f;
      }
      return speed;
    }
  }
}
