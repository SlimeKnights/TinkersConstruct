package slimeknights.tconstruct.library.json.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.tconstruct.shared.TinkerCommons;

/**
 * Loot condition that only runs if all required values in the given loot context set are present. Good heuristic for using that set.
 * TODO: migrate to Mantle
 */
public record HasLootContextSetCondition(LootContextParamSet set) implements LootItemCondition {
  public static final MapCodec<HasLootContextSetCondition> CODEC = RecordCodecBuilder.mapCodec(
    instance -> instance.group(LootContextParamSets.CODEC.fieldOf("set").forGetter(HasLootContextSetCondition::set))
                        .apply(instance, HasLootContextSetCondition::new)
  );

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

}
