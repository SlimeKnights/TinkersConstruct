package slimeknights.tconstruct.library.materials.definition;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Material implements IMaterial {
  /** Default white color */
  protected static final Color WHITE = Color.fromInt(0xFFFFFF);

  /** This resource location uniquely identifies a material. */
  @Getter
  private final MaterialId identifier;
  /** Materials tier, mostly for sorting right now */
  @Getter
  private final int tier;
  /** Materials order within the tier, for sorting */
  @Getter
  private final int sortOrder;

  /** Material can be crafted into parts in the PartBuilder */
  @Getter
  private final boolean craftable;
  /** Key used for localizing the material */
  @Getter
  private final String translationKey;
  /** the text color for this material */
  @Getter
  private final Color color;
  /** if true, this material is hidden */
  @Getter
  private final boolean hidden;
  /** Cache of display name text component */
  private ITextComponent displayName = null;
  /** Cache of colored display name text component */
  private ITextComponent coloredDisplayName = null;

  /**
   * Materials should only be created by the MaterialManager, except when used for data gen
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, int tier, int order, boolean craftable, Color color, boolean hidden) {
    this.identifier = new MaterialId(identifier);
    this.tier = tier;
    this.sortOrder = order;
    this.craftable = craftable;
    this.translationKey = Util.makeTranslationKey("material", identifier);
    this.color = color;
    this.hidden = hidden;
  }

  protected Material(ResourceLocation identifier, boolean craftable, boolean hidden) {
    this(identifier, 0, -1, craftable, WHITE, hidden);
  }

  @Override
  public ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(getTranslationKey());
    }
    return displayName;
  }

  @Override
  public ITextComponent getColoredDisplayName() {
    if (coloredDisplayName == null) {
      coloredDisplayName = new TranslationTextComponent(getTranslationKey()).modifyStyle(style -> style.setColor(getColor()));
    }
    return coloredDisplayName;
  }

  @Override
  public String toString() {
    return "Material{" + identifier + '}';
  }
}
