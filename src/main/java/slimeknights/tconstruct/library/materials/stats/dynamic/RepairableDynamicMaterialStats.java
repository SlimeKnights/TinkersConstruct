package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;

/**
 * A material stat that has dynamic stat fields.
 * This class is used to create material stats that can be repaired.
 */
public class RepairableDynamicMaterialStats extends DynamicMaterialStats implements IRepairableMaterialStats {
	@Getter
	@Accessors(fluent = true)
	private final int durability;
	
	public RepairableDynamicMaterialStats(MaterialStatType<?> type, Map<String, DynamicStat> stats, int durability) {
		super(type, stats);
		this.durability = durability;
	}
}
