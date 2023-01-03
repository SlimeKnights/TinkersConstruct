package slimeknights.tconstruct.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

/** Dynamic item holding a modifier */
public class ModifierCrystalItem extends Item {
  private static final Component TOOLTIP_MISSING = TConstruct.makeTranslation("item", "modifier_crystal.missing").withStyle(ChatFormatting.GRAY);
  private static final Component TOOLTIP_APPLY = TConstruct.makeTranslation("item", "modifier_crystal.tooltip").withStyle(ChatFormatting.GRAY);
  private static final String TAG_MODIFIER = "modifier";
  public ModifierCrystalItem(Properties props) {
    super(props);
  }

  @Override
  public boolean isFoil(ItemStack pStack) {
    return true;
  }

  @Override
  public Component getName(ItemStack stack) {
    ModifierId modifier = getModifier(stack);
    if (modifier != null) {
      return new TranslatableComponent(getDescriptionId(stack) + ".format", new TranslatableComponent(Util.makeTranslationKey("modifier", modifier)));
    }
    return super.getName(stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced) {
    if (getModifier(stack) != null) {
      tooltip.add(TOOLTIP_APPLY);
    }
    else {
      tooltip.add(TOOLTIP_MISSING);
    }
  }

  @Nullable
  @Override
  public String getCreatorModId(ItemStack stack) {
    ModifierId modifier = getModifier(stack);
    if (modifier != null) {
      return modifier.getNamespace();
    }
    return null;
  }


  /* Helpers */

  /** Creates a stack with the given modifier */
  public static ItemStack withModifier(ModifierId modifier) {
    ItemStack stack = new ItemStack(TinkerModifiers.modifierCrystal.get());
    stack.getOrCreateTag().putString(TAG_MODIFIER, modifier.toString());
    return stack;
  }

  /** Gets the modifier stored on this stack */
  @Nullable
  public static ModifierId getModifier(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    if (tag != null) {
      return ModifierId.tryParse(tag.getString(TAG_MODIFIER));
    }
    return null;
  }

  @Override
  public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
    if (this.allowdedIn(category)) {
      ModifierRecipeLookup.getRecipeModifierList().forEach(modifier -> items.add(withModifier(modifier.getId())));
    }
  }
}
