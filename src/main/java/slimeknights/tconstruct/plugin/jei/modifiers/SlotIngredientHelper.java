package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;

import java.util.List;

/** Helper for treating modifier slots as ingredients in recipes */
public class SlotIngredientHelper implements IIngredientHelper<SlotCount> {
  private static final Component SLOTLESS = TConstruct.makeTranslation("stat", "slot.prefix.slotless");
  /** Gets the name of the given slots */
  private static String getName(@Nullable SlotCount slots) {
    if (slots == null || slots.count() <= 0) {
      return "slotless";
    }
    return slots.type().getName();
  }

  @Override
  public IIngredientType<SlotCount> getIngredientType() {
    return TConstructJEIConstants.SLOT_TYPE;
  }

  @Override
  public String getDisplayName(SlotCount slots) {
    if (slots.count() <= 0) {
      return SLOTLESS.getString();
    }
    return Component.translatable(slots.type().getPrefix()).getString() + slots.count();
  }

  @Override
  public String getUniqueId(SlotCount slots, UidContext context) {
    return getName(slots);
  }

  @Override
  public ResourceLocation getResourceLocation(SlotCount slots) {
    return TConstruct.getResource(getName(slots));
  }

  @Override
  public SlotCount copyIngredient(SlotCount slots) {
    return slots;
  }

  @Override
  public String getErrorInfo(@Nullable SlotCount slots) {
    return getName(slots);
  }

  @Override
  public ItemStack getCheatItemStack(SlotCount slots) {
    if (slots.count() <= 0) {
      return ItemStack.EMPTY;
    }
    return CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem, slots.count()), slots.type());
  }

  @Override
  public boolean isValidIngredient(SlotCount slots) {
    return slots.count() >= 0;
  }


  /* Counts */

  @Override
  public long getAmount(SlotCount slots) {
    return slots.count();
  }

  @Override
  public SlotCount copyWithAmount(SlotCount slots, long amount) {
    return new SlotCount(slots.type(), (int) amount);
  }

  @Override
  public SlotCount normalizeIngredient(SlotCount slots) {
    if (slots.count() <= 0) {
      // normalizing slotless to upgrade for simplicity, have to choose something
      return new SlotCount(SlotType.UPGRADE, 0);
    }
    if (slots.count() == 1) {
      return slots;
    }
    return new SlotCount(slots.type(), 1);
  }


  /* Other methods */

  @Override
  public Iterable<Integer> getColors(SlotCount slots) {
    if (slots.count() <= 0) {
      return List.of(0xFF000000);
    }
    return List.of(0xFF000000 | slots.type().getColor().getValue());
  }
}
