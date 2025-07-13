package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeUniqueField;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/** Module implementing the movement speed side of lightspeed */
public record LightspeedAttributeModule(String unique, UUID uuid, Attribute attribute, Operation operation, @Nullable LightLayer lightLayer, int minLight, float amount, float damageChance) implements ModifierModule, ArmorWalkModifierHook, EquipmentChangeModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<LightspeedAttributeModule>defaultHooks(ModifierHooks.BOOT_WALK, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<LightspeedAttributeModule> LOADER = RecordLoadable.create(
    new AttributeUniqueField<>(LightspeedAttributeModule::unique),
    Loadables.ATTRIBUTE.requiredField("attribute", LightspeedAttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", LightspeedAttributeModule::operation),
    TinkerLoadables.LIGHT_LAYER.nullableField("light_layer", LightspeedAttributeModule::lightLayer),
    IntLoadable.range(0, 14).requiredField("min_light", LightspeedAttributeModule::minLight),
    FloatLoadable.ANY.requiredField("per_level", LightspeedAttributeModule::amount),
    FloatLoadable.FROM_ZERO.requiredField("damage_chance", LightspeedAttributeModule::damageChance),
    LightspeedAttributeModule::new);

  public LightspeedAttributeModule(String unique, Attribute attribute, Operation operation, LightLayer lightLayer, int minLight, float amount, float damageChance) {
    this(unique, UUID.nameUUIDFromBytes(unique.getBytes()), attribute, operation, lightLayer, minLight, amount, damageChance);
  }

  @Override
  public RecordLoadable<LightspeedAttributeModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the light at the given position */
  private int getLight(Level level, BlockPos pos) {
    if (lightLayer == null) {
      return Math.max(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
    }
    return level.getBrightness(lightLayer, pos);
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    // no point trying if not on the ground
    Level level = living.level();
    if (tool.isBroken() || !living.onGround() || level.isClientSide) {
      return;
    }
    // must have speed
    AttributeInstance attribute = living.getAttribute(this.attribute);
    if (attribute == null) {
      return;
    }
    // start by removing the attribute, we are likely going to give it a new number
    if (attribute.getModifier(uuid) != null) {
      attribute.removeModifier(uuid);
    }

    // not above air
    Vec3 vecPos = living.position();
    BlockPos pos = BlockPos.containing(vecPos.x, vecPos.y + 0.5f, vecPos.z);
    int light = getLight(level, pos);
    if (light > minLight) {
      int scaledLight = light - minLight;
      attribute.addTransientModifier(new AttributeModifier(uuid, unique, scaledLight * amount * modifier.getEffectiveLevel(), operation));

      // damage boots
      if (level.random.nextFloat() < (damageChance * scaledLight)) {
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
      if (newTool == null || newTool.isBroken() || newTool.getModifier(modifier.getId()).getEffectiveLevel() != modifier.getEffectiveLevel()) {
        AttributeInstance attribute = livingEntity.getAttribute(this.attribute);
        if (attribute != null && attribute.getModifier(uuid) != null) {
          attribute.removeModifier(uuid);
        }
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    if (!tool.hasTag(TinkerTags.Items.BOOTS)) {
      return;
    }
    int light = 15;
    if (player != null && key == TooltipKey.SHIFT) {
      light = getLight(player.level(), player.blockPosition());
    }
    float boost = amount * (light - minLight) * entry.getEffectiveLevel();
    if (boost > 0) {
      if (operation == Operation.ADDITION) {
        // multiplies addition boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
        // percentages make sense
        boost *= 10;
      }
      Modifier modifier = entry.getModifier();
      TooltipModifierHook.addPercentBoost(modifier, modifier.getDisplayName(), boost, tooltip);
    }
  }
}
