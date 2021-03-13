package slimeknights.tconstruct.tools.harvest;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

public class SledgeHammerTool extends HarvestTool {
  private static final AOEToolHarvestLogic HARVEST_LOGIC = new MaterialHarvestLogic(PickaxeTool.EXTRA_MATERIALS, 3, 3, 1);
  public SledgeHammerTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity player, Entity entity, float damage, boolean isCriticalHit, boolean fullyCharged) {
    // bonus damage vs. undead!
    if (entity instanceof LivingEntity && ((LivingEntity) entity).getCreatureAttribute() == CreatureAttribute.UNDEAD) {
      damage += 3 + TConstruct.random.nextInt(4);
    }
    boolean hit = super.dealDamage(tool, player, entity, damage, isCriticalHit, fullyCharged);
    if (hit && fullyCharged) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.hammerAttackParticle.get(), player, 0.8d);
    }

    return hit;
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
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
