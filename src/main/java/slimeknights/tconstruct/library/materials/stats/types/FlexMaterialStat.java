package slimeknights.tconstruct.library.materials.stats.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Supplier;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.types.StatTypes.Stat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public class FlexMaterialStat implements IMaterialStats {

	public FlexMaterialStat(MaterialStatType<?> type, Map<String, Supplier<Stat<?, ?>>> stats, Object... values) {
		this.type = type;
		var map = new LinkedHashMap<String, Stat<?, ?>>();
		stats.forEach((name, supplier) -> {
			map.put(name, supplier.get());
		});
		for (int i = 0; i < values.length; i++) {
			var stat = map.values().toArray(new Stat<?, ?>[0])[i];
			stat.setValue(values[i]);
		}
		this.stats = map;
	}

	private final MaterialStatType<?> type;
	private final Map<String, Stat<?, ?>> stats;

	public Object get(String name) {
		return stats.get(name).getValue();
	}

	@Override
	public MaterialStatType<?> getType() {
		return type;
	}

	@Override
	public List<Component> getLocalizedInfo() {
		try {
			List<Component> list = new ArrayList<>();
			stats.forEach((name, stat) -> {
				list.add(stat.format(name));
			});
			return list;
		} catch (Exception e) {
			return List.of(Component.literal("Error: " + e.getMessage()));
		}
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		try {
			List<Component> list = new ArrayList<>();
			stats.forEach((name, stat) -> {
				list.add(Component.translatable(stat.descToolTip));
			});
			return list;
		} catch (Exception e) {
			return List.of(Component.literal("Error: " + e.getMessage()));
		}
	}

	@Override
	public void apply(ModifierStatsBuilder builder, float scale) {
		stats.forEach((name, stat) -> {
			stat.apply(builder, name, scale);
		});
	}
}
