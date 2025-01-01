package slimeknights.tconstruct.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags.Modifiers;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/** Dynamic item holding a modifier */
public class ModifierCrystalItem extends Item {
  private static final Component TOOLTIP_MISSING = TConstruct.makeTranslation("item", "modifier_crystal.missing").withStyle(ChatFormatting.GRAY);
  private static final Component TOOLTIP_APPLY = TConstruct.makeTranslation("item", "modifier_crystal.tooltip").withStyle(ChatFormatting.GRAY);
  private static final String MODIFIER_KEY = TConstruct.makeTranslationKey("item", "modifier_crystal.modifier_id");
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
      return Component.translatable(getDescriptionId(stack) + ".format", Component.translatable(Util.makeTranslationKey("modifier", modifier)));
    }
    return super.getName(stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced) {
    ModifierId id = getModifier(stack);
    if (id != null) {
      if (ModifierManager.INSTANCE.contains(id)) {
        tooltip.addAll(ModifierManager.INSTANCE.get(id).getDescriptionList());
      }
      tooltip.add(TOOLTIP_APPLY);
      if (advanced.isAdvanced()) {
        tooltip.add((Component.translatable(MODIFIER_KEY, id.toString())).withStyle(ChatFormatting.DARK_GRAY));
      }
    } else {
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
  public static ItemStack withModifier(ModifierId modifier, int count) {
    ItemStack stack = new ItemStack(TinkerModifiers.modifierCrystal.get(), count);
    stack.getOrCreateTag().putString(TAG_MODIFIER, modifier.toString());
    return stack;
  }

  /** Creates a stack with the given modifier */
  public static ItemStack withModifier(ModifierId modifier) {
    return withModifier(modifier, 1);
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

  /** Gets all variants of this item */
  public static void addVariants(Consumer<ItemStack> items) {
    ModifierRecipeLookup.getRecipeModifierList().forEach(modifier -> {
      if (!ModifierManager.isInTag(modifier.getId(), Modifiers.EXTRACT_MODIFIER_BLACKLIST)) {
        items.accept(withModifier(modifier.getId()));
      }
    });
  }
}
