package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Deferred wrapper holding gold, sand, and red sand casts
 */
public class CastItemObject extends ItemObject<Item> {
  private final Supplier<? extends Item> sand;
  private final Supplier<? extends Item> redSand;
  @Getter
  private final IOptionalNamedTag<Item> singleUseTag;

  public CastItemObject(Item gold, Item sand, Item redSand) {
    super(gold);
    this.sand = sand.delegate;
    this.redSand = redSand.delegate;
    singleUseTag = makeSingleUseTag();
  }

  public CastItemObject(ItemObject<? extends Item> gold, Supplier<? extends Item> sand, Supplier<? extends Item> redSand) {
    super(gold);
    this.sand = sand;
    this.redSand = redSand;
    singleUseTag = makeSingleUseTag();
  }

  /**
   * Gets the single use tag for this object
   * @return  Single use tag
   */
  protected IOptionalNamedTag<Item> makeSingleUseTag() {
    ResourceLocation id = getRegistryName();
    return ItemTags.createOptional(new ResourceLocation(id.getNamespace(), "casts/single_use/" + id.getPath()));
  }

  /**
   * Gets the yellow sand variant
   * @return  Yellow sand variant
   */
  public Item getSand() {
    return Objects.requireNonNull(this.sand.get(), "CastItemObject missing sand");
  }

  /**
   * Gets the red sand variant
   * @return  Red sand variant
   */
  public Item getRedSand() {
    return Objects.requireNonNull(this.redSand.get(), "CastItemObject missing red sand");
  }

  /**
   * Gets a list of all variants
   * @return  All variants
   */
  public List<Item> values() {
    return Arrays.asList(this.get(), this.getSand(), this.getRedSand());
  }
}
