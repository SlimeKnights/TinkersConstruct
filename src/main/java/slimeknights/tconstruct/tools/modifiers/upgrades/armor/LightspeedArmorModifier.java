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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LightspeedArmorModifier extends IncrementalModifier implements IArmorWalkModifier {
  /** UUID for speed boost */
  private static final UUID ATTRIBUTE_BONUS = UUID.fromString("8790747b-6654-4bd8-83c7-dbe9ae04c0ca");

  @Override
  public void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    // no point trying if not on the ground
    if (tool.isBroken() || !living.isOnGround() || living.level.isClientSide) {
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
    BlockPos pos = new BlockPos(vecPos.x, vecPos.y + 0.5f, vecPos.z);
    int light = living.level.getBrightness(LightLayer.BLOCK, pos);
    if (light > 5) {
      int scaledLight = light - 5;
      attribute.addTransientModifier(new AttributeModifier(ATTRIBUTE_BONUS, "tconstruct.modifier.lightspeed", scaledLight * 0.0015f * getScaledLevel(tool, level), Operation.ADDITION));

      // damage boots
      if (RANDOM.nextFloat() < (0.005f * scaledLight)) {
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    // remove boost when boots are removed
    LivingEntity livingEntity = context.getEntity();
    if (context.getChangedSlot() == EquipmentSlot.FEET) {
      IToolStackView newTool = context.getReplacementTool();
      // damaging the tool will trigger this hook, so ensure the new tool has the same level
      if (newTool == null || newTool.isBroken() || getScaledLevel(newTool, newTool.getModifierLevel(this)) != getScaledLevel(tool, level)) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null && attribute.getModifier(ATTRIBUTE_BONUS) != null) {
          attribute.removeModifier(ATTRIBUTE_BONUS);
        }
      }
    }
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorWalkModifier.class, this);
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
    // percentages make sense
    float boost;
    if (player != null && key == TooltipKey.SHIFT) {
      int light = player.level.getBrightness(LightLayer.BLOCK, player.blockPosition());
      boost = 0.015f * (light - 5) * getScaledLevel(tool, level);
    } else {
      boost = 0.15f * getScaledLevel(tool, level);
    }
    if (boost > 0) {
      addPercentTooltip(getDisplayName(), boost, tooltip);
    }
  }
}
