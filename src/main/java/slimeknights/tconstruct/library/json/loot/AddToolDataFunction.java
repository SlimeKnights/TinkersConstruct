package slimeknights.tconstruct.library.json.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.loadable.LoadableCodec;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

/** Loot function to add data to a tool. */
public class AddToolDataFunction extends LootItemConditionalFunction {
  public static final ResourceLocation ID = TConstruct.getResource("add_tool_data");
  private static final Codec<List<RandomMaterial>> MATERIAL_LIST_CODEC = new LoadableCodec<>(RandomMaterial.LOADER).listOf();
  public static final MapCodec<AddToolDataFunction> CODEC = RecordCodecBuilder.mapCodec(
    instance -> commonFields(instance).and(instance.group(
      Codec.floatRange(0, 1).optionalFieldOf("damage_percent", 0f).forGetter(loot -> loot.damage),
      MATERIAL_LIST_CODEC.optionalFieldOf("materials", List.of()).forGetter(loot -> loot.materials)
    )).apply(instance, AddToolDataFunction::new)
  );

  /** Percentage of damage on the tool, if 0 the tool is undamaged */
  private final float damage;
  /** Fixed materials on the tool, any nulls in the list will randomize */
  private final List<RandomMaterial> materials;

  protected AddToolDataFunction(List<LootItemCondition> conditionsIn, float damage, List<RandomMaterial> materials) {
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
    return TinkerTools.lootAddToolData.get();
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    if (stack.is(TinkerTags.Items.MODIFIABLE)) {
      ToolStack tool = ToolStack.from(stack);
      ToolDefinition definition = tool.getDefinition();
      if (definition.hasMaterials() && !materials.isEmpty()) {
        tool.setMaterials(RandomMaterial.build(ToolMaterialHook.stats(definition), materials, context.getRandom()));
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
