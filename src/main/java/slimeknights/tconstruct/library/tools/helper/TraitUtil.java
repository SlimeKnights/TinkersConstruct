package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraitUtil {

  public static void forEachTrait(ItemStack stack, Consumer<ITrait> action) {
    // todo
  }

}
