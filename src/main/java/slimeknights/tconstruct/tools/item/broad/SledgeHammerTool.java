package slimeknights.tconstruct.tools.item.broad;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.small.HarvestTool;
import slimeknights.tconstruct.tools.item.small.PickaxeTool;

public class SledgeHammerTool extends HarvestTool {
  private static final MaterialHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(PickaxeTool.EXTRA_MATERIALS, 1, 1, 0);
  public SledgeHammerTool(Properties properties, ToolDefinition toolDefinition) {
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

  @Override
  public ToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  /*@Override
  public int[] getRepairParts() {
    return new int[] { 1, 2, 3 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.6f;
  }*/
}
