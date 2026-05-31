package slimeknights.tconstruct.library.tools.definition;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.compat.neoforged.neoforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;

/**
 * This class serves primarily as a container where the datapack tool data will be injected on datapack load
 * @see #create(ResourceLocation)
 */
@RequiredArgsConstructor
public class ToolDefinition implements IdAwareObject {
  /** Empty tool definition instance to prevent the need for null for a fallback */
  public static final ToolDefinition EMPTY = new ToolDefinition(TConstruct.getResource("empty"));

  @Getter
  private final ResourceLocation id;
  /** Base data loaded from JSON, contains stats, traits, and starting slots */
  @Getter
  protected ToolDefinitionData data = ToolDefinitionData.EMPTY;

  /** Creates and registers a new tool definition */
  public static ToolDefinition create(ResourceLocation id) {
    ToolDefinition definition = new ToolDefinition(id);
    ToolDefinitionLoader.getInstance().registerToolDefinition(definition);
    return definition;
  }

  /** Creates and registers a new tool definition */
  public static ToolDefinition create(RegistryObject<? extends ItemLike> item) {
    return create(item.getId());
  }

  /** Creates and registers a new tool definition */
  public static ToolDefinition create(IdAwareObject item) {
    return create(item.getId());
  }

  /** Gets the given module from the tool */
  public <T> T getHook(ModuleHook<T> hook) {
    return data.getHook(hook);
  }

  /** Checks if the tool uses multipart stats */
  public boolean hasMaterials() {
    return !data.getHook(ToolHooks.TOOL_MATERIALS).getStatTypes(this).isEmpty();
  }


  /* Loader methods */

  /** Updates the data in this tool definition from the JSON loader, should not be called directly other than by the loader */
  @VisibleForTesting
  public void setData(ToolDefinitionData data) {
    this.data = data;
  }

  /** Sets the data back to empty */
  protected void clearData() {
    setData(ToolDefinitionData.EMPTY);
  }

  /** If true, the definition data is loaded from the datapack, so we can expect it to be reliable. False typically means datapacks are not yet loaded (e.g. menu startup) */
  public boolean isDataLoaded() {
    return data != ToolDefinitionData.EMPTY;
  }
}
