package slimeknights.tconstruct.library.client.data.material;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.mapping.CollectionLoadable;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.data.util.AbstractSpriteReader;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Base class for listing sprites to generate */
@SuppressWarnings("SameParameterValue")
@RequiredArgsConstructor
@CanIgnoreReturnValue
public abstract class AbstractPartSpriteProvider {

  /** List of created sprites */
  private final List<PartSpriteInfo.Builder> sprites = new ArrayList<>();
  @Nullable
  private List<PartSpriteInfo> finalSprites = null;
  private final List<ToolSpriteBuilder> toolSprites = new ArrayList<>();
  /** Default mod ID for helper functions */
  private final String modID;

  /** Gets the name of these part sprites */
  @CheckReturnValue
  public abstract String getName();

  /** Function to add both sprites and materials */
  @CheckReturnValue
  protected abstract void addAllSpites(); // TODO 1.21: rename to addAllSprites


  /* Builder functions */

  /** Adds a given texture to the list to generate */
  protected PartSpriteInfo.Builder addTexture(ResourceLocation sprite, MaterialStatsId... requiredStats) {
    PartSpriteInfo.Builder builder = new PartSpriteInfo.Builder(sprite, requiredStats);
    sprites.add(builder);
    return builder;
  }

  /** Adds a given sprite to the list to generate, for the local namespace */
  protected PartSpriteInfo.Builder addTexture(String name, MaterialStatsId... requiredStats) {
    return addTexture(new ResourceLocation(modID, name), requiredStats);
  }

  /** Adds a given sprite to the list to generated, located in the tools folder */
  protected PartSpriteInfo.Builder addSprite(String name, MaterialStatsId... requiredStats) {
    return addTexture(new ResourceLocation(modID, "item/tool/" + name), requiredStats);
  }

  /** Adds a sprite for a generic tool part from the parts folder */
  protected PartSpriteInfo.Builder addPart(String name, MaterialStatsId... requiredStats) {
    return addSprite("parts/" + name, requiredStats);
  }

  /** Adds a sprite requiring head stats */
  protected PartSpriteInfo.Builder addHead(String name) {
    return addPart(name, HeadMaterialStats.ID);
  }

  /** Adds a sprite requiring handle stats */
  protected PartSpriteInfo.Builder addHandle(String name) {
    return addPart(name, HandleMaterialStats.ID);
  }

  /** Adds a sprite requiring extra stats */
  protected PartSpriteInfo.Builder addBinding(String name) {
    return addPart(name, StatlessMaterialStats.BINDING.getIdentifier());
  }

  /** Adds a sprite requiring head stats */
  protected PartSpriteInfo.Builder addLimb(String name) {
    return addPart(name, LimbMaterialStats.ID);
  }

  /** Adds a sprite requiring head stats */
  protected PartSpriteInfo.Builder addBowstring(String name) {
    return addPart(name, StatlessMaterialStats.BOWSTRING.getIdentifier());
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

  /** Gets all sprites produced by this provider */
  public List<PartSpriteInfo> getSprites() {
    if (finalSprites == null) {
      addAllSpites();
      toolSprites.forEach(ToolSpriteBuilder::build);
      toolSprites.clear();
      finalSprites = sprites.stream().map(PartSpriteInfo.Builder::build).toList();
    }
    return finalSprites;
  }

  /** Closes all open images and resets all caches */
  public void cleanCache() {
    sprites.clear();
    toolSprites.clear();
    finalSprites = null;
  }

  /** Data class containing a sprite path, and different bases */
  @RequiredArgsConstructor
  public static class PartSpriteInfo {
    /** Loadable instance */
    public static final RecordLoadable<PartSpriteInfo> LOADABLE = RecordLoadable.create(
      Loadables.RESOURCE_LOCATION.requiredField("path", i -> i.path),
      MaterialStatsId.PARSER.set(CollectionLoadable.COMPACT).requiredField("stat_type", i -> i.statTypes),
      BooleanLoadable.INSTANCE.defaultField("allow_animated", true, false, i -> i.allowAnimated),
      BooleanLoadable.INSTANCE.defaultField("skip_variants", false, false, i -> i.skipVariants),
      PartSpriteInfo::new);
    /** Loadable for a list, since its the main usage of this */
    public static final Loadable<List<PartSpriteInfo>> LIST_LOADABLE = LOADABLE.list(1);

    /** Path to the base sprite */
    @Getter
    private final ResourceLocation path;
    /** Stat type of this part */
    @Getter
    private final Set<MaterialStatsId> statTypes;
    @Getter
    private final boolean allowAnimated;
    /** If true, this sprite skips variant textures, used by ancient tools to skip adding unneeded sprites */
    @Getter
    private final boolean skipVariants;
    /** Cache of fetched images for each sprite name */
    private transient final Map<String,NativeImage> sprites = new HashMap<>();

    /** Gets the texture for the given fallback name, use empty string for the default */
    @Nullable
    public NativeImage getTexture(AbstractSpriteReader spriteReader, String name) {
      if (sprites.containsKey(name)) {
        return sprites.get(name);
      }
      // determine the path to try for the sprite
      ResourceLocation fallbackPath = path;
      if (!name.isEmpty()) {
        fallbackPath = new ResourceLocation(path.getNamespace(), path.getPath() + "_" + name);
      }
      // if the image exists, fetch it and return it
      NativeImage image = spriteReader.readIfExists(fallbackPath);
      sprites.put(name, image);
      return image;
    }

    /** Builder used for serialization */
    @CanIgnoreReturnValue
    @Accessors(fluent = true)
    @Setter(AccessLevel.PRIVATE)
    public static class Builder {
      private final ResourceLocation path;
      private final Set<MaterialStatsId> statTypes;
      private boolean allowAnimated = true;
      private boolean skipVariants = false;

      private Builder(ResourceLocation path, MaterialStatsId[] requiredStats) {
        this.path = path;
        this.statTypes = ImmutableSet.copyOf(requiredStats);
      }

      /** Disallows animating this sprite. Used for things that don't support animation such as armor */
      public Builder disallowAnimated() {
        return allowAnimated(false);
      }

      /** If true, this sprite skips variant textures, used by ancient tools to skip adding unneeded sprites */
      public Builder skipVariants() {
        return skipVariants(true);
      }

      /** Builds the final sprite info */
      @CheckReturnValue
      private PartSpriteInfo build() {
        return new PartSpriteInfo(path, statTypes, allowAnimated, skipVariants);
      }
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  protected class ToolSpriteBuilder {
    private final ResourceLocation name;
    private final Map<String, MaterialStatsId[]> parts = new LinkedHashMap<>();
    private boolean hasLarge = false;
    private boolean allowAnimated = true;
    private boolean skipVariants = false;

    /** Adds sprites for large parts as well */
    public ToolSpriteBuilder withLarge() {
      this.hasLarge = true;
      return this;
    }

    /** Prevents sprites for this tool from being animated. Used for armor to be more consistent with the worn texture. */
    public ToolSpriteBuilder disallowAnimated() {
      this.allowAnimated = false;
      return this;
    }

    /** Skips adding variant textures for this tool. Used for ancient tools to remove sprites that will never be seen. */
    public ToolSpriteBuilder skipVariants() {
      this.skipVariants = true;
      return this;
    }


    /** Adds a part to the tool */
    public ToolSpriteBuilder addPart(String name, MaterialStatsId... statTypes) {
      parts.put(name, statTypes);
      return this;
    }
    /** Adds a part to the tool */
    public ToolSpriteBuilder addPart(String name, IMaterialStats statType) {
      return addPart(name, statType.getIdentifier());
    }

    /**
     * Adds a part to the tool with a broken texture
     * 1.19 note: to simplify model generators, we changed from a broken prefix to a broken suffix for the part.
     * If you are not generating models and prefer less effort, just override this method.
     */
    public ToolSpriteBuilder addBreakablePart(String name, MaterialStatsId... statTypes) {
      addPart(name, statTypes);
      addPart(name + "_broken", statTypes);
      return this;
    }

    /**
     * Adds a part to the tool with a broken texture
     */
    public ToolSpriteBuilder addBreakablePart(String name, IMaterialStats statType) {
      return addBreakablePart(name, statType.getIdentifier());
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
      return addPart(name, StatlessMaterialStats.BINDING);
    }

    /** Adds a sprite requiring limb stats */
    public ToolSpriteBuilder addLimb(String name) {
      return addPart(name, LimbMaterialStats.ID);
    }

    /** Adds a sprite requiring grip stats */
    public ToolSpriteBuilder addGrip(String name) {
      return addPart(name, GripMaterialStats.ID);
    }

    /** Adds a sprite requiring bowstring stats */
    public ToolSpriteBuilder addBowstring(String name) {
      return addPart(name, StatlessMaterialStats.BOWSTRING);
    }

    /** Adds a breakable sprite requiring bowstring stats */
    public ToolSpriteBuilder addBreakableBowstring(String name) {
      return addBreakablePart(name, StatlessMaterialStats.BOWSTRING);
    }

    /** Adds a sprite requiring head stats */
    public ToolSpriteBuilder addArrowHead(String name) {
      return addPart(name, StatlessMaterialStats.ARROW_HEAD);
    }

    /** Helper to add all parts for a size */
    private void addParts(String path) {
      for (Entry<String,MaterialStatsId[]> entry : parts.entrySet()) {
        addTexture(new ResourceLocation(name.getNamespace(), "item/tool/" + path + "/" + entry.getKey()), entry.getValue())
          .allowAnimated(allowAnimated).skipVariants(skipVariants);
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
