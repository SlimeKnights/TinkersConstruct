package slimeknights.tconstruct.tools.item.small;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

public class HandAxeTool extends ToolItem {
  public HandAxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(IModifierToolStack tool, ToolAttackContext context, float damage) {
    boolean hit = super.dealDamage(tool, context, damage);
    if (hit && context.isFullyCharged()) {
      ToolAttackUtil.spawnAttackParticle(TinkerTools.axeAttackParticle.get(), context.getAttacker(), 0.8d);
    }
    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return !ToolDamageUtil.isBroken(stack);
  }
}
