package slimeknights.tconstruct.library.materials.stats.dynamic;

import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

public interface DynamicStatField<S extends DynamicStatField.DynamicStat, T extends IToolStat<?>> extends RecordLoadable<S>, IHaveLoader {

    public static final GenericLoaderRegistry<DynamicStatField<?, ?>> REGISTRY = new GenericLoaderRegistry<>(
            "Dynamic Stat Field", false);

    public static final LoadableField<String, DynamicStatField<?, ?>> NAME_FIELD = StringLoadable.DEFAULT.requiredField("name", DynamicStatField::name);
    public static final LoadableField<String, DynamicStatField<?, ?>> DESC_FIELD = StringLoadable.DEFAULT.defaultField("description", "", DynamicStatField::localizedDescription);
    public static final LoadableField<String, DynamicStatField<?, ?>> TOOLTIP_FIELD = StringLoadable.DEFAULT.defaultField("tooltip", "", DynamicStatField::tooltipKey);
    public static final LoadableField<String, DynamicStatField<?, ?>> TOOL_STAT_FIELD = StringLoadable.DEFAULT.requiredField("stat", DynamicStatField::toolStat);
    public static final String DEFAULT_VALUE = "default_value";
    /**
     * Get the name of the stat field
     * 
     * @return Name of the stat field
     */
    @Nonnull
    String name();

    /**
     * Get the localized description of the stat field
     * 
     * @return Localized description of the stat field
     */
    @Nonnull
    String localizedDescription();

    /**
     * Get the tooltip key of the stat field
     * 
     * @return Tooltip key of the stat field
     */
    @Nonnull
    String tooltipKey();

    /**
     * Get the tool stat of the stat field
     * 
     * @return Tool stat of the stat field
     */
    @Nonnull
    String toolStat();

    /**
     * Get the tool stat of the stat field
     * 
     * @return Tool stat of the stat field
     */
    @Nonnull
    T getToolStat();

    /**
     * Formats the stat field into a component
     * 
     * @return Formatted component
     */
    Component getLocalizedInfo(S value);

    /**
     * Formats the stat field into a component
     * 
     * @return Formatted component
     */
    default Component getLocalizedDescription() {
        return localizedDescription() != "" ? Component.translatable(localizedDescription())
                : getToolStat().getDescription();
    }

    /**
     * Formats the stat field into a resource location with default namespace "tconstruct"
     * 
     * @return Formatted resource location
     */
    public static ToolStatId withDefaultNamespace(String path) {
        if (!path.contains(":")) {
            return new ToolStatId(TConstruct.MOD_ID + ":" + path);
        }
        return new ToolStatId(path);
    }

    public static interface DynamicStat<D extends DynamicStat<D>> extends GenericLoaderRegistry.IHaveLoader {
        /**
         * Applies the stat field to the stats builder
         * 
         * @param builder Stats builder to apply to
         * @param scale   Scale to apply
         */
        public void apply(ModifierStatsBuilder builder, float scale);

        /**
         * Formats the stat field into a component
         * 
         * @return Formatted component
         */
        Component getLocalizedInfo();

        /**
         * Formats the stat field into a component
         * 
         * @return Formatted component
         */
        Component getLocalizedDescription();

        @Override
        public RecordLoadable<D> getLoader();
    }
}
