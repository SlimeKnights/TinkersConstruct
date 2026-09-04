package slimeknights.tconstruct.library.data;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonObject;
import lombok.Setter;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMapManager;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.PotionModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.BannerModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.CompoundModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModelLoadable;
import slimeknights.tconstruct.library.client.modifiers.model.NestedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TankModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.ranged.ammo.SmashingModule;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

/** Data provider for modifier model maps */
public abstract class AbstractModifierModelMapProvider extends GenericDataProvider {
  private final Map<ResourceLocation, Builder> models = new HashMap<>();

  private final String modId;
  public AbstractModifierModelMapProvider(PackOutput output, String modId) {
    super(output, Target.RESOURCE_PACK, ModifierModelMapManager.FOLDER);
    this.modId = modId;
  }


  /* Creates a material at root */

  /** Creates a new material for the given texture */
  @Contract("!null->!null")
  @Nullable
  protected static Material material(@Nullable ResourceLocation texture) {
    return texture == null ? null : ModifierModel.blockAtlas(texture);
  }

  /** Creates a new material for the given texture */
  @SuppressWarnings("removal")
  protected Material material(String texture) {
    return ModifierModel.blockAtlas(new ResourceLocation(modId, texture));
  }

  /** Creates a tool texture path from the given path */
  @Nullable
  protected static ResourceLocation toolPath(@Nullable ResourceLocation path) {
    return  path == null ? null : path.withPrefix("item/tool/");
  }

  /** Creates a tool texture path from the given path */
  @SuppressWarnings("removal")
  @Nullable
  protected ResourceLocation toolPath(String path) {
    return new ResourceLocation(modId, "item/tool/" + path);
  }

  /** Creates a tool material from the given path */
  @Nullable
  protected static Material toolMaterial(@Nullable ResourceLocation path) {
    return  path == null ? null : ModifierModel.blockAtlas(path.withPrefix("item/tool/"));
  }

  /** Creates a tool texture for the given name */
  protected Material toolMaterial(String texture) {
    return material("item/tool/" + texture);
  }

  /** Converts the modifier into a texture suffix */
  private static String toSuffix(ModifierId modifier) {
    return modifier.getNamespace() + '_' + modifier.getPath();
  }


  /* Main methods */

  /** Adds all models */
  protected abstract void addModels();

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    addModels();
    return allOf(models.entrySet().stream()
      .filter(file -> !file.getValue().isEmpty())
      .map(file -> saveJson(output, file.getKey(), file.getValue().build())));
  }


  /* Builder */

  /** Gets the builder for the given tool */
  protected Builder tool(ResourceLocation tool, ResourceLocation base) {
    return this.models.computeIfAbsent(tool, id -> new Builder(base));
  }

  /** Gets the builder for the given tool */
  protected Builder tool(ResourceLocation tool) {
    return tool(tool, tool);
  }

  /** Gets the builder for the given tool */
  @SuppressWarnings("removal")
  protected Builder tool(String tool) {
    return tool(new ResourceLocation(modId, tool));
  }

  /** Adds the given model to the tool variant */
  protected Builder tool(ResourceLocation tool, String variant) {
    return tool(tool.withSuffix(variant), tool);
  }

  /** Adds the given model to the tool */
  protected Builder tool(IdAwareObject tool) {
    return tool(tool.getId());
  }

  /** Adds the given model to the tool variant */
  protected Builder tool(IdAwareObject tool, String variant) {
    return tool(tool.getId(), variant);
  }

  /** Adds the given model to the tool */
  protected Builder tool(Item tool) {
    return tool(Loadables.ITEM.getKey(tool));
  }

  /** Adds the given model to the tool variant */
  protected Builder tool(Item tool, String variant) {
    return tool(Loadables.ITEM.getKey(tool), variant);
  }


  /**
   * Builder for adding modifier models
   * General rules for understanding how texture paths are constructed:
   * <ul>
   *   <li>Pair of strings or resource locations before modifier ID is a prefix.</li>
   *   <li>Single string before modifier ID is a suffix.</li>
   *   <li>Part of strings or resource locations after modifier ID is the full path.</li>
   *   <li>Single string after modifier ID is the texture after the prefix.</li>
   *   <li>If the method has no modifier ID, pairs are full paths and single is texture override.</li>
   *   <li>When practical, set {@link #smallFolder} and {@link #largeFolder} over adding additional folder overloads.</li>
   *   <li>When domain is unspecified (e.g. string instead of resource location), use the tool's domain.</li>
   *   <li>Variants of tools (e.g. broken or pulling bows) share the same modifier folder as the original tool.</li>
   * </ul>
   * If an addon needs additional helpers for common use cases, you can define them as helpers that take a {@link Builder} as a parameter.
   */
  @CanIgnoreReturnValue
  protected static class Builder {
    private final Map<String, ModifierModel> constant = new LinkedHashMap<>();
    private final Map<ModifierId, ModifierModel> modifiers = new LinkedHashMap<>();
    /** Root path making up other paths */
    private ResourceLocation root;
    /** Path to small modifiers */
    @Nullable
    private ResourceLocation smallFolder;
    /** Path to large modifiers */
    @Nullable
    @Setter
    private ResourceLocation largeFolder = null;

    private Builder(ResourceLocation id) {
      this.root = id;
      this.smallFolder = id.withSuffix("/modifiers/");
    }

    /* Default paths */

    /** Prefixes the texture folders */
    public Builder prefixFolder(String prefix) {
      root = root.withPrefix(prefix);
      if (smallFolder != null) {
        smallFolder = smallFolder.withPrefix(prefix);
      }
      if (largeFolder != null) {
        largeFolder = largeFolder.withPrefix(prefix);
      }
      return this;
    }

    /** Prefixes the texture path with armor */
    public Builder armor() {
      return prefixFolder("armor/");
    }

    /** Sets the root path for small modiifers */
    public Builder smallFolder(@Nullable ResourceLocation path) {
      this.smallFolder = path;
      return this;
    }

    /** Sets the root path for large modifiers */
    public Builder smallFolder(@Nullable String path) {
      return smallFolder(localize(path));
    }

    /** Disables default small textures */
    public Builder noSmall() {
      this.smallFolder = null;
      return this;
    }

    /** Sets the root path for small modiifers */
    public Builder largeFolder(@Nullable ResourceLocation path) {
      this.largeFolder = path;
      return this;
    }

    /** Sets the root path for large modifiers */
    public Builder largeFolder(@Nullable String path) {
      return largeFolder(localize(path));
    }

    /** Disables default large textures */
    public Builder noLarge() {
      this.largeFolder = null;
      return this;
    }

    /** Sets this builder to include large modifiers using the given separator */
    public Builder large(char separator) {
      return largeFolder(root.withSuffix("/large" + separator + "modifiers/"));
    }

    /** Updates both folders at once, used commonly to relocate a batch of modifiers */
    public Builder folder(@Nullable ResourceLocation small, @Nullable ResourceLocation large) {
      return smallFolder(small).largeFolder(large);
    }

    /** Updates both folders at once, used commonly to relocate a batch of modifiers */
    public Builder folder(@Nullable String small, @Nullable String large) {
      return smallFolder(small).largeFolder(large);
    }


    /* Direct models */

    /** Merges the variable arguments */
    private static ModifierModel merge(ModifierModel model, ModifierModel... models) {
      if (models.length > 0) {
        List<ModifierModel> modelList = new ArrayList<>(models.length + 1);
        modelList.add(model);
        Collections.addAll(modelList, models);
        return new CompoundModifierModel(modelList);
      }
      return model;
    }

    /** Adds a new fixed model that always shows */
    public Builder constant(String id, ModifierModel model, ModifierModel... models) {
      ModifierModel existing = constant.putIfAbsent(id, merge(model, models));
      if (existing != null) {
        throw new IllegalArgumentException("Duplicate constant model: " + id + ", previous " + existing);
      }
      return this;
    }

    /** Adds a new modifier model that shows when the given crafted modifier is present */
    public Builder modifier(ModifierId id, ModifierModel model, ModifierModel... models) {
      ModifierModel existing = modifiers.putIfAbsent(id, merge(model, models));
      if (existing != null) {
        throw new IllegalArgumentException("Duplicate modifier: " + id + ", previous " + existing);
      }
      return this;
    }

    /**
     * Override a "lower" priority modifier map that sets a modifier.
     * <p>Example: removing modifier textures on the hook for a cast fishing rod.</p>
     */
    public Builder empty(ModifierId id) {
      ModifierModel existing = modifiers.putIfAbsent(id, merge(ModifierModel.EMPTY));
      if (existing != null) {
        throw new IllegalArgumentException("Duplicate modifier: " + id + ", previous " + existing);
      }
      return this;
    }

    /**
     * Override a "lower" priority modifier map that sets a modifier.
     * <p>Example: removing modifier textures on the hook for a cast fishing rod.</p>
     */
    public Builder empty(ModifierId... ids) {
      for (ModifierId id : ids) {
        empty(id);
      }
      return this;
    }


    /* Common models */

    /** Creates a path from the given folder and suffix */
    @Contract("!null, _ -> !null")
    @Nullable
    private static ResourceLocation toPath(@Nullable ResourceLocation folder, String suffix) {
      return folder == null ? null : folder.withSuffix(suffix);
    }

    /** Creates a path from the given folder and suffix */
    @Contract("!null, _ -> !null")
    @Nullable
    private static Material toMaterial(@Nullable ResourceLocation folder, String suffix) {
      if (folder == null) {
        return null;
      }
      return toolMaterial(toPath(folder, suffix));
    }

    /** Converts the path to a folder, using null if empty */
    @Contract("!null->!null")
    @Nullable
    private ResourceLocation localize(@Nullable String path) {
      return path == null ? null : root.withPath(path);
    }

    /** Creates a modifier with a specific path. */
    public Builder luminosity(int light, ModifierId modifier, @Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return modifier(modifier, new NormalModifierModel(toolMaterial(smallPath), toolMaterial(largePath), -1, light));
    }

    /** Creates a modifier with a specific path. */
    public Builder luminosity(int light, ModifierId modifier, @Nullable String smallPath, @Nullable String largePath) {
      return luminosity(light, modifier, localize(smallPath), localize(largePath));
    }

    /** Creates a modifier using the standard folder and the given texture name */
    public Builder luminosity(int light, ModifierId modifier, String texture) {
      return luminosity(light, modifier, toPath(smallFolder, texture), toPath(largeFolder, texture));
    }

    /** Creates a modifier using the standard folder and the given texture name */
    public Builder basic(ModifierId modifier, String texture) {
      return luminosity(0, modifier, texture);
    }


    /* Modifier lists */

    /** Adds a list of modifiers using the full ID as suffix. */
    public Builder luminosity(int light, ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        luminosity(light, modifier, toSuffix(modifier));
      }
      return this;
    }

    /** Adds a list of modifiers using the full ID as suffix. */
    public Builder basic(ModifierId... modifiers) {
      return luminosity(0, modifiers);
    }

    /** Adds a list of modifiers using the full ID plus the given string as suffix. */
    public Builder luminosity(int light, String suffix, ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        luminosity(light, modifier, toSuffix(modifier) + suffix);
      }
      return this;
    }

    /** Adds a list of modifiers using the full ID plus the given string as suffix. */
    public Builder basic(String suffix, ModifierId... modifiers) {
      return luminosity(0, suffix, modifiers);
    }

    /** Adds a list of modifiers using the full ID as suffix. */
    public Builder compact(int light, ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        luminosity(light, modifier, modifier.getPath());
      }
      return this;
    }

    /** Adds a list of modifiers using the full ID as suffix. */
    public Builder compact(ModifierId... modifiers) {
      return compact(0, modifiers);
    }


    /* Nested models */

    /** Adds a new modifier model that shows when the given crafted modifier is present */
    public Builder trait(String key, ModifierId id, ModifierModel model, ModifierModel... models) {
      return constant(key, new NestedModifierModel.Trait(id, merge(model, models)));
    }

    /** Adds a new modifier model that shows when the given crafted modifier is present */
    public Builder trait(ModifierId id, ModifierModel model, ModifierModel... models) {
      return trait(id.getPath(), id, model, models);
    }

    /** Adds a new modifier model that shows when the given crafted modifier is present */
    public Builder first(String key, ModifierId id, ModifierModel model, ModifierModel... models) {
      return constant(key, new NestedModifierModel.Crafted(id, merge(model, models)));
    }

    /** Adds a new modifier model that shows when the given crafted modifier is present */
    public Builder first(ModifierId id, ModifierModel model, ModifierModel... models) {
      return first('_' + id.getPath(), id, model, models);
    }


    /* Fluid */

    /** Creates a model for a constant tank following standard folder vs path rules */
    public Builder constantFluid(@Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      String partial = "_partial";
      String full = "_full";
      return constant("fluid", new TankModifierModel(
        toMaterial(smallPath, partial), toMaterial(smallPath, full),
        toMaterial(largePath, partial), toMaterial(largePath, full),
        0));
    }

    /** Creates a model for a constant tank following standard folder vs path rules */
    public Builder constantFluid(String smallPath, String largePath) {
      return constantFluid(localize(smallPath), localize(largePath));
    }

    /** Creates a model for a constant tank on a tool. Uses the root path instead of the modifiers folder. */
    public Builder constantFluid() {
      return constantFluid(
        smallFolder != null ? toPath(root, "/fluid") : null,
        largeFolder != null ? toPath(root, "/fluid_large") : null
      );
    }


    /* Specific fluid modifiers */

    /** Adds models for a fluid, with a constant texture and a fluid texture */
    public Builder fluid(ModifierId modifier, @Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return modifier(modifier,
        new FluidModifierModel(toMaterial(smallPath, "_full"), toMaterial(largePath, "_full")),
        new NormalModifierModel(toolMaterial(smallPath), toolMaterial(largePath))
      );
    }

    /** Adds models for a fluid, with a constant texture and a fluid texture */
    public Builder fluid(ModifierId modifier, @Nullable String smallPath, @Nullable String largePath) {
      return fluid(modifier, localize(smallPath), localize(largePath));
    }

    /** Adds models for a fluid, with a constant texture and a fluid texture */
    public Builder fluid(ModifierId modifier, String suffix) {
      return fluid(modifier, toPath(smallFolder, suffix), toPath(largeFolder, suffix));
    }

    /** Adds models for a fluid, with a constant texture and a fluid texture using the modifier to set the path */
    public Builder fluid(ModifierId modifier) {
      return fluid(modifier, toSuffix(modifier));
    }

    /* Tanks - add in a partial texture */

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(ModifierId modifier, @Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      String partial = "_partial";
      String full = "_full";
      return modifier(modifier,
        new TankModifierModel(
          toMaterial(smallPath, partial), toMaterial(smallPath, full),
          toMaterial(largePath, partial), toMaterial(largePath, full),
          0),
        new NormalModifierModel(toolMaterial(smallPath), toolMaterial(largePath))
      );
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(ModifierId modifier, @Nullable String smallPath, @Nullable String largePath) {
      return tank(modifier, localize(smallPath), localize(largePath));
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(ModifierId modifier, String suffix) {
      return tank(modifier, toPath(smallFolder, suffix), toPath(largeFolder, suffix));
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(ModifierId modifier) {
      return tank(modifier, toSuffix(modifier));
    }

    /* Standard tank */

    /** Adds models for the standard tank */
    public Builder tank(@Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return tank(ModifierIds.tank, smallPath, largePath);
    }

    /** Adds models for the standard tank */
    public Builder tank(@Nullable String smallPath, @Nullable String largePath) {
      return tank(ModifierIds.tank, smallPath, largePath);
    }

    /** Adds models for the standard tank */
    public Builder tank(String suffix) {
      return tank(ModifierIds.tank, suffix);
    }

    /** Adds models for the standard tank */
    public Builder tank() {
      return tank(ModifierIds.tank);
    }


    /* Ammo */

    /** Creates a model for smashing on a small tool. */
    public Builder smashing(ResourceLocation path) {
      return trait(ModifierIds.smashing, new FluidModifierModel(toolMaterial(path), null, SmashingModule.TANK_HELPER));
    }

    /** Creates a model for smashing on a small tool */
    public Builder smashing() {
      assert smallFolder != null;
      return smashing(toPath(smallFolder, "smashing"));
    }

    /** Creates a model for tipping a small tool */
    public Builder tipped(@Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return trait("__tipped", ModifierIds.tipped, new PotionModifierModel(toolMaterial(smallPath), toolMaterial(largePath)));
    }

    /** Creates a model for tipping a small tool */
    public Builder tipped(@Nullable String smallPath, @Nullable String largePath) {
      return tipped(localize(smallPath), localize(largePath));
    }

    /** Creates a model for tipping a small tool */
    public Builder tipped() {
      String suffix = "tipped";
      return tipped(toPath(smallFolder, suffix), toPath(largeFolder, suffix));
    }

    /** Removes tipped from an upper level */
    public Builder emptyTipped() {
      return constant("__tipped", ModifierModel.EMPTY);
    }


    /* Dyed */

    /** Adds a model for dyed */
    public Builder dyed(ModifierModel model, ModifierModel... models) {
      // whatever is dyed we usually want first next to materials so want extra early
      return first("__dyed", TinkerModifiers.dyed.getId(), merge(model, models));
    }

    /** Adds a model for dyed */
    public Builder dyed(@Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return dyed(new DyedModifierModel(toolMaterial(smallPath), toolMaterial(largePath)));
    }

    /** Adds a model for dyed */
    public Builder dyed(@Nullable String smallPath, @Nullable String largePath) {
      return dyed(localize(smallPath), localize(largePath));
    }

    /** Adds a model for dyed */
    public Builder dyed() {
      String suffix = "dyed";
      return dyed(toPath(smallFolder, suffix), toPath(largeFolder, suffix));
    }


    /* Trim */

    /** Adds the trim model to the tool */
    public Builder trim(ArmorItem.Type type) {
      return first(TinkerModifiers.trim.getId(), TrimModifierModel.Armor.values()[type.ordinal()]);
    }

    /** Creates a custom trim in the given folder, using the given name for the large variant. */
    public Builder customTrim(ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      return first(TinkerModifiers.trim.getId(), new TrimModifierModel.Custom(toolPath(smallPath), toolPath(largePath)));
    }

    /** Creates a custom trim in the default folder, using the given name for the large variant */
    public Builder customTrim(String smallPath, @Nullable String largePath) {
      return customTrim(localize(smallPath), localize(largePath));
    }


    /* Embellishment */

    /** Adds the embellishment model to the tool using the given textures */
    public Builder embellishment(@Nullable ResourceLocation smallPath, @Nullable ResourceLocation largePath) {
      ModifierId embellishment = TinkerModifiers.embellishment.getId();
      // whatever is embellish is basically a part so want extra early
      return first("__embellishment", embellishment, new MaterialModifierModel.PersistentData(toolMaterial(smallPath), toolMaterial(largePath)))
        // set the embellishment to empty so its skipped in the legacy system. TODO 1.21: remove this
        .empty(embellishment);
    }

    /** Adds the embellishment model to the tool using the given textures */
    public Builder embellishment(@Nullable String smallPath, @Nullable String largePath) {
      return embellishment(localize(smallPath), localize(largePath));
    }

    /** Adds the embellishment model to the tool from the modifiers folder */
    public Builder embellishment() {
      String suffix = toSuffix(TinkerModifiers.embellishment.getId());
      return embellishment(toPath(smallFolder, suffix), toPath(largeFolder, suffix));
    }


    /* Banner */

    /** Adds the banner model to the tool */
    public Builder banner(@Nullable ResourceLocation smallPrefix, @Nullable ResourceLocation largePrefix) {
      return first(TinkerModifiers.banner.getId(), new BannerModifierModel(toolPath(smallPrefix), toolPath(largePrefix)));
    }

    /** Adds the banner model to the tool */
    public Builder banner(@Nullable String smallPrefix, @Nullable String largePrefix) {
      return banner(localize(smallPrefix), localize(largePrefix));
    }


    /* Building */

    /** Checks if this file has anything */
    private boolean isEmpty() {
      return constant.isEmpty() && modifiers.isEmpty();
    }

    /** Builds the final JSON */
    private JsonObject build() {
      JsonObject json = new JsonObject();
      if (!this.constant.isEmpty()) {
        JsonObject constant = new JsonObject();
        for (Entry<String, ModifierModel> entry : this.constant.entrySet()) {
          constant.add(entry.getKey(), ModifierModelLoadable.COMPACT.serialize(entry.getValue()));
        }
        json.add("constant", constant);
      }
      if (!this.modifiers.isEmpty()) {
        JsonObject modifiers = new JsonObject();
        for (Entry<ModifierId, ModifierModel> entry : this.modifiers.entrySet()) {
          modifiers.add(entry.getKey().toString(), ModifierModelLoadable.COMPACT.serialize(entry.getValue()));
        }
        json.add("modifiers", modifiers);
      }
      return json;
    }
  }
}
