package slimeknights.tconstruct.compat.neoforged.neoforge.common.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.recipe.condition.ConditionHelper;

/** Minimal datagen compatibility shim for conditional advancements. */
public final class ConditionalAdvancement {
  private ConditionalAdvancement() {}

  public static class Builder {
    private final List<ICondition> conditions = new ArrayList<>();
    private Advancement.Builder advancement;

    public void addCondition(ICondition condition) {
      conditions.add(condition);
    }

    public void addAdvancement(Advancement.Builder advancement) {
      this.advancement = advancement;
    }

    public JsonObject write(ResourceLocation id) {
      if (advancement == null) {
        throw new IllegalStateException("Conditional advancement " + id + " has no advancement");
      }
      JsonObject json = Advancement.CODEC.encodeStart(JsonOps.INSTANCE, advancement.build(id).value()).getOrThrow(JsonSyntaxException::new).getAsJsonObject();
      if (!conditions.isEmpty()) {
        json.add("neoforge:conditions", ConditionHelper.serialize(conditions));
      }
      return json;
    }
  }
}
