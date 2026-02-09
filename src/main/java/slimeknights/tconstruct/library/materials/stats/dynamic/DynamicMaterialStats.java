package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

/**
 * A material stat that has dynamic stat fields.
 */
public class DynamicMaterialStats implements IMaterialStats {

	private final MaterialStatType<?> type;
	private final Map<String,DynamicStat> stats;

	@Getter
	private final List<Component> localizedInfo;
	@Getter
	private final List<Component> localizedDescriptions;
	/**
	 * Constructs a dynamic material stat.
	 * 
	 * @param type   The material stat type.
	 * @param stats  The dynamic stats.
	 */
	public DynamicMaterialStats(MaterialStatType<?> type, Map<String,DynamicStat> stats) {
		this.type = type;
		this.stats = stats;
		this.localizedInfo = stats.values().stream().map(DynamicStat::getLocalizedInfo).collect(Collectors.toList());
		this.localizedDescriptions = stats.values().stream().map(DynamicStat::getLocalizedDescription).collect(Collectors.toList());
	}


	/**
	 * Gets a stat by name.
	 * @see DynamicStatField need this to encode and serialize
	 * @param name  Stat name
	 * @return  Stat, or null if not found
	 */
	@Nullable
	public DynamicStat getStat(String name)	{
		return stats.get(name);
	}

	@Override
	public MaterialStatType<?> getType() {
		return type;
	}

	@Override
	public void apply(@Nonnull ModifierStatsBuilder builder, float scale) {
		stats.values().forEach(stat -> stat.apply(builder, scale));
	}
}
