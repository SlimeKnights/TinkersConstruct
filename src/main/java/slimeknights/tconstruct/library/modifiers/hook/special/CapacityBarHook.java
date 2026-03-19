package slimeknights.tconstruct.library.modifiers.hook.special;

import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarValidator;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Hook for a capacity bar on a tool, typically used for durability. */
public interface CapacityBarHook {
  /** Gets the current amount. */
  int getAmount(IToolStackView tool);

  /** Gets the capacity for the given tool and modifier entry. */
  int getCapacity(IToolStackView tool, ModifierEntry entry);

  /** Sets the amount on the tool. Note that {@code entry} may be {@link ModifierEntry#EMPTY} indicating removal. */
  void setAmount(IToolStackView tool, ModifierEntry entry, int amount);

  /** Adds the given amount to the current shield. */
  default void addAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    setAmount(tool, modifier, getAmount(tool) + amount);
  }

  /** Adds the given amount to the current shield. */
  default void removeAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    setAmount(tool, modifier, getAmount(tool) - amount);
  }

  /** Capacity bar implementation storing data in persistent data */
  @RequiredArgsConstructor
  abstract class PersistentDataCapacityBar implements CapacityBarHook, HookProvider {
    private static final List<ModuleHook<?>> HOOKS = HookProvider.<PersistentDataCapacityBar>defaultHooks(ModifierHooks.CAPACITY_BAR);
    protected final ResourceLocation key;

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return HOOKS;
    }

    @Override
    public void addModules(Builder builder) {
      builder.addModule(new CapacityBarValidator(this));
    }

    @Override
    public int getAmount(IToolStackView tool) {
      return tool.getPersistentData().getInt(key);
    }

    @Override
    public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {
      if (amount <= 0) {
        tool.getPersistentData().remove(key);
      } else {
        tool.getPersistentData().putInt(key, Math.min(amount, getCapacity(tool, entry)));
      }
    }
  }
}
