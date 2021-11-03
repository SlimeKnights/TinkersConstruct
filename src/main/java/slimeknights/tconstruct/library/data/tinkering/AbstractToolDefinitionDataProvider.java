package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Base datagenerator to generate tool definition data */
public abstract class AbstractToolDefinitionDataProvider extends GenericDataProvider {
  private final Map<ResourceLocation,ToolDefinitionDataBuilder> allTools = new HashMap<>();
  /** Mod ID to filter definitions we care about */
  private final String modId;

  public AbstractToolDefinitionDataProvider(DataGenerator generator, String modId) {
    super(generator, ResourcePackType.SERVER_DATA, ToolDefinitionLoader.FOLDER, ToolDefinitionLoader.GSON);
    this.modId = modId;
  }

  /**
   * Function to add all relevant tool definitions
   */
  protected abstract void addToolDefinitions();

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(ResourceLocation id) {
    return allTools.computeIfAbsent(id, i -> ToolDefinitionDataBuilder.builder());
  }

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(IItemProvider item) {
    return define(Objects.requireNonNull(item.asItem().getRegistryName()));
  }

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(ToolDefinition definition) {
    return define(definition.getId());
  }

  @Override
  public void act(DirectoryCache cache) throws IOException {
    addToolDefinitions();
    Map<ResourceLocation,ToolDefinition> relevantDefinitions = ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions().stream()
                                                                                   .filter(def -> def.getId().getNamespace().equals(modId))
                                                                                   .collect(Collectors.toMap(ToolDefinition::getId, Function.identity()));
    // ensure all required definitions are included
    for (ToolDefinition definition : relevantDefinitions.values()) {
      ResourceLocation name = definition.getId();
      if (!allTools.containsKey(name)) {
        throw new IllegalStateException(String.format("Missing tool definition for '%s'", name));
      }
    }
    // ensure all included ones are required, and the built ones are valid
    for (Entry<ResourceLocation,ToolDefinitionDataBuilder> entry : allTools.entrySet()) {
      ResourceLocation id = entry.getKey();
      ToolDefinition definition = relevantDefinitions.get(id);
      if (definition == null) {
        throw new IllegalStateException("Unknown tool definition with ID " + id);
      }
      ToolDefinitionData data = entry.getValue().build();
      definition.validate(data);
      saveThing(cache, id, data);
    }
  }
}
