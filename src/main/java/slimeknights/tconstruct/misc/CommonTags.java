package slimeknights.tconstruct.misc;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;

public class CommonTags {

  public static final Tag<Item> HEADS = TagRegistry.item(new Identifier("heads"));
}
