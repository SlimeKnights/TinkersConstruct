package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.function.Supplier;

/** Object wrapper containing ingots, nuggets, and blocks */
public class MetalItemObject extends ItemObject<Block> {
  @Getter
  private final String name;
  private final Supplier<? extends Item> ingot;
  private final Supplier<? extends Item> nugget;
  @Getter
  private final IOptionalNamedTag<Block> blockTag;
  @Getter
  private final IOptionalNamedTag<Item> blockItemTag;
  @Getter
  private final IOptionalNamedTag<Item> ingotTag;
  @Getter
  private final IOptionalNamedTag<Item> nuggetTag;

  public MetalItemObject(String name, ItemObject<? extends Block> block, Supplier<? extends Item> ingot, Supplier<? extends Item> nugget) {
    super(block);
    this.name = name;
    this.ingot = ingot;
    this.nugget = nugget;
    this.blockTag = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/" + name));
    this.blockItemTag = getTag("storage_blocks/" + name);
    this.ingotTag = getTag("ingots/" + name);
    this.nuggetTag = getTag("nuggets/" + name);
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
    return ItemTags.createOptional(new ResourceLocation("forge", name));
  }
}
