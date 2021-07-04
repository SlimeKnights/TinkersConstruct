package slimeknights.tconstruct.tools.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class ToolsRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper {
  public ToolsRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addToolBuildingRecipes(consumer);
    this.addPartRecipes(consumer);
    this.addMaterialsRecipes(consumer);
    this.addMaterialSmeltery(consumer);
  }

  private void addToolBuildingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/building/";
    // stone
    toolBuilding(consumer, TinkerTools.pickaxe, folder);
    toolBuilding(consumer, TinkerTools.sledgeHammer, folder);
    toolBuilding(consumer, TinkerTools.veinHammer, folder);
    // dirt
    toolBuilding(consumer, TinkerTools.mattock, folder);
    toolBuilding(consumer, TinkerTools.excavator, folder);
    // wood
    toolBuilding(consumer, TinkerTools.handAxe, folder);
    toolBuilding(consumer, TinkerTools.broadAxe, folder);
    // plants
    toolBuilding(consumer, TinkerTools.kama, folder);
    toolBuilding(consumer, TinkerTools.scythe, folder);
    // sword
    toolBuilding(consumer, TinkerTools.dagger, folder);
    toolBuilding(consumer, TinkerTools.sword, folder);
    toolBuilding(consumer, TinkerTools.cleaver, folder);
  }

  private void addPartRecipes(Consumer<IFinishedRecipe> consumer) {
    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";
    partRecipes(consumer, TinkerToolParts.repairKit, TinkerSmeltery.repairKitCast, 2, partFolder, castFolder);
    // head
    partRecipes(consumer, TinkerToolParts.pickaxeHead,  TinkerSmeltery.pickaxeHeadCast,  2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.hammerHead,   TinkerSmeltery.hammerHeadCast,   8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallAxeHead, TinkerSmeltery.smallAxeHeadCast, 2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadAxeHead, TinkerSmeltery.broadAxeHeadCast, 8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallBlade,   TinkerSmeltery.smallBladeCast,   2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadBlade,   TinkerSmeltery.broadBladeCast,   8, partFolder, castFolder);
    // other parts
    partRecipes(consumer, TinkerToolParts.toolBinding, TinkerSmeltery.toolBindingCast, 1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.largePlate,  TinkerSmeltery.largePlateCast,  4, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toolHandle,  TinkerSmeltery.toolHandleCast,  1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toughHandle, TinkerSmeltery.toughHandleCast, 3, partFolder, castFolder);
  }

  private void addMaterialsRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/materials/";
    // tier 1
    materialRecipe(consumer, MaterialIds.wood, Ingredient.fromTag(Tags.Items.RODS_WOODEN), 1, 2, folder + "wood/sticks");
    materialRecipe(consumer, MaterialIds.wood, new IngredientWithout(Ingredient.fromTag(ItemTags.PLANKS), Ingredient.fromTag(TinkerTags.Items.SLIMY_PLANKS)),
                   1, 1, folder + "wood/planks");
    materialRecipe(consumer, MaterialIds.wood, new IngredientWithout(Ingredient.fromTag(ItemTags.LOGS), Ingredient.fromTag(TinkerTags.Items.SLIMY_LOGS)),
                   4, 1, ItemOutput.fromStack(new ItemStack(Items.STICK, 2)), folder + "wood/logs");
    materialRecipe(consumer, MaterialIds.stone, CompoundIngredient.from(
      Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromTag(Tags.Items.COBBLESTONE), Ingredient.fromItems(Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE)), 1, 1, folder + "stone");
    materialRecipe(consumer, MaterialIds.flint, Ingredient.fromItems(Items.FLINT, Blocks.BASALT, Blocks.POLISHED_BASALT), 1, 1, folder + "flint");
    materialRecipe(consumer, MaterialIds.bone, Ingredient.fromTag(Tags.Items.BONES), 1, 1, folder + "bone");
    materialRecipe(consumer, MaterialIds.necroticBone, Ingredient.fromTag(TinkerTags.Items.WITHER_BONES), 1, 1, folder + "necrotic_bone");
    // tier 2
    metalMaterialRecipe(consumer, MaterialIds.iron, folder, "iron", false);
    materialRecipe(consumer, MaterialIds.searedStone, Ingredient.fromItems(TinkerSmeltery.searedBrick),       1, 2, folder + "seared_stone/brick");
    materialRecipe(consumer, MaterialIds.searedStone, Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS),     2, 1, ItemOutput.fromItem(TinkerSmeltery.searedBrick), folder + "seared_stone/block");
    materialRecipe(consumer, MaterialIds.scorchedStone, Ingredient.fromItems(TinkerSmeltery.scorchedBrick),   1, 2, folder + "scorched_stone/brick");
    materialRecipe(consumer, MaterialIds.scorchedStone, Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS), 2, 1, ItemOutput.fromItem(TinkerSmeltery.scorchedBrick), folder + "scorched_stone/block");
    metalMaterialRecipe(consumer, MaterialIds.copper, folder, "copper", false);
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerTags.Items.SLIMY_PLANKS), 1, 1, folder + "slimewood/planks");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.greenheart.getLogItemTag()),  4, 1, ItemOutput.fromItem(TinkerWorld.greenheart),  folder + "slimewood/greenheart_logs");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.skyroot.getLogItemTag()),     4, 1, ItemOutput.fromItem(TinkerWorld.skyroot),     folder + "slimewood/skyroot_logs");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.bloodshroom.getLogItemTag()), 4, 1, ItemOutput.fromItem(TinkerWorld.bloodshroom), folder + "slimewood/bloodshroom_logs");
    metalMaterialRecipe(consumer, MaterialIds.roseGold, folder, "rose_gold", false);
    // tier 3
    metalMaterialRecipe(consumer, MaterialIds.slimesteel, folder, "slimesteel", false);
    materialRecipe(consumer, MaterialIds.nahuatl, Ingredient.fromItems(Items.OBSIDIAN), 1, 1, folder + "nahuatl");
    metalMaterialRecipe(consumer, MaterialIds.tinkersBronze, folder, "silicon_bronze", false);
    metalMaterialRecipe(consumer, MaterialIds.pigIron, folder, "pig_iron", false);
    materialRecipe(consumer, MaterialIds.pigIron, Ingredient.fromItems(TinkerCommons.bacon), 1, 4, folder + "pig_iron/bacon");

    // tier 2 (nether)
    // tier 3 (nether)
    metalMaterialRecipe(consumer, MaterialIds.cobalt, folder, "cobalt", false);
    // tier 4
    metalMaterialRecipe(consumer, MaterialIds.queensSlime, folder, "queens_slime", false);
    metalMaterialRecipe(consumer, MaterialIds.manyullyn, folder, "manyullyn", false);
    metalMaterialRecipe(consumer, MaterialIds.hepatizon, folder, "hepatizon", false);
    //registerMetalMaterial(consumer, MaterialIds.soulsteel,   "soulsteel",    false);

    // tier 2 (mod compat)
    metalMaterialRecipe(consumer, MaterialIds.silver, folder, "silver", true);
    metalMaterialRecipe(consumer, MaterialIds.lead, folder, "lead", true);
    metalMaterialRecipe(consumer, MaterialIds.electrum, folder, "electrum", true);
    // tier 3 (mod integration)
    metalMaterialRecipe(consumer, MaterialIds.bronze, folder, "bronze", true);
    metalMaterialRecipe(consumer, MaterialIds.steel, folder, "steel", true);
    metalMaterialRecipe(consumer, MaterialIds.constantan, folder, "constantan", true);
  }

  private void addMaterialSmeltery(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/materials/";

    // melting and casting
    // tier 2
    addMaterialMeltingCasting(consumer, MaterialIds.iron, TinkerFluids.moltenIron, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.copper,        TinkerFluids.moltenCopper, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.searedStone, TinkerFluids.searedStone, FluidValues.INGOT * 2, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.scorchedStone, TinkerFluids.scorchedStone, FluidValues.INGOT * 2, folder);
    // half a clay is 1 seared brick per grout amounts
    addCompositeMaterialRecipe(consumer, MaterialIds.stone, MaterialIds.searedStone, TinkerFluids.moltenClay, FluidValues.SLIMEBALL, false, folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.wood, MaterialIds.slimewood, TinkerFluids.earthSlime, FluidValues.SLIMEBALL, true, folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.flint, MaterialIds.scorchedStone, TinkerFluids.magma, FluidValues.SLIMEBALL, true, folder);

    // tier 3
    addMaterialMeltingCasting(consumer, MaterialIds.slimesteel,    TinkerFluids.moltenSlimesteel,    folder);
    addMaterialMeltingCasting(consumer, MaterialIds.tinkersBronze, TinkerFluids.moltenTinkersBronze, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.roseGold,      TinkerFluids.moltenRoseGold,      folder);
    addMaterialMeltingCasting(consumer, MaterialIds.pigIron,       TinkerFluids.moltenPigIron,       folder);
    addMaterialMeltingCasting(consumer, MaterialIds.cobalt,        TinkerFluids.moltenCobalt,        folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.wood, MaterialIds.nahuatl, TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, false, folder);
    MaterialMeltingRecipeBuilder.material(MaterialIds.nahuatl, new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK))
                                .build(consumer, modResource(folder + "melting/nahuatl"));

    // tier 4
    addMaterialMeltingCasting(consumer, MaterialIds.queensSlime, TinkerFluids.moltenQueensSlime, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.hepatizon,   TinkerFluids.moltenHepatizon,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.manyullyn,   TinkerFluids.moltenManyullyn,   folder);

    // tier 2 compat
    addMaterialMeltingCasting(consumer, MaterialIds.lead,   TinkerFluids.moltenLead,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.silver, TinkerFluids.moltenSilver, folder);
    // tier 3 compat
    addMaterialMeltingCasting(consumer, MaterialIds.electrum,   TinkerFluids.moltenElectrum,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.bronze,     TinkerFluids.moltenBronze,     folder);
    addMaterialMeltingCasting(consumer, MaterialIds.steel,      TinkerFluids.moltenSteel,      folder);
    addMaterialMeltingCasting(consumer, MaterialIds.constantan, TinkerFluids.moltenConstantan, folder);
  }
}
