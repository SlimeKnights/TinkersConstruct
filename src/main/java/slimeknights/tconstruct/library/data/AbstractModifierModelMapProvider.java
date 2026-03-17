package slimeknights.tconstruct.library.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.library.client.modifiers.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMapManager;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.PotionModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.BannerModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.CompoundModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TankModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TraitModel;
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

  /** Argument for {@code largeSeparator} to disable large textures entirely. */
  protected static final char SMALL = '\0';

  private final String modId;
  public AbstractModifierModelMapProvider(PackOutput output, String modId) {
    super(output, Target.RESOURCE_PACK, ModifierModelMapManager.FOLDER);
    this.modId = modId;
  }

  /** Creates a new material for the given texture */
  protected Material material(ResourceLocation texture) {
    return ModifierModel.blockAtlas(texture);
  }

  /** Creates a new material for the given texture */
  @SuppressWarnings("removal")
  protected Material material(String texture) {
    return ModifierModel.blockAtlas(new ResourceLocation(modId, texture));
  }

  /** Creates a tool texture for the given name */
  protected Material toolMaterial(String texture) {
    return material("item/tool/" + texture);
  }

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


  /** Converts the modifier into a texture suffix */
  protected static String suffix(ModifierId modifier) {
    return modifier.getNamespace() + '_' + modifier.getPath();
  }

  /** Builder for adding modifier models */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  protected class Builder {
    private final Map<String, ModifierModel> constant = new LinkedHashMap<>();
    private final Map<ModifierId, ModifierModel> modifiers = new LinkedHashMap<>();
    private final ResourceLocation id;

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


    /* Common models */

    /** Gets the large modifiers folder */
    @Nullable
    private static String largeFolder(String folder, char largeSeparator) {
      return largeSeparator != SMALL ? folder + "/large" + largeSeparator + "modifiers" : null;
    }

    /** Adds a basic modifier in the given folder */
    public Builder luminosity(int light, ModifierId modifier, String texture, @Nullable String largeTexture) {
      return modifier(modifier, new NormalModifierModel(toolMaterial(texture), largeTexture != null ? toolMaterial(largeTexture) : null, -1, light));
    }

    /** Adds a basic modifier in the given folder */
    public Builder luminosity(int light, String folder, @Nullable String largeFolder, ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        String suffix = '/' + suffix(modifier);
        luminosity(light, modifier, folder + suffix, largeFolder != null ? largeFolder + suffix : null);
      }
      return this;
    }

    /** Adds a basic modifier in the default folder */
    public Builder luminosity(int light, char largeSeparator, ModifierId... modifiers) {
      String path = id.getPath();
      return luminosity(light, path + "/modifiers", largeFolder(path, largeSeparator), modifiers);
    }

    /** Adds a basic modifier in the given folder */
    public Builder basic(ModifierId modifier, String texture, @Nullable String largeTexture) {
      return luminosity(0, modifier, texture, largeTexture);
    }

    /** Adds a basic modifier in the given folder */
    public Builder basic(String folder, @Nullable String largeFolder, ModifierId... modifiers) {
      return luminosity(0, folder, largeFolder, modifiers);
    }

    /** Adds a basic modifier in the default folder */
    public Builder basic(char largeSeparator, ModifierId... modifiers) {
      return luminosity(0, largeSeparator, modifiers);
    }

    /** Adds a basic modifier in the given folder using just the modifier path as the name */
    public Builder compact(String folder, ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        return basic(modifier, folder + '/' + modifier.getPath(), null);
      }
      return this;
    }

    /** Adds a basic modifier in the default folder */
    public Builder compact(ModifierId... modifiers) {
      return compact(id.getPath() + "/modifiers", modifiers);
    }

    /** Creates a model for a constant tank on a small tool */
    public Builder fluid(String folder) {
      return constant("fluid", new TankModifierModel(toolMaterial(folder + "/fluid_partial"), toolMaterial(folder + "/fluid_full"), null, null, 0));
    }

    /** Creates a model for a constant tank on a small tool */
    public Builder fluid() {
      return fluid(id.getPath());
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder fluid(String folder, @Nullable String largeFolder, ModifierId modifier) {
      String name = suffix(modifier);
      Material tank = toolMaterial(folder + '/' + name);
      Material full = toolMaterial(folder + '/' + name + "_full");
      if (largeFolder != null) {
        return modifier(modifier,
          new FluidModifierModel(full, toolMaterial(largeFolder + '/' + name + "_full")),
          new NormalModifierModel(tank, toolMaterial(largeFolder + '/' + name)));
      } else {
        return modifier(modifier, new FluidModifierModel(full, null), new NormalModifierModel(tank, null));
      }
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder fluid(ModifierId modifier, char largeSeparator) {
      String path = id.getPath();
      return fluid(path + "/modifiers", largeFolder(path, largeSeparator), modifier);
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(String folder, @Nullable String largeFolder) {
      String name = suffix(ModifierIds.tank);
      Material tank = toolMaterial(folder + '/' + name);
      Material partial = toolMaterial(folder + '/' + name + "_partial");
      Material full = toolMaterial(folder + '/' + name + "_full");
      if (largeFolder != null) {
        return modifier(ModifierIds.tank,
          new TankModifierModel(partial, full, toolMaterial(largeFolder + '/' + name + "_partial"), toolMaterial(largeFolder + '/' + name + "_full"), 0),
          new NormalModifierModel(tank, toolMaterial(largeFolder + '/' + name)));
      } else {
        return modifier(ModifierIds.tank, new TankModifierModel(partial, full, null, null, 0), new NormalModifierModel(tank, null));
      }
    }

    /** Adds models for a tank, with a partial and full state */
    public Builder tank(char largeSeparator) {
      String path = id.getPath();
      return tank(path + "/modifiers", largeFolder(path, largeSeparator));
    }

    /** Creates a model for smashing on a small tool */
    public Builder smashing(String texture) {
      return constant("smashing", new TraitModel(ModifierIds.smashing, new FluidModifierModel(toolMaterial(texture), null, SmashingModule.TANK_HELPER)));
    }

    /** Creates a model for tipping a small tool */
    public Builder tipped(String texture) {
      return constant("tipped", new TraitModel(ModifierIds.tipped, new PotionModifierModel(toolMaterial(texture), null)));
    }

    /* Cosmetic */

    /** Adds the trim model to the tool */
    public Builder trim(ArmorItem.Type type) {
      return modifier(TinkerModifiers.trim.getId(), TrimModifierModel.Armor.values()[type.ordinal()]);
    }

    /** Creates a custom trim in the given folder, using the given name for the large variant. */
    public Builder customTrim(String folder, @Nullable String largeTexture) {
      return modifier(TinkerModifiers.trim.getId(), new TrimModifierModel.Custom(toolMaterial(folder + "/trim").texture(), largeTexture != null ? toolMaterial(folder + '/' + largeTexture).texture() : null));
    }

    /** Creates a custom trim in the default folder, using the given name for the large variant */
    public Builder customTrim(@Nullable String largeTexture) {
      return customTrim(id.getPath(), largeTexture);
    }

    /** Adds the embellishment model to the tool */
    public Builder embellishment(String folder, @Nullable String largeFolder) {
      ModifierId embellishment = TinkerModifiers.embellishment.getId();
      String name = '/' + suffix(embellishment);
      return modifier(embellishment, new MaterialModifierModel(toolMaterial(folder + name), largeFolder != null ? toolMaterial(largeFolder + name) : null));
    }

    /** Adds the embellishment model to the tool */
    public Builder embellishment(char largeSeparator) {
      String path = id.getPath();
      return embellishment(path + "/modifiers", largeFolder(path, largeSeparator));
    }

    /** Adds the banner model to the tool */
    public Builder banner(@Nullable String smallPrefix, @Nullable String largePrefix) {
      return modifier(TinkerModifiers.banner.getId(), new BannerModifierModel(
        smallPrefix != null ? toolMaterial(smallPrefix).texture() : null,
        largePrefix != null ? toolMaterial(largePrefix).texture() : null
      ));
    }


    /* Building */

    /** Checks if this file has anything */
    private boolean isEmpty() {
      return constant.isEmpty() && modifiers.isEmpty();
    }

    /** Serializes the model to JSON */
    private static JsonElement serialize(ModifierModel model) {
      // serialize compound as an array
      if (model instanceof CompoundModifierModel compound) {
        return CompoundModifierModel.LIST_LOADABLE.serialize(compound.models());
      }
      if (model.getLoader() == NormalModifierModel.LOADER) {
        NormalModifierModel basic = (NormalModifierModel) model;
        Material small = basic.small();
        if (small != null && basic.large() == null && basic.luminosity() == 0 && basic.color() == -1) {
          return new JsonPrimitive(small.texture().toString());
        }
        // type of objects is optional as long as its basic, leave it out so large tools are not as big
        return NormalModifierModel.LOADER.serialize(basic);
      }
      return ModifierModel.LOADER.serialize(model);
    }

    /** Builds the final JSON */
    private JsonObject build() {
      JsonObject json = new JsonObject();
      if (!this.constant.isEmpty()) {
        JsonObject constant = new JsonObject();
        for (Entry<String, ModifierModel> entry : this.constant.entrySet()) {
          constant.add(entry.getKey(), serialize(entry.getValue()));
        }
        json.add("constant", constant);
      }
      if (!this.modifiers.isEmpty()) {
        JsonObject modifiers = new JsonObject();
        for (Entry<ModifierId, ModifierModel> entry : this.modifiers.entrySet()) {
          modifiers.add(entry.getKey().toString(), serialize(entry.getValue()));
        }
        json.add("modifiers", modifiers);
      }
      return json;
    }
  }
}
