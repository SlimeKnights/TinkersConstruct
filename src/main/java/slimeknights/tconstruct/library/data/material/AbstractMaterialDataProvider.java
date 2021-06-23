package slimeknights.tconstruct.library.data.material;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.text.Color;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
  public static final int ORDER_NETHER = 7;
  /** Order for end materials in tiers 1-4 */
  public static final int ORDER_END = 10;

  /** List of all added materials */
  private final Map<MaterialId, Pair<IMaterial,ICondition>> allMaterials = new HashMap<>();

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
    allMaterials.forEach((id, pair) -> saveThing(cache, pair.getFirst().getIdentifier(), convert(pair.getFirst(), pair.getSecond())));
  }

  /**
   * Gets a list of all material IDs that are generated. Note this will run {@link #addMaterials()}, so generally its better to run your material data provider first
   * @return  Material ID list
   */
  public Set<MaterialId> getAllMaterials() {
    ensureAddMaterialsRun();
    return allMaterials.keySet();
  }


  /* Methods to use */

  /** Adds a material to be generated with a condition */
  protected void addMaterial(IMaterial material, @Nullable ICondition condition) {
    allMaterials.put(material.getIdentifier(), Pair.of(material, condition));
  }

  /** Creates a normal material with a condition */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable, int color, boolean hidden, @Nullable ICondition condition) {
    addMaterial(new Material(location, tier, order, craftable, Color.fromInt(color), hidden), condition);
  }

  /** Creates a normal material */
  protected void addMaterial(MaterialId location, int tier, int order, boolean craftable, int color) {
    addMaterial(location, tier, order, craftable, color, false, null);
  }

  /** Creates a new compat material */
  protected void addCompatMetalMaterial(MaterialId location, int tier, int order, int color) {
    // all our addon materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    ICondition condition = new NotCondition(new TagEmptyCondition("forge", "ingots/" + location.getPath()));
    addMaterial(location, tier, order, false, color & 0xFFFFFF, false, condition);
  }


  /* Helpers */

  /**
   * Converts a material to JSON
   * @param material   Material
   * @param condition  Material condition
   * @return  Material JSON
   */
  private MaterialJson convert(IMaterial material, @Nullable ICondition condition) {
    return new MaterialJson(condition, material.isCraftable(), material.getTier(), material.getSortOrder(), material.getColor().getName(), material.isHidden());
  }
}
