package slimeknights.tconstruct.tools.data.material;

import net.minecraft.Util;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Map;

/** Provider for trim materials */
public class TrimMaterialProvider {
  private static final String TRIM_FORMAT = TConstruct.makeDescriptionId("trim_material", "format");

  /** Registers all providers */
  public static void register(RegistrySetBuilder builder) {
    builder.add(Registries.TRIM_MATERIAL, TrimMaterialProvider::registerTrimMaterials);
  }

  /** Registers all trim materials with the context */
  private static void registerTrimMaterials(BootstapContext<TrimMaterial> context) {
    // we set model indexes as fallbacks for when trimmed is not installed so you have at least something on vanilla models
    material(context, MaterialIds.slimesteel,     TinkerMaterials.slimesteel,     0x27C6C6, 0.8f); // diamond
    material(context, MaterialIds.amethystBronze, TinkerMaterials.amethystBronze, 0xC687BD, 1.0f); // amethyst
    material(context, MaterialIds.pigIron,        TinkerMaterials.pigIron,        0xF0A8A4, 0.5f); // copper
    material(context, MaterialIds.roseGold,       TinkerMaterials.roseGold,       0xF7CDBB, 0.1f); // quartz
    
    material(context, MaterialIds.cobalt,      TinkerMaterials.cobalt,      0x2376dd, 0.9f); // lapis
    material(context, MaterialIds.steel,       TinkerMaterials.steel,       0x959595, 0.2F); // iron
    material(context, MaterialIds.manyullyn,   TinkerMaterials.manyullyn,   0x9261cc, 1.0f); // amethyst
    material(context, MaterialIds.hepatizon,   TinkerMaterials.hepatizon,   0x60496b, 0.3f); // netherite
    material(context, MaterialIds.knightmetal, TinkerMaterials.knightmetal, 0xC4D6AE, 0.1f); // quartz
    material(context, MaterialIds.cinderslime, TinkerMaterials.cinderslime, 0xB80000, 0.4F); // redstone
    material(context, MaterialIds.queensSlime, TinkerMaterials.queensSlime, 0x236c45, 0.7f); // emerald
    
    material(context, MaterialIds.earthslime, TinkerWorld.earthGeode, 0x01cd4e, 0.7f); // emerald
    material(context, MaterialIds.skyslime,   TinkerWorld.skyGeode,   0x01cbcd, 0.8f); // diamond
    material(context, MaterialIds.ichor,      TinkerWorld.ichorGeode, 0xff970d, 0.5f); // copper
    material(context, MaterialIds.enderslime, TinkerWorld.enderGeode, 0xaf4cf6, 1.0f); // amethyst
  }

  /** Registers a trim materials using the ingot with the context */
  private static void material(BootstapContext<TrimMaterial> context, MaterialId material, MetalItemObject ingredient, int color, float modelIndex) {
    material(context, material, ingredient.getIngot(), color, modelIndex);
  }

  /** Registers a trim materials with the context */
  private static void material(BootstapContext<TrimMaterial> context, MaterialId material, ItemLike ingredient, int color, float modelIndex) {
    context.register(
      ResourceKey.create(Registries.TRIM_MATERIAL, material),
      TrimMaterial.create(material.getSuffix(), ingredient.asItem(), modelIndex,
        Component.translatable(TRIM_FORMAT, Component.translatable(Util.makeDescriptionId("material", material))).withStyle(style -> style.withColor(color)),
        Map.of())
    );
  }
}
