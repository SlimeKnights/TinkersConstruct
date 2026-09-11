package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

/**
 * A material stat that has dynamic stat fields.
 */
public record DynamicMaterialStats(MaterialStatType<?> type, List<DynamicStat<?>> stats, List<Function<Float, Component>> localizedInfo, List<Component> localizedDescriptions) implements IMaterialStats.ScaledTooltip {

	@Override
	public MaterialStatType<?> getType() {
		return type;
	}

	@Override
	public void apply(@Nonnull ModifierStatsBuilder builder, float scale) {
		stats.forEach(stat -> stat.apply(builder, scale));
	}

	@Override
	public List<Component> getLocalizedInfo(float scale) {
		return localizedInfo.stream().map(f -> f.apply(scale)).toList();
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return localizedDescriptions;
	}
}
