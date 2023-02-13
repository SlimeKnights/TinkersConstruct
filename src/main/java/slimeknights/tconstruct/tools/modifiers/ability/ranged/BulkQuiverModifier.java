package slimeknights.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class BulkQuiverModifier extends InventoryMenuModifier implements BowAmmoModifierHook {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("bulk_quiver");
  private static final ResourceLocation LAST_SLOT = TConstruct.getResource("quiver_last_selected");
  private static final Pattern ARROW = new Pattern(TConstruct.getResource("arrow"));
  public BulkQuiverModifier() {
    super(INVENTORY_KEY, 2);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.BOW_AMMO);
  }

  @Override
  public int getPriority() {
    return 50; // after crystalshot
  }

  @Override
  public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
    Item item = stack.getItem();
    return (item == Items.FIREWORK_ROCKET && tool.hasTag(TinkerTags.Items.CROSSBOWS)) || stack.getItem() instanceof ArrowItem;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : ARROW;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    // skip if we have standard ammo, this quiver holds backup arrows
    if (!standardAmmo.isEmpty()) {
      return ItemStack.EMPTY;
    }
    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = getInventoryKey();
    ListTag slots = persistentData.get(key, GET_COMPOUND_LIST);
    if (!slots.isEmpty()) {
      // search all slots for the first match
      for (int i = 0; i < slots.size(); i++) {
        CompoundTag compound = slots.getCompound(i);
        ItemStack stack = ItemStack.of(compound);
        if (!stack.isEmpty() && ammoPredicate.test(stack)) {
          persistentData.putInt(LAST_SLOT, compound.getInt(TAG_SLOT));
          return stack;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    // we assume no one else touched the quiver inventory, this is a good assumption, do not make it a bad assumption by modifying the quiver in other modifiers
    ammo.shrink(needed);
    setStack(tool, modifier, tool.getPersistentData().getInt((LAST_SLOT)), ammo);
  }
}
