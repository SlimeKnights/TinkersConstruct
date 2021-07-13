package slimeknights.tconstruct.gadgets.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class GadgetRecipeProvider extends BaseRecipeProvider {
  public GadgetRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Gadget Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    for (SlimeType slime : SlimeType.values()) {
      ResourceLocation name = modResource(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerWorld.congealedSlime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
    }

    folder = "gadgets/slimesling/";
    for (SlimeType slime : SlimeType.TRUE_SLIME) {
      ResourceLocation name = modResource(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
    }

    // throw balls
    folder = "gadgets/throwball/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.efln.get())
                       .key('#', Tags.Items.GUNPOWDER)
                       .key('X', Items.FLINT)
                       .patternLine(" # ")
                       .patternLine("#X#")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.efln, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
                       .key('#', Items.SNOWBALL)
                       .key('X', Tags.Items.DUSTS_GLOWSTONE)
                       .patternLine("###")
                       .patternLine("#X#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.glowBall, folder));

    // Shurikens
    folder = "gadgets/shuriken/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.flintShuriken.get(), 4)
                        .key('X', Items.FLINT)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.FLINT))
                        .build(consumer, prefix(TinkerGadgets.flintShuriken, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.quartzShuriken.get(), 4)
                        .key('X', Items.QUARTZ)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.QUARTZ))
                        .build(consumer, prefix(TinkerGadgets.quartzShuriken, folder));

    // piggybackpack
    folder = "gadgets/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
                       .key('P', TinkerMaterials.pigIron.getIngotTag())
                       .key('S', Items.SADDLE)
                       .patternLine("P")
                       .patternLine("S")
                       .addCriterion("has_item", hasItem(Items.SADDLE))
                       .build(consumer, prefix(TinkerGadgets.piggyBackpack, folder));

    // frames
    folder = "gadgets/fancy_frame/";
    frameCrafting(consumer, TinkerModifiers.silkyCloth, FrameType.JEWEL);
    frameCrafting(consumer, TinkerMaterials.cobalt.getNugget(), FrameType.COBALT);
    frameCrafting(consumer, TinkerMaterials.manyullyn.getNugget(), FrameType.MANYULLYN);
    frameCrafting(consumer, Items.GOLD_NUGGET, FrameType.GOLD);
    Item clearFrame = TinkerGadgets.itemFrame.get(FrameType.CLEAR);
    ShapedRecipeBuilder.shapedRecipe(clearFrame)
                       .key('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .key('M', Tags.Items.GLASS_COLORLESS)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                       .setGroup(modPrefix("fancy_item_frame"))
                       .build(consumer, prefix(clearFrame, folder));

    String cakeFolder = "gadgets/cake/";
    TinkerGadgets.cake.forEach((slime, cake) -> {
      Item bucket = TinkerFluids.slime.get(slime).asItem();
      ShapedRecipeBuilder.shapedRecipe(cake)
                         .key('M', bucket)
                         .key('S', slime == SlimeType.BLOOD ? Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE) : Ingredient.fromItems(Items.SUGAR))
                         .key('E', Items.EGG)
                         .key('W', TinkerWorld.slimeTallGrass.get(slime))
                         .patternLine("MMM").patternLine("SES").patternLine("WWW")
                         .addCriterion("has_slime", hasItem(bucket))
                         .build(consumer, modResource(cakeFolder + slime.getString()));
    });
    Item bucket = TinkerFluids.magma.asItem();
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.magmaCake)
                       .key('M', bucket)
                       .key('S', Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE))
                       .key('E', Items.EGG)
                       .key('W', Blocks.CRIMSON_ROOTS)
                       .patternLine("MMM").patternLine("SES").patternLine("WWW")
                       .addCriterion("has_slime", hasItem(bucket))
                       .build(consumer, modResource(cakeFolder + "magma"));
  }


  /* Helpers */

  /**
   * Adds a campfire cooking recipe
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void campfireCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 600, IRecipeSerializer.CAMPFIRE_COOKING)
                        .addCriterion("has_item", hasItem(input))
                        .build(consumer, wrap(output.asItem(), folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void foodCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    campfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    ICriterionInstance criteria = hasItem(input);
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, experience, 200)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output.asItem(), folder, "_furnace"));
    // smoker 100 ticks
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 100, IRecipeSerializer.SMOKING)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output.asItem(), folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void frameCrafting(Consumer<IFinishedRecipe> consumer, IItemProvider edges, FrameType type) {
    Item frame = TinkerGadgets.itemFrame.get(type);
    ShapedRecipeBuilder.shapedRecipe(frame)
                       .key('e', edges)
                       .key('M', Items.OBSIDIAN)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(edges))
                       .setGroup(modPrefix("fancy_item_frame"))
                       .build(consumer, prefix(frame, "gadgets/fancy_frame/"));

  }
}
