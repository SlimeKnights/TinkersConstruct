package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/** Base data generator to generate station slot layouts */
public abstract class AbstractStationSlotLayoutProvider extends GenericDataProvider {

  /** Sort index for weapons */
  protected static final int SORT_WEAPON = 3;
  /** Sort index for harvest */
  protected static final int SORT_HARVEST = 6;
  /** Index for large tools, add to either weapon or harvest */
  protected static final int SORT_LARGE = 6;

  private final Map<ResourceLocation,StationSlotLayout.Builder> allLayouts = new HashMap<>();

  public AbstractStationSlotLayoutProvider(DataGenerator generator) {
    super(generator, ResourcePackType.SERVER_DATA, StationSlotLayoutLoader.FOLDER, StationSlotLayoutLoader.GSON);
  }

  /**
   * Function to add all relevant layouts
   */
  protected abstract void addLayouts();

  /** Defines the given ID as a general layout */
  protected StationSlotLayout.Builder define(ResourceLocation id) {
    return allLayouts.computeIfAbsent(id, i -> StationSlotLayout.builder());
  }

  /** Defines the given ID as a item layout */
  protected StationSlotLayout.Builder define(IItemProvider item) {
    return define(Objects.requireNonNull(item.asItem().getRegistryName()));
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(IModifiableDisplay item) {
    return define(Objects.requireNonNull(item.asItem().getRegistryName()))
      .translationKey(item.asItem().getTranslationKey())
      .icon(item.getRenderTool());
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(Supplier<? extends IModifiableDisplay> item) {
    return defineModifiable(item.get());
  }

  @Override
  public void act(DirectoryCache cache) throws IOException {
    addLayouts();
    allLayouts.forEach((id, builder) -> saveThing(cache, id, builder.build()));
  }
}
