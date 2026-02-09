package slimeknights.tconstruct.fixture;

import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicMaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField;
import slimeknights.tconstruct.library.materials.stats.dynamic.FloatDynamicStatField;
import slimeknights.tconstruct.library.materials.stats.dynamic.TierDynamicStatField;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolTierStat;

import java.util.List;

public final class MaterialStatTypesFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "dynamic");

  public static final FloatToolStat TestFloatStat = new FloatToolStat(new ToolStatId(TConstruct.MOD_ID, "test_stat"), 0xFF47CC47, 1, 1, Integer.MAX_VALUE, TinkerTags.Items.DURABILITY);
  public static final ToolTierStat TestTierStat = new ToolTierStat(new ToolStatId(TConstruct.MOD_ID, "test_tier_stat"));
  static {
    try {
      DynamicStatField.REGISTRY.register(new TierDynamicStatField.TierDynamicStatDecoder());
      DynamicStatField.REGISTRY.register(new FloatDynamicStatField.FloatDynamicStatDecoder());
      ToolStats.register(TestFloatStat);
      ToolStats.register(TestTierStat);
    } catch (Exception e) {
      //
    }
  }
  
  public static final List<DynamicStatField<?>> statFields = List.of(
    new FloatDynamicStatField("test1",TestFloatStat,1f,FloatDynamicStatField.Operation.PERCENT, "desc1", "info1"),
    new FloatDynamicStatField("test2",TestFloatStat,2f,FloatDynamicStatField.Operation.UPDATE, "desc2", "info2"),
    new TierDynamicStatField("test3",TestTierStat,Tiers.STONE,"desc3")
  );
  public static final DynamicMaterialStatType DYNAMIC_TYPE = new DynamicMaterialStatType(STATS_TYPE, "test2", statFields);

  public static final IMaterialStats MATERIAL_STATS = DYNAMIC_TYPE.getDefaultStats();

  private MaterialStatTypesFixture() {
  }
}
