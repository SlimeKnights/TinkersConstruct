package slimeknights.tconstruct.common.registration;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagRegistry;
import slimeknights.mantle.registration.object.ItemObject;

/** Object wrapper containing ingots, nuggets, and blocks */
public class MetalItemObject extends ItemObject<Block> {
  private final Supplier<? extends Item> ingot;
  private final Supplier<? extends Item> nugget;
  private final Tag.Identified<Block> blockTag;
  private final Tag.Identified<Item> blockItemTag;
  private final Tag.Identified<Item> ingotTag;
  private final Tag.Identified<Item> nuggetTag;

  public MetalItemObject(String tagName, ItemObject<? extends Block> block, Supplier<? extends Item> ingot, Supplier<? extends Item> nugget) {
    super(block);
    this.ingot = ingot;
    this.nugget = nugget;
    this.blockTag = (Tag.Identified<Block>) TagRegistry.block(new Identifier("c", "storage_blocks/" + tagName));
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
  private static Tag.Identified<Item> getTag(String name) {
    return (Tag.Identified<Item>) TagRegistry.item(new Identifier("c", name));
  }

  public Tag.Identified<Block> getBlockTag() {
    return this.blockTag;
  }

  public Tag.Identified<Item> getBlockItemTag() {
    return this.blockItemTag;
  }

  public Tag.Identified<Item> getIngotTag() {
    return this.ingotTag;
  }

  public Tag.Identified<Item> getNuggetTag() {
    return this.nuggetTag;
  }
}
