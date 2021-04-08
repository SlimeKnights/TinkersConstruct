package slimeknights.tconstruct.common.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.NoArgsConstructor;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

@NoArgsConstructor
public class BlockOrEntityCondition implements LootCondition {
  public static final Identifier ID = Util.getResource("block_or_entity");
  public static final BlockOrEntityCondition INSTANCE = new BlockOrEntityCondition();
  public static final Serializer SERIALIZER = new Serializer();

  @Override
  public LootConditionType getType() {
    return TinkerCommons.lootBlockOrEntity;
  }

  @Override
  public boolean test(LootContext lootContext) {
    return lootContext.hasParameter(LootContextParameters.THIS_ENTITY) || lootContext.hasParameter(LootContextParameters.BLOCK_STATE);
  }

  private static class Serializer implements JsonSerializer<BlockOrEntityCondition> {
    @Override
    public void serialize(JsonObject json, BlockOrEntityCondition loot, JsonSerializationContext context) { }

    @Override
    public BlockOrEntityCondition fromJson(JsonObject loot, JsonDeserializationContext context) {
      return BlockOrEntityCondition.INSTANCE;
    }
  }
}
