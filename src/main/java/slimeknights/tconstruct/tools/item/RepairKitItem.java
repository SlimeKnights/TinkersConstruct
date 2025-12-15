package slimeknights.tconstruct.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IRepairKitItem;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

public class RepairKitItem extends MaterialItem implements IRepairKitItem {
  private static final String TOOLTIP_KEY = TConstruct.makeTranslationKey("item", "repair_kit.tooltip");
  private final float repairAmount;
  public RepairKitItem(Properties properties, float repairAmount) {
    super(properties);
    this.repairAmount = repairAmount;
  }

  /** Constructor using config for repair amount */
  public RepairKitItem(Properties properties) {
    this(properties, 0);
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance()
                           .getAllStats(material)
                           .stream()
                           .anyMatch(stats -> stats == StatlessMaterialStats.REPAIR_KIT || stats.getType().canRepair());
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, world, tooltip, flag);
    // tooltip is about inventory repair
    if (canRepairInCraftingTable()) {
      tooltip.add(Component.translatable(TOOLTIP_KEY, TranslationHelper.COMMA_FORMAT.format(getRepairAmount())).withStyle(ChatFormatting.GRAY));
    }
  }

  @Override
  public float getRepairAmount() {
    if (repairAmount == 0) {
      return Config.COMMON.repairKitAmount.get().floatValue();
    }
    return repairAmount;
  }

  @Override
  public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
    // stacking on a tool repairs the tool, if the material is valid
    if (canRepairInCraftingTable() && action == ClickAction.SECONDARY && slot.allowModification(player)) {
      // tool must be modifiable, if so block interactions beyond repair
      ItemStack toolItem = slot.getItem();
      if (!toolItem.isEmpty() && toolItem.is(TinkerTags.Items.MODIFIABLE)) {
        ToolStack tool = ToolStack.from(toolItem);
        MaterialId material = getMaterial(stack).getId();
        // tool must be damaged for us to repair it, and we must have a material
        if (tool.getDamage() > 0 && material != IMaterial.UNKNOWN_ID) {
          // ask the tool how much this material is worth
          float amount = MaterialRepairToolHook.repairAmount(tool, material);
          if (amount > 0) {
            // if its worth anything, add in repair kit value, then ask modifiers to change the amount
            amount *= getRepairAmount() / MaterialRecipe.INGOTS_PER_REPAIR;
            for (ModifierEntry entry : tool.getModifierList()) {
              amount = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, amount);
              if (amount <= 0) {
                return true;
              }
            }
            // assuming no modifier said no repair, we are good, time to repair
            ToolDamageUtil.repair(tool, (int)amount);
            tool.updateStack(toolItem);
            stack.shrink(1);
            player.playSound(Sounds.SAW.getSound(), 1, 0.8f + 0.4f * player.level().random.nextFloat());
          }
        }
        return true;
      }
    }
    return false;
  }
}
