/*     */ package net.bjculk.magic;
/*     */ 
/*     */ import cpw.mods.fml.common.registry.GameRegistry;

/*     */ import java.util.List;

/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.crafting.CraftingManager;
/*     */ import net.minecraft.item.crafting.FurnaceRecipes;
/*     */ import net.minecraft.item.crafting.IRecipe;
/*     */ import net.minecraft.item.crafting.ShapedRecipes;
/*     */ 
/*     */ public class InfiBlockRecipes
/*     */ {
/*  19 */   static ItemStack[] workbenchArray = { new ItemStack(Block.cobblestone), new ItemStack(Item.ingotIron), new ItemStack(Item.redstone), new ItemStack(Item.bone), new ItemStack(Block.sandStone, 1, 2), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Block.obsidian), new ItemStack(Block.cactus), new ItemStack(Block.netherrack), new ItemStack(Block.ice), new ItemStack(Block.stoneBrick) };
/*     */ 
/*  33 */   static ItemStack[] furnaceArray = { new ItemStack(Item.brick), new ItemStack(Block.sandStone), new ItemStack(Block.obsidian), new ItemStack(Item.redstone), new ItemStack(Block.netherrack), new ItemStack(Block.stoneBrick), new ItemStack(Block.whiteStone), new ItemStack(Block.glowStone) };
/*     */ 
/*  45 */   static ItemStack[] magicSlabStoneArray = { new ItemStack(Block.stone), new ItemStack(Block.stoneSingleSlab, 1, 0), new ItemStack(Block.cobblestone), new ItemStack(Block.stoneBrick, 1, 0), new ItemStack(Block.stoneBrick, 1, 1), new ItemStack(Block.stoneBrick, 1, 2), new ItemStack(Block.brick), new ItemStack(Block.cobblestoneMossy), new ItemStack(Block.obsidian), new ItemStack(Block.netherrack), new ItemStack(Block.sandStone), new ItemStack(Item.ingotIron), new ItemStack(Item.ingotGold), new ItemStack(Item.diamond), new ItemStack(Block.whiteStone), new ItemStack(Block.netherBrick) };
/*     */ 
/*  64 */   static ItemStack[] magicSlabSoilArray = { new ItemStack(Block.dirt), new ItemStack(Block.grass), new ItemStack(Block.mycelium), new ItemStack(Block.sand), new ItemStack(Block.gravel), new ItemStack(Block.wood, 1, 0), new ItemStack(Block.wood, 1, 1), new ItemStack(Block.wood, 1, 2), new ItemStack(Block.planks), new ItemStack(Block.blockSnow), new ItemStack(Block.slowSand), new ItemStack(Block.mushroomBrown), new ItemStack(Block.mushroomRed), new ItemStack(Block.glowStone), new ItemStack(Block.glass), new ItemStack(Block.leaves, 1, -1) };
/*     */ 
/*  83 */   static ItemStack[] chestArray = { new ItemStack(Block.planks, 1, 0), new ItemStack(Block.planks, 1, 1), new ItemStack(Block.planks, 1, 2), new ItemStack(Block.planks, 1, 3), new ItemStack(Block.cactus), new ItemStack(Block.wood, 1, -1), new ItemStack(Block.stoneBrick, 1, 1), new ItemStack(Block.stone), new ItemStack(Block.stoneBrick, 1, 0), new ItemStack(Block.obsidian), new ItemStack(Block.ice), new ItemStack(Item.ingotIron), new ItemStack(Item.ingotGold), new ItemStack(Item.slimeBall), new ItemStack(Block.whiteStone) };
/*     */ 
/* 101 */   public static String[] chestRecipe = { "mmm", "mbm", "mmm" };
/*     */ 
/*     */   public static void recipeStorm()
/*     */   {
/* 109 */     redoVanillaRecipes();
/*     */   }
/*     */ 
/*     */   public static void magicSlabFrenzy()
/*     */   {
/* 337 */     for (int iter = 0; iter < magicSlabStoneArray.length; iter++)
/*     */     {
/* 339 */       GameRegistry.addRecipe(new ItemStack(InfiBlockContent.magicSlabStone, 8, iter), new Object[] { " b ", "b b", " b ", Character.valueOf('b'), magicSlabStoneArray[iter] });
/*     */     }
/*     */ 
/* 345 */     for (int iter = 0; iter < magicSlabSoilArray.length; iter++)
/*     */     {
/* 347 */       GameRegistry.addRecipe(new ItemStack(InfiBlockContent.magicSlabSoil, 8, iter), new Object[] { " b ", "b b", " b ", Character.valueOf('b'), magicSlabSoilArray[iter] });
/*     */     }
/*     */ 
/* 353 */     for (int iter = 0; iter < 16; iter++)
/*     */     {
/* 355 */       GameRegistry.addRecipe(new ItemStack(InfiBlockContent.magicSlabWool, 8, iter), new Object[] { " b ", "b b", " b ", Character.valueOf('b'), new ItemStack(Block.cloth, 1, iter) });
/*     */     }
/*     */ 
/* 412 */     for (int iter = 0; iter < magicSlabStoneArray.length; iter++)
/*     */     {
/* 414 */       GameRegistry.addRecipe(magicSlabStoneArray[iter], new Object[] { "c", "c", Character.valueOf('c'), new ItemStack(InfiBlockContent.magicSlabStone, 1, iter) });
/*     */     }
/*     */ 
/* 420 */     for (int iter = 0; iter < magicSlabSoilArray.length; iter++)
/*     */     {
/* 422 */       GameRegistry.addRecipe(magicSlabSoilArray[iter], new Object[] { "c", "c", Character.valueOf('c'), new ItemStack(InfiBlockContent.magicSlabSoil, 1, iter) });
/*     */     }
/*     */ 
/* 428 */     for (int iter = 0; iter < 16; iter++)
/*     */     {
/* 430 */       GameRegistry.addRecipe(new ItemStack(Block.cloth, 1, iter), new Object[] { "c", "c", Character.valueOf('c'), new ItemStack(InfiBlockContent.magicSlabWool, 1, iter) });
/*     */     }
/*     */ 
/* 487 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 489 */       GameRegistry.addRecipe(new ItemStack(InfiBlockContent.magicSlabWool, 8, i), new Object[] { "bbb", "bxb", "bbb", Character.valueOf('b'), new ItemStack(InfiBlockContent.magicSlabWool, 1, -1), Character.valueOf('x'), new ItemStack(Item.dyePowder, 1, 15 - i) });
/*     */     }
/*     */ 
/* 496 */     for (int i = 0; i <= 15; i++)
/*     */     {
/* 498 */       GameRegistry.addShapelessRecipe(new ItemStack(InfiBlockContent.magicSlabWool, 1, i), new Object[] { new ItemStack(InfiBlockContent.magicSlabWool, 1, -1), new ItemStack(Item.dyePowder, 1, 15 - i) });
/*     */     }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static void redoVanillaRecipes()
/*     */   {
/* 536 */     removeRecipe(new ItemStack(Block.stairsBrick, 4));
/* 538 */     removeRecipe(new ItemStack(Block.stairsNetherBrick, 4));
/*     */ 
/* 540 */     removeRecipe(new ItemStack(Block.sandStone, 4, 2));
/* 541 */     GameRegistry.addRecipe(new ItemStack(Block.sandStone, 4, 2), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Block.sandStone, 4, 0) });
/*     */    
/* 577 */     GameRegistry.addRecipe(new ItemStack(Block.stairsBrick, 6), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.brick });
/*     */ 
/* 585 */     GameRegistry.addRecipe(new ItemStack(Block.stairsNetherBrick, 6), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.netherBrick });
/*     */   }
/*     */ 
/*     */   public static void furnaceBlaze()
/*     */   {
/* 594 */     
/*     */   }
/*     */ 
/*     */   public static void initDetails()
/*     */   {
/*     */   }
/*     */ 
/*     */   private static void removeRecipe(ItemStack resultItem)
/*     */   {
/* 664 */     List recipes = CraftingManager.getInstance().getRecipeList();
/* 665 */     for (int i = 0; i < recipes.size(); i++)
/*     */     {
/* 667 */       IRecipe tmpRecipe = (IRecipe)recipes.get(i);
/* 668 */       if ((tmpRecipe instanceof ShapedRecipes)) {
/* 669 */         ShapedRecipes recipe = (ShapedRecipes)tmpRecipe;
/* 670 */         ItemStack recipeResult = recipe.getRecipeOutput();
/*     */ 
/* 672 */         if (ItemStack.areItemStacksEqual(resultItem, recipeResult))
/* 673 */           recipes.remove(i--);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.InfiBlockRecipes
 * JD-Core Version:    0.6.2
 */