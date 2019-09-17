package slimeknights.tconstruct.common.data;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;

import java.nio.file.Path;
import java.util.function.Consumer;

import static slimeknights.tconstruct.shared.TinkerCommons.*;

public class TConstructRecipeProvider extends RecipeProvider {

  public TConstructRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommon(consumer);

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.addWorld(consumer);
    }

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_GADGETS_PULSE_ID)) {
      this.addGadgets(consumer);
    }
  }

  private void addCommon(Consumer<IFinishedRecipe> consumer) {
    ShapelessRecipeBuilder.shapelessRecipe(firewood).addIngredient(Items.BLAZE_POWDER).addIngredient(lavawood).addIngredient(Items.BLAZE_POWDER).addCriterion("has_lavawood", this.hasItem(lavawood)).build(consumer, "tconstruct:common/firewood/firewood");
    ShapedRecipeBuilder.shapedRecipe(firewood_slab, 6).key('#', firewood).patternLine("###").addCriterion("has_firewood", this.hasItem(firewood)).build(consumer, "tconstruct:common/firewood/firewood_slab");
    ShapedRecipeBuilder.shapedRecipe(firewood_stairs, 4).key('#', firewood).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_firewood", this.hasItem(firewood)).build(consumer, "tconstruct:common/firewood/firewood_stairs");
    ShapedRecipeBuilder.shapedRecipe(lavawood_slab, 6).key('#', firewood).patternLine("###").addCriterion("has_firewood", this.hasItem(firewood)).build(consumer, "tconstruct:common/firewood/lavawood_slab");
    ShapedRecipeBuilder.shapedRecipe(lavawood_stairs, 4).key('#', firewood).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_firewood", this.hasItem(firewood)).build(consumer, "tconstruct:common/firewood/lavawood_stairs");

    ShapedRecipeBuilder.shapedRecipe(black_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_BLACK).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/black_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(blue_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_BLUE).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(brown_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_BROWN).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/brown_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(cyan_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_CYAN).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/cyan_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(gray_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_GRAY).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(green_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_GREEN).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/green_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(light_blue_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_LIGHT_BLUE).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/light_blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(light_gray_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_LIGHT_GRAY).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/light_gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(lime_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_LIME).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/lime_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(magenta_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_MAGENTA).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/magenta_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(orange_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_ORANGE).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/orange_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(pink_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_PINK).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/pink_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(purple_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_PURPLE).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/purple_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(red_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_RED).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/red_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(white_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_WHITE).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/white_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(yellow_clear_stained_glass, 8).key('#', clear_glass).key('X', Tags.Items.DYES_YELLOW).patternLine("###").patternLine("#X#").patternLine("###").setGroup("tconstruct:stained_clear_glass").addCriterion("has_clear_glass", this.hasItem(clear_glass)).build(consumer, "tconstruct:common/glass/yellow_clear_stained_glass");

    //ShapedRecipeBuilder.shapedRecipe(alubrass_block).key('#', alubrass_ingot)

    ShapelessRecipeBuilder.shapelessRecipe(graveyard_soil).addIngredient(Blocks.DIRT).addIngredient(Items.ROTTEN_FLESH).addIngredient(Items.BONE_MEAL).addCriterion("has_dirt", this.hasItem(Blocks.DIRT)).addCriterion("has_rotten_flesh", this.hasItem(Items.ROTTEN_FLESH)).addCriterion("has_bone_meal", this.hasItem(Items.BONE_MEAL)).build(consumer, "tconstruct:common/soil/graveyard_soil");
    ShapedRecipeBuilder.shapedRecipe(mud_bricks).key('#', mud_brick).patternLine("##").patternLine("##").addCriterion("has_mud_brick", this.hasItem(mud_brick)).build(consumer, "tconstruct:common/soil/mud_bricks_block");
    ShapedRecipeBuilder.shapedRecipe(mud_bricks_slab, 6).key('#', mud_bricks).patternLine("###").setGroup("tconstruct:mud_brick_slab").addCriterion("has_mud_bricks", this.hasItem(mud_bricks)).build(consumer, "tconstruct:common/soil/mud_bricks_slab_block");
    ShapedRecipeBuilder.shapedRecipe(mud_bricks_slab).key('#', mud_brick).patternLine("##").setGroup("tconstruct:mud_brick_slab").addCriterion("has_mud_brick", this.hasItem(mud_brick)).build(consumer, "tconstruct:common/soil/mud_bricks_slab");
    ShapedRecipeBuilder.shapedRecipe(mud_bricks_stairs, 4).key('#', mud_bricks).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_mud_bricks", this.hasItem(mud_bricks)).build(consumer, "tconstruct:common/soil/mud_bricks_stairs");

    ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT).addIngredient(Blocks.GRAVEL).addIngredient(Blocks.GRAVEL).addIngredient(Blocks.GRAVEL).addCriterion("has_gravel", this.hasItem(Blocks.GRAVEL)).build(consumer, "tconstruct:common/flint");
  }

  private void addWorld(Consumer<IFinishedRecipe> consumer) {

  }

  private void addGadgets(Consumer<IFinishedRecipe> consumer) {
    /*ShapedRecipeBuilder.shapedRecipe(LOADER_BLOCK, 10)
            .key('#', Items.ENDER_PEARL)
            .patternLine("# #").patternLine("###").patternLine("# #")
            .addCriterion("has_cobblestone", hasItem(Items.ENDER_PEARL))
            .build(consumer);*/
  }
}
