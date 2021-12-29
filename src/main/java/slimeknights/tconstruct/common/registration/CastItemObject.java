package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
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
  @Getter
  private final ResourceLocation name;
  private final Supplier<? extends Item> sand;
  private final Supplier<? extends Item> redSand;
  @Getter
  private final IOptionalNamedTag<Item> singleUseTag;
  @Getter
  private final IOptionalNamedTag<Item> multiUseTag;

  public CastItemObject(ResourceLocation name, Item gold, Item sand, Item redSand) {
    super(gold);
    this.name = name;
    this.sand = sand.delegate;
    this.redSand = redSand.delegate;
    this.singleUseTag = makeTag("single_use");
    this.multiUseTag = makeTag("multi_use");
  }

  public CastItemObject(ResourceLocation name, ItemObject<? extends Item> gold, Supplier<? extends Item> sand, Supplier<? extends Item> redSand) {
    super(gold);
    this.name = name;
    this.sand = sand;
    this.redSand = redSand;
    this.singleUseTag = makeTag("single_use");
    this.multiUseTag = makeTag("multi_use");
  }

  /**
   * Gets the single use tag for this object
   * @return  Single use tag
   */
  protected IOptionalNamedTag<Item> makeTag(String type) {
    return ItemTags.createOptional(new ResourceLocation(name.getNamespace(), "casts/" + type + "/" + name.getPath()));
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
