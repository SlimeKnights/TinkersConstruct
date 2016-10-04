/*     */ package net.bjculk.magic;
/*     */ 
/*     */ /*     */ import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
/*     */ public class InfiBlockContent
/*     */ {
/*     */   public static Block workbench;
/*     */   public static Block woolCarpet;
/*     */   public static Block furnace;
/*     */   public static Block chest;
/*     */   public static Block magicSlabStone;
/*     */   public static Block magicSlabSoil;
/*     */   public static Block magicSlabWool;
/*     */   public static Block stainedGlass;
/*     */   public static Block stainedGlassPane;
/*     */   public static Block stainedGlassMagicSlab;
/*     */   public static Block infiGlass;
/*     */   public static Block infiGlassPane;
/*     */   public static Block storageBlock;
/*     */   public static Block brick;
/*     */   public static Block crackedBrick;
/*     */   public static Block fancyBrick;
/*     */   public static Block runeBrick;
/*     */   public static Block brownstone;
/*     */   public static Block iceBrick;
/*     */   public static Block infiGlassMagicSlab;
/*     */   public static Block storageBlockMagicSlab;
/*     */   public static Block brickMagicSlab;
/*     */   public static Block crackedBrickMagicSlab;
/*     */   public static Block fancyBrickMagicSlab;
/*     */   public static Block runeBrickMagicSlab;
/*     */   public static Block brownstoneMagicSlab;
/*     */   public static Block iceBrickMagicSlab;
/*     */   public static Item chiselIron;
/*     */   public static Item chiselDiamond;
/*     */   public static Block stoneStairSlab;
/*     */   public static int chestModelID;
/*     */   public static int magicSlabModel;
/*     */   public static int paneModelID;
/*     */   public static int brickModelID;
/*     */ 
/*     */   public void init()
/*     */   {
/*  39 */     setupBlocks();
/*  40 */     setupItems();
/*     */   }
/*     */ 
/*     */   public void setupBlocks()
/*     */   {

/*  49 */     magicSlabStone = new MagicSlabStone(PHInfiBlocks.magicSlabStoneID).setHardness(0.3F).setUnlocalizedName("Magic Slab Stone");
/*  50 */     magicSlabSoil = new MagicSlabSoil(PHInfiBlocks.magicSlabSoilID).setHardness(0.3F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("Magic Slab Soil");
/*  51 */     magicSlabWool = new MagicSlabCloth(PHInfiBlocks.magicSlabWoolID, Material.cloth).setHardness(0.3F).setStepSound(Block.soundClothFootstep).setUnlocalizedName("Magic Slab Wool");
/*  80 */     GameRegistry.registerBlock(magicSlabStone, MagicSlabStoneItem.class, "magicSlabStone");
/*  81 */     GameRegistry.registerBlock(magicSlabSoil, MagicSlabSoilItem.class, "magicSlabSoil");
/*  82 */     GameRegistry.registerBlock(magicSlabWool, MagicSlabWoolItem.class, "magicSlabCloth");
			  LanguageRegistry.addName(magicSlabSoil, "Soil Magic Slab");
			  LanguageRegistry.addName(magicSlabStone, "Stone Magic Slab");
			  LanguageRegistry.addName(magicSlabWool, "Cloth Magic Slab");
/*     */ 
/*  84 */   }
/*     */ 
/*     */   public void setupItems()
/*     */   {
/* 112 */     //chiselIron = new Chisel(PHInfiBlocks.chiselID, 256).setIconCoord(14, 15).setItemName("chiselIron");
/* 113 */     //chiselDiamond = new Chisel(PHInfiBlocks.chiselID + 1, 2048).setIconCoord(15, 15).setItemName("chiselDiamond");
/*     */   }
/*     */ 
/*     */   public void resolveModConflicts()
/*     */   {
/*     */     
/*     */   }
/*     */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.InfiBlockContent
 * JD-Core Version:    0.6.2
 */