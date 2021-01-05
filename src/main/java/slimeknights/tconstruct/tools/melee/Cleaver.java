package slimeknights.tconstruct.tools.melee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class Cleaver extends SwordCore {

  public static final float DURABILITY_MODIFIER = 2f;

  public Cleaver(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(ItemStack stack, LivingEntity player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    if (hit && this.readyForSpecialAttack(player)) {
      //ToolAttackUtil.spawnAttachParticle(TinkerTools.cleaverAttackParticle.get(), player, 0.85d);
    }

    return hit;
  }

  // no offhand for you
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStackIn = playerIn.getHeldItem(handIn);

    return ActionResult.resultConsume(itemStackIn);
  }

  @Override
  public StatsNBT buildToolStats(List<IMaterial> materials) {
    StatsNBT statsNBT = super.buildToolStats(materials);

    return new StatsNBT((int) (statsNBT.durability * DURABILITY_MODIFIER), statsNBT.harvestLevel, (statsNBT.attack * 1.3f) + 3f, statsNBT.miningSpeed, statsNBT.attackSpeedMultiplier, statsNBT.freeModifiers, statsNBT.broken);
  }
}
