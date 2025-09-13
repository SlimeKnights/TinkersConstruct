package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Enum representing sources of {@link IModDataView} on tools. */
public enum ModDataSource {
  PERSISTENT, VOLATILE;

  public static final EnumLoadable<ModDataSource> LOADABLE = new EnumLoadable<>(ModDataSource.class);

  /**
   * Gets the data for this source
   */
  public IModDataView getData(IToolStackView tool) {
    return this == PERSISTENT ? tool.getPersistentData() : tool.getVolatileData();
  }
}
