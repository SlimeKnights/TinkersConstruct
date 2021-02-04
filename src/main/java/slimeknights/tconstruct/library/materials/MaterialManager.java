package slimeknights.tconstruct.library.materials;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.json.MaterialJson;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.network.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.utils.SyncingJsonReloadListener;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads the material data from datapacks and provides them to whatever needs them.
 * Contains only the very basic material information, craftability, traits, but no stats.
 * See {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager} for stats.
 * <p>
 * The location inside datapacks is "materials".
 * So if your mods name is "foobar", the location for your mods materials is "data/foobar/materials".
 */
@Log4j2
public class MaterialManager extends SyncingJsonReloadListener {

  public static final String FOLDER = "materials/definition";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(ModifierEntry.class, ModifierEntry.SERIALIZER)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  private Map<MaterialId, IMaterial> materials = ImmutableMap.of();
  private Map<Fluid, IMaterial> fluidLookup = ImmutableMap.of();

  public MaterialManager() {
    this(TinkerNetwork.getInstance());
  }

  @VisibleForTesting
  protected MaterialManager(TinkerNetwork tinkerNetwork) {
    super(tinkerNetwork, GSON, FOLDER);
  }

  /**
   * Gets a collection of all loaded materials
   * @return  All loaded materials
   */
  public Collection<IMaterial> getAllMaterials() {
    return materials.values();
  }

  /**
   * Gets a material based on its ID
   * @param materialId  Material ID
   * @return  Optional of material, empty if missing
   */
  public Optional<IMaterial> getMaterial(MaterialId materialId) {
    return Optional.ofNullable(materials.get(materialId));
  }

  /**
   * Gets a material based on a fluid
   * @param fluid  Fluid to check
   * @return  Optional of material, empty if fluid does not match any material
   */
  public Optional<IMaterial> getMaterial(Fluid fluid) {
    return Optional.ofNullable(fluidLookup.get(fluid));
  }

  /**
   * Recreates the fluid lookup using the new materials map
   */
  private void reloadFluidLookup() {
    this.fluidLookup = this.materials.values().stream()
                                     .filter((mat) -> mat.getFluid() != Fluids.EMPTY)
                                     .collect(Collectors.toMap(IMaterial::getFluid, Function.identity()));
  }

  /**
   * Updates the material list from the server.list. Should only be called client side
   * @param materialList  Server material list
   */
  public void updateMaterialsFromServer(Collection<IMaterial> materialList) {
    this.materials = materialList.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        Function.identity())
      );
    reloadFluidLookup();
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    this.materials = splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .map(entry -> loadMaterial(entry.getKey(), entry.getValue().getAsJsonObject()))
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        material -> material)
      );
    reloadFluidLookup();
    
    log.debug("Loaded materials: {}", Util.toIndentedStringList(materials.keySet()));
    log.info("{} materials loaded", materials.size());
  }

  @Override
  protected Object getUpdatePacket() {
    return new UpdateMaterialsPacket(materials.values());
  }

  @Nullable
  private IMaterial loadMaterial(ResourceLocation materialId, JsonObject jsonObject) {
    try {
      MaterialJson materialJson = GSON.fromJson(jsonObject, MaterialJson.class);

      if (materialJson.getCraftable() == null) {
        throw TinkerJSONException.materialJsonWithoutCraftingInformation(materialId);
      }

      boolean isCraftable = Boolean.TRUE.equals(materialJson.getCraftable());
      int temperature = 0;
      int fluidPerUnit = 0;
      Fluid fluid = loadFluid(materialId, materialJson);
      if (fluid != Fluids.EMPTY) {
        fluidPerUnit = Optional.ofNullable(materialJson.getFluidPerUnit()).orElse(0);
        temperature = Optional.ofNullable(materialJson.getTemperature()).filter(n -> n >= 0).orElse(0);
      }

      // parse color from string
      Color color = Optional.ofNullable(materialJson.getTextColor())
                            .filter(str -> !str.isEmpty())
                            .map(Color::fromHex)
                            .orElse(Material.WHITE);

      // parse trait
      ModifierEntry trait = materialJson.getTrait();
      return new Material(materialId, fluid, fluidPerUnit, isCraftable, color, temperature, trait);
    } catch (Exception e) {
      log.error("Could not deserialize material {}. JSON: {}", materialId, jsonObject, e);
      return null;
    }
  }

  /**
   * Find a fluid for the material JSON
   * @param materialId    Material ID
   * @param materialJson  Material JSON
   * @return  Fluid, or Fluids.EMPTY if none
   */
  private Fluid loadFluid(ResourceLocation materialId, MaterialJson materialJson) {
    ResourceLocation fluidId = materialJson.getFluid();
    Fluid fluid = Fluids.EMPTY;
    if (fluidId != null) {
      fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      if (fluid == null || fluid.getDefaultState().isEmpty()) {
        log.warn("Could not find fluid {} for material {}", fluidId, materialId);
        fluid = Fluids.EMPTY;
      }
    }
    return fluid;
  }
}
