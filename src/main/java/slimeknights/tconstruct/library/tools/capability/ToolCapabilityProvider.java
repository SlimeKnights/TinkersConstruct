package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Capability provider for tool stacks, returns the proper cap for  */
public class ToolCapabilityProvider implements ICapabilityProvider {
  private static final List<BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider>> PROVIDER_CONSTRUCTORS = new ArrayList<>();

  private final ItemStack stack;
  private final Lazy<ToolStack> tool;
  private final List<IToolCapabilityProvider> providers;

  public ToolCapabilityProvider(ItemStack stack) {
    // NBT is not yet initialized when capabilities are created, so delay tool stack creation
    this.stack = stack;
    this.tool = Lazy.of(() -> ToolStack.from(stack));
    this.providers = PROVIDER_CONSTRUCTORS.stream().map(con -> con.apply(stack, tool)).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    // clear the tool cache, as it may have changed since the last time a cap was fetched
    ToolStack toolStack = tool.get();
    toolStack.refreshTag(stack);
    // return the first successful provider
    for (IToolCapabilityProvider provider : providers) {
      provider.clearCache();
      LazyOptional<T> optional = provider.getCapability(toolStack, cap);
      if (optional.isPresent()) {
        return optional;
      }
    }
    return LazyOptional.empty();
  }

  /** Registers a tool capability provider constructor. Every new tool will call this constructor to create your provider.
   * Is it valid for this constructor to return null, just note that it will not be called a second time if the tools state changes. Thus you should avoid conditioning on anything other than item type */
  public static void register(BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider> constructor) {
    PROVIDER_CONSTRUCTORS.add(constructor);
  }

  /** Interface to get a capability on a tool */
  @FunctionalInterface
  public interface IToolCapabilityProvider {
    /** Gets a capability on the given tool */
    <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap);

    /** Called to clear the cache of the provider */
    default void clearCache() {}
  }
}
