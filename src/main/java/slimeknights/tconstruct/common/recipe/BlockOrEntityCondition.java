package slimeknights.tconstruct.common.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.NoArgsConstructor;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

@NoArgsConstructor
public class BlockOrEntityCondition implements ILootCondition {
  public static final ResourceLocation ID = Util.getResource("block_or_entity");
  public static final BlockOrEntityCondition INSTANCE = new BlockOrEntityCondition();
  public static final Serializer SERIALIZER = new Serializer();

  @Override
  public LootConditionType func_230419_b_() {
    return TinkerCommons.lootBlockOrEntity;
  }

  @Override
  public boolean test(LootContext lootContext) {
    return lootContext.has(LootParameters.THIS_ENTITY) || lootContext.has(LootParameters.BLOCK_STATE);
  }

  private static class Serializer implements ILootSerializer<BlockOrEntityCondition> {
    @Override
    public void serialize(JsonObject json, BlockOrEntityCondition loot, JsonSerializationContext context) { }

    @Override
    public BlockOrEntityCondition deserialize(JsonObject loot, JsonDeserializationContext context) {
      return BlockOrEntityCondition.INSTANCE;
    }
  }
}
