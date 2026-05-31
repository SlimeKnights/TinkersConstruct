package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import slimeknights.tconstruct.compat.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.ClientTextureFluidType;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    return Potion.getName(Optional.of(PotionUtils.getPotion(TagUtil.getTag(stack))), "item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    TagUtil.setTag(itemStack, TagUtil.getTag(fluidStack));
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
        CompoundTag tag = TagUtil.getTag(stack);
        if (tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)) {
          return tag.getInt("CustomPotionColor") | 0xFF000000;
        }
        if (PotionUtils.getPotion(tag).is(Potions.WATER)) {
          return getTintColor();
        }
        return PotionUtils.getColor(PotionUtils.getAllEffects(tag)) | 0xFF000000;
      }
    });
  }

  /** Creates the potion tag */
  private static CompoundTag potionTag(ResourceLocation location) {
    CompoundTag tag = new CompoundTag();
    tag.putString("Potion", location.toString());
    return tag;
  }

  /** Creates a potion fluid stack with legacy potion custom data. */
  private static FluidStack potionFluid(@Nullable CompoundTag tag, int size) {
    FluidStack stack = new FluidStack(TinkerFluids.potion.get(), size);
    TagUtil.setTag(stack, tag);
    return stack;
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(ResourceKey<Potion> potion, int size) {
    CompoundTag tag = null;
    if (!potion.location().equals(Potions.WATER.unwrapKey().orElseThrow().location())) {
      tag = potionTag(potion.location());
    }
    return potionFluid(tag, size);
  }

  /** Creates a fluid stack for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static FluidStack potionFluid(Potion potion, int size) {
    CompoundTag tag = null;
    Holder<Potion> holder = BuiltInRegistries.POTION.wrapAsHolder(potion);
    if (!holder.is(Potions.WATER)) {
      tag = potionTag(BuiltInRegistries.POTION.getKey(potion));
    }
    return potionFluid(tag, size);
  }

  /** Creates a fluid output for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static FluidOutput potionResult(Potion potion, int size) {
    CompoundTag tag = null;
    Holder<Potion> holder = BuiltInRegistries.POTION.wrapAsHolder(potion);
    if (!holder.is(Potions.WATER)) {
      tag = potionTag(BuiltInRegistries.POTION.getKey(potion));
    }
    return FluidOutput.fromTag(Objects.requireNonNull(TinkerFluids.potion.getCommonTag()), size, tag);
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(ResourceKey<Potion> potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    if (!potion.location().equals(Potions.WATER.unwrapKey().orElseThrow().location())) {
      TagUtil.setTag(stack, potionTag(potion.location()));
    }
    return stack;
  }

  /** Creates a potion bucket for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static ItemStack potionBucket(Potion potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    Holder<Potion> holder = BuiltInRegistries.POTION.wrapAsHolder(potion);
    if (!holder.is(Potions.WATER)) {
      TagUtil.setTag(stack, potionTag(BuiltInRegistries.POTION.getKey(potion)));
    }
    return stack;
  }
}
