package slimeknights.tconstruct.library.materials;

import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.Collections;
import java.util.List;

@Getter
public class Material implements IMaterial {
  /** Default white color */
  protected static final Color WHITE = Color.fromInt(0xFFFFFF);

  /** This resource location uniquely identifies a material. */
  private final MaterialId identifier;
  /** Materials tier, mostly for sorting right now */
  private final int tier;
  /** Materials order within the tier, for sorting */
  private final int sortOrder;

  /**
   * The fluid associated with this material, can not be null, but Fluids.EMPTY.
   * If non-null also indicates that the material can be cast.
   */
  protected final Fluid fluid;
  /** Amount of fluid needed to produce one "unit" of this material. */
  private final int fluidPerUnit;
  /** Material can be crafted into parts in the PartBuilder */
  private final boolean craftable;
  /** Key used for localizing the material */
  private final String translationKey;
  /** the text color for this material */
  private final Color color;
  /** Temperature for recipe calculations */
  private final int temperature;
  /** Traits applied by this material */
  private final List<ModifierEntry> traits;

  /**
   * Materials should only be created by the MaterialManager, except when used for data gen
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, int tier, int order, Fluid fluid, int fluidPerUnit, boolean craftable, Color color, int temperature, List<ModifierEntry> traits) {
    this.identifier = new MaterialId(identifier);
    this.tier = tier;
    this.sortOrder = order;
    this.fluid = fluid;
    this.fluidPerUnit = fluidPerUnit;
    this.craftable = craftable;
    this.translationKey = Util.makeTranslationKey("material", identifier);
    this.color = color;
    this.temperature = temperature;
    this.traits = traits;
  }

  protected Material(ResourceLocation identifier, Fluid fluid, boolean craftable) {
    this(identifier, 0, -1, fluid, 0, craftable, WHITE, 0, Collections.emptyList());
  }
}
