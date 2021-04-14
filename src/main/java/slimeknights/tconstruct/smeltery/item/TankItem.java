package slimeknights.tconstruct.smeltery.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.fluids.FluidTank;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.List;

public class TankItem extends BlockTooltipItem {
  private static final String KEY_FLUID = Util.makeTranslationKey("block", "tank.fluid");
  private static final String KEY_MB = Util.makeTranslationKey("block", "tank.mb");
  private static final String KEY_INGOTS = Util.makeTranslationKey("block", "tank.ingots");
  private static final String KEY_MIXED = Util.makeTranslationKey("block", "tank.mixed");

  public TankItem(Block blockIn, Settings builder) {
    super(blockIn, builder);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
//    if (stack.hasTag()) {
//      FluidTank tank = getFluidTank(stack);
//      if (tank.getFluidAmount() > 0) {
//        tooltip.add(new TranslatableText(KEY_FLUID, tank.getFluid().getDisplayName()).formatted(Formatting.GRAY));
//        int amount = tank.getFluidAmount();
//        if (tank.getCapacity() % FluidAttributes.BUCKET_VOLUME == 0 || Screen.hasShiftDown()) {
//          tooltip.add(new TranslatableText(KEY_MB, amount).formatted(Formatting.GRAY));
//        } else {
//          int ingots = amount / MaterialValues.INGOT;
//          int mb = amount % MaterialValues.INGOT;
//          if (mb == 0) {
//            tooltip.add(new TranslatableText(KEY_INGOTS, ingots).formatted(Formatting.GRAY));
//          } else {
//            tooltip.add(new TranslatableText(KEY_MIXED, ingots, mb).formatted(Formatting.GRAY));
//          }
//          tooltip.add(FluidTooltipHandler.HOLD_SHIFT);
//        }
//
//      }
//    }
//    else {
//      super.appendTooltip(stack, worldIn, tooltip, flagIn);
//    }
    // FIXME: PORT
    super.appendTooltip(stack, worldIn, tooltip, flagIn);
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    throw new RuntimeException("CRAB!"); // FIXME: PORT
//    FluidTank tank = new FluidTank(TankTileEntity.getCapacity(stack.getItem()));
//    if (stack.hasTag()) {
//      assert stack.getTag() != null;
//      tank.readFromNBT(stack.getTag().getCompound(Tags.TANK));
//    }
//    return tank;
  }
}
