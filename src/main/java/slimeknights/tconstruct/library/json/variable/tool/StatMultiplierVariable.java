package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/**
 * Variable to get a stat from the tool
 */
public record StatMultiplierVariable(INumericToolStat<?> stat) implements ToolVariable {
  public static final RecordLoadable<StatMultiplierVariable> LOADER = RecordLoadable.create(ToolStats.NUMERIC_LOADER.requiredField("stat", StatMultiplierVariable::stat), StatMultiplierVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return tool.getMultiplier(stat);
  }

  @Override
  public RecordLoadable<StatMultiplierVariable> getLoader() {
    return LOADER;
  }
}
