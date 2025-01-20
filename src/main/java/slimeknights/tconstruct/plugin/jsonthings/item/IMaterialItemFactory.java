package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.serializers.IItemFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Objects;

/** Extension of {@link IItemFactory} for material items */
public interface IMaterialItemFactory<T extends Item & IMaterialItem> extends IItemFactory<T> {
  @Override
  default void provideVariants(ResourceKey<CreativeModeTab> tabKey, Output output, ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit) {
    if (Objects.requireNonNull(context).getItem() instanceof IMaterialItem materialItem) {
      materialItem.addVariants(output::accept, "");
    }
  }
}
