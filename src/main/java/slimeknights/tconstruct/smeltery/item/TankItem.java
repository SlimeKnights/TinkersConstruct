package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class TankItem extends BlockTooltipItem {
  private static final String KEY_FLUID = Util.makeTranslationKey("block", "tank.fluid");
  private static final String KEY_MB = Util.makeTranslationKey("block", "tank.mb");
  private static final String KEY_INGOTS = Util.makeTranslationKey("block", "tank.ingots");
  private static final String KEY_MIXED = Util.makeTranslationKey("block", "tank.mixed");

  private final boolean limitStackSize;
  public TankItem(Block blockIn, Properties builder, boolean limitStackSize) {
    super(blockIn, builder);
    this.limitStackSize = limitStackSize;
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    if (!limitStackSize) {
      return super.getItemStackLimit(stack);
    }
    FluidTank tank = getFluidTank(stack);
    return tank.isEmpty() ? 64 : 16;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (stack.hasTag()) {
      FluidTank tank = getFluidTank(stack);
      if (tank.getFluidAmount() > 0) {
        tooltip.add(new TranslationTextComponent(KEY_FLUID, tank.getFluid().getDisplayName()).mergeStyle(TextFormatting.GRAY));
        int amount = tank.getFluidAmount();
        if (tank.getCapacity() % MaterialValues.INGOT != 0 || Screen.hasShiftDown()) {
          tooltip.add(new TranslationTextComponent(KEY_MB, amount).mergeStyle(TextFormatting.GRAY));
        } else {
          int ingots = amount / MaterialValues.INGOT;
          int mb = amount % MaterialValues.INGOT;
          if (mb == 0) {
            tooltip.add(new TranslationTextComponent(KEY_INGOTS, ingots).mergeStyle(TextFormatting.GRAY));
          } else {
            tooltip.add(new TranslationTextComponent(KEY_MIXED, ingots, mb).mergeStyle(TextFormatting.GRAY));
          }
          tooltip.add(FluidTooltipHandler.HOLD_SHIFT);
        }

      }
    }
    else {
      super.addInformation(stack, worldIn, tooltip, flagIn);
    }
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
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
      CompoundNBT nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(NBTTags.TANK);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      stack.getOrCreateTag().put(NBTTags.TANK, tank.writeToNBT(new CompoundNBT()));
    }
    return stack;
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    FluidTank tank = new FluidTank(TankTileEntity.getCapacity(stack.getItem()));
    if (stack.hasTag()) {
      assert stack.getTag() != null;
      tank.readFromNBT(stack.getTag().getCompound(NBTTags.TANK));
    }
    return tank;
  }
}
