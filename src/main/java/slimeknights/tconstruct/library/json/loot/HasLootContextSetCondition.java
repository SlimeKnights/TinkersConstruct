package slimeknights.tconstruct.library.json.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.Objects;

/**
 * Loot condition that only runs if all required values in the given loot context set are present. Good heuristic for using that set.
 * TODO: migrate to Mantle
 */
public record HasLootContextSetCondition(LootContextParamSet set) implements LootItemCondition {
  /** Creates a new builder instance */
  public static Builder builder(LootContextParamSet set) {
    return new Builder(set);
  }

  @Override
  public LootItemConditionType getType() {
    return TinkerCommons.hasLootContextSet.get();
  }

  @Override
  public boolean test(LootContext context) {
    for (LootContextParam<?> param : set.getRequired()) {
      if (!context.hasParam(param)) {
        return false;
      }
    }
    return true;
  }

  /** Builder logic for this condition */
  public record Builder(LootContextParamSet set) implements LootItemCondition.Builder {
    @Override
    public LootItemCondition build() {
      return new HasLootContextSetCondition(set);
    }
  }

  /** Serializer logic */
  public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<HasLootContextSetCondition> {
    @Override
    public void serialize(JsonObject json, HasLootContextSetCondition value, JsonSerializationContext context) {
      json.addProperty("set", Objects.requireNonNull(LootContextParamSets.getKey(value.set), "Unregistered loot LootContextParamSets").toString());
    }

    @Override
    public HasLootContextSetCondition deserialize(JsonObject json, JsonDeserializationContext context) {
      ResourceLocation key = JsonHelper.getResourceLocation(json, "set");
      LootContextParamSet set = LootContextParamSets.get(key);
      if (set == null) {
        throw new JsonSyntaxException("Unknown LootContextParamSet " + key);
      }
      return new HasLootContextSetCondition(set);
    }
  }
}
