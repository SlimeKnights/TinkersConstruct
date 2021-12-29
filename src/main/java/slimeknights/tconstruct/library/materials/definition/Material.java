package slimeknights.tconstruct.library.materials.definition;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class Material implements IMaterial {
  /** Default white color */
  protected static final TextColor WHITE = TextColor.fromRgb(0xFFFFFF);

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
  private final TextColor color;
  /** if true, this material is hidden */
  @Getter
  private final boolean hidden;
  /** Cache of display name text component */
  private Component displayName = null;
  /** Cache of colored display name text component */
  private Component coloredDisplayName = null;

  /**
   * Materials should only be created by the MaterialManager, except when used for data gen
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, int tier, int order, boolean craftable, TextColor color, boolean hidden) {
    this.identifier = new MaterialId(identifier);
    this.tier = tier;
    this.sortOrder = order;
    this.craftable = craftable;
    this.translationKey = Util.makeDescriptionId("material", identifier);
    this.color = color;
    this.hidden = hidden;
  }

  protected Material(ResourceLocation identifier, boolean craftable, boolean hidden) {
    this(identifier, 0, -1, craftable, WHITE, hidden);
  }

  @Override
  public Component getDisplayName() {
    if (displayName == null) {
      displayName = new TranslatableComponent(getTranslationKey());
    }
    return displayName;
  }

  @Override
  public Component getColoredDisplayName() {
    if (coloredDisplayName == null) {
      coloredDisplayName = new TranslatableComponent(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
    }
    return coloredDisplayName;
  }

  @Override
  public String toString() {
    return "Material{" + identifier + '}';
  }
}
