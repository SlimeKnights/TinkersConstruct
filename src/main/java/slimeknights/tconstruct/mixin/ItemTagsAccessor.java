package slimeknights.tconstruct.mixin;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ItemTags.class)
public interface ItemTagsAccessor {

  @Invoker("register")
  public static Tag.Identified<Item> invokeRegister(String id) {
    throw new AssertionError();
  }
}
