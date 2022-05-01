package slimeknights.tconstruct.library.json;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Loot function to add data to a tool */
public class AddToolDataFunction extends LootItemConditionalFunction {
  public static final ResourceLocation ID = TConstruct.getResource("add_tool_data");
  public static final Serializer SERIALIZER = new Serializer();

  /** Percentage of damage on the tool, if 0 the tool is undamaged */
  private final float damage;
  /** Fixed materials on the tool, any nulls in the list will randomize */
  private final List<RandomMaterial> materials;

  protected AddToolDataFunction(LootItemCondition[] conditionsIn, float damage, List<RandomMaterial> materials) {
    super(conditionsIn);
    this.damage = damage;
    this.materials = materials;
  }

  /** Creates a new builder */
  public static AddToolDataFunction.Builder builder() {
    return new AddToolDataFunction.Builder();
  }

  @Override
  public LootItemFunctionType getType() {
    return TinkerTools.lootAddToolData;
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    if (stack.is(TinkerTags.Items.MODIFIABLE)) {
      ToolStack tool = ToolStack.from(stack);
      tool.ensureSlotsBuilt();
      if (tool.getDefinition().isMultipart() && !materials.isEmpty()) {
        MaterialNBT.Builder builder = MaterialNBT.builder();
        Random random = context.getRandom();
        for (RandomMaterial material : materials) {
          builder.add(material.getMaterial(random));
        }
        tool.setMaterials(builder.build());
      } else {
        // not multipart? no sense doing materials, just initialize stats
        tool.rebuildStats();
      }
      // set damage last to a percentage of max damage if requested
      if (damage > 0) {
        tool.setDamage((int)(tool.getStats().get(ToolStats.DURABILITY) * damage));
      }
    }
    return stack;
  }

  /** Serializer logic for the function */
  private static class Serializer extends LootItemConditionalFunction.Serializer<AddToolDataFunction> {
    @Override
    public void serialize(JsonObject json, AddToolDataFunction loot, JsonSerializationContext context) {
      super.serialize(json, loot, context);
      // initial damage
      if (loot.damage > 0) {
        json.addProperty("damage_percent", loot.damage);
      }
      if (!loot.materials.isEmpty()) {
        JsonArray array = new JsonArray();
        for (RandomMaterial material : loot.materials) {
          array.add(material.serialize());
        }
        json.add("materials", array);
      }
    }

    @Override
    public AddToolDataFunction deserialize(JsonObject object, JsonDeserializationContext context, LootItemCondition[] conditions) {
      float damage = GsonHelper.getAsFloat(object, "damage_percent", 0f);
      if (damage < 0 || damage > 1) {
        throw new JsonSyntaxException("damage_percent must be between 0 and 1, given " + damage);
      }
      List<RandomMaterial> materials = Collections.emptyList();
      if (object.has("materials")) {
        materials = JsonHelper.parseList(object, "materials", RandomMaterial::deserialize);
      }
      return new AddToolDataFunction(conditions, damage, materials);
    }
  }

  /** Builder to create a new add tool data function */
  @Accessors(chain = true)
  public static class Builder extends LootItemConditionalFunction.Builder<AddToolDataFunction.Builder> {
    private final ImmutableList.Builder<RandomMaterial> materials = ImmutableList.builder();
    private float damage = 0;

    protected Builder() {}

    @Override
    protected Builder getThis() {
      return this;
    }

    /** Sets the damage for the tool */
    public void setDamage(float damage) {
      if (damage < 0 || damage > 1) {
        throw new IllegalArgumentException("Damage must be between 0 and 1, given " + damage);
      }
      this.damage = damage;
    }

    /** Adds a material to the builder */
    public Builder addMaterial(RandomMaterial mat) {
      materials.add(mat);
      return this;
    }

    /** Adds a material to the builder */
    public Builder addMaterial(MaterialId mat) {
      return addMaterial(RandomMaterial.fixed(mat));
    }

    @Override
    public LootItemFunction build() {
      return new AddToolDataFunction(getConditions(), damage, materials.build());
    }
  }
}
