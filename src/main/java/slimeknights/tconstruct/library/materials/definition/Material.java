package slimeknights.tconstruct.library.materials.definition;

import lombok.Getter;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.ApiStatus.Internal;

/** Base {@link IMaterial} implementation. */
@Getter
public class Material implements IMaterial {
  /** Default white color */
  protected static final TextColor WHITE = TextColor.fromRgb(0xFFFFFF);

  /** This resource location uniquely identifies a material. */
  private final MaterialId identifier;
  /** Materials tier, mostly for sorting right now */
  private final int tier;
  /** Materials order within the tier, for sorting */
  private final int sortOrder;

  /** Material can be crafted into parts in the PartBuilder */
  private final boolean craftable;
  /** if true, this material is hidden */
  private final boolean hidden;
  /** Rarity for this material's name */
  private final Rarity rarity;

  /**
   * Materials should only be created by the MaterialManager, except when used for data gen
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  @Internal
  public Material(ResourceLocation identifier, int tier, int order, Rarity rarity, boolean craftable, boolean hidden) {
    this.identifier = new MaterialId(identifier);
    this.tier = tier;
    this.sortOrder = order;
    this.rarity = rarity;
    this.craftable = craftable;
    this.hidden = hidden;
  }

  /** @deprecated use {@link #Material(ResourceLocation, int, int, Rarity, boolean, boolean)} */
  @Deprecated(forRemoval = true)
  public Material(ResourceLocation identifier, int tier, int order, boolean craftable, boolean hidden) {
    this(identifier, tier, order, IMaterial.computeRarity(tier), craftable, hidden);
  }

  protected Material(ResourceLocation identifier, boolean craftable, boolean hidden) {
    this(identifier, 0, -1, Rarity.COMMON, craftable, hidden);
  }

  @Override
  public String toString() {
    return "Material{" + identifier + '}';
  }
}
