package slimeknights.tconstruct.tools.modules.durability;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Module which converts some tool to temporary shield on another tool */
public record ShareDurabilityModule(LazyModifier shield, LevelingInt grant, LevelingInt consume) implements ModifierModule, ToolDamageModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ShareDurabilityModule>defaultHooks(ModifierHooks.TOOL_DAMAGE);
  public static final RecordLoadable<ShareDurabilityModule> LOADER = RecordLoadable.create(
    ModifierId.PARSER.requiredField("shield", m -> m.shield.getId()),
    LevelingInt.LOADABLE.requiredField("grant", ShareDurabilityModule::grant),
    LevelingInt.LOADABLE.requiredField("consume", ShareDurabilityModule::consume),
    ShareDurabilityModule::new);

  public ShareDurabilityModule(ModifierId modifier, LevelingInt grant, LevelingInt consumed) {
    this(new LazyModifier(modifier), grant, consumed);
  }

  @Override
  public RecordLoadable<ShareDurabilityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return ToolDamageModifierHook.super.onDamageTool(tool, modifier, amount, holder, null);
  }

  /** Checks if the stack may be considered */
  private static boolean canTarget(ItemStack stack) {
    return !stack.isEmpty() && stack.is(TinkerTags.Items.DURABILITY);
  }

  /** Adds the stack to the options list if it is to be considered */
  private static void consider(List<ItemStack> list, ItemStack check, @Nullable ItemStack skip) {
    if (check != skip && canTarget(check)) {
      list.add(check);
    }
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder, @Nullable ItemStack stack) {
    if (holder != null) {
      // want to randomly select a slot, but skip any that lack tools
      List<ItemStack> options = new ArrayList<>(10);
      // if the stack is armor, consider all armor slots
      if (tool.hasTag(TinkerTags.Items.WORN_ARMOR)) {
        for (EquipmentSlot slot : ModifiableArmorMaterial.ARMOR_SLOTS) {
          ItemStack check = holder.getItemBySlot(slot);
          // with armor, we can check the items as a heuristic to work around the potential of null stack
          if (canTarget(check) && check.getItem() != tool.getItem()) {
            options.add(check);
          }
        }
      } else {
        // if its a player, consider all hotbar slots
        if (holder instanceof Player player) {
          Inventory inventory = player.getInventory();
          for (int i = 0; i < 9; i++) {
            consider(options, inventory.getItem(i), stack);
          }
        } else {
          // non player adds just the mainhand to consider
          consider(options, holder.getMainHandItem(), stack);
        }
        // always consider offhand
        consider(options, holder.getOffhandItem(), stack);
      }

      // now that we have our options, randomly choose one
      if (!options.isEmpty()) {
        // figure out how much durability from the local tool to eat
        int consumed = Math.min(this.consume.compute(modifier.getEffectiveLevel()) * amount, tool.getCurrentDurability());
        int granted = this.grant.compute(modifier.getEffectiveLevel());
        if (consumed > 0 && granted > 0) {
          // deal extra damage to the local tool, specifically direct damage
          ToolDamageUtil.directDamage(tool, consumed, holder, stack);

          // grant some shield to the choice tool
          ItemStack choiceStack = options.get(TConstruct.RANDOM.nextInt(options.size()));
          ToolStack choice = ToolStack.from(choiceStack);
          Modifier shield = this.shield.get();
          ModifierEntry entry = choice.getModifier(shield);
          // if the modifier is missing, add it
          if (entry.getLevel() == 0) {
            entry = new ModifierEntry(shield, 1);
            choice.addModifier(shield.getId(), 1);
          }
          // update the durability bar
          shield.getHook(ModifierHooks.CAPACITY_BAR).addAmount(choice, entry, granted * amount);
        }
      }
    }
    return amount;
  }
}
