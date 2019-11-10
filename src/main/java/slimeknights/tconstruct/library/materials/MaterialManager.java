package slimeknights.tconstruct.library.materials;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads the material data from datapacks and provides them to whatever needs them.
 * Contains stats and which traits the material has. Traits need to have been registered beforehand.
 */
public class MaterialManager extends JsonReloadListener {

  private static final Logger LOGGER = LogManager.getLogger();

  @VisibleForTesting
  protected static final String FOLDER = "materials";
  @VisibleForTesting
  protected static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  private Map<ResourceLocation, IMaterial> materials = ImmutableMap.of();

  public MaterialManager() {
    super(GSON, FOLDER);
  }

  public Collection<IMaterial> getAllMaterials() {
    return materials.values();
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    materials = splashList.entrySet().stream()
      .map(entry -> loadMaterial(entry.getKey(), entry.getValue()))
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        material -> material)
      );
  }

  @Nullable
  private IMaterial loadMaterial(ResourceLocation resourceLocation, JsonObject jsonObject) {
    try {
      MaterialJson materialJson = GSON.fromJson(jsonObject, MaterialJson.class);

      String id = Objects.requireNonNull(materialJson.getId()).getPath();
      ResourceLocation materialId = new ResourceLocation(resourceLocation.getNamespace(), id);
      boolean isCraftable = Boolean.TRUE.equals(materialJson.getCraftable());
      Fluid fluid = loadFluid(materialJson);
      ItemStack shard = loadShardItem(materialJson);

      return new Material(materialId, fluid, isCraftable, shard);
    } catch (Exception e) {
      LOGGER.error("Could not deserialize material {}. JSON: {}", resourceLocation, jsonObject, e);
      return null;
    }
  }

  private ItemStack loadShardItem(MaterialJson materialJson) {
    ResourceLocation shardItemId = materialJson.getShardItem();
    ItemStack shard = ItemStack.EMPTY;
    if(shardItemId != null) {
      Item shardItem = ForgeRegistries.ITEMS.getValue(shardItemId);
      // air is the default, but the contract also allows null
      if(shardItem != null && shardItem != Items.AIR) {
        shard = new ItemStack(shardItem);
      } else {
        LOGGER.warn("Could not find shard item {} for material {}", shardItemId, materialJson.getId());
      }
    }
    return shard;
  }

  private Fluid loadFluid(MaterialJson materialJson) {
    ResourceLocation fluidId = materialJson.getFluid();
    Fluid fluid = Fluids.EMPTY;
    if (fluidId != null) {
      fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      if (fluid == null || fluid.getDefaultState().isEmpty()) {
        LOGGER.warn("Could not find fluid {} for material {}", fluidId, materialJson.getId());
        fluid = Fluids.EMPTY;
      }
    }
    return fluid;
  }

}
