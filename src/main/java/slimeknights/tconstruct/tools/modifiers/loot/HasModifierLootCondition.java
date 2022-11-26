package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Condition to check if a held tool has the given modifier */
@RequiredArgsConstructor
public class HasModifierLootCondition implements LootItemCondition {
  private final ModifierId modifier;

  @Override
  public LootItemConditionType getType() {
    return TinkerModifiers.hasModifierLootCondition.get();
  }

  @Override
  public boolean test(LootContext context) {
    ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
    return tool != null && tool.is(TinkerTags.Items.MODIFIABLE) && ModifierUtil.getModifierLevel(tool, modifier) > 0;
  }

  public static class ConditionSerializer implements Serializer<HasModifierLootCondition> {
    @Override
    public void serialize(JsonObject json, HasModifierLootCondition condition, JsonSerializationContext context) {
      json.addProperty("modifier", condition.modifier.toString());
    }

    @Override
    public HasModifierLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
      return new HasModifierLootCondition(new ModifierId(JsonHelper.getResourceLocation(json, "modifier")));
    }
  }
}
