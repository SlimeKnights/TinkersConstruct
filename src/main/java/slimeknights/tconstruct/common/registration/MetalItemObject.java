package slimeknights.tconstruct.common.registration;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
    this.blockTag = TagRegistry.block(new Identifier("forge", "storage_blocks/" + tagName));
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
  private static Tag<Item> getTag(String name) {
    return TagRegistry.item(new Identifier("forge", name));
  }
}
