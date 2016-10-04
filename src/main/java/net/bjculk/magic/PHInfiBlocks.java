/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;

/*    */ import net.minecraftforge.common.Configuration;
/*    */ import net.minecraftforge.common.Property;
/*    */ 
/*    */ public class PHInfiBlocks
/*    */ {
/*    */   public static int blockCraftingID;
/*    */   public static int woolCarpetID;
/*    */   public static int blockChestID;
/*    */   public static int blockFurnaceID;
/*    */   public static int magicSlabStoneID;
/*    */   public static int magicSlabSoilID;
/*    */   public static int magicSlabWoolID;
/*    */   public static int stainedGlassID;
/*    */   public static int stainedGlassPaneID;
/*    */   public static int stainedGlassMagicSlabID;
/*    */   public static int infiGlassID;
/*    */   public static int infiGlassPaneID;
/*    */   public static int storageBlockID;
/*    */   public static int brickID;
/*    */   public static int crackedBrickID;
/*    */   public static int fancyBrickID;
/*    */   public static int runeBrickID;
/*    */   public static int brownstoneID;
/*    */   public static int iceBrickID;
/*    */   public static int infiGlassMagicSlabID;
/*    */   public static int storageBlockMagicSlabID;
/*    */   public static int brickMagicSlabID;
/*    */   public static int crackedBrickMagicSlabID;
/*    */   public static int fancyBrickMagicSlabID;
/*    */   public static int runeBrickMagicSlabID;
/*    */   public static int brownstoneMagicSlabID;
/*    */   public static int iceBrickMagicSlabID;
/*    */   public static int stoneStairSlabID;
/*    */   public static int chiselID;
/*    */   public static int chiselDiamondID;
/*    */ 
/*    */   public static void initProps()
/*    */   {
/* 18 */     File file = new File(InfiBlocks.proxy.getMinecraftDir() + "/config/InfiCraft");
/* 19 */     file.mkdir();
/* 20 */     File newFile = new File(InfiBlocks.proxy.getMinecraftDir() + "/config/InfiCraft/InfiBlocks.txt");
/*    */     try
/*    */     {
/* 25 */       newFile.createNewFile();
/* 26 */       System.out.println("Successfully created/read configuration file");
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 30 */       System.out.println("Could not create configuration file for mod_FloraSoma. Reason:");
/* 31 */       System.out.println(e);
/*    */     }
/*    */ 
/* 35 */     Configuration config = new Configuration(newFile);
/*    */ 
/* 38 */     config.load();
/*    */ 
/* 44 */     blockCraftingID = config.getBlock("Crafting_Table", 3271).getInt(3271);
/* 45 */     blockChestID = config.getBlock("Chest", 3272).getInt(3272);
/* 46 */     blockFurnaceID = config.getBlock("Furnace", 3273).getInt(3273);
/* 47 */     woolCarpetID = config.getBlock("Carpet", 3274).getInt(3274);
/*    */ 
/* 49 */     magicSlabStoneID = config.getBlock("Magic_Slab_Stone", 3275).getInt(3275);
/* 50 */     magicSlabSoilID = config.getBlock("Magic_Slab_Soil", 3276).getInt(3276);
/* 51 */     magicSlabWoolID = config.getBlock("Magic_Slab_Wool", 3277).getInt(3277);
/*    */ 
/* 53 */     stainedGlassID = config.getBlock("Stained_Glass", 3278).getInt(3278);
/* 54 */     stainedGlassPaneID = config.getBlock("Stained_Glass_Pane", 3279).getInt(3279);
/* 55 */     stainedGlassMagicSlabID = config.getBlock("Stained_Glass_Magic_Slab", 3280).getInt(32802);
/*    */ 
/* 57 */     storageBlockID = config.getBlock("Storage_Block", 3281).getInt(3281);
/* 58 */     storageBlockMagicSlabID = config.getBlock("Storage_Block_Magic_Slab", 3282).getInt(3282);
/*    */ 
/* 60 */     brickID = config.getBlock("Brick", 3283).getInt(3283);
/* 61 */     brickMagicSlabID = config.getBlock("Brick_Magic_Slab", 3284).getInt(3284);
/*    */ 
/* 63 */     crackedBrickID = config.getBlock("Cracked_Brick", 3285).getInt(3285);
/* 64 */     crackedBrickMagicSlabID = config.getBlock("Cracked_Brick_Magic_Slab", 3286).getInt(3286);
/*    */ 
/* 66 */     fancyBrickID = config.getBlock("Fancy_Brick", 3287).getInt(3287);
/* 67 */     fancyBrickMagicSlabID = config.getBlock("Fancy_Brick_Magic_Slab", 3288).getInt(3288);
/*    */ 
/* 69 */     infiGlassID = config.getBlock("Glass", 3291).getInt(3291);
/* 70 */     infiGlassPaneID = config.getBlock("Glass_Pane", 3292).getInt(3292);
/* 71 */     infiGlassMagicSlabID = config.getBlock("Glass_Magic_Slab", 3293).getInt(3293);
/*    */ 
/* 73 */     iceBrickID = config.getBlock("Ice_Brick", 3294).getInt(3294);
/* 74 */     iceBrickMagicSlabID = config.getBlock("Ice_Brick_Magic_Slab", 3295).getInt(3295);
/*    */ 
/* 76 */     brownstoneID = config.getBlock("Brownstone", 3296).getInt(3296);
/* 77 */     brownstoneMagicSlabID = config.getBlock("Brownstone_Magic_Slab", 3297).getInt(3297);
/*    */ 
/* 79 */     stoneStairSlabID = config.getBlock("Stone_Stair_Slab_Pillar", 3298).getInt(3298);
/*    */ 
/* 81 */     chiselID = config.getItem("Iron_Chisel", 12101).getInt(12101);
/* 82 */     chiselDiamondID = config.getItem("Diamond_Chisel", 12102).getInt(12102);
/*    */ 
/* 85 */     config.save();
/*    */   }
/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.PHInfiBlocks
 * JD-Core Version:    0.6.2
 */