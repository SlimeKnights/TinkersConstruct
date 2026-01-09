package slimeknights.tconstruct.fixture;

import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.types.DynamicMaterialStatType;
import slimeknights.tconstruct.library.materials.stats.types.DynamicStatField;
import slimeknights.tconstruct.library.materials.stats.types.FloatDynamicStatField;
import slimeknights.tconstruct.library.materials.stats.types.TierDynamicStatField;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import java.util.List;

public final class MaterialStatTypesFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_type_1");

  public static final List<DynamicStatField<?>> statFields = List.of(
    new FloatDynamicStatField("test1",ToolStats.DURABILITY,1f,FloatDynamicStatField.Operation.PERCENT, "desc1", "info1"),
    new FloatDynamicStatField("test2",ToolStats.MINING_SPEED,2f,FloatDynamicStatField.Operation.UPDATE, "desc2", "info2"),
    new TierDynamicStatField("test3",ToolStats.HARVEST_TIER,Tiers.STONE,"desc3")
  );
  public static final DynamicMaterialStatType DYNAMIC_TYPE = new DynamicMaterialStatType(STATS_TYPE, false, statFields);

  public static final IMaterialStats MATERIAL_STATS = DYNAMIC_TYPE.getDefaultStats();

  private MaterialStatTypesFixture() {
  }
}
