package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.tools.TinkerTools;

public class AxeTool extends ToolCore implements IAoeTool {

  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
    ImmutableSet.of(Material.WOOD,
      Material.TALL_PLANTS,
      Material.PLANTS,
      Material.GOURD,
      Material.CACTUS,
      Material.BAMBOO);

  public AxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean isEffective(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || AxeItem.EFFECTIVE_ON_BLOCKS.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity player = context.getPlayer();

    if (player == null || player.isSneaking()) {
      return ActionResultType.PASS;
    }

    Hand hand = context.getHand();
    ItemStack stack = player.getHeldItem(hand);
    if (ToolDamageUtil.isBroken(stack)) {
      return ActionResultType.FAIL;
    }

    World world = context.getWorld();
    BlockPos pos = context.getPos();
    BlockState clickedState = world.getBlockState(pos);
    BlockState strippedState = clickedState.getToolModifiedState(world, pos, player, stack, ToolType.AXE);

    if (strippedState == null) {
      return ActionResultType.PASS;
    }

    if (world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    world.setBlockState(pos, strippedState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
    world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
    context.getItem().damageItem(1, player, (onBroken) -> onBroken.sendBreakAnimation(context.getHand()));

    return ActionResultType.SUCCESS;
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (state.getMaterial() == Material.LEAVES) {
      return this.getToolMiningLogic().calcDigSpeed(stack, state);
    }

    return super.getDestroySpeed(stack, state);
  }

  @Override
  public void afterBlockBreak(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity player, int damage, boolean wasEffective) {
    if (state.getBlock().isIn(BlockTags.LEAVES)) {
      damage = 0;
    }

    super.afterBlockBreak(stack, world, state, pos, player, damage, wasEffective);
  }

  @Override
  public boolean dealDamage(ItemStack stack, LivingEntity player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    if (hit && this.readyForSpecialAttack(player)) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.axeAttackParticle.get(), player, 0.8d);
    }

    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return true;
  }
}
