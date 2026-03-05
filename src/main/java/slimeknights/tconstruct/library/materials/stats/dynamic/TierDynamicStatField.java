package slimeknights.tconstruct.library.materials.stats.dynamic;

import static slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.withDefaultNamespace;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolTierStat;
import slimeknights.tconstruct.library.utils.HarvestTiers;

public record TierDynamicStatField(
        String name, String localizedDescription, String tooltipKey, String toolStat, Tier defaultValue) implements DynamicStatField<TierDynamicStatField.TierDynamicStat, ToolTierStat> {
    
    public static final ResourceLocation TYPE = TConstruct.getResource("tier");
    public static final RecordLoadable<TierDynamicStatField> LOADER = RecordLoadable.create(
            NAME_FIELD,
            DESC_FIELD,
            TOOLTIP_FIELD,
            TOOL_STAT_FIELD,
            TinkerLoadables.TIER.requiredField(DynamicStatField.DEFAULT_VALUE, TierDynamicStatField::defaultValue),
            TierDynamicStatField::new);

    public TierDynamicStatField(String name, String localizedDescription, String tooltipKey, String toolStat, Tier defaultValue) {
        this.name = name;
        this.localizedDescription = localizedDescription;
        this.tooltipKey = tooltipKey;
        this.toolStat = toolStat;
        this.defaultValue = defaultValue;
        if(getToolStat() == null) {
            throw new JsonParseException("Cannot Find Tier Tool Stat: " + toolStat);
        }
    }

    public static record TierDynamicStat(Tier value, ToolTierStat toolStat, TierDynamicStatField loader) implements DynamicStatField.DynamicStat<TierDynamicStat> {

        @Override
        public RecordLoadable<TierDynamicStat> getLoader() {
            return loader;
        }

        @Override
        public void apply(ModifierStatsBuilder builder, float scale) {
            toolStat.update(builder, value);
        }

        @Override
        public Component getLocalizedInfo() {
            return loader.getLocalizedInfo(this);
        }

        @Override
        public Component getLocalizedDescription() {
            return loader.getLocalizedDescription();
        }
    }

    @Override
    public TierDynamicStat deserialize(JsonObject json, TypedMap context) {
        String str=GsonHelper.getAsString(json, name,null);
        Tier value = str==null?defaultValue:TierSortingRegistry.byName(new ResourceLocation(str));
        return new TierDynamicStat(value, getToolStat(), this);
    }

    @Override
    public void serialize(TierDynamicStat object, JsonObject json) {
        json.addProperty(name, TierSortingRegistry.getName(object.value).toString());
    }

    @Override
    public TierDynamicStat decode(FriendlyByteBuf buffer, TypedMap context) {
        Tier value = TierSortingRegistry.byName(buffer.readResourceLocation());
        return new TierDynamicStat(value, getToolStat(), this);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, TierDynamicStat value) {
        buffer.writeResourceLocation(TierSortingRegistry.getName(value.value));
    }

    @Override
    public RecordLoadable<? extends IHaveLoader> getLoader() {
        return LOADER;
    }

    @Override
    public Component getLocalizedInfo(TierDynamicStat value) {
        return tooltipKey==""?getToolStat().formatValue(value.value):Component.translatable(tooltipKey).append(HarvestTiers.getName(value.value));
    }

    @Override
    @Nonnull
    public ToolTierStat getToolStat() {
        return (ToolTierStat) ToolStats.getToolStat(withDefaultNamespace(toolStat));
    }
}
