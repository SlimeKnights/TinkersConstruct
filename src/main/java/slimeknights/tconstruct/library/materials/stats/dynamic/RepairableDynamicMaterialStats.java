package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.List;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

/**
 * A material stat that has dynamic stat fields.
 * This class is used to create material stats that can be repaired.
 */
public record RepairableDynamicMaterialStats(DynamicMaterialStats stats, int durability) implements IRepairableMaterialStats {

	@Override
	public MaterialStatType<?> getType() {
		return stats.getType();
	}

	@Override
	public List<Component> getLocalizedInfo() {
		return stats.getLocalizedInfo();
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return stats.getLocalizedDescriptions();
	}

	@Override
	public void apply(ModifierStatsBuilder builder, float scale) {
		stats.apply(builder, scale);
	}
}
