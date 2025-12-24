package slimeknights.tconstruct.tools.modules;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module implementing the overburn modifier.
 * TODO 1.21: move to {@link slimeknights.tconstruct.tools.modules.durability}
 */
public enum OverburnModule implements ModifierModule, InventoryTickModifierHook, ModifierRemovalHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OverburnModule>defaultHooks(ModifierHooks.INVENTORY_TICK, ModifierHooks.REMOVE);

  @Getter
  private final SingletonLoader<OverburnModule> loader = new SingletonLoader<>(this);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }

  /**
   * Keeps track of fuel consumed on the tool
   * @param expiration  Tick when this fuel info is discarded
   * @param rate        Fuel rate, gives more or less overslime
   */
  private record FuelInfo(long expiration, int rate) {
    private static final String EXPIRATION = "expiration";
    private static final String RATE = "rate";

    /** Reads the info from the tool */
    @Nullable
    public static FuelInfo read(IToolStackView tool, ResourceLocation location) {
      ModDataNBT persistentData = tool.getPersistentData();
      if (persistentData.contains(location, Tag.TAG_COMPOUND)) {
        CompoundTag tag = persistentData.getCompound(location);
        return new FuelInfo(tag.getLong(EXPIRATION), tag.getInt(RATE));
      }
      return null;
    }

    /** Writes the info to the tool */
    public void write(IToolStackView tool, ResourceLocation location) {
      CompoundTag tag = new CompoundTag();
      tag.putLong(EXPIRATION, expiration);
      tag.putInt(RATE, rate);
      tool.getPersistentData().put(location, tag);
    }
  }

  @Override
  public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    // overslime increases every 2^(4-level) seconds
    // that is 1 per second at 4 levels, 1 per 8 seconds at 1 level
    // we support max level 6, as that leaves us working with integers
    int level = Math.min(modifier.getLevel(), 6);
    int updateInterval = 5 << (6 - level);

    // don't run if drawing back a bow, prevents losing animation
    // does mean you may end up wasting some fuel, could be as much as 19 lost. So, don't hold your bows for 20 updates?
    if (!world.isClientSide && holder.tickCount % updateInterval == 0 && holder.getUseItem() != stack) {
      // must have overslime and space to fill
      if (OverslimeModule.INSTANCE.getAmount(tool) < OverslimeModule.getCapacity(tool)) {
        // find current fuel info
        ResourceLocation key = modifier.getId();
        FuelInfo info = FuelInfo.read(tool, key);

        // if we have no fuel, try and find some
        boolean neededFuel = info == null;
        // since we will write this to NBT, use game time as that is global
        long time = holder.level().getGameTime();
        if (neededFuel || info.expiration < time) {
          info = null;
          FluidStack fluid = ToolTankHelper.TANK_HELPER.getFluid(tool);
          if (!fluid.isEmpty()) {
            MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid.getFluid());
            if (fuel != null) {
              // scale amount consumed by level
              int amount = fuel.getAmount(fluid.getFluid());
              // scale up fuel duration so we always get the same amount of overslime per fuel bucket
              // if we didn't do this, lower levels would consume way more than higher ones
              // this works out to equivalent to the alloyer/melter at level 4
              // note this does mean changing trait levels multiplies fuel efficiency, but the part swap costs more than just adding a slimeball so its fine
              int duration = fuel.getDuration() * updateInterval / 5;
              // if we don't have a full recipe, use what is left but scale down the duration
              if (amount > fluid.getAmount()) {
                ToolTankHelper.TANK_HELPER.setFluid(tool, FluidStack.EMPTY);
                duration = duration * fluid.getAmount() / amount;
              } else {
                // if we have a complete recipe, just decrease fluid in the tank
                fluid.shrink(amount);
                ToolTankHelper.TANK_HELPER.setFluid(tool, fluid);
              }
              info = new FuelInfo(time + duration, fuel.getRate());
            }
          }
          // store current fuel in NBT for next round if it will last
          // no need to store if it will expire before our next update tick though
          if (info != null && info.expiration >= time + updateInterval) {
            info.write(tool, key);
          } else if (!neededFuel) {
            // no need to remove if we had nothing, saves some hash map modifications
            tool.getPersistentData().remove(key);
          }
        }
        // if we have fuel, increase overslime
        if (info != null) {
          // restore 1 per 10 rate. For the remainder, treat it as a chance
          int restore = info.rate / 10;
          int remainder = info.rate % 10;
          if (remainder > 0 && Modifier.RANDOM.nextInt(10) < remainder) {
            restore++;
          }
          OverslimeModule.INSTANCE.addAmount(tool, restore);
        }
      }
    }
  }
}
