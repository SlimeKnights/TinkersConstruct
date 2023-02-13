package slimeknights.tconstruct.plugin.jsonthings.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Modifiable item that supports left click and right click interaction
 */
public class FlexModifiableStaffItem extends FlexModifiableItem {
  public FlexModifiableStaffItem(Properties properties, ToolDefinition toolDefinition, boolean breakBlocksInCreative) {
    super(properties, toolDefinition, breakBlocksInCreative);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
    return EntityInteractionModifierHook.leftClickEntity(stack, player, target);
  }

  @Override
  public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
    return super.getStatInformation(tool, player, tooltips, key, tooltipFlag);
  }
}
