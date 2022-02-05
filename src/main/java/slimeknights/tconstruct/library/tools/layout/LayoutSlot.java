package slimeknights.tconstruct.library.tools.layout;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Objects;

/** A single slot in a slot layout */
@RequiredArgsConstructor
public class LayoutSlot {
  public static final LayoutSlot EMPTY = new LayoutSlot(null, "", -1, -1, null);

  /** Icon to display when the slot is empty */
  @Nullable @Getter
  private final Pattern icon;
  /** Name to display in the sidebar for the slot's "needs" */
  @Nullable
  private final String translation_key;
  @Getter
  private final int x;
  @Getter
  private final int y;
  /** Filter to only allow certain items in the slot under this layout */
  @Nullable @Getter(AccessLevel.PROTECTED) @VisibleForTesting
  private final Ingredient filter;

  /** If true, this is an empty slot */
  public boolean isEmpty() {
    return getTranslationKey().isEmpty();
  }

  public boolean isHidden() {
    return x == -1 && y == -1;
  }

  /** Gets the translation key of this slot */
  public String getTranslationKey() {
    return Objects.requireNonNullElse(translation_key, "");
  }

  /** Checks if the given stack is valid for this slot */
  public boolean isValid(ItemStack stack) {
    return !stack.isEmpty() && (filter == null || filter.test(stack));
  }


  /* Buffers */

  /** Reads a slot from the packet buffer */
  public static LayoutSlot read(FriendlyByteBuf buffer) {
    Pattern pattern = null;
    if (buffer.readBoolean()) {
      pattern = new Pattern(buffer.readResourceLocation());
    }
    String name = buffer.readUtf(Short.MAX_VALUE);
    int x = buffer.readVarInt();
    int y = buffer.readVarInt();
    Ingredient ingredient = null;
    if (buffer.readBoolean()) {
      ingredient = Ingredient.fromNetwork(buffer);
    }
    return new LayoutSlot(pattern, name, x, y, ingredient);
  }

  /** Writes a slot to the packet buffer */
  public void write(FriendlyByteBuf buffer) {
    if (icon != null) {
      buffer.writeBoolean(true);
      buffer.writeResourceLocation(icon);
    } else {
      buffer.writeBoolean(false);
    }
    buffer.writeUtf(getTranslationKey());
    buffer.writeVarInt(x);
    buffer.writeVarInt(y);
    if (filter != null) {
      buffer.writeBoolean(true);
      filter.toNetwork(buffer);
    } else {
      buffer.writeBoolean(false);
    }
  }
}
