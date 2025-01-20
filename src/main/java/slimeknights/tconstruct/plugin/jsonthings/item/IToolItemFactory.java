package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.serializers.IItemFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.Objects;

/** Extension of {@link IItemFactory} for tool items */
public interface IToolItemFactory<T extends Item & IModifiable> extends IItemFactory<T> {
  @Override
  default void provideVariants(ResourceKey<CreativeModeTab> tabKey, Output output, ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit) {
    if (Objects.requireNonNull(context).get().self() instanceof IModifiable modifiable) {
      ToolBuildHandler.addVariants(output::accept, modifiable, "");
    }
  }
}
