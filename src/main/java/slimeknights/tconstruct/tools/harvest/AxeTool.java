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
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;

public class AxeTool extends ToolCore {

  public static final ImmutableSet<Material> effective_materials =
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
    return effective_materials.contains(state.getMaterial()) || AxeItem.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity playerentity = context.getPlayer();
    ItemStack itemStack = playerentity.getHeldItem(context.getHand());

    if (ToolData.from(itemStack).getStats().broken) {
      return ActionResultType.FAIL;
    }

    ActionResultType resultType = Items.DIAMOND_AXE.onItemUse(context);
    if (resultType == ActionResultType.SUCCESS) {
      //TODO event
    }

    return resultType;
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
      //TinkerTools.proxy.spawnAttackParticle(Particles.HATCHET_ATTACK, player, 0.8d);
    }

    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return true;
  }

  @Override
  public StatsNBT buildToolStats(List<IMaterial> materials) {
    StatsNBT statsNBT = super.buildToolStats(materials);

    return new StatsNBT(statsNBT.durability, statsNBT.harvestLevel, (int) (statsNBT.attack + 0.5f),
      statsNBT.miningSpeed, statsNBT.attackSpeedMultiplier, statsNBT.freeModifiers, statsNBT.broken);
  }
}
