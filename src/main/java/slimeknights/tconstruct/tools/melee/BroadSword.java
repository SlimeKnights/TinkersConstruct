package slimeknights.tconstruct.tools.melee;

import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.ToolRegistry;

import java.util.List;

public class BroadSword extends SwordCore {

  public static final float DURABILITY_MODIFIER = 1.1f;

  public BroadSword(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);

    ToolRegistry.registerToolCrafting(this);
  }

  @Override
  public StatsNBT buildToolStats(List<IMaterial> materials) {
    StatsNBT statsNBT = super.buildToolStats(materials);

    return new StatsNBT((int) (statsNBT.durability * DURABILITY_MODIFIER), statsNBT.harvestLevel, statsNBT.attack + 1f, statsNBT.miningSpeed, statsNBT.attackSpeedMultiplier, statsNBT.freeModifiers, statsNBT.broken);
  }
}
