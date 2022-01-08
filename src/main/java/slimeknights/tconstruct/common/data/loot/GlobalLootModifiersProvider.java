package slimeknights.tconstruct.common.data.loot;

import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext.EntityTarget;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import slimeknights.mantle.loot.AddEntryLootModifier;
import slimeknights.mantle.loot.ReplaceItemLootModifier;
import slimeknights.mantle.loot.condition.BlockTagLootCondition;
import slimeknights.mantle.loot.condition.ContainsItemModifierLootCondition;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.recipe.BlockOrEntityCondition;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;

public class GlobalLootModifiersProvider extends GlobalLootModifierProvider {
  public GlobalLootModifiersProvider(DataGenerator gen) {
    super(gen, TConstruct.MOD_ID);
  }

  @Override
  protected void start() {
    ReplaceItemLootModifier.builder(Ingredient.fromItems(Items.BONE), ItemOutput.fromItem(TinkerMaterials.necroticBone))
                           .addCondition(LootTableIdCondition.builder(new ResourceLocation("entities/wither_skeleton")).build())
                           .addCondition(ConfigEnabledCondition.WITHER_BONE_DROP)
                           .build("wither_bone", this);

    // generic modifier hook
    ItemPredicate.Builder meleeHarvest = ItemPredicate.Builder.create().tag(TinkerTags.Items.MELEE_OR_HARVEST);
    ModifierLootModifier.builder()
                        .addCondition(BlockOrEntityCondition.INSTANCE)
                        .addCondition(MatchTool.builder(meleeHarvest)
                                               .alternative(EntityHasProperty.builder(EntityTarget.KILLER, EntityPredicate.Builder.create().equipment(mainHand(meleeHarvest.build()))))
                                               .build())
                        .build("modifier_hook", this);

    // chrysophilite modifier hook
    AddEntryLootModifier.builder(ItemLootEntry.builder(Items.GOLD_NUGGET))
                        .addCondition(new BlockTagLootCondition(TinkerTags.Blocks.CHRYSOPHILITE_ORES))
                        .addCondition(new ContainsItemModifierLootCondition(Ingredient.fromTag(TinkerTags.Items.CHRYSOPHILITE_ORES)).inverted())
                        .addCondition(ChrysophiliteLootCondition.INSTANCE)
                        .addFunction(SetCount.builder(RandomValueRange.of(2, 6)).build())
                        .addFunction(ChrysophiliteBonusFunction.oreDrops(false).build())
                        .addFunction(ExplosionDecay.builder().build())
                        .build("chrysophilite_modifier", this);
  }

  /** Creates an equipment predicate for mainhand */
  private static EntityEquipmentPredicate mainHand(ItemPredicate mainHand) {
    EntityEquipmentPredicate.Builder builder = EntityEquipmentPredicate.Builder.createBuilder();
    builder.mainHand = mainHand;
    return builder.build();
  }
}
