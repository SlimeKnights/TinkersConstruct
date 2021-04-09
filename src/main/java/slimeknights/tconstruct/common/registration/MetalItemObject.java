package slimeknights.tconstruct.common.registration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.function.Supplier;

/** Object wrapper containing ingots, nuggets, and blocks */
public class MetalItemObject extends ItemObject<Block> {
  private final Supplier<? extends Item> ingot;
  private final Supplier<? extends Item> nugget;
  private final Tag<Block> blockTag;
  private final Tag<Item> blockItemTag;
  private final Tag<Item> ingotTag;
  private final Tag<Item> nuggetTag;

  public MetalItemObject(String tagName, ItemObject<? extends Block> block, Supplier<? extends Item> ingot, Supplier<? extends Item> nugget) {
    super(block);
    this.ingot = ingot;
    this.nugget = nugget;
    this.blockTag = BlockTags.createOptional(new Identifier("forge", "storage_blocks/" + tagName));
    this.blockItemTag = getTag("storage_blocks/" + tagName);
    this.ingotTag = getTag("ingots/" + tagName);
    this.nuggetTag = getTag("nuggets/" + tagName);
  }

  /** Gets the ingot for this object */
  public Item getIngot() {
    return ingot.get();
  }

  /** Gets the ingot for this object */
  public Item getNugget() {
    return nugget.get();
  }

  /**
   * Creates a tag for a resource
   * @param name  Tag name
   * @return  Tag
   */
  private static IOptionalNamedTag<Item> getTag(String name) {
    return ItemTags.createOptional(new Identifier("forge", name));
  }
}
