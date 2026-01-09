package slimeknights.tconstruct.library.materials.stats.types;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.types.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public class DynamicMaterialStat implements IMaterialStats {


	private final MaterialStatType<?> type;
	private final Map<String,DynamicStat> stats;

	public DynamicMaterialStat(MaterialStatType<?> type, Map<String,DynamicStat> stats) {
		this.type = type;
		this.stats = stats;
	}


	/**
	 * Gets a stat by name.
	 * @see DynamicStatField need this to encode and serialize
	 * @param name  Stat name
	 * @return  Stat, or null if not found
	 */
	@Nullable
	public DynamicStat getStat(String name)
	{
		return stats.get(name);
	}

	@Override
	public MaterialStatType<?> getType() {
		return type;
	}

	@Override
	public List<Component> getLocalizedInfo() {
		return stats.values().stream().map(stat->stat.getLocalizedInfo()).collect(Collectors.toList());
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return stats.values().stream().map(stat->stat.getLocalizedDescription()).collect(Collectors.toList());
	}

	@Override
	public void apply(@Nonnull ModifierStatsBuilder builder, float scale) {
		stats.values().forEach(stat -> {
			stat.apply(builder, scale);
		});
	}
}
