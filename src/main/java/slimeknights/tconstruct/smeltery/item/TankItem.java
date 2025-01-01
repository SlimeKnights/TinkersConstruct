package slimeknights.tconstruct.smeltery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class TankItem extends BlockTooltipItem {
  private static final String KEY_FLUID = TConstruct.makeTranslationKey("block", "tank.fluid");
  private static final String KEY_MB = TConstruct.makeTranslationKey("block", "tank.mb");
  private static final String KEY_INGOTS = TConstruct.makeTranslationKey("block", "tank.ingots");
  private static final String KEY_MIXED = TConstruct.makeTranslationKey("block", "tank.mixed");

  private final boolean limitStackSize;
  public TankItem(Block blockIn, Properties builder, boolean limitStackSize) {
    super(blockIn, builder);
    this.limitStackSize = limitStackSize;
  }

  /** Checks if the tank item is filled */
  private static boolean isFilled(ItemStack stack) {
    // has a container if not empty
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.contains(NBTTags.TANK, Tag.TAG_COMPOUND);
  }

  @Override
  public boolean hasCraftingRemainingItem(ItemStack stack) {
    return isFilled(stack);
  }

  @Override
  public ItemStack getCraftingRemainingItem(ItemStack stack) {
    return isFilled(stack) ? new ItemStack(this) : ItemStack.EMPTY;
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    if (!limitStackSize) {
      return super.getMaxStackSize(stack);
    }
    return isFilled(stack) ? 16: 64;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    if (stack.hasTag()) {
      FluidTank tank = getFluidTank(stack);
      if (tank.getFluidAmount() > 0) {
        // TODO: migrate to a fluid tooltip JSON?
        tooltip.add(Component.translatable(KEY_FLUID, tank.getFluid().getDisplayName()).withStyle(ChatFormatting.GRAY));
        int amount = tank.getFluidAmount();
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (tank.getCapacity() % FluidValues.INGOT != 0 || key == TooltipKey.SHIFT) {
          tooltip.add(Component.translatable(KEY_MB, amount).withStyle(ChatFormatting.GRAY));
        } else {
          int ingots = amount / FluidValues.INGOT;
          int mb = amount % FluidValues.INGOT;
          if (mb == 0) {
            tooltip.add(Component.translatable(KEY_INGOTS, ingots).withStyle(ChatFormatting.GRAY));
          } else {
            tooltip.add(Component.translatable(KEY_MIXED, ingots, mb).withStyle(ChatFormatting.GRAY));
          }
          if (key != TooltipKey.UNKNOWN) {
            tooltip.add(FluidTooltipHandler.HOLD_SHIFT);
          }
        }

      }
    }
    else {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new TankItemFluidHandler(stack);
  }

  /**
   * Sets the tank to the given stack
   * @param stack  Stack
   * @param tank   Tank instance
   * @return  Stack with tank
   */
  public static ItemStack setTank(ItemStack stack, FluidTank tank) {
    if (tank.isEmpty()) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(NBTTags.TANK);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      stack.getOrCreateTag().put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    }
    return stack;
  }

  /**
   * Sets the tank to the given stack
   * @param stack  Stack
   * @param fluid  Fluid
   * @return  Stack with tank
   */
  public static ItemStack setTank(ItemStack stack, FluidStack fluid) {
    if (fluid.isEmpty()) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(NBTTags.TANK);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      stack.getOrCreateTag().put(NBTTags.TANK, fluid.writeToNBT(new CompoundTag()));
    }
    return stack;
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    FluidTank tank = new FluidTank(TankBlockEntity.getCapacity(stack.getItem()));
    if (stack.hasTag()) {
      assert stack.getTag() != null;
      tank.readFromNBT(stack.getTag().getCompound(NBTTags.TANK));
    }
    return tank;
  }

  /**
   * Gets a string variant name for the given stack
   * @param stack  Stack instance to check
   * @return  String variant name
   */
  public static String getSubtype(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && nbt.contains(NBTTags.TANK, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(NBTTags.TANK).getString("FluidName");
    }
    return "";
  }

  /** Adds filled variants of all standard tank items to the given consumer */
  @SuppressWarnings("deprecation")
  public static void addFilledVariants(Consumer<ItemStack> output) {
    for (Fluid fluid : BuiltInRegistries.FLUID) {
      if (fluid.isSource(fluid.defaultFluidState()) && !fluid.is(TinkerTags.Fluids.HIDE_IN_CREATIVE)) {
        // use an ingot variety for metals
        TankType tank, gauge;
        if (fluid.is(TinkerTags.Fluids.METAL_TOOLTIPS)) {
          tank = TankType.INGOT_TANK;
          gauge = TankType.INGOT_GAUGE;
        } else {
          tank = TankType.FUEL_TANK;
          gauge = TankType.FUEL_GAUGE;
        }
        output.accept(setTank(new ItemStack(TinkerSmeltery.searedLantern), new FluidStack(fluid, FluidValues.LANTERN_CAPACITY)));
        output.accept(fillTank(TinkerSmeltery.searedTank, tank, fluid));
        output.accept(fillTank(TinkerSmeltery.searedTank, gauge, fluid));
        output.accept(setTank(new ItemStack(TinkerSmeltery.scorchedLantern), new FluidStack(fluid, FluidValues.LANTERN_CAPACITY)));
        output.accept(fillTank(TinkerSmeltery.scorchedTank, tank, fluid));
        output.accept(fillTank(TinkerSmeltery.scorchedTank, gauge, fluid));
      }
    }
  }

  /** Fills a tank stack with the given fluid */
  public static ItemStack fillTank(EnumObject<TankType,? extends ItemLike> tank, TankType type, Fluid fluid) {
    return TankItem.setTank(new ItemStack(tank.get(type)), new FluidStack(fluid, type.getCapacity()));
  }
}
