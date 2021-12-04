package slimeknights.tconstruct.tools.data.material;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.ingredient.FluidContainerIngredient;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class MaterialRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper {
  public MaterialRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Recipe";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    addMaterialItems(consumer);
    addMaterialSmeltery(consumer);
  }

  private void addMaterialItems(Consumer<IFinishedRecipe> consumer) {
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
    materialRecipe(consumer, MaterialIds.string, Ingredient.fromTag(Tags.Items.STRING), 1, 4, folder + "string");
    materialRecipe(consumer, MaterialIds.leather, Ingredient.fromTag(Tags.Items.LEATHER), 1, 1, folder + "leather");
    materialRecipe(consumer, MaterialIds.vine, Ingredient.fromItems(Items.VINE, Items.TWISTING_VINES, Items.WEEPING_VINES), 1, 1, folder + "vine");

    // tier 2
    metalMaterialRecipe(consumer, MaterialIds.iron, folder, "iron", false);
    materialRecipe(consumer, MaterialIds.searedStone, Ingredient.fromItems(TinkerSmeltery.searedBrick), 1, 2, folder + "seared_stone/brick");
    materialRecipe(consumer, MaterialIds.searedStone, Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS),     2, 1, ItemOutput.fromItem(TinkerSmeltery.searedBrick), folder + "seared_stone/block");
    materialRecipe(consumer, MaterialIds.scorchedStone, Ingredient.fromItems(TinkerSmeltery.scorchedBrick),   1, 2, folder + "scorched_stone/brick");
    materialRecipe(consumer, MaterialIds.scorchedStone, Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS), 2, 1, ItemOutput.fromItem(TinkerSmeltery.scorchedBrick), folder + "scorched_stone/block");
    metalMaterialRecipe(consumer, MaterialIds.copper, folder, "copper", false);
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerTags.Items.SLIMY_PLANKS), 1, 1, folder + "slimewood/planks");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.greenheart.getLogItemTag()), 4, 1, ItemOutput.fromItem(TinkerWorld.greenheart), folder + "slimewood/greenheart_logs");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.skyroot.getLogItemTag()),     4, 1, ItemOutput.fromItem(TinkerWorld.skyroot),     folder + "slimewood/skyroot_logs");
    materialRecipe(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerWorld.bloodshroom.getLogItemTag()), 4, 1, ItemOutput.fromItem(TinkerWorld.bloodshroom), folder + "slimewood/bloodshroom_logs");
    materialRecipe(consumer, MaterialIds.bloodbone, Ingredient.fromItems(TinkerMaterials.bloodbone), 1, 1, folder + "bloodbone");
    metalMaterialRecipe(consumer, MaterialIds.roseGold, folder, "rose_gold", false);
    materialRecipe(consumer, MaterialIds.chain, Ingredient.fromItems(Blocks.CHAIN), 1, 1, folder + "chain");
    materialRecipe(consumer, MaterialIds.skyslimeVine, Ingredient.fromItems(TinkerWorld.skySlimeVine), 1, 1, folder + "skyslime_vine");
    // tier 3
    metalMaterialRecipe(consumer, MaterialIds.slimesteel, folder, "slimesteel", false);
    materialRecipe(consumer, MaterialIds.nahuatl, Ingredient.fromItems(TinkerMaterials.nahuatl), 1, 1, folder + "nahuatl");
    metalMaterialRecipe(consumer, MaterialIds.tinkersBronze, folder, "silicon_bronze", false);
    metalMaterialRecipe(consumer, MaterialIds.pigIron, folder, "pig_iron", false);

    // tier 2 (nether)
    // tier 3 (nether)
    metalMaterialRecipe(consumer, MaterialIds.cobalt, folder, "cobalt", false);
    // tier 4
    metalMaterialRecipe(consumer, MaterialIds.queensSlime, folder, "queens_slime", false);
    metalMaterialRecipe(consumer, MaterialIds.manyullyn, folder, "manyullyn", false);
    metalMaterialRecipe(consumer, MaterialIds.hepatizon, folder, "hepatizon", false);
    materialRecipe(consumer, MaterialIds.blazingBone, Ingredient.fromItems(TinkerMaterials.blazingBone), 1, 1, folder + "blazing_bone");
    //registerMetalMaterial(consumer, MaterialIds.soulsteel,   "soulsteel",    false);

    // tier 5
    materialRecipe(consumer, MaterialIds.enderslimeVine, Ingredient.fromItems(TinkerWorld.enderSlimeVine), 1, 1, folder + "enderslime_vine");

    // tier 2 (mod compat)
    metalMaterialRecipe(consumer, MaterialIds.osmium, folder, "osmium", true);
    metalMaterialRecipe(consumer, MaterialIds.tungsten, folder, "tungsten", true);
    metalMaterialRecipe(consumer, MaterialIds.platinum, folder, "platinum", true);
    metalMaterialRecipe(consumer, MaterialIds.silver, folder, "silver", true);
    metalMaterialRecipe(consumer, MaterialIds.lead, folder, "lead", true);
    // no whitestone, use repair kits
    // tier 3 (mod integration)
    metalMaterialRecipe(consumer, MaterialIds.steel, folder, "steel", true);
    metalMaterialRecipe(consumer, MaterialIds.bronze, folder, "bronze", true);
    metalMaterialRecipe(consumer, MaterialIds.constantan, folder, "constantan", true);
    metalMaterialRecipe(consumer, MaterialIds.invar, folder, "invar", true);
    materialRecipe(withCondition(consumer, tagCondition("ingots/uranium")), MaterialIds.necronium, Ingredient.fromItems(TinkerMaterials.necroniumBone), 1, 1, folder + "necronium");
    metalMaterialRecipe(consumer, MaterialIds.electrum, folder, "electrum", true);
    // no plated slimewood, use repair kits

    // slimesuit
    materialRecipe(consumer, MaterialIds.gunpowder, Ingredient.fromTag(Tags.Items.GUNPOWDER), 1, 4, folder + "gunpowder");
    materialRecipe(consumer, MaterialIds.enderPearl, Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 1, 1, folder + "ender_pearl");
    materialRecipe(consumer, MaterialIds.rabbit, Ingredient.fromItems(Items.RABBIT_HIDE), 1, 2, folder + "rabbit_hide");
    materialRecipe(consumer, MaterialIds.spider, Ingredient.fromItems(Items.SPIDER_EYE), 1, 4, folder + "spider");
    materialRecipe(consumer, MaterialIds.venom, FluidContainerIngredient.fromFluid(TinkerFluids.venom, false), 4, 1, folder + "venom_bucket");
  }

  private void addMaterialSmeltery(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/materials/";

    // melting and casting
    // tier 2
    materialMeltingCasting(consumer, MaterialIds.iron, TinkerFluids.moltenIron, true, folder);
    materialMeltingCasting(consumer, MaterialIds.copper,        TinkerFluids.moltenCopper,  true,  folder);
    materialMeltingCasting(consumer, MaterialIds.searedStone, TinkerFluids.searedStone, false, FluidValues.INGOT * 2, folder);
    materialMeltingCasting(consumer, MaterialIds.scorchedStone, TinkerFluids.scorchedStone, false, FluidValues.INGOT * 2, folder);
    materialMelting(consumer, MaterialIds.chain, TinkerFluids.moltenIron.get(), FluidValues.INGOT + (FluidValues.NUGGET * 2), folder);
    // half a clay is 1 seared brick per grout amounts
    materialComposite(consumer, MaterialIds.stone, MaterialIds.searedStone,   TinkerFluids.moltenClay, FluidValues.SLIMEBALL, false, folder);
    materialComposite(consumer, MaterialIds.wood,  MaterialIds.slimewood,     TinkerFluids.earthSlime, FluidValues.SLIMEBALL, true,  folder);
    materialComposite(consumer, MaterialIds.flint, MaterialIds.scorchedStone, TinkerFluids.magma,      FluidValues.SLIMEBALL, true,  folder);
    materialComposite(consumer, MaterialIds.bone,  MaterialIds.bloodbone,     TinkerFluids.blood,      FluidValues.SLIMEBALL, false, folder);

    // tier 3
    materialMeltingCasting(consumer, MaterialIds.slimesteel,    TinkerFluids.moltenSlimesteel,    false, folder);
    materialMeltingCasting(consumer, MaterialIds.tinkersBronze, TinkerFluids.moltenTinkersBronze, false, folder);
    materialMeltingCasting(consumer, MaterialIds.roseGold,      TinkerFluids.moltenRoseGold,      true,  folder);
    materialMeltingCasting(consumer, MaterialIds.pigIron,       TinkerFluids.moltenPigIron,       false, folder);
    materialMeltingCasting(consumer, MaterialIds.cobalt,        TinkerFluids.moltenCobalt,        true,  folder);
    materialMeltingComposite(consumer, MaterialIds.wood, MaterialIds.nahuatl, TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, false, folder);

    // tier 4
    materialMeltingCasting(consumer, MaterialIds.queensSlime, TinkerFluids.moltenQueensSlime, false, folder);
    materialMeltingCasting(consumer, MaterialIds.hepatizon,   TinkerFluids.moltenHepatizon,   true,  folder);
    materialMeltingCasting(consumer, MaterialIds.manyullyn,   TinkerFluids.moltenManyullyn,   true,  folder);
    materialComposite(consumer, MaterialIds.necroticBone, MaterialIds.blazingBone, TinkerFluids.blazingBlood, FluidAttributes.BUCKET_VOLUME / 5, false, folder);

    // tier 2 compat
    materialMeltingCasting(consumer, MaterialIds.osmium,   TinkerFluids.moltenOsmium,   true, folder);
    materialMeltingCasting(consumer, MaterialIds.tungsten, TinkerFluids.moltenTungsten, true, folder);
    materialMeltingCasting(consumer, MaterialIds.platinum, TinkerFluids.moltenPlatinum, true, folder);
    materialMeltingCasting(consumer, MaterialIds.silver,   TinkerFluids.moltenSilver,   true, folder);
    materialMeltingCasting(consumer, MaterialIds.lead,     TinkerFluids.moltenLead,     true, folder);
    materialComposite(withCondition(consumer, tagCondition("ingots/aluminum")), MaterialIds.stone, MaterialIds.whitestone, TinkerFluids.moltenAluminum, FluidValues.INGOT, true, folder, "whitestone_from_aluminum");
    materialComposite(withCondition(consumer, tagCondition("ingots/tin")),      MaterialIds.stone, MaterialIds.whitestone, TinkerFluids.moltenTin,      FluidValues.INGOT, true, folder, "whitestone_from_tin");
    materialComposite(withCondition(consumer, tagCondition("ingots/zinc")),     MaterialIds.stone, MaterialIds.whitestone, TinkerFluids.moltenZinc,     FluidValues.INGOT, true, folder, "whitestone_from_zinc");
    // tier 3 compat
    materialMeltingCasting(consumer, MaterialIds.steel,          TinkerFluids.moltenSteel,      true, folder);
    materialMeltingCasting(consumer, MaterialIds.bronze,         TinkerFluids.moltenBronze,     true, folder);
    materialMeltingCasting(consumer, MaterialIds.constantan,     TinkerFluids.moltenConstantan, true, folder);
    materialMeltingCasting(consumer, MaterialIds.invar,          TinkerFluids.moltenInvar,      true, folder);
    materialMeltingCasting(consumer, MaterialIds.electrum,       TinkerFluids.moltenElectrum,   true, folder);
    materialMeltingComposite(consumer, MaterialIds.necroticBone, MaterialIds.necronium,       TinkerFluids.moltenUranium, FluidValues.INGOT, true, folder);
    materialMeltingComposite(consumer, MaterialIds.slimewood,    MaterialIds.platedSlimewood, TinkerFluids.moltenBrass,   FluidValues.INGOT, true, folder);
  }
}
