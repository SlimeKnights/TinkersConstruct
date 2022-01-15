package slimeknights.tconstruct.library.client.data.material;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Base class for listing sprites to generate */
@RequiredArgsConstructor
public abstract class AbstractPartSpriteProvider {

  /** List of created sprites */
  private final List<PartSpriteInfo> sprites = new ArrayList<>();
  private final List<ToolSpriteBuilder> toolSprites = new ArrayList<>();
  /** If true, sprites have been added */
  private boolean added = false;
  /** Default mod ID for helper functions */
  private final String modID;

  /** Gets the name of these part sprites */
  public abstract String getName();

  /** Function to add both sprites and materials */
  protected abstract void addAllSpites();


  /* Builder functions */

  /**
   * Adds a given sprite to the list to generate
   * @param sprite  Sprite name
   * @param requiredStats  At least one of these stat types must be present for this sprite to be generated
   */
  protected void addSprite(ResourceLocation sprite, MaterialStatsId requiredStats) {
    sprites.add(new PartSpriteInfo(sprite, requiredStats, null));
  }

  /**
   * Adds a given sprite to the list to generated
   * @param name           Name relative to the mod
   * @param requiredStats  At least one of these stat types must be present for this sprite to be generated
   */
  protected void addSprite(String name, MaterialStatsId requiredStats) {
    addSprite(new ResourceLocation(modID, name), requiredStats);
  }

  /**
   * Adds a given texture to the list to generate, local to textures instead of tool
   * @param sprite  Sprite name
   * @param requiredStats  At least one of these stat types must be present for this sprite to be generated
   */
  protected void addTexture(ResourceLocation sprite, MaterialStatsId requiredStats) {
    sprites.add(new PartSpriteInfo(sprite, requiredStats, true));
  }

  /**
   * Adds a given sprite to the list to generated, local to textures instead of tool
   * @param name           Name relative to the mod
   * @param requiredStats  At least one of these stat types must be present for this sprite to be generated
   */
  protected void addTexture(String name, MaterialStatsId requiredStats) {
    addTexture(new ResourceLocation(modID, name), requiredStats);
  }

  /**
   * Adds a sprite for a generic tool part from the parts folder
   * @param name  Part name relative to item/tool/parts
   * @param requiredStats  At least one of these stat types must be present for this part to be generated
   */
  protected void addPart(String name, MaterialStatsId requiredStats) {
    addSprite("parts/" + name, requiredStats);
  }

  /** Adds a sprite requiring head stats */
  protected void addHead(String name) {
    addPart(name, HeadMaterialStats.ID);
  }

  /** Adds a sprite requiring handle stats */
  protected void addHandle(String name) {
    addPart(name, HandleMaterialStats.ID);
  }

  /** Adds a sprite requiring extra stats */
  protected void addBinding(String name) {
    addPart(name, ExtraMaterialStats.ID);
  }

  /** Create a builder for tool sprites */
  protected ToolSpriteBuilder buildTool(ResourceLocation name) {
    ToolSpriteBuilder builder = new ToolSpriteBuilder(name);
    toolSprites.add(builder);
    return builder;
  }

  /** Create a builder for tool sprites relative to the default mod ID */
  protected ToolSpriteBuilder buildTool(String name) {
    return buildTool(new ResourceLocation(modID, name));
  }


  /* For use in the data generator */

  /** Runs {@link #addAllSpites()} if not yet run */
  private void ensureSpritesAdded() {
    if (!added) {
      addAllSpites();
      toolSprites.forEach(ToolSpriteBuilder::build);
      toolSprites.clear();
      added = true;
    }
  }

  /** Gets all sprites produced by this provider */
  public List<PartSpriteInfo> getSprites() {
    ensureSpritesAdded();
    return sprites;
  }

  /** Closes all open images and resets all caches */
  public void cleanCache() {
    sprites.clear();
    added = false;
  }

  /** Data class containing a sprite path, and different bases */
  @RequiredArgsConstructor
  public static class PartSpriteInfo {
    /** Path to the base sprite */
    private final ResourceLocation path;
    /** Stat type of this part */
    @Getter
    private final MaterialStatsId statType;
    /** If true, the texture comes from textures instead of textures/item/tool */
    private final Boolean baseFolder;
    /** Cache of fetched images for each sprite name */
    private transient final Map<String,NativeImage> sprites = new HashMap<>();

    /** Path including the item tool folder */
    private transient ResourceLocation computedPath;

    public PartSpriteInfo(ResourceLocation path, MaterialStatsId statType) {
      this(path, statType, null);
    }

    /** Gets the path to the sprite */
    public ResourceLocation getPath() {
      if (computedPath == null) {
        if (baseFolder == Boolean.TRUE) {
          computedPath = path;
        } else {
          computedPath = new ResourceLocation(path.getNamespace(), "item/tool/" + path.getPath());
        }
      }
      return computedPath;
    }

    /** Gets the texture for the given fallback name, use empty string for the default */
    @Nullable
    public NativeImage getTexture(AbstractSpriteReader spriteReader, String name) {
      if (sprites.containsKey(name)) {
        return sprites.get(name);
      }
      // determine the path to try for the sprite
      ResourceLocation path = getPath();
      ResourceLocation fallbackPath = path;
      if (!name.isEmpty()) {
        fallbackPath = new ResourceLocation(path.getNamespace(), path.getPath() + "_" + name);
      }
      // if the image exists, fetch it and return it
      NativeImage image = spriteReader.readIfExists(fallbackPath);
      sprites.put(name, image);
      return image;
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  protected class ToolSpriteBuilder {
    private final ResourceLocation name;
    private final Map<String, MaterialStatsId> parts = new HashMap<>();
    private boolean hasLarge = false;

    /** Adds a part to the tool */
    public ToolSpriteBuilder addPart(String name, MaterialStatsId statTypes) {
      parts.put(name, statTypes);
      return this;
    }

    /** Adds a part to the tool with a broken texture */
    public ToolSpriteBuilder addBreakablePart(String name, MaterialStatsId statTypes) {
      addPart(name, statTypes);
      addPart("broken_" + name, statTypes);
      return this;
    }

    /** Adds a sprite requiring head stats */
    public ToolSpriteBuilder addHead(String name) {
      return addPart(name, HeadMaterialStats.ID);
    }

    /** Adds a breakable part requiring head stats */
    public ToolSpriteBuilder addBreakableHead(String name) {
      return addBreakablePart(name, HeadMaterialStats.ID);
    }

    /** Adds a sprite requiring handle stats */
    public ToolSpriteBuilder addHandle(String name) {
      return addPart(name, HandleMaterialStats.ID);
    }

    /** Adds a sprite requiring extra stats */
    public ToolSpriteBuilder addBinding(String name) {
      return addPart(name, ExtraMaterialStats.ID);
    }

    /** Adds sprites for large parts as well */
    public ToolSpriteBuilder withLarge() {
      this.hasLarge = true;
      return this;
    }

    /** Helper to add all parts for a size */
    private void addParts(String path) {
      for (Entry<String,MaterialStatsId> entry : parts.entrySet()) {
        addSprite(new ResourceLocation(name.getNamespace(), path + "/" + entry.getKey()), entry.getValue());
      }
    }

    /** Adds all sprites into the list of sprites */
    private void build() {
      addParts(name.getPath());
      if (hasLarge) {
        addParts(name.getPath() + "/large");
      }
    }
  }
}
