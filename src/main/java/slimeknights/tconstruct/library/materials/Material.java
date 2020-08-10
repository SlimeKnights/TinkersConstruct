package slimeknights.tconstruct.library.materials;

import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

@Getter
public class Material implements IMaterial {
  /** This resource location uniquely identifies a material. */
  private final MaterialId identifier;

  /**
   * The fluid associated with this material, can not be null, but Fluids.EMPTY.
   * If non-null also indicates that the material can be cast.
   */
  protected final Fluid fluid;

  /** Material can be crafted into parts in the PartBuilder */
  private final boolean craftable;

  /** Key used for localizing the material */
  private final String translationKey;

  /** the unencoded color for the material */
  private final String textColor;

  /** Temperature for recipe calculations */
  private final int temperature;

  /**
   * Materials should only be created by the MaterialManager, except when used for data gen
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, Fluid fluid, boolean craftable, String textColor, int temperature) {
    this.identifier = new MaterialId(identifier);
    this.fluid = fluid;
    this.craftable = craftable;
    this.translationKey = Util.makeTranslationKey("material", identifier);
    this.textColor = textColor;
    this.temperature = temperature;
  }

  protected Material(ResourceLocation identifier, Fluid fluid, boolean craftable) {
    this(identifier, fluid, craftable, "ffffff", 0);
  }
}
