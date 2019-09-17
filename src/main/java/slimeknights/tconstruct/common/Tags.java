package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class Tags {

  public static class Blocks {

    public static final Tag<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag<Block> COBBLE_STONE = tag("cobblestone_stone");

    private static Tag<Block> tag(String name) {
      return new BlockTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<Block> forgeTag(String name) {
      return new BlockTags.Wrapper(new ResourceLocation("forge", name));
    }
  }

  public static class Items {

    public static final Tag<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag<Item> COBBLE_STONE = tag("cobblestone_stone");

    private static Tag<Item> tag(String name) {
      return new ItemTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<Item> forgeTag(String name) {
      return new ItemTags.Wrapper(new ResourceLocation("forge", name));
    }
  }
}
