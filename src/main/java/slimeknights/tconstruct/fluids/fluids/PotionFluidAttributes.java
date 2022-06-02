package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/** Fluid attributes to color potion fluids */
public class PotionFluidAttributes extends FluidAttributes {
  private static final int EMPTY_COLOR = 0xf800f8;

  /** Creates a new builder */
  public static FluidAttributes.Builder builder(ResourceLocation texturePrefix) {
    String modId = texturePrefix.getNamespace();
    String path = texturePrefix.getPath();
    return new Builder(new ResourceLocation(modId, path + "still"), new ResourceLocation(modId, path + "flowing"), PotionFluidAttributes::new);
  }

  protected PotionFluidAttributes(FluidAttributes.Builder builder, Fluid fluid) {
    super(builder, fluid);
  }

  @Override
  public String getTranslationKey() {
    return "item.minecraft.potion.effect.empty";
  }

  @Override
  public String getTranslationKey(FluidStack stack) {
    return PotionUtils.getPotion(stack.getTag()).getName("item.minecraft.potion.effect.");
  }

  @Override
  public Component getDisplayName(FluidStack stack) {
    // stupid forge, not calling the stack sensitive translation key in super...
    return new TranslatableComponent(getTranslationKey(stack));
  }

  @Override
  public int getColor() {
    return EMPTY_COLOR | 0xFF000000;
  }

  @Override
  public int getColor(FluidStack stack) {
    return getColor(stack.getTag()) | 0xFF000000;
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    itemStack.setTag(fluidStack.getTag());
    return itemStack;
  }

  /** Gets the color from a fluid tag, based on potion utils */
  private static int getColor(@Nullable CompoundTag tag) {
    if (tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)) {
      return tag.getInt("CustomPotionColor");
    }
    if (PotionUtils.getPotion(tag) == Potions.EMPTY) {
      return EMPTY_COLOR;
    }
    return PotionUtils.getColor(PotionUtils.getAllEffects(tag));
  }

  /** Fluid attributes for the potion fluid */
  private static class Builder extends FluidAttributes.Builder {
    protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
      super(stillTexture, flowingTexture, factory);
    }
  }
}
