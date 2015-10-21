package slimeknights.tconstruct.library.materials;

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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontRenderer;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.traits.ITrait;

public class Material extends RecipeMatchRegistry {

  public static final Material UNKNOWN = new Material("unknown", EnumChatFormatting.WHITE);
  public static final String LOC_Name = "material.%s.name";
  public static final String LOC_Prefix = "material.%s.prefix";

  // How much the different items are "worth"
  // the values are used for both liquid conversion as well as part crafting
  public static final int VALUE_Ingot = 144;
  public static final int VALUE_Nugget = VALUE_Ingot/9;
  public static final int VALUE_Fragment = VALUE_Ingot/4;
  public static final int VALUE_Shard = VALUE_Ingot/2;

  static {
    UNKNOWN.addStats(new ToolMaterialStats(1, 1, 1, 1, 1, 0));
  }

  /**
   * This String uniquely identifies a material.
   */
  @Nonnull
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
  private ItemStack representativeItem;

  /**
   * This item will be used instead of the generic shard item when returning leftovers.
   */
  private ItemStack shardItem;

  // we use a specific map for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * the linked map to ensure the order when iterating
  protected final Map<String, IMaterialStats> stats = new LinkedHashMap<String, IMaterialStats>();
  protected final Map<String, ITrait> traits = new LinkedHashMap<String, ITrait>();

  public Material(String identifier, EnumChatFormatting textColor) {
    this(identifier, Util.enumChatFormattingToColor(textColor));
  }

  public Material(String identifier, int color) {
    this.identifier = Util.sanitizeLocalizationString(identifier); // lowercases and removes whitespaces
    this.materialTextColor = color;
    if(FMLCommonHandler.instance().getSide().isClient()) {
      setRenderInfo(color);
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

  public void setRepresentativeItem(Item representativeItem) {
    setRepresentativeItem(new ItemStack(representativeItem));
  }

  public void setRepresentativeItem(Block representativeBlock) {
    setRepresentativeItem(new ItemStack(representativeBlock));
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


  public void setShard(Item item) {
    setShard(new ItemStack(item));
  }

  public void setShard(ItemStack stack) {
    if(stack == null) {
      this.shardItem = null;
    }
    else {
      RecipeMatch.Match match = matches(stack);
      if(match != null) {
        if(match.amount == VALUE_Shard) {
          this.shardItem = stack;
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
    if(shardItem != null) {
      return shardItem.copy();
    }
    return null;
  }

  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }

  /** Takes a string and turns it into a named variant for this material. E.g. pickaxe -> wooden pickaxe */
  public String getLocalizedItemName(String itemName) {
    if(this == UNKNOWN) return itemName;

    if(StatCollector.canTranslate(String.format(LOC_Prefix, getIdentifier()))) {
      return StatCollector.translateToLocalFormatted(String.format(LOC_Prefix, Util
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
    return CustomFontRenderer.encodeColor(materialTextColor);
  }
}
