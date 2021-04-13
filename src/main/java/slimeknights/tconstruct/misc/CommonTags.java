package slimeknights.tconstruct.misc;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;

public class CommonTags {

  public static final Tag<Item> GUNPOWDER = TagRegistry.item(new Identifier("gunpowder"));
  public static final Tag<Item> DUSTS_GLOWSTONE = TagRegistry.item(new Identifier("dusts_glowstone"));
  public static final Tag<Item> GLASS_PANES_COLORLESS = TagRegistry.item(new Identifier("colorless_panes"));
  public static final Tag<Item> GLASS_COLORLESS = TagRegistry.item(new Identifier("colorless_glass"));
  public static final Tag<Item> RODS_WOODEN = TagRegistry.item(new Identifier("wooden_rods"));
  public static final Tag<Item> LEATHER = TagRegistry.item(new Identifier("leather"));
    public static final Tag<Item> STONE = TagRegistry.item(new Identifier("stone"));
  public static final Tag<Item> SLIMEBALLS = TagRegistry.item(new Identifier("slimeballs"));
  public static final Tag<Item> HEADS = TagRegistry.item(new Identifier("heads"));
}
