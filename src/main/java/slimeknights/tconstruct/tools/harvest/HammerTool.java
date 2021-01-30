package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;

public class HammerTool extends PickaxeTool implements IAoeTool {

  public HammerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(ItemStack stack, LivingEntity player, Entity entity, float damage) {
    // bonus damage vs. undead!
    if (entity instanceof LivingEntity && ((LivingEntity) entity).getCreatureAttribute() == CreatureAttribute.UNDEAD) {
      damage += 3 + TConstruct.random.nextInt(4);
    }

    boolean hit = super.dealDamage(stack, player, entity, damage);

    if (hit && this.readyForSpecialAttack(player)) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.hammerAttackParticle.get(), player, 0.8d);
    }

    return hit;
  }

  /*@Override
  public int[] getRepairParts() {
    return new int[] { 1, 2, 3 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.6f;
  }*/

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return AoeToolInteractionUtil.calculateAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }
}
