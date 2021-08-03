package slimeknights.tconstruct.tools.modifiers.internal;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripWireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.library.events.TinkerToolEvent.ToolShearEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ShearsAbilityModifier extends SingleUseModifier {
  private final int range;
  @Getter
  private final int priority;
  
  public ShearsAbilityModifier(int color, int range, int priority) {
    super(color);
    this.range = range;
    this.priority = priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }
  
  /**
   * Swings the given's player hand
   *
   * @param player the current player
   * @param hand the given hand the tool is in
   */
  protected void swingTool(PlayerEntity player, Hand hand) {
    player.swingArm(hand);
    player.spawnSweepParticles();
  }

  /**
   * Checks whether the tool counts as shears for modifier logic
   *
   * @param tool  Current tool instance
   */
  protected boolean isShears(IModifierToolStack tool) {
    return true;
  }

  @Override
  public ActionResultType onEntityUseFirst(IModifierToolStack tool, int level, PlayerEntity player, Entity target, Hand hand) {
    if (tool.isBroken()) {
      return ActionResultType.PASS;
    }
    ItemStack stack = player.getHeldItem(hand);

    // use looting instead of fortune, as that is our hook with entity access
    // modifier can always use tags or the nullable parameter to distinguish if needed
    int looting = ModifierUtil.getLootingLevel(tool, player, target, null);
    World world = player.getEntityWorld();
    if (isShears(tool) && shearEntity(stack, tool, world, player, target, looting)) {
      boolean broken = ToolDamageUtil.damageAnimated(tool, 1, player, hand);
      this.swingTool(player, hand);
      runShearHook(tool, player, target, true);

      // AOE shearing
      if (!broken) {
        // if expanded, shear all in range
        int expanded = range + tool.getModifierLevel(TinkerModifiers.expanded.get());
        if (expanded > 0) {
          for (LivingEntity aoeTarget : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(expanded, 0.25D, expanded))) {
            if (aoeTarget != player && aoeTarget != target && (!(aoeTarget instanceof ArmorStandEntity) || !((ArmorStandEntity)aoeTarget).hasMarker())) {
              if (shearEntity(stack, tool, world, player, aoeTarget, looting)) {
                broken = ToolDamageUtil.damageAnimated(tool, 1, player, hand);
                runShearHook(tool, player, aoeTarget, false);
                if (broken) {
                  break;
                }
              }
            }
          }
        }
      }

      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  /** Runs the hook after shearing an entity */
  private static void runShearHook(IModifierToolStack tool, PlayerEntity player, Entity entity, boolean isTarget) {
    for (ModifierEntry entry : tool.getModifierList()) {
      IShearModifier shearModifier = entry.getModifier().getModule(IShearModifier.class);
      if (shearModifier != null) {
        shearModifier.afterShearEntity(tool, entry.getLevel(), player, entity, isTarget);
      }
    }
  }

  /**
   * Tries to shear an given entity, returns false if it fails and true if it succeeds
   *
   * @param itemStack the current item stack
   * @param world the current world
   * @param player the current player
   * @param entity the entity to try to shear
   * @param fortune the fortune to apply to the sheared entity
   * @return if the sheering of the entity was performed or not
   */
  private static boolean shearEntity(ItemStack itemStack, IModifierToolStack tool, World world, PlayerEntity player, Entity entity, int fortune) {
    // event to override entity shearing
    Result result = new ToolShearEvent(itemStack, tool, world, player, entity, fortune).fire();
    if (result != Result.DEFAULT) {
      return result == Result.ALLOW;
    }
    // fallback to forge shearable
    if (entity instanceof IForgeShearable) {
      IForgeShearable target = (IForgeShearable) entity;
      if (target.isShearable(itemStack, world, entity.getPosition())) {
        if (!world.isRemote) {
          target.onSheared(player, itemStack, world, entity.getPosition(), fortune)
                .forEach(stack -> ModifierUtil.dropItem(entity, stack));
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean removeBlock(IModifierToolStack tool, int level, ToolHarvestContext context) {
    BlockState state = context.getState();
    if (isShears(tool) && state.getBlock() instanceof TripWireBlock) {
      context.getWorld().setBlockState(context.getPos(), state.with(BlockStateProperties.DISARMED, Boolean.TRUE), 4);
    }
    return null;
  }

  /** Interface that allows another modifier to hook into the shears modifier */
  public interface IShearModifier {
    /**
     * Called after a block is successfully harvested
     * @param tool     Tool used in harvesting
     * @param level    Modifier level
     * @param player   Player shearing
     * @param entity   Entity sheared
     * @param isTarget If true, the sheared entity was targeted. If false, this is AOE shearing
     */
    void afterShearEntity(IModifierToolStack tool, int level, PlayerEntity player, Entity entity, boolean isTarget);
  }
}
