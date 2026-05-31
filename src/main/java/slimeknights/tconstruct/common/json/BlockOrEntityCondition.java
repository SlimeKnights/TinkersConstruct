package slimeknights.tconstruct.common.json;

import com.mojang.serialization.MapCodec;
import lombok.NoArgsConstructor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.tconstruct.shared.TinkerCommons;

@NoArgsConstructor
public class BlockOrEntityCondition implements LootItemCondition {
  public static final BlockOrEntityCondition INSTANCE = new BlockOrEntityCondition();
  public static final MapCodec<BlockOrEntityCondition> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public LootItemConditionType getType() {
    return TinkerCommons.lootBlockOrEntity.get();
  }

  @Override
  public boolean test(LootContext lootContext) {
    return lootContext.hasParam(LootContextParams.THIS_ENTITY) || lootContext.hasParam(LootContextParams.BLOCK_STATE);
  }

}
