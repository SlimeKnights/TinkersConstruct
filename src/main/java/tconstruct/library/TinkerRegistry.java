package tconstruct.library;

import net.minecraftforge.fml.common.Loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.tools.materials.IMaterialStats;
import tconstruct.library.tools.traits.IMaterialTrait;

public final class TinkerRegistry {

  private TinkerRegistry() {
  }

  /* MATERIALS */

  // Identifier to Material mapping. Hashmap so we can look it up directly without iterating
  private static final Map<String, ToolMaterial> toolMaterials = new HashMap<>();
  // traceability information who registered what. Used to find errors.
  private static final Map<String, String> materialRegisteredByMod = new HashMap<>();
  private static final Map<String, Map<Class<? extends IMaterialStats>, String>>
      statRegisteredByMod = new HashMap<>();
  private static final Map<String, Map<Class<? extends IMaterialTrait>, String>>
      traitRegisteredByMod = new HashMap<>();

  public static void addToolMaterial(ToolMaterial material) {
    // duplicate material
    if (toolMaterials.containsKey(material.identifier)) {
      String registeredBy = materialRegisteredByMod.get(material.identifier);
      error(String.format(
          "Could not register Material: \"%s\" was already registered by %s", material.identifier,
          registeredBy));
      return;
    }

    // register material
    toolMaterials.put(material.identifier, material);
    String activeMod = Loader.instance().activeModContainer().getModId();
    putMaterialTrace(material.identifier, activeMod);
  }

  public static ToolMaterial getToolMaterial(String identifier) {
    return toolMaterials.get(identifier);
  }

  public static Collection<ToolMaterial> getAllToolMaterials() {
    return toolMaterials.values();
  }

  /* MATERIAL TRAITS AND STATS */
  public static void addMaterialStats(String identifier, IMaterialStats stats) {
    if (!toolMaterials.containsKey(identifier)) {
      error(String.format("Could not add Stats to \"%s\": Unknown Material", identifier));
      return;
    }

    ToolMaterial material = toolMaterials.get(identifier);
    // duplicate stats
    if (material.getStats(stats.getClass()) != null) {
      String registeredBy = "Unknown";
      Map<Class<? extends IMaterialStats>, String>
          matReg =
          statRegisteredByMod.get(identifier);
      if (matReg != null) {
        registeredBy = matReg.get(stats.getClass());
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

  public static void addMaterialTrait(String identifier, IMaterialTrait trait) {
    if (!toolMaterials.containsKey(identifier)) {
      error(String.format("Could not add Trait \"%s\" to \"%s\": Unknown Material", trait.getIdentifier(), identifier));
      return;
    }

    ToolMaterial material = toolMaterials.get(identifier);
    // duplicate traits
    if (material.hasTrait(trait.getClass()) || material.hasTrait(trait.getIdentifier())) {
      String registeredBy = "Unknown";
      Map<Class<? extends IMaterialTrait>, String>
          matReg = traitRegisteredByMod.get(identifier);
      if (matReg != null) {
        registeredBy = matReg.get(trait.getClass());
      }

      error(String.format(
          "Could not add Trait to \"%s\": Trait \"%s\" was already registered by %s",
          identifier, trait.getIdentifier(), registeredBy));
      return;
    }

    material.addTrait(trait);

    String activeMod = Loader.instance().activeModContainer().getModId();
    putTraitTrace(identifier, trait, activeMod);
  }

  /* Traceability info */
  static void putMaterialTrace(String materialIdentifier, String trace) {
    String activeMod = Loader.instance().activeModContainer().getModId();
    materialRegisteredByMod.put(materialIdentifier, activeMod);
  }

  static void putStatTrace(String materialIdentifier, IMaterialStats stats, String trace) {
    if (!statRegisteredByMod.containsKey(materialIdentifier)) {
      statRegisteredByMod.put(materialIdentifier, new HashMap<>());
    }
    statRegisteredByMod.get(materialIdentifier).put(stats.getClass(), trace);
  }

  static void putTraitTrace(String materialIdentifier, IMaterialTrait trait, String trace) {
    if (!traitRegisteredByMod.containsKey(materialIdentifier)) {
      traitRegisteredByMod.put(materialIdentifier, new HashMap<>());
    }
    traitRegisteredByMod.get(materialIdentifier).put(trait.getClass(), trace);
  }

  private static void error(String message) {
    throw new TinkerAPIException(message);
  }
}
