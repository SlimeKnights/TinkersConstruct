package slimeknights.tconstruct.library.modifiers.modules.fluid;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability.FluidModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Module granting a tool a tank.
 * TODO: cleanup fluid keys, ideally JSON only would specify one key
 */
public class TankModule extends TankCapacityModule implements FluidModifierHook, TooltipModifierHook {
  private static final String FILLED_KEY = TConstruct.makeTranslationKey("modifier", "tank.filled");
  private static final String CAPACITY_KEY = TConstruct.makeTranslationKey("modifier", "tank.capacity");
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA, ToolFluidCapability.HOOK, TinkerHooks.TOOLTIP);
  /** Default key for owner */
  public static final ResourceLocation DEFAULT_OWNER_KEY = TConstruct.getResource("tank_owner");
  /** Default key for fluid */
  public static final ResourceLocation DEFAULT_FLUID_KEY = TConstruct.getResource("tank_fluid");
  /** Loader instance */
  public static final RecordLoadable<TankModule> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.defaultField("capacity_key", DEFAULT_CAPACITY_KEY, TankCapacityModule::getCapacityKey),
    IntLoadable.FROM_ZERO.field("capacity", TankCapacityModule::getCapacity),
    BooleanLoadable.INSTANCE.field("scale_capacity", TankCapacityModule::isScaleCapacity),
    Loadables.RESOURCE_LOCATION.defaultField("fluid_key", DEFAULT_FLUID_KEY, TankModule::getFluidKey),
    Loadables.RESOURCE_LOCATION.defaultField("owner_key", DEFAULT_OWNER_KEY, TankModule::getOwnerKey),
    TankModule::new);

  /** Helper function to parse a fluid from NBT */
  public static final BiFunction<CompoundTag, String, FluidStack> PARSE_FLUID = (nbt, key) -> FluidStack.loadFluidStackFromNBT(nbt.getCompound(key));

  /** Volatile NBT string indicating which modifier is in charge of logic for the one tank */
  @Getter
  private final ResourceLocation ownerKey;
  /** Persistent NBT compound containing the fluid in the tank */
  @Getter
  private final ResourceLocation fluidKey;

  public TankModule(ResourceLocation capacityKey, int capacity, boolean scaleCapacity, ResourceLocation fluidKey, ResourceLocation ownerKey) {
    super(capacityKey, capacity, scaleCapacity);
    this.ownerKey = ownerKey;
    this.fluidKey = fluidKey;
  }

  public TankModule(int capacity, boolean scaleCapacity) {
    this(DEFAULT_CAPACITY_KEY, capacity, scaleCapacity, DEFAULT_FLUID_KEY, DEFAULT_OWNER_KEY);
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (isOwner(tool, modifier.getId())) {
      FluidStack current = getFluid(tool);
      if (!current.isEmpty()) {
        tooltip.add(Component.translatable(FILLED_KEY, current.getAmount(), current.getDisplayName()));
      }
      tooltip.add(Component.translatable(CAPACITY_KEY, getCapacity(tool)));
    }
  }


  /* Properties */

  /** Checks if the given modifier is the owner of the tank */
  public boolean isOwner(IToolContext tool, ModifierId modifier) {
    ResourceLocation key = getOwnerKey();
    if (key == null) {
      return true;
    }
    return modifier.toString().equals(tool.getVolatileData().getString(key));
  }

  // TODO: may be worth separating tanks vs unique tanks, unique tanks are used for drain/fill hooks while total tanks for anyone interacting directly such as modifiers
  @Override
  public int getTanks(IToolContext tool, Modifier modifier) {
    return isOwner(tool, modifier.getId()) ? 1 : 0;
  }

  @Override
  public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
    return getCapacity(tool);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    super.addVolatileData(context, modifier, volatileData);
    ResourceLocation ownerKey = getOwnerKey();
    if (!volatileData.contains(ownerKey, Tag.TAG_STRING)) {
      volatileData.putString(ownerKey, modifier.getId().toString());
    }
    ToolFluidCapability.addTanks(context, modifier.getModifier(), volatileData, this);
  }


  /* Fluid setting */

  /** Gets the fluid in the tank */
  public FluidStack getFluid(IToolStackView tool) {
    return tool.getPersistentData().get(getFluidKey(), PARSE_FLUID);
  }

  /** Sets the fluid in the tank */
  public FluidStack setFluid(IToolStackView tool, FluidStack fluid) {
    if (fluid.isEmpty()) {
      tool.getPersistentData().remove(getFluidKey());
      return fluid;
    }
    int capacity = getCapacity(tool);
    if (fluid.getAmount() > capacity) {
      fluid.setAmount(capacity);
    }
    tool.getPersistentData().put(getFluidKey(), fluid.writeToNBT(new CompoundTag()));
    return fluid;
  }

  @Override
  public FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
    return getFluid(tool);
  }


  /* Filling and draining */

  @Override
  public int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
    // make sure this modifier is in charge of the tank, that is first come first serve
    if (!resource.isEmpty() && isOwner(tool, modifier.getId())) {
      // if empty, just directly fill, setFluid will check capacity
      FluidStack current = getFluid(tool);
      int capacity = getCapacity(tool);
      if (current.isEmpty()) {
        if (action.execute()) {
          setFluid(tool, resource);
        }
        return Math.min(resource.getAmount(), capacity);
      }
      // if the fluid matches and we have space, update
      if (current.getAmount() < capacity && current.isFluidEqual(resource)) {
        int filled = Math.min(resource.getAmount(), capacity - current.getAmount());
        if (filled > 0 && action.execute()) {
          current.grow(filled);
          setFluid(tool, current);
        }
        return filled;
      }
    }
    return 0;
  }

  @Override
  public FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
    if (!resource.isEmpty() && isOwner(tool, modifier.getId())) {
      // ensure we have something and it matches the request
      FluidStack current = getFluid(tool);
      if (!current.isEmpty() && current.isFluidEqual(resource)) {
        // create the drained stack
        FluidStack drained = new FluidStack(current, Math.min(current.getAmount(), resource.getAmount()));
        // if executing, removing it
        if (action.execute()) {
          if (drained.getAmount() == current.getAmount()) {
            setFluid(tool, FluidStack.EMPTY);
          } else {
            current.shrink(drained.getAmount());
            setFluid(tool, current);
          }
        }
        return drained;
      }
    }
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, FluidAction action) {
    if (maxDrain > 0 && isOwner(tool, modifier.getId())) {
      // ensure we have something and it matches the request
      FluidStack current = getFluid(tool);
      if (!current.isEmpty()) {
        // create the drained stack
        FluidStack drained = new FluidStack(current, Math.min(current.getAmount(), maxDrain));
        // if executing, removing it
        if (action.execute()) {
          if (drained.getAmount() == current.getAmount()) {
            setFluid(tool, FluidStack.EMPTY);
          } else {
            current.shrink(drained.getAmount());
            setFluid(tool, current);
          }
        }
        return drained;
      }
    }
    return FluidStack.EMPTY;
  }


  /* Module logic */

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
