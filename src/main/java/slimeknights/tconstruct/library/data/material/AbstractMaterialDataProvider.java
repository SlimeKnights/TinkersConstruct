package slimeknights.tconstruct.library.data.material;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extendable material provider, useful for addons
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public abstract class AbstractMaterialDataProvider extends GenericDataProvider {
  /** General purpose materials */
  public static final int ORDER_GENERAL = 0;
  /** Materials primarily used for harvest */
  public static final int ORDER_HARVEST = 1;
  /** Materials primarily used for weapons */
  public static final int ORDER_WEAPON = 2;
  /** General purpose materials */
  public static final int ORDER_SPECIAL = 3;
  /** Ranged exclusive materials */
  public static final int ORDER_RANGED = 4;
  /** Order for mod integration materials */
  public static final int ORDER_COMPAT = 5;
  /** Order for nether materials in tiers 1-3 */
  public static final int ORDER_NETHER = 10;
  /** Order for end materials in tiers 1-4 */
  public static final int ORDER_END = 15;
  /** Order for materials that are just a binding */
  public static final int ORDER_BINDING = 20;
  /** Order for materials that are just used for repair or textures */
  public static final int ORDER_REPAIR = 25;

  /** List of all added materials */
  private final Map<MaterialId, DataMaterial> allMaterials = new HashMap<>();

  /** Boolean just in case material stats run first */
  private boolean addMaterialsRun = false;

  public AbstractMaterialDataProvider(DataGenerator gen) {
    super(gen, PackType.SERVER_DATA, MaterialManager.FOLDER, MaterialManager.GSON);
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
  public void run(CachedOutput cache) {
    ensureAddMaterialsRun();
    allMaterials.forEach((id, data) -> saveJson(cache, id, convert(data)));
  }

  /**
   * Gets a list of all material IDs that are generated. Note this will run {@link #addMaterials()}, so generally its better to run your material data provider first
   * @return  Material ID list
   */
  public Set<MaterialId> getAllMaterials() {
    ensureAddMaterialsRun();
    // ignore any materials with no IMaterial defintion, means its purely a redirect and will never exist in game
    return allMaterials.values().stream()
                       .map(DataMaterial::material)
                       .filter(Objects::nonNull)
                       .map(IMaterial::getIdentifier)
                       .collect(Collectors.toSet());
  }


  /* Base methods */

  /** Adds a material to be generated with a condition and redirect data */
  protected void addMaterial(IMaterial material, @Nullable ICondition condition, JsonRedirect... redirect) {
    allMaterials.put(material.getIdentifier(), new DataMaterial(material, condition, redirect));
  }

  /** Adds JSON to redirect an ID to another ID */
  protected void addRedirect(MaterialId id, @Nullable ICondition condition, JsonRedirect... redirect) {
    allMaterials.put(id, new DataMaterial(null, condition, redirect));
  }

  /** Adds JSON to redirect an ID to another ID */
  protected void addRedirect(MaterialId id, JsonRedirect... redirect) {
    addRedirect(id, null, redirect);
  }

  /* Material helpers */

  /** Conditions on a forge tag existing */
  protected static ICondition tagExistsCondition(String name) {
    return new NotCondition(new TagEmptyCondition("forge", name));
  }

  /** Creates a normal material with a condition and a redirect */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable, boolean hidden, @Nullable ICondition condition, JsonRedirect... redirect) {
    addMaterial(new Material(location, tier, order, craftable, hidden), condition, redirect);
  }

  /** Creates a normal material */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable) {
    addMaterial(location, tier, order, craftable, false, null);
  }

  /** Creates a new compat material */
  protected void addCompatMaterial(MaterialId location, int tier, int order, boolean craftable, String... tagNames) {
    ICondition condition = new OrCondition(Stream.concat(
      Stream.of(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS),
      Arrays.stream(tagNames).map(AbstractMaterialDataProvider::tagExistsCondition)
    ).toArray(ICondition[]::new));
    addMaterial(location, tier, order, craftable, false, condition);
  }

  /** @deprecated use {@link #addCompatMaterial(MaterialId, int, int, boolean, String...)} */
  @Deprecated(forRemoval = true)
  protected void addCompatMaterial(MaterialId location, int tier, int order, String tagName, boolean craftable) {
    addCompatMaterial(location, tier, order, craftable, tagName);
  }

  /** Creates a new compat material */
  protected void addCompatMetalMaterial(MaterialId location, int tier, int order, String... ingotNames) {
    addCompatMaterial(location, tier, order, false, Arrays.stream(ingotNames).map(name -> "ingots/" + name).toArray(String[]::new));
  }

  /** Creates a new compat material */
  protected void addCompatMetalMaterial(MaterialId location, int tier, int order) {
    addCompatMetalMaterial(location, tier, order, location.getPath());
  }


  /* Redirect helpers */

  /** Makes a conditional redirect to the given ID */
  protected JsonRedirect conditionalRedirect(MaterialId id, @Nullable ICondition condition) {
    return new JsonRedirect(id, condition);
  }

  /** Makes an unconditional redirect to the given ID */
  protected JsonRedirect redirect(MaterialId id) {
    return conditionalRedirect(id, null);
  }


  /* Helpers */

  /**
   * Converts a material to JSON
   * @param data   Data to save
   * @return  Material JSON
   */
  private MaterialJson convert(DataMaterial data) {
    IMaterial material = data.material;
    JsonRedirect[] redirect = data.redirect;
    if (redirect != null && redirect.length == 0) {
      redirect = null;
    }
    if (material == null) {
      return new MaterialJson(data.condition, null, null, null, null, redirect);
    }
    return new MaterialJson(data.condition, material.isCraftable(), material.getTier(), material.getSortOrder(), material.isHidden(), redirect);
  }

  private record DataMaterial(@Nullable IMaterial material, @Nullable ICondition condition, JsonRedirect[] redirect) {}
}
