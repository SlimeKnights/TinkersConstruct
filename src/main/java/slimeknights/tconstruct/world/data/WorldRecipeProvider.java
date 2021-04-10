package slimeknights.tconstruct.world.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.misc.CommonTags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class WorldRecipeProvider extends BaseRecipeProvider {
  public WorldRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct World Recipes";
  }

  @Override
  protected void generate(Consumer<RecipeJsonProvider> consumer) {
    // Add recipe for all slimeball <-> congealed and slimeblock <-> slimeball
    // only earth slime recipe we need here slime
    ShapedRecipeJsonFactory.create(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                       .input('#', SlimeType.EARTH.getSlimeBallTag())
                       .pattern("##")
                       .pattern("##")
                       .criterion("has_item", conditionsFromTag(SlimeType.EARTH.getSlimeBallTag()))
                       .group("tconstruct:congealed_slime")
                       .offerTo(consumer, location("common/slime/earth/congealed"));

    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      Identifier name = location("common/slime/" + slimeType.asString() + "/congealed");
      ShapedRecipeJsonFactory.create(TinkerWorld.congealedSlime.get(slimeType))
                         .input('#', slimeType.getSlimeBallTag())
                         .pattern("##")
                         .pattern("##")
                         .criterion("has_item", conditionsFromTag(slimeType.getSlimeBallTag()))
                         .group("tconstruct:congealed_slime")
                         .offerTo(consumer, name);
      Identifier blockName = location("common/slime/" + slimeType.asString() + "/slimeblock");
      ShapedRecipeJsonFactory.create(TinkerWorld.slime.get(slimeType))
                         .input('#', slimeType.getSlimeBallTag())
                         .pattern("###")
                         .pattern("###")
                         .pattern("###")
                         .criterion("has_item", conditionsFromTag(slimeType.getSlimeBallTag()))
                         .group("slime_blocks")
                         .offerTo(consumer, blockName);
      // green already can craft into slime balls
      ShapelessRecipeJsonFactory.create(TinkerCommons.slimeball.get(slimeType), 9)
                            .input(TinkerWorld.slime.get(slimeType))
                            .criterion("has_item", conditionsFromItem(TinkerWorld.slime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .offerTo(consumer, "tconstruct:common/slime/" + slimeType.asString() + "/slimeball_from_block");
    }
    // all types of congealed need a recipe to a block
    for (SlimeType slimeType : SlimeType.values()) {
      ShapelessRecipeJsonFactory.create(TinkerCommons.slimeball.get(slimeType), 4)
                            .input(TinkerWorld.congealedSlime.get(slimeType))
                            .criterion("has_item", conditionsFromItem(TinkerWorld.congealedSlime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .offerTo(consumer, "tconstruct:common/slime/" + slimeType.asString() + "/slimeball_from_congealed");
    }

    // craft other slime based items, forge does not automatically add recipes using the tag anymore
    ShapedRecipeJsonFactory.create(Blocks.STICKY_PISTON)
                       .pattern("#")
                       .pattern("P")
                       .input('#', CommonTags.SLIMEBALLS)
                       .input('P', Blocks.PISTON)
                       .criterion("has_slime_ball", conditionsFromTag(CommonTags.SLIMEBALLS))
                       .offerTo(consumer, location("common/slime/sticky_piston"));
    ShapedRecipeJsonFactory.create(Items.LEAD, 2)
                       .input('~', Items.STRING)
                       .input('O', CommonTags.SLIMEBALLS)
                       .pattern("~~ ")
                       .pattern("~O ")
                       .pattern("  ~")
                       .criterion("has_slime_ball", conditionsFromTag(CommonTags.SLIMEBALLS))
                       .offerTo(consumer, location("common/slime/lead"));
    ShapelessRecipeJsonFactory.create(Items.MAGMA_CREAM)
                          .input(Items.BLAZE_POWDER)
                          .input(CommonTags.SLIMEBALLS)
                          .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                          .offerTo(consumer, location("common/slime/magma_cream"));
  }
}
