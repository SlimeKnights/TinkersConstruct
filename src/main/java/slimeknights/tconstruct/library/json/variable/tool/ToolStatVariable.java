package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/**
 * Variable to get a stat from the tool
 */
public record ToolStatVariable(INumericToolStat<?> stat) implements ToolVariable {
  public static final RecordLoadable<ToolStatVariable> LOADER = RecordLoadable.create(ToolStats.NUMERIC_LOADER.requiredField("stat", ToolStatVariable::stat), ToolStatVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return tool.getStats().get(stat).floatValue();
  }

  @Override
  public RecordLoadable<ToolStatVariable> getLoader() {
    return LOADER;
  }
}
