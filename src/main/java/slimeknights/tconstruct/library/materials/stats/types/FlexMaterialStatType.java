package slimeknights.tconstruct.library.materials.stats.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Tiers;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.types.StatTypes.FloatStat;
import slimeknights.tconstruct.library.materials.stats.types.StatTypes.Stat;
import slimeknights.tconstruct.library.materials.stats.types.StatTypes.TierStat;

public class FlexMaterialStatType extends MaterialStatType<FlexMaterialStat> {

    public FlexMaterialStatType(MaterialStatsId id, boolean canRepair,
            LinkedHashMap<String, Supplier<Stat<?, ?>>> stats) {
        super(id, (FlexMaterialStat) null, null);
        this.canRepair = canRepair;
        this.stats = stats;
    }

    /**
     * Decodes a stat type from the network.
     * @param buffer Buffer instance
     * @return Decoded stat type
     */
    public static FlexMaterialStatType decode(FriendlyByteBuf buffer) {
        MaterialStatsId id = MaterialStatsId.PARSER.decode(buffer);
        boolean canRepair = buffer.readBoolean();
        int statCount = buffer.readVarInt();
        LinkedHashMap<String, Supplier<Stat<?, ?>>> stats = new LinkedHashMap<>(statCount);
        for (int i = 0; i < statCount; i++) {
            String statName = buffer.readUtf();
            StatTypes.Operator operator = buffer.readEnum(StatTypes.Operator.class);
            String desc=buffer.readUtf();
            String info=buffer.readUtf();
            byte statType = buffer.readByte();
            switch (statType) {
                case 0:
                    Tiers tier=buffer.readEnum(Tiers.class);
                    stats.put(statName,()->new TierStat(tier, desc, info));
                    break;
                case 1:
                    float value=buffer.readFloat();
                    stats.put(statName,()->new FloatStat(value, operator, desc, info));
                    break;
            }
        }
        return new FlexMaterialStatType(id, canRepair, stats);
    }

    /**
     * Encodes this stat type to the network.
     * @param buffer Buffer instance
     */

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(canRepair);
        buffer.writeVarInt(stats.size());
        stats.forEach((str, sup) -> {
            buffer.writeUtf(str);
            // TODO: make stat encode itself.
            var stat = sup.get();
            buffer.writeEnum(stat.operator);
            buffer.writeUtf(stat.descToolTip);
            buffer.writeUtf(stat.infoToolTip);
            if (stat instanceof TierStat tierStat) {
                buffer.writeByte(0);
                buffer.writeEnum((Tiers)tierStat.getValue());
            }
            else if (stat instanceof FloatStat floatStat) {
                buffer.writeByte(1);
                buffer.writeFloat(floatStat.getValue());
            }
        });
    }

    private final Map<String, Supplier<Stat<?, ?>>> stats;
    private final boolean canRepair;

    @Override
    public boolean canRepair() {
        return canRepair;
    }

    @Override
    public FlexMaterialStat getDefaultStats() {
        return new FlexMaterialStat(this, stats);
    }

    @Override
    public RecordLoadable<FlexMaterialStat> getLoadable() {
        var fields = new ArrayList<RecordField<Object, FlexMaterialStat>>();
        stats.forEach((str, sup) -> {
            fields.add((RecordField) sup.get().getLoadable(str));
        });
        switch (fields.size())// 1~15
        {
            case 1:
                return RecordLoadable.create(fields.get(0), (Object value) -> {
                    return new FlexMaterialStat(this, stats, value);
                });
            case 2:
                return RecordLoadable.create(fields.get(0), fields.get(1),
                        (Object a, Object b) -> new FlexMaterialStat(this, stats, a, b));
            case 3:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2),
                        (Object a, Object b, Object c) -> new FlexMaterialStat(this, stats, a, b, c));
            case 4:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3),
                        (Object a, Object b, Object c, Object d) -> new FlexMaterialStat(this, stats, a, b, c, d));
            case 5:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        (Object a, Object b, Object c, Object d, Object e) -> new FlexMaterialStat(this, stats, a, b, c,
                                d, e));
            case 6:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), (Object a, Object b, Object c, Object d, Object e,
                                Object f) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f));
            case 7:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), (Object a, Object b, Object c, Object d, Object e, Object f,
                                Object g) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g));
            case 8:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g,
                                Object h) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h));
            case 9:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h,
                                Object i) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h, i));
            case 10:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h, i, j));
            case 11:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9), fields.get(10),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j,
                                Object k) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h, i, j, k));
            case 12:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9), fields.get(10),
                        fields.get(11),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j, Object k,
                                Object l) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h, i, j, k, l));
            case 13:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9), fields.get(10),
                        fields.get(11), fields.get(12),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j, Object k, Object l,
                                Object m) -> new FlexMaterialStat(this, stats, a, b, c, d, e, f, g, h, i, j, k, l, m));
            case 14:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9), fields.get(10),
                        fields.get(11), fields.get(12), fields.get(13),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j, Object k, Object l, Object m, Object n) -> new FlexMaterialStat(this, stats,
                                        a, b, c, d, e, f, g, h, i, j, k, l, m, n));
            case 15:
                return RecordLoadable.create(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4),
                        fields.get(5), fields.get(6), fields.get(7), fields.get(8), fields.get(9), fields.get(10),
                        fields.get(11), fields.get(12), fields.get(13), fields.get(14),
                        (Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
                                Object j, Object k, Object l, Object m, Object n, Object o) -> new FlexMaterialStat(
                                        this, stats, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o));
            default:
                throw new IllegalArgumentException("MaterialStatType must have between 1 and 15 stats");
        }
    }
}
