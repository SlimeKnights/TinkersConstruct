package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.library.recipe.RecipeResult;

import javax.annotation.Nullable;

/** Helper which contains a lazily loaded tool stack, used for recipe output to reduce NBT parsing */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LazyToolStack {
  @Nullable
  private ItemStack stack;
  @Nullable
  private ToolStack tool;
  @Getter
  private final int size;

  /* Constructors */

  /** Creates from a stack, lazily loading the tool stack */
  public static LazyToolStack from(ItemStack stack) {
    return new LazyToolStack(stack, null, stack.getCount());
  }

  /** Creates from a tool with the given count, lazily loading the stack */
  public static LazyToolStack from(ToolStack tool, int count) {
    return new LazyToolStack(null, tool, count);
  }

  /**
   * Creates from a tool by copying the passed stack.
   * @param tool      Tool instance, will be used for NBT. Not copied.
   * @param count     Desired count, need not match original count.
   * @param original  Original stack to copy. NBT and count will be overridden but capabilities preserved.
   */
  public static LazyToolStack copyFrom(ToolStack tool, int count, ItemStack original) {
    return new LazyToolStack(tool.copyStack(original, count), tool, count);
  }

  /**
   * Creates from a tool by copying the passed stack
   * @param tool      Tool instance, will be used for NBT. Not copied.
   * @param original  Original stack to copy. NBT will be overridden but capabilities preserved.
   */
  public static LazyToolStack copyFrom(ToolStack tool, ItemStack original) {
    return new LazyToolStack(tool.copyStack(original), tool, original.getCount());
  }


  /* Result helpers */

  /** Creates a success for a tinker station or modifier worktable recipe */
  public static RecipeResult<LazyToolStack> success(ItemStack stack) {
    return RecipeResult.success(LazyToolStack.from(stack));
  }

  /** Creates a success for a tinker station or modifier worktable recipe */
  public static RecipeResult<LazyToolStack> success(ToolStack tool, int count) {
    return RecipeResult.success(LazyToolStack.from(tool, count));
  }

  /**
   * Creates a success from a tool by copying the passed stack.
   * @param tool      Tool instance, will be used for NBT. Not copied.
   * @param count     Desired count, need not match original count.
   * @param original  Original stack to copy. NBT and count will be overridden but capabilities preserved.
   */
  public static RecipeResult<LazyToolStack> successCopy(ToolStack tool, int count, ItemStack original) {
    return RecipeResult.success(LazyToolStack.copyFrom(tool, count, original));
  }

  /**
   * Creates a success from a tool by copying the passed stack
   * @param tool      Tool instance, will be used for NBT. Not copied.
   * @param original  Original stack to copy. NBT will be overridden but capabilities preserved.
   */
  public static RecipeResult<LazyToolStack> successCopy(ToolStack tool, ItemStack original) {
    return RecipeResult.success(LazyToolStack.copyFrom(tool, original));
  }

  /** Gets the item inside this stack without a need to resolving. */
  public Item getItem() {
    if (stack != null) {
      return stack.getItem();
    }
    if (tool != null) {
      return tool.getItem();
    }
    return Items.AIR;
  }

  /** Checks if the stack has the given tag without resolving. */
  public boolean hasTag(TagKey<Item> tag) {
    if (stack != null) {
      return stack.is(tag);
    }
    if (tool != null) {
      return tool.hasTag(tag);
    }
    return false;
  }

  /** Gets the tool for this instance */
  public ToolStack getTool() {
    if (tool == null) {
      assert stack != null;
      tool = ToolStack.from(stack);
    }
    return tool;
  }

  /** Gets the item stack for this instance */
  public ItemStack getStack() {
    if (stack == null) {
      assert tool != null;
      // if we have an original, base the stack off that
      stack = tool.createStack(size);
    }
    return stack;
  }
}
