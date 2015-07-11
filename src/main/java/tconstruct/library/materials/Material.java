package tconstruct.library.materials;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import tconstruct.library.TinkerAPIException;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.mantle.RecipeMatch;
import tconstruct.library.mantle.RecipeMatchRegistry;
import tconstruct.library.traits.ITrait;

public class Material {

  public static final Material UNKNOWN = new Material("Unknown");
  public static final String LOCALIZATION_STRING = "material.%s.name";

  /**
   * This String uniquely identifies a material.
   */
  @Nonnull
  public final String identifier;

  /**
   * Items associated with this material. Used for repairing and identifying items that belong to a material.
   */
  protected RecipeMatchRegistry materialItems;

  /** The fluid associated with this material, can be null */
  protected Fluid fluid;

  /** Material can be crafted into parts in the PartBuilder */
  public boolean craftable;

  /** Material can be cast into parts using the Smeltery and a Cast. Fluid must be NON NULL */
  public boolean castable;


  /**
   * Client-Information
   * How the material will be rendered on tinker tools etc.
   */
  @SideOnly(Side.CLIENT)
  public MaterialRenderInfo renderInfo;// = new MaterialRenderInfo.Default(0xffffff);
  @SideOnly(Side.CLIENT)
  public EnumChatFormatting textColor;// = EnumChatFormatting.WHITE; // used in tooltips and other text

  /**
   * This item, if it is not null, represents the material for rendering.
   * In general if you want to give a person this material, you can give them this item.
   */
  private ItemStack representativeItem;


  // we use a Treemap for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * A treemap because we can sort it, so that all materials have the same order when iterating
  protected final Map<String, IMaterialStats> stats = new TreeMap<>();
  protected final Map<String, ITrait> traits = new TreeMap<>();

  public Material(String identifier) {
    this.identifier = identifier;
    if(FMLCommonHandler.instance().getSide().isClient()) {
      setRenderInfo(0xffffff, EnumChatFormatting.WHITE);
    }
  }

  /** Associates this fluid with the material. Used for melting/casting items. */
  public Material setFluid(Fluid fluid) {
    if(!FluidRegistry.isFluidRegistered(fluid)) {
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


  /** Setting this to true allows to cast parts of this material. NEEDS TO HAVE A FLUID SET BEFOREHAND! */
  public Material setCastable(boolean castable) {
    if(!hasFluid()) {
      throw new TinkerAPIException("Castable materials need a Fluid set");
    }
    this.castable = castable;
    return this;
  }

  /**
   * The display information for the Material. You should totally set this if you want your material to be visible.
   *
   * @param renderInfo How the textures for the material are generated
   * @param textColor  The color of the text associated with the material. Tooltips etc.
   */
  @SideOnly(Side.CLIENT)
  public void setRenderInfo(MaterialRenderInfo renderInfo, EnumChatFormatting textColor) {
    this.renderInfo = renderInfo;
    this.textColor = textColor;
  }

  @SideOnly(Side.CLIENT)
  public void setRenderInfo(int color, EnumChatFormatting textColor) {
    setRenderInfo(new MaterialRenderInfo.Default(color), textColor);
  }

  /* Stats */

  /**
   * Do not use this function directly stats. Use TinkerRegistry.addMaterialStats instead.
   */
  public Material addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getMaterialType(), materialStats);
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
      if(identifier.equals(stat.getMaterialType())) {
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

  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  public boolean hasStats(String identifier) {
    return getStats(identifier) != null;
  }

  /* Traits */

  /**
   * Do not use this function with unregistered traits. Use TinkerRegistry.addMaterialTrait instead.
   */
  public Material addTrait(ITrait materialTrait) {
    // rgister unregistered traits
    if(TinkerRegistry.getTrait(materialTrait.getIdentifier()) == null) {
      TinkerRegistry.addTrait(materialTrait);
    }
    this.traits.put(materialTrait.getIdentifier(), materialTrait);
    return this;
  }

  /**
   * Returns whether the material has a trait with that identifier.
   */
  public boolean hasTrait(String identifier) {
    if(identifier == null || identifier.isEmpty()) {
      return false;
    }

    return traits.containsKey(identifier);
  }

  public Collection<ITrait> getAllTraits() {
    return this.traits.values();
  }

  /* Data about the material itself */

  public boolean hasFluid() {
    return fluid != null;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public RecipeMatch.Match matches(ItemStack... stacks) {
    return materialItems.matches(stacks);
  }

  public void addItem(String oredictItem) {
    materialItems.addItem(oredictItem);
  }

  public void addItem(Item item) {
    materialItems.addItem(item);
  }

  public void addItem(Block block, int count) {
    materialItems.addItem(block, count);
  }

  public RecipeMatchRegistry getItemRegistry() {
    return materialItems;
  }

  public void setRepresentativeItem(ItemStack representativeItem) {
    if(representativeItem == null) {
      this.representativeItem = null;
    }
    else if(matches(representativeItem) != null) {
      this.representativeItem = representativeItem;
    }
    else {
      TinkerRegistry.log.warn("Itemstack {} cannot represent material {} since it is not associated with the material!",
                              representativeItem.toString(),
                              identifier);
    }
  }

  public ItemStack getRepresentativeItem() {
    return representativeItem;
  }

  public String getLocalizedName() {
    return StatCollector
        .translateToLocal(String.format(LOCALIZATION_STRING, Util.sanitizeLocalizationString(identifier)));
  }

  public String getIdentifier() {
    return identifier;
  }
}
