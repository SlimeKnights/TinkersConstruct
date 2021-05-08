package slimeknights.tconstruct.tools.item.broad;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.small.HarvestTool;
import slimeknights.tconstruct.tools.item.small.PickaxeTool;

public class SledgeHammerTool extends HarvestTool {
  private static final MaterialHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(PickaxeTool.EXTRA_MATERIALS, 1, 1, 0);
  public SledgeHammerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity player, Entity entity, float damage, boolean isCriticalHit, boolean fullyCharged) {
    boolean hit = super.dealDamage(tool, player, entity, damage, isCriticalHit, fullyCharged);
    if (hit && fullyCharged) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.hammerAttackParticle.get(), player, 0.8d);
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
