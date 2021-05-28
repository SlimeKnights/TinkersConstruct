package slimeknights.tconstruct.smeltery.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Fluid container holding 1 ingot of fluid
 */
public class CopperCanItem extends Item {
  private static final String TAG_FLUID = "fluid";

  public CopperCanItem(Properties properties) {
    super(properties);
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return new CopperCanFluidHandler(stack);
  }

  @Override
  public boolean hasContainerItem(ItemStack stack) {
    return getFluid(stack) != Fluids.EMPTY;
  }

  @Override
  public ItemStack getContainerItem(ItemStack stack) {
    Fluid fluid = getFluid(stack);
    if (fluid != Fluids.EMPTY) {
      return new ItemStack(this);
    }
    return ItemStack.EMPTY;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    Fluid fluid = getFluid(stack);
    if (fluid != Fluids.EMPTY) {
      tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".contents", new TranslationTextComponent(fluid.getAttributes().getTranslationKey())).mergeStyle(TextFormatting.GRAY));
    } else {
      tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip").mergeStyle(TextFormatting.GRAY));
    }
  }

  /** Sets the fluid on the given stack */
  public static ItemStack setFluid(ItemStack stack, Fluid fluid) {
    // if empty, try to remove the NBT, helps with recipes
    if (fluid == Fluids.EMPTY) {
      CompoundNBT nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(TAG_FLUID);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      CompoundNBT nbt = stack.getOrCreateTag();
      nbt.putString(TAG_FLUID, Objects.requireNonNull(fluid.getRegistryName()).toString());
    }
    return stack;
  }

  /** Gets the fluid from the given stack */
  public static Fluid getFluid(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      ResourceLocation location = ResourceLocation.tryCreate(nbt.getString(TAG_FLUID));
      if (location != null && ForgeRegistries.FLUIDS.containsKey(location)) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(location);
        if (fluid != null) {
          return fluid;
        }
      }
    }
    return Fluids.EMPTY;
  }

  /**
   * Gets a string variant name for the given stack
   * @param stack  Stack instance to check
   * @return  String variant name
   */
  public static String getSubtype(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      return nbt.getString(TAG_FLUID);
    }
    return "";
  }
}
