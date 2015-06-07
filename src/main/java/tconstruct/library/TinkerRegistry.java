package tconstruct.library;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TLinkedHashSet;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.materials.IMaterialStats;
import tconstruct.library.tinkering.modifiers.IModifier;
import tconstruct.library.tinkering.traits.ITrait;

public final class TinkerRegistry {

  // the logger for the library
  public static final Logger log = Util.getLogger("API");

  private TinkerRegistry() {
  }


  /***********************************************************
   * MATERIALS                                               *
   ***********************************************************/

  // Identifier to Material mapping. Hashmap so we can look it up directly without iterating
  private static final Map<String, Material> materials = new THashMap<>();
  private static final Map<String, ITrait> traits = new THashMap<>();
  // traceability information who registered what. Used to find errors.
  private static final Map<String, String> materialRegisteredByMod = new THashMap<>();
  private static final Map<String, Map<String, String>> statRegisteredByMod = new THashMap<>();
  private static final Map<String, Map<String, String>> traitRegisteredByMod = new THashMap<>();

  public static void addMaterial(Material material, IMaterialStats stats, ITrait trait) {
    addMaterial(material, stats);
    addMaterialTrait(material.identifier, trait);
  }

  public static void addMaterial(Material material, IMaterialStats stats) {
    addMaterial(material);
    addMaterialStats(material.identifier, stats);
  }

  public static void addMaterial(Material material) {
    // duplicate material
    if (materials.containsKey(material.identifier)) {
      String registeredBy = materialRegisteredByMod.get(material.identifier);
      error(String.format(
          "Could not register Material \"%s\": It was already registered by %s",
          material.identifier,
          registeredBy));
      return;
    }

    // duplicate metadata mapping
    for (Material mat : materials.values()) {
      if (material.metadata == mat.metadata) {
        String registeredBy = materialRegisteredByMod.get(mat.identifier);
        error(String.format(
            "Could not register Material \"%s\": Metadata Mapping \"%d\" is already in use for Material \"%s\" from Mod %s",
            material.identifier, mat.metadata, mat.identifier, registeredBy));
        return;
      }
    }

    // register material
    materials.put(material.identifier, material);
    String activeMod = Loader.instance().activeModContainer().getModId();
    putMaterialTrace(material.identifier, activeMod);
  }

  public static Material getMaterial(String identifier) {
    return materials.containsKey(identifier) ? materials.get(identifier) : Material.UNKNOWN;
  }

  public static Collection<Material> getAllMaterials() {
    return materials.values();
  }


  /***********************************************************
   * TRAITS & STATS                                          *
   ***********************************************************/

  public static void addTrait(ITrait trait) {
    // Trait might already have been registered since modifiers and materials share traits
    if(traits.containsKey(trait.getIdentifier()))
      return;

    String activeMod = Loader.instance().activeModContainer().getModId();
    putTraitTrace(trait.getIdentifier(), trait, activeMod);
  }

  public static void addMaterialStats(String materialIdentifier, IMaterialStats stats) {
    if (!materials.containsKey(materialIdentifier)) {
      error(String.format("Could not add Stats \"%s\" to \"%s\": Unknown Material", stats.getMaterialType(), materialIdentifier));
      return;
    }

    Material material = materials.get(materialIdentifier);
    addMaterialStats(material, stats);
  }

  public static void addMaterialStats(Material material, IMaterialStats stats) {
    if (material == null) {
      error(String.format("Could not add Stats \"%s\": Material is null", stats.getMaterialType()));
      return;
    }

    String identifier = material.identifier;
    // duplicate stats
    if (material.getStats(stats.getMaterialType()) != null) {
      String registeredBy = "Unknown";
      Map<String, String> matReg = statRegisteredByMod.get(identifier);
      if (matReg != null) {
        registeredBy = matReg.get(stats.getMaterialType());
      }

      error(String.format(
          "Could not add Stats to \"%s\": Stats of type \"%s\" were already registered by %s",
          identifier, stats.getMaterialType(), registeredBy));
      return;
    }

    material.addStats(stats);

    String activeMod = Loader.instance().activeModContainer().getModId();
    putStatTrace(identifier, stats, activeMod);
  }

  public static void addMaterialTrait(String materialIdentifier, ITrait trait) {
    if (!materials.containsKey(materialIdentifier)) {
      error(String.format("Could not add Trait \"%s\" to \"%s\": Unknown Material",
                          trait.getIdentifier(), materialIdentifier));
      return;
    }

    Material material = materials.get(materialIdentifier);
    addMaterialTrait(material, trait);
  }

  public static void addMaterialTrait(Material material, ITrait trait) {
    if (material == null) {
      error(String.format("Could not add Trait \"%s\": Material is null", trait.getIdentifier()));
      return;
    }

    String identifier = material.identifier;
    // duplicate traits
    if (material.hasTrait(trait.getIdentifier())) {
      String registeredBy = "Unknown";
      Map<String, String> matReg = traitRegisteredByMod.get(identifier);
      if (matReg != null) {
        registeredBy = matReg.get(trait.getIdentifier());
      }

      error(String.format(
          "Could not add Trait to \"%s\": Trait \"%s\" was already registered by %s",
          identifier, trait.getIdentifier(), registeredBy));
      return;
    }

    addTrait(trait);
    material.addTrait(trait);
  }

  /***********************************************************
   * TOOLS & WEAPONS                                         *
   ***********************************************************/

  /**This set contains all known tools */
  public static final Set<Item> tools = new TLinkedHashSet<>();

  public static void addTool(Item tool) {
    tools.add(tool);
  }


  /***********************************************************
   *  MODIFIERS                                              *
   ***********************************************************/

  public static final Map<String, IModifier> modifiers = new THashMap<>();

  public static void registerModifier(IModifier modifier) {
    modifiers.put(modifier.getIdentifier(), modifier);
  }

  public static IModifier getModifier(String identifier) {
    return modifiers.get(identifier);
  }

  public static Collection<IModifier> getAllModifiers() {
    return modifiers.values();
  }

  /***********************************************************
   * Traceability & Internal stuff                           *
   ***********************************************************/

  static void putMaterialTrace(String materialIdentifier, String trace) {
    String activeMod = Loader.instance().activeModContainer().getModId();
    materialRegisteredByMod.put(materialIdentifier, activeMod);
  }

  static void putStatTrace(String materialIdentifier, IMaterialStats stats, String trace) {
    if (!statRegisteredByMod.containsKey(materialIdentifier)) {
      statRegisteredByMod.put(materialIdentifier, new HashMap<String, String>());
    }
    statRegisteredByMod.get(materialIdentifier).put(stats.getMaterialType(), trace);
  }

  static void putTraitTrace(String materialIdentifier, ITrait trait, String trace) {
    if (!traitRegisteredByMod.containsKey(materialIdentifier)) {
      traitRegisteredByMod.put(materialIdentifier, new HashMap<String, String>());
    }
    traitRegisteredByMod.get(materialIdentifier).put(trait.getIdentifier(), trace);
  }

  private static void error(String message) {
    throw new TinkerAPIException(message);
  }
}
