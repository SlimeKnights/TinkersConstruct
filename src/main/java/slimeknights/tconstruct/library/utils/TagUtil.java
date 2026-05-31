package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;

/** Helpers related to Tag */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagUtil {
  public static final HolderLookup.Provider BUILTIN_LOOKUP = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

  /* Helper functions */

  /** Gets a copy of the custom data tag on the stack. */
  @Nullable
  public static CompoundTag getTag(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    return data == null || data.isEmpty() ? null : data.copyTag();
  }

  /** Gets a copy of the custom data tag on the stack, or a new empty tag. */
  public static CompoundTag getOrCreateTag(ItemStack stack) {
    CompoundTag tag = getTag(stack);
    return tag == null ? new CompoundTag() : tag;
  }

  /** Replaces the custom data tag on the stack. */
  public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
    if (tag == null || tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }

  /** Checks if the stack has a non-empty custom data tag. */
  public static boolean hasTag(ItemStack stack) {
    return stack.has(DataComponents.CUSTOM_DATA);
  }

  /** Gets a copy of the custom data tag on the fluid stack. */
  @Nullable
  public static CompoundTag getTag(FluidStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    return data == null || data.isEmpty() ? null : data.copyTag();
  }

  /** Gets a copy of the custom data tag on the fluid stack, or a new empty tag. */
  public static CompoundTag getOrCreateTag(FluidStack stack) {
    CompoundTag tag = getTag(stack);
    return tag == null ? new CompoundTag() : tag;
  }

  /** Replaces the custom data tag on the fluid stack. */
  public static void setTag(FluidStack stack, @Nullable CompoundTag tag) {
    if (tag == null || tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }

  /** Checks if the fluid stack has a non-empty custom data tag. */
  public static boolean hasTag(FluidStack stack) {
    return stack.has(DataComponents.CUSTOM_DATA);
  }

  /** Reads an item stack using the built-in registry lookup. */
  public static ItemStack readItem(CompoundTag tag) {
    return ItemStack.parseOptional(BUILTIN_LOOKUP, tag);
  }

  /** Saves an item stack using the built-in registry lookup. */
  public static CompoundTag saveItem(ItemStack stack, CompoundTag tag) {
    return (CompoundTag)stack.save(BUILTIN_LOOKUP, tag);
  }

  /** Creates a fluid stack and applies the custom data tag if present. */
  public static FluidStack createFluidStack(Fluid fluid, int amount, @Nullable CompoundTag tag) {
    FluidStack stack = new FluidStack(fluid, amount);
    setTag(stack, tag);
    return stack;
  }

  /**
   * Reads a block position from Tag
   * @param parent  Parent tag
   * @param key     Position key
   * @param offset  Amount to offset position by
   * @return  Block position, or null if invalid or missing
   */
  @Nullable
  public static BlockPos readOptionalPos(CompoundTag parent, String key, BlockPos offset) {
    return NbtUtils.readBlockPos(parent, key).map(pos -> pos.offset(offset)).orElse(null);
  }

  /**
   * Checks if the given tag is a numeric type
   * @param tag  Tag to check
   * @return  True if the type matches
   */
  public static boolean isNumeric(Tag tag) {
    byte type = tag.getId();
    return type == Tag.TAG_BYTE || type == Tag.TAG_SHORT || type == Tag.TAG_INT || type == Tag.TAG_LONG || type == Tag.TAG_FLOAT || type == Tag.TAG_DOUBLE;
  }
}
