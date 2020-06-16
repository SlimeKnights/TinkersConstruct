package slimeknights.tconstruct.library.materials;

import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontColor;

@Getter
public class Material implements IMaterial {

  // todo config
//  public static int VALUE_Ore() {
//    return (int) (VALUE_Ingot * Config.oreToIngotRatio);
//  }
  // todo default stats. figure out how to hadnle them. maybe have to register stat types and handle it there?
/*
  static {
    UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
    UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
    UNKNOWN.addStats(new ExtraMaterialStats(0));
    UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
    UNKNOWN.addStats(new BowStringMaterialStats(1f));
    UNKNOWN.addStats(new ArrowShaftMaterialStats(1f, 0));
    UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
    UNKNOWN.addStats(new ProjectileMaterialStats());
  }*/

  /**
   * This String uniquely identifies a material.
   */
  private final MaterialId identifier;

  /**
   * The fluid associated with this material, can not be null, but Fluids.EMPTY.
   * If non-null also indicates that the material can be cast.
   */
  protected final Fluid fluid;

  /**
   * Material can be crafted into parts in the PartBuilder
   */
  private final boolean craftable;

  /**
   * Key used for localizing the material
   */
  private final String translationKey;

  /**
   * the unencoded color for the material
   */
  private final String materialTextColor;

  /**
   * Materials should only be created by the MaterialManager.
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, Fluid fluid, boolean craftable, String materialTextColor) {
    // lowercases and removes whitespaces
    this.identifier = new MaterialId(identifier);
    this.fluid = fluid;
    this.craftable = craftable;
    this.translationKey = Util.makeTranslationKey("material", identifier);
    this.materialTextColor = materialTextColor;
  }

  public Material(ResourceLocation identifier, Fluid fluid, boolean craftable) {
    this(identifier, fluid, craftable, "ffffff");
  }

  @Override
  public String getTranslationKey() {
    return this.translationKey;
  }

  @Override
  public String getEncodedTextColor() {
    int color = Integer.parseInt(this.materialTextColor, 16);

    if((color & 0xFF000000) == 0) {
      color |= 0xFF000000;
    }

    return CustomFontColor.encodeColor(color);
  }

  @Override
  public String getTextColor() {
    return this.materialTextColor;
  }
}
