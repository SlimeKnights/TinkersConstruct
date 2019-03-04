package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.RecipeUtil;

public class Material extends RecipeMatchRegistry {

  public static final Material UNKNOWN = new Material("unknown", TextFormatting.WHITE);
  public static final String LOC_Name = "material.%s.name";
  public static final String LOC_Prefix = "material.%s.prefix";

  // How much the different items are "worth"
  // the values are used for both liquid conversion as well as part crafting
  public static final int VALUE_Ingot = 144;
  public static final int VALUE_Nugget = VALUE_Ingot / 9;
  public static final int VALUE_Fragment = VALUE_Ingot / 4;
  public static final int VALUE_Shard = VALUE_Ingot / 2;

  public static final int VALUE_Gem = 666; // divisible by 3!
  public static final int VALUE_Block = VALUE_Ingot * 9;

  public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
  public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
  public static final int VALUE_Glass = 1000;

  public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

  public static final int VALUE_SlimeBall = 250;

  public static int VALUE_Ore() {
    return (int) (VALUE_Ingot * Config.oreToIngotRatio);
  }

  static {
    UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
    UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
    UNKNOWN.addStats(new ExtraMaterialStats(0));
    UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
    UNKNOWN.addStats(new BowStringMaterialStats(1f));
    UNKNOWN.addStats(new ArrowShaftMaterialStats(1f, 0));
    UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
    UNKNOWN.addStats(new ProjectileMaterialStats());
  }

  /**
   * This String uniquely identifies a material.
   */
  public final String identifier;

  /** The fluid associated with this material, can be null */
  protected Fluid fluid;

  /** Material can be crafted into parts in the PartBuilder */
  protected boolean craftable;

  /** Material can be cast into parts using the Smeltery and a Cast. Fluid must be NON NULL */
  protected boolean castable;


  /**
   * Client-Information
   * How the material will be rendered on tinker tools etc.
   */
  @SideOnly(Side.CLIENT)
  public MaterialRenderInfo renderInfo;// = new MaterialRenderInfo.Default(0xffffff);
  public int materialTextColor = 0xffffff; // used in tooltips and other text. Saved in NBT.

  /**
   * This item, if it is not null, represents the material for rendering.
   * In general if you want to give a person this material, you can give them this item.
   */
  private ItemStack representativeItem = ItemStack.EMPTY;

  /**
   * Ore name that represents this material
   */
  private String representativeOre = null;

  /**
   * This item will be used instead of the generic shard item when returning leftovers.
   */
  private ItemStack shardItem = ItemStack.EMPTY;

  /**
   * If true, the material is not shown
   */
  private boolean hidden;

  // we use a specific map for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * the linked map to ensure the order when iterating
  protected final Map<String, IMaterialStats> stats = new LinkedHashMap<>();
  /** Stat-ID -> Traits */
  protected final Map<String, List<ITrait>> traits = new LinkedHashMap<>();

  public Material(String identifier, TextFormatting textColor) {
    this(identifier, Util.enumChatFormattingToColor(textColor));
  }

  public Material(String identifier, int color) {
    this(identifier, color, false);
  }

  public Material(String identifier, int color, boolean hidden) {
    this.identifier = Util.sanitizeLocalizationString(identifier); // lowercases and removes whitespaces

    this.hidden = hidden;

    // if invisible, make it fully opaque.
    if(((color >> 24) & 0xFF) == 0) {
      color |= 0xFF << 24;
    }

    this.materialTextColor = color;
    if(FMLCommonHandler.instance().getSide().isClient()) {
      setRenderInfo(color);
    }
  }

  /*** If true the material will not be displayed to the user anywhere. Used for special or internal materials. */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Call to declare the material is visible. Used by integration to make materials visible that were previously hidden
   */
  public void setVisible() {
    hidden = false;
  }

  /** Associates this fluid with the material. Used for melting/casting items. */
  public Material setFluid(Fluid fluid) {
    if(fluid != null && !FluidRegistry.isFluidRegistered(fluid)) {
      TinkerRegistry.log.warn("Materials cannot have an unregistered fluid associated with them!");
    }
    this.fluid = fluid;
    return this;
  }

  /** Setting this to true allows to craft parts in the PartBuilder */
  public Material setCraftable(boolean craftable) {
    this.craftable = craftable;
    return this;
  }

  public boolean isCraftable() {
    return this.craftable || (Config.craftCastableMaterials && castable);
  }

  /** Setting this to true allows to cast parts of this material. NEEDS TO HAVE A FLUID SET BEFOREHAND! */
  public Material setCastable(boolean castable) {
    this.castable = castable;
    return this;
  }

  public boolean isCastable() {
    return hasFluid() && this.castable;
  }

  /**
   * The display information for the Material. You should totally set this if you want your material to be visible.
   *
   * @param renderInfo How the textures for the material are generated
   */
  @SideOnly(Side.CLIENT)
  public void setRenderInfo(MaterialRenderInfo renderInfo) {
    this.renderInfo = renderInfo;
  }

  @SideOnly(Side.CLIENT)
  public MaterialRenderInfo setRenderInfo(int color) {
    setRenderInfo(new MaterialRenderInfo.Default(color));
    return renderInfo;
  }

  /* Stats */

  /**
   * Do not use this function directly stats. Use TinkerRegistry.addMaterialStats instead.
   */
  public Material addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getIdentifier(), materialStats);
    return this;
  }

  /**
   * Returns the given type of stats if the material has them. Returns null Otherwise.
   */
  private IMaterialStats getStatsSafe(String identifier) {
    if(identifier == null || identifier.isEmpty()) {
      return null;
    }

    for(IMaterialStats stat : stats.values()) {
      if(identifier.equals(stat.getIdentifier())) {
        return stat;
      }
    }

    return null;
  }

  /**
   * Returns the material stats of the given type of this material.
   *
   * @param identifier Identifier of the material.
   * @param <T>        Type of the Stats are determined by return value. Use the correct
   * @return The stats found or null if none present.
   */
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getStats(String identifier) {
    return (T) getStatsSafe(identifier);
  }

  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getStatsOrUnknown(String identifier) {
    T stats = (T) getStatsSafe(identifier);
    if(stats == null && this != UNKNOWN) {
      return UNKNOWN.getStats(identifier);
    }
    return stats;
  }

  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  public boolean hasStats(String identifier) {
    return getStats(identifier) != null;
  }

  /* Traits */

  /**
   * Adds the trait as the default trait, will be used if no more specific one is present time.
   */
  public Material addTrait(ITrait materialTrait) {
    return addTrait(materialTrait, null);
  }

  /**
   * Adds the trait to be added if the specified stats are used.
   */
  public Material addTrait(ITrait materialTrait, String dependency) {
    // register unregistered traits
    if(TinkerRegistry.checkMaterialTrait(this, materialTrait, dependency)) {
      getStatTraits(dependency).add(materialTrait);
    }

    return this;
  }

  /** Obtains the list of traits for the given stat, creates it if it doesn't exist yet. */
  protected List<ITrait> getStatTraits(String id) {
    if(!this.traits.containsKey(id)) {
      this.traits.put(id, new LinkedList<>());
    }
    return this.traits.get(id);
  }

  /**
   * Returns whether the material has a trait with that identifier.
   */
  public boolean hasTrait(String identifier, String stats) {
    if(identifier == null || identifier.isEmpty()) {
      return false;
    }

    for(ITrait trait : getStatTraits(stats)) {
      if(trait.getIdentifier().equals(identifier)) {
        return true;
      }
    }
    return false;
  }

  public List<ITrait> getDefaultTraits() {
    return ImmutableList.copyOf(getStatTraits(null));
  }

  public List<ITrait> getAllTraitsForStats(String stats) {
    if(this.traits.containsKey(stats)) {
      return ImmutableList.copyOf(this.traits.get(stats));
    }
    else if(this.traits.containsKey(null)) {
      return ImmutableList.copyOf(this.traits.get(null));
    }
    return ImmutableList.of();
  }

  public Collection<ITrait> getAllTraits() {
    ImmutableSet.Builder<ITrait> builder = ImmutableSet.builder();
    for(List<ITrait> traitlist : traits.values()) {
      builder.addAll(traitlist);
    }
    return builder.build();
  }

  /* Data about the material itself */

  public boolean hasFluid() {
    return fluid != null;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public void addItemIngot(String oredict) {
    this.addItem(oredict, 1, Material.VALUE_Ingot);
  }

  public void addCommonItems(String oredictSuffix) {
    this.addItem("ingot" + oredictSuffix, 1, Material.VALUE_Ingot);
    this.addItem("nugget" + oredictSuffix, 1, Material.VALUE_Nugget);
    this.addItem("block" + oredictSuffix, 1, Material.VALUE_Block);
  }

  public void setRepresentativeItem(String representativeOre) {
    this.representativeOre = representativeOre;
  }

  public void setRepresentativeItem(Item representativeItem) {
    setRepresentativeItem(new ItemStack(representativeItem));
  }

  public void setRepresentativeItem(Block representativeBlock) {
    setRepresentativeItem(new ItemStack(representativeBlock));
  }


  public void setRepresentativeItem(ItemStack representativeItem) {
    if(representativeItem == null || representativeItem.isEmpty()) {
      this.representativeItem = ItemStack.EMPTY;
    }
    else if(matches(representativeItem).isPresent()) {
      this.representativeItem = representativeItem;
    }
    else {
      TinkerRegistry.log.warn("Itemstack {} cannot represent material {} since it is not associated with the material!",
                              representativeItem.toString(),
                              identifier);
    }
  }

  public ItemStack getRepresentativeItem() {
    if(representativeOre != null) {
      ItemStack ore = RecipeUtil.getPreference(representativeOre);
      if(!ore.isEmpty()) {
        return ore;
      }
    }
    return representativeItem;
  }


  public void setShard(Item item) {
    setShard(new ItemStack(item));
  }

  public void setShard(@Nonnull ItemStack stack) {
    if(stack.isEmpty()) {
      this.shardItem = ItemStack.EMPTY;
    }
    else {
      Optional<RecipeMatch.Match> matchOptional = matches(stack);
      if(matchOptional.isPresent()) {
        RecipeMatch.Match match = matchOptional.get();
        if(match.amount == VALUE_Shard) {
          this.shardItem = stack;
          if(representativeItem.isEmpty()) {
            this.setRepresentativeItem(shardItem.copy());
          }
        }
        else {
          TinkerRegistry.log.warn("Itemstack {} cannot be shard of material {} since it does not have the correct value! (is {}, has to be {})",
                                  representativeItem.toString(),
                                  identifier,
                                  match.amount,
                                  VALUE_Shard);
        }
      }
      else {
        TinkerRegistry.log.warn("Itemstack {} cannot be shard of material {} since it is not associated with the material!",
                                stack.toString(),
                                identifier);
      }
    }
  }

  public ItemStack getShard() {
    if(shardItem != ItemStack.EMPTY) {
      return shardItem.copy();
    }
    return ItemStack.EMPTY;
  }

  public boolean hasItems() {
    return !items.isEmpty();
  }

  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }

  /** Takes a string and turns it into a named variant for this material. E.g. pickaxe -> wooden pickaxe */
  public String getLocalizedItemName(String itemName) {
    if(this == UNKNOWN) {
      return itemName;
    }

    if(I18n.canTranslate(String.format(LOC_Prefix, getIdentifier()))) {
      return I18n.translateToLocalFormatted(String.format(LOC_Prefix, Util
          .sanitizeLocalizationString(identifier)), itemName);
    }

    return getLocalizedName() + " " + itemName;
  }

  public String getLocalizedNameColored() {
    return getTextColor() + getLocalizedName();
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getTextColor() {
    return CustomFontColor.encodeColor(materialTextColor);
  }

  public static String getCombinedItemName(String itemName, Collection<Material> materials) {

    // no material
    if(materials.isEmpty() || materials.stream().allMatch(Material.UNKNOWN::equals)) {
      return itemName;
    }
    // only one material - prefix
    if(materials.size() == 1) {
      return materials.iterator().next().getLocalizedItemName(itemName);
    }

    // multiple materials. we'll have to combine
    StringBuilder sb = new StringBuilder();
    Iterator<Material> iter = materials.iterator();
    Material material = iter.next();
    sb.append(material.getLocalizedName());
    while(iter.hasNext()) {
      material = iter.next();
      sb.append("-");
      sb.append(material.getLocalizedName());
    }
    sb.append(" ");
    sb.append(itemName);

    return sb.toString();
  }
}
