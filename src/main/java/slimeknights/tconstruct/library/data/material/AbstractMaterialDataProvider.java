package slimeknights.tconstruct.library.data.material;

import lombok.Data;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.text.Color;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extendable material provider, useful for addons
 */
public abstract class AbstractMaterialDataProvider extends GenericDataProvider {
  /** General purpose materials */
  public static final int ORDER_GENERAL = 0;
  /** Materials primarily used for harvest */
  public static final int ORDER_HARVEST = 1;
  /** Materials primarily used for weapons */
  public static final int ORDER_WEAPON = 2;
  /** General purpose materials */
  public static final int ORDER_SPECIAL = 3;
  /** Order for mod integration materials */
  public static final int ORDER_COMPAT = 5;
  /** Order for nether materials in tiers 1-3 */
  public static final int ORDER_NETHER = 10;
  /** Order for end materials in tiers 1-4 */
  public static final int ORDER_END = 15;

  /** List of all added materials */
  private final Map<MaterialId, DataMaterial> allMaterials = new HashMap<>();

  /** Boolean just in case material stats run first */
  private boolean addMaterialsRun = false;

  public AbstractMaterialDataProvider(DataGenerator gen) {
    super(gen, MaterialManager.FOLDER, MaterialManager.GSON);
  }

  /**
   * Function to add all relevant materials
   */
  protected abstract void addMaterials();

  private void ensureAddMaterialsRun() {
    if (addMaterialsRun) {
      return;
    }
    addMaterialsRun = true;
    addMaterials();
  }

  @Override
  public void act(DirectoryCache cache) {
    ensureAddMaterialsRun();
    allMaterials.forEach((id, data) -> saveThing(cache, id, convert(data)));
  }

  /**
   * Gets a list of all material IDs that are generated. Note this will run {@link #addMaterials()}, so generally its better to run your material data provider first
   * @return  Material ID list
   */
  public Set<MaterialId> getAllMaterials() {
    ensureAddMaterialsRun();
    // ignore any materials with no IMaterial defintion, means its purely a redirect and will never exist in game
    return allMaterials.values().stream()
                       .map(DataMaterial::getMaterial)
                       .filter(Objects::nonNull)
                       .map(IMaterial::getIdentifier)
                       .collect(Collectors.toSet());
  }


  /* Base methods */

  /** Adds a material to be generated with a condition and redirect data */
  protected void addMaterial(IMaterial material, @Nullable ICondition condition, MaterialJson.Redirect... redirect) {
    allMaterials.put(material.getIdentifier(), new DataMaterial(material, condition, redirect));
  }

  /** Adds JSON to redirect an ID to another ID */
  protected void addRedirect(MaterialId id, @Nullable ICondition condition, MaterialJson.Redirect... redirect) {
    allMaterials.put(id, new DataMaterial(null, condition, redirect));
  }

  /** Adds JSON to redirect an ID to another ID */
  protected void addRedirect(MaterialId id, MaterialJson.Redirect... redirect) {
    addRedirect(id, null, redirect);
  }

  /* Material helpers */

  /** Creates a normal material with a condition and a redirect */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable, int color, boolean hidden, @Nullable ICondition condition, MaterialJson.Redirect... redirect) {
    addMaterial(new Material(location, tier, order, craftable, Color.fromInt(color), hidden), condition, redirect);
  }

  /** Creates a normal material */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable, int color) {
    addMaterial(location, tier, order, craftable, color, false, null);
  }

  /** Creates a new compat material */
  protected void addCompatMetalMaterial(MaterialId location, int tier, int order, int color) {
    ICondition condition = new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new NotCondition(new TagEmptyCondition("forge", "ingots/" + location.getPath())));
    addMaterial(location, tier, order, false, color & 0xFFFFFF, false, condition);
  }


  /* Redirect helpers */

  /** Makes a conditional redirect to the given ID */
  protected MaterialJson.Redirect conditionalRedirect(MaterialId id, @Nullable ICondition condition) {
    return new MaterialJson.Redirect(id, condition);
  }

  /** Makes an unconditional redirect to the given ID */
  protected MaterialJson.Redirect redirect(MaterialId id) {
    return conditionalRedirect(id, null);
  }


  /* Helpers */

  /**
   * Converts a material to JSON
   * @param data   Data to save
   * @return  Material JSON
   */
  private MaterialJson convert(DataMaterial data) {
    IMaterial material = data.getMaterial();
    MaterialJson.Redirect[] redirect = data.getRedirect();
    if (redirect != null && redirect.length == 0) {
      redirect = null;
    }
    if (material == null) {
      return new MaterialJson(data.getCondition(), null, null, null, null, null, redirect);
    }
    return new MaterialJson(data.getCondition(), material.isCraftable(), material.getTier(), material.getSortOrder(), material.getColor().getName(), material.isHidden(), redirect);
  }

  @Data
  private static class DataMaterial {
    @Nullable
    private final IMaterial material;
    @Nullable
    private final ICondition condition;
    private final MaterialJson.Redirect[] redirect;
  }
}
