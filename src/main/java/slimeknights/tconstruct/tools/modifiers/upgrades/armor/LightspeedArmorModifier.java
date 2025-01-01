package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LightspeedArmorModifier extends Modifier implements ArmorWalkModifierHook, EquipmentChangeModifierHook, TooltipModifierHook {
  /** UUID for speed boost */
  private static final UUID ATTRIBUTE_BONUS = UUID.fromString("8790747b-6654-4bd8-83c7-dbe9ae04c0ca");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.BOOT_WALK, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    // no point trying if not on the ground
    Level level = living.level();
    if (tool.isBroken() || !living.onGround() || level.isClientSide) {
      return;
    }
    // must have speed
    AttributeInstance attribute = living.getAttribute(Attributes.MOVEMENT_SPEED);
    if (attribute == null) {
      return;
    }
    // start by removing the attribute, we are likely going to give it a new number
    if (attribute.getModifier(ATTRIBUTE_BONUS) != null) {
      attribute.removeModifier(ATTRIBUTE_BONUS);
    }

    // not above air
    Vec3 vecPos = living.position();
    BlockPos pos = BlockPos.containing(vecPos.x, vecPos.y + 0.5f, vecPos.z);
    int light = level.getBrightness(LightLayer.BLOCK, pos);
    if (light > 5) {
      int scaledLight = light - 5;
      attribute.addTransientModifier(new AttributeModifier(ATTRIBUTE_BONUS, "tconstruct.modifier.lightspeed", scaledLight * 0.0015f * modifier.getEffectiveLevel(), Operation.ADDITION));

      // damage boots
      if (RANDOM.nextFloat() < (0.005f * scaledLight)) {
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    // remove boost when boots are removed
    LivingEntity livingEntity = context.getEntity();
    if (context.getChangedSlot() == EquipmentSlot.FEET) {
      IToolStackView newTool = context.getReplacementTool();
      // damaging the tool will trigger this hook, so ensure the new tool has the same level
      if (newTool == null || newTool.isBroken() || newTool.getModifier(this).getEffectiveLevel() != modifier.getEffectiveLevel()) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null && attribute.getModifier(ATTRIBUTE_BONUS) != null) {
          attribute.removeModifier(ATTRIBUTE_BONUS);
        }
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
    // percentages make sense
    float boost;
    float level = modifier.getEffectiveLevel();
    if (player != null && key == TooltipKey.SHIFT) {
      int light = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition());
      boost = 0.015f * (light - 5) * level;
    } else {
      boost = 0.15f * level;
    }
    if (boost > 0) {
      TooltipModifierHook.addPercentBoost(this, getDisplayName(), boost, tooltip);
    }
  }
}
