package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.ClientTextureFluidType;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.function.Consumer;

public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    return PotionUtils.getPotion(stack.getTag()).getName("item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    itemStack.setTag(fluidStack.getTag());
    return itemStack;
  }

  @Override
  public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    consumer.accept(new ClientTextureFluidType(this) {
      /**
       * Gets the color, based on {@link PotionUtils#getColor(ItemStack)}
       * @param stack  Fluid stack instance
       * @return  Color for the fluid
       */
      @Override
      public int getTintColor(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)) {
          return tag.getInt("CustomPotionColor") | 0xFF000000;
        }
        if (PotionUtils.getPotion(tag) == Potions.EMPTY) {
          return getTintColor();
        }
        return PotionUtils.getColor(PotionUtils.getAllEffects(tag)) | 0xFF000000;
      }
    });
  }

  /** Creates a fluid stack for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static FluidStack potionFluid(Potion potion, int size) {
    CompoundTag tag = null;
    if (potion != Potions.EMPTY) {
      tag = new CompoundTag();
      tag.putString("Potion", BuiltInRegistries.POTION.getKey(potion).toString());
    }
    return new FluidStack(TinkerFluids.potion.get(), size, tag);
  }
}
