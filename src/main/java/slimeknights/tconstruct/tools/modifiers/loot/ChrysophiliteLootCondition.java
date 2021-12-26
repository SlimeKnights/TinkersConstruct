package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;

import java.util.Set;

/** Condition to check if the enemy has the chrysophilite modifier */
public class ChrysophiliteLootCondition implements ILootCondition {
  public static final ResourceLocation ID = TConstruct.getResource("has_chrysophilite");
  public static final Serializer SERIALIZER = new Serializer();
  public static final ChrysophiliteLootCondition INSTANCE = new ChrysophiliteLootCondition();

  private ChrysophiliteLootCondition() {}

  @Override
  public boolean test(LootContext context) {
    return ChrysophiliteModifier.getTotalGold(context.getParamOrNull(LootParameters.THIS_ENTITY)) > 0;
  }

  @Override
  public Set<LootParameter<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootParameters.THIS_ENTITY);
  }

  @Override
  public LootConditionType getType() {
    return TinkerModifiers.chrysophiliteLootCondition;
  }

  /** Loot serializer instance */
  private static class Serializer implements ILootSerializer<ChrysophiliteLootCondition> {
    @Override
    public void serialize(JsonObject json, ChrysophiliteLootCondition loot, JsonSerializationContext context) {}

    @Override
    public ChrysophiliteLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
      return INSTANCE;
    }
  }
}
