package slimeknights.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.hook.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TrickQuiverModifier extends InventoryMenuModifier implements BowAmmoModifierHook {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("trick_quiver");
  private static final ResourceLocation SELECTED_SLOT = TConstruct.getResource("trick_quiver_selected");
  private static final Pattern TRICK_ARROW = new Pattern(TConstruct.getResource("tipped_arrow"));
  /** Message when disabling the trick quiver */
  private static final Component DISABLED = TConstruct.makeTranslation("modifier", "trick_quiver.disabled");
  /** Message displayed when the selected slot is empty */
  private static final String EMPTY = TConstruct.makeTranslationKey("modifier", "trick_quiver.empty");
  /** Message to display selected slot */
  private static final String SELECTED = TConstruct.makeTranslationKey("modifier", "trick_quiver.selected");

  public TrickQuiverModifier() {
    super(INVENTORY_KEY, 3);
  }

  @Override
  public int getPriority() {
    return 70; // run after interaction modifiers, but before crystal shot
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.BOW_AMMO);
  }

  @Override
  public int getSlots(IToolContext tool, int level) {
    return 3;
  }

  @Override
  public int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
    return modifier.getLevel() == 1 ? 32 : 64;
  }

  @Override
  public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
    Item item = stack.getItem();
    return (item == Items.FIREWORK_ROCKET && tool.hasTag(TinkerTags.Items.CROSSBOWS)) || stack.getItem() instanceof ArrowItem;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : TRICK_ARROW;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    // if selected is too big (disabled), will automatially return nothing
    return getStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT));
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    // assume no one else touched our selected slot, good assumption
    ammo.shrink(needed);
    setStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT), ammo);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!player.isCrouching()) {
      if (!player.level.isClientSide) {
        // first, increment the number
        ModDataNBT data = tool.getPersistentData();
        int totalSlots = getSlots(tool, modifier.getLevel());
        // support going 1 above max to disable the trick arrows
        int newSelected = (data.getInt(SELECTED_SLOT) + 1) % (totalSlots + 1);
        data.putInt(SELECTED_SLOT, newSelected);

        // display a message about what is now selected
        if (newSelected == totalSlots) {
          player.displayClientMessage(DISABLED, true);
        } else {
          ItemStack selectedStack = getStack(tool, modifier, newSelected);
          if (selectedStack.isEmpty()) {
            player.displayClientMessage(new TranslatableComponent(EMPTY, newSelected + 1), true);
          } else {
            player.displayClientMessage(new TranslatableComponent(SELECTED, selectedStack.getDisplayName(), newSelected + 1), true);
          }
        }
      }
      return InteractionResult.SUCCESS;
    }
    return super.onToolUse(tool, modifier, player, hand, source);
  }
}
