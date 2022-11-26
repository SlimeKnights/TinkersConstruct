package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;

import java.util.Set;

/** Condition to check if the enemy has the chrysophilite modifier */
public class ChrysophiliteLootCondition implements LootItemCondition {
  public static final ChrysophiliteSerializer SERIALIZER = new ChrysophiliteSerializer();
  public static final ChrysophiliteLootCondition INSTANCE = new ChrysophiliteLootCondition();

  private ChrysophiliteLootCondition() {}

  @Override
  public boolean test(LootContext context) {
    return ChrysophiliteModifier.getTotalGold(context.getParamOrNull(LootContextParams.THIS_ENTITY)) > 0;
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.THIS_ENTITY);
  }

  @Override
  public LootItemConditionType getType() {
    return TinkerModifiers.chrysophiliteLootCondition.get();
  }

  /** Loot serializer instance */
  private static class ChrysophiliteSerializer implements Serializer<ChrysophiliteLootCondition> {
    @Override
    public void serialize(JsonObject json, ChrysophiliteLootCondition loot, JsonSerializationContext context) {}

    @Override
    public ChrysophiliteLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
      return INSTANCE;
    }
  }
}
