package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
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
  private final Identifier name;
  private final Supplier<? extends Item> sand;
  private final Supplier<? extends Item> redSand;
  @Getter
  private final IOptionalNamedTag<Item> singleUseTag;

  public CastItemObject(Identifier name, Item gold, Item sand, Item redSand) {
    super(gold);
    this.name = name;
    this.sand = sand.delegate;
    this.redSand = redSand.delegate;
    singleUseTag = makeSingleUseTag();
  }

  public CastItemObject(Identifier name, ItemObject<? extends Item> gold, Supplier<? extends Item> sand, Supplier<? extends Item> redSand) {
    super(gold);
    this.name = name;
    this.sand = sand;
    this.redSand = redSand;
    singleUseTag = makeSingleUseTag();
  }

  /**
   * Gets the single use tag for this object
   * @return  Single use tag
   */
  protected IOptionalNamedTag<Item> makeSingleUseTag() {
    return ItemTags.createOptional(new Identifier(name.getNamespace(), "casts/single_use/" + name.getPath()));
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
