package slimeknights.tconstruct.tools.item.broad;

import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

public class VeinHammerTool extends ToolItem {
  public VeinHammerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(IModifierToolStack tool, ToolAttackContext context, float damage) {
    boolean hit = super.dealDamage(tool, context, damage);
    if (hit && context.isFullyCharged()) {
      ToolAttackUtil.spawnAttackParticle(TinkerTools.hammerAttackParticle.get(), context.getAttacker(), 0.8d);
    }
    return hit;
  }
}
