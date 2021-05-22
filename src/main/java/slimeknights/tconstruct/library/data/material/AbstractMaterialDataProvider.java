package slimeknights.tconstruct.library.data.material;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.Color;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.materials.json.MaterialJson;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
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

  /** Creates a supplier for the given trait */
  private static Supplier<List<ModifierEntry>> traitSupplier(@Nullable Supplier<? extends Modifier> trait) {
    if (trait == null) {
      return Collections::emptyList;
    }
    return () -> Collections.singletonList(new ModifierEntry(trait.get(), 1));
  }

  /** Adds a material to be generated with a condition */
  protected void addMaterial(IMaterial material, @Nullable ICondition condition) {
    allMaterials.put(material.getIdentifier(), Pair.of(material, condition));
  }

  /** Creates a normal material with a condition */
  protected void addMaterial(MaterialId location, int tier, int order, Fluid fluid, int fluidPerUnit, boolean craftable, int color, Modifier[] traits, @Nullable ICondition condition) {
    int temperature = fluid == Fluids.EMPTY ? 0 : fluid.getAttributes().getTemperature() - 300;
    addMaterial(new Material(location, tier, order, fluid, fluidPerUnit, craftable, Color.fromInt(color), temperature, Arrays.stream(traits).map(trait -> new ModifierEntry(trait, 1)).collect(Collectors.toList())), condition);
  }

  /** Creates a normal material */
  protected void addMaterial(MaterialId location, int tier, int order, Fluid fluid, int fluidPerUnit, boolean craftable, int color, Modifier... traits) {
    addMaterial(location, tier, order, fluid, fluidPerUnit, craftable, color, traits, null);
  }

  /** Creates a material with a fluid */
  protected void addMaterialWithFluid(MaterialId location, int tier, int order, Fluid fluid, int fluidPerUnit, boolean craftable, int color, Modifier... traits) {
    addMaterial(location, tier, order, fluid, fluidPerUnit, craftable, color, traits);
  }

  /** Creates a material with a fluid */
  protected void addMetalMaterial(MaterialId location, int tier, int order, Fluid fluid, int color, Modifier... traits) {
    addMaterialWithFluid(location, tier, order, fluid, MaterialValues.INGOT, false, color, traits);
  }

  /** Creates a material with no fluid */
  protected void addMaterialNoFluid(MaterialId location, int tier, int order, boolean craftable, int color, Modifier... traits) {
    addMaterial(location, tier, order, Fluids.EMPTY, 0, craftable, color, traits);
  }


  /** Creates a new compat material */
  protected void addCompatMetalMaterial(MaterialId location, int tier, int order, Fluid fluid, int color, Modifier... traits) {
    // all our addon materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    ICondition condition = new NotCondition(new TagEmptyCondition("forge", "ingots/" + location.getPath()));
    addMaterial(location, tier, order, fluid, MaterialValues.INGOT, false, color & 0xFFFFFF, traits, condition);
  }


  /* Helpers */

  /**
   * Converts a material to JSON
   * @param material   Material
   * @param condition  Material condition
   * @return  Material JSON
   */
  private MaterialJson convert(IMaterial material, @Nullable ICondition condition) {
    List<ModifierEntry> list = material.getTraits();
    ModifierEntry[] traits = list.isEmpty() ? null : list.toArray(new ModifierEntry[0]);

    // if empty, no fluid, no temperature
    String color = material.getColor().getName();
    if (material.getFluid() == Fluids.EMPTY) {
      return new MaterialJson(condition, material.isCraftable(), material.getTier(), material.getSortOrder(), null, null, color, null, traits);
    }
    return new MaterialJson(condition, material.isCraftable(), material.getTier(), material.getSortOrder(), material.getFluid().getRegistryName(), material.getFluidPerUnit(), color, material.getTemperature(), traits);
  }
}
