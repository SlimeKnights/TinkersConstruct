package slimeknights.tconstruct.library.materials.client;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;

public class MaterialDisplay implements IMaterialDisplay {

  /**
   * The material this display information belongs to
   */
  // todo: check if we even need this
  private final IMaterial material;

  /**
   * Text color used in tooltips and other text. Saved in NBT.
   */
  private final int materialTextColor;

  /**
   * How the material will be rendered on tinker tools etc.
   * Loaded from resources
   */
  private IMaterialRenderInfo renderInfo;

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
   * If true, the material is not shown anywhere
   * includes items with this material in JEI, in books,...
   * This is mainly used for internal materials that only serve rendering purposes
   */
  private boolean hidden;

  public MaterialDisplay(IMaterial material, int materialTextColor) {
    this.material = material;
    this.materialTextColor = materialTextColor;
  }

  /*** If true the material will not be displayed to the user anywhere. Used for special or internal materials. */
  @Override
  public boolean isHidden() {
    return hidden;
  }

  @Override
  public ItemStack getRepresentativeItem() {
    // todo: use shard fallback
    return null;
  }

  @Override
  public String getLocalizedName() {
    return null;
  }

  @Override
  public String getLocalizedItemName(String itemName) {
    return null;
  }

  @Override
  public String getLocalizedNameColored() {
    return null;
  }

  @Override
  public String getTextColor() {
    return null;
  }

  /**
   * Call to declare the material is visible. Used by integration to make materials visible that were previously hidden
   */
  public void setVisible() {
    hidden = false;
  }

  /**
   * The display information for the Material. You should totally set this if you want your material to be visible.
   *
   * @param renderInfo How the textures for the material are generated
   */
  public void setRenderInfo(IMaterialRenderInfo renderInfo) {
    this.renderInfo = renderInfo;
  }
  // todo fix commented out code

/*
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

  @Override
  public ItemStack getRepresentativeItem() {
    if(representativeOre != null) {
      ItemStack ore = RecipeUtil.getPreference(representativeOre);
      if(!ore.isEmpty()) {
        return ore;
      }
    }
    // todo: return shard if no item is set. moved from setShard
    //if(representativeItem.isEmpty()) {
      //this.setRepresentativeItem(shardItem.copy());
    //}
    return representativeItem;
  }


  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }
*/
  /** Takes a string and turns it into a named variant for this material. E.g. pickaxe -> wooden pickaxe */
 /* @Override
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

  @Override
  public String getLocalizedNameColored() {
    return getTextColor() + getLocalizedName();
  }

  @Override
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
  }*/
}
