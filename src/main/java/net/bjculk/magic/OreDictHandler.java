/*     */ package net.bjculk.magic;
/*     */ 
/*     */ import cpw.mods.fml.common.registry.GameRegistry;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ 
/*     */ public class OreDictHandler
/*     */ {
/*     */   public void oreDictionarySupport()
/*     */   {
/*  17 */     String ore = "derp";
/*  18 */     ItemStack stack = new ItemStack(Block.dirt);
/*  19 */     if (ore.equals("customCobblestone"))
/*     */     {
/*  21 */       InfiBlocks.getContentInstance(); GameRegistry.addRecipe(new ItemStack(InfiBlockContent.workbench, 1), new Object[] { "##", "##", Character.valueOf('#'), stack });
/*     */     }
/*     */ 
/*  24 */     if (ore.equals("stoneRod"))
/*     */     {
/*  26 */       addStickRecipe(stack);
/*     */     }
/*  28 */     else if (ore.equals("ironRod"))
/*     */     {
/*  30 */       addStickRecipe(stack);
/*     */     }
/*  32 */     else if (ore.equals("diamondRod"))
/*     */     {
/*  34 */       addStickRecipe(stack);
/*     */     }
/*  36 */     else if (ore.equals("redstoneRod"))
/*     */     {
/*  38 */       addStickRecipe(stack);
/*     */     }
/*  40 */     else if (ore.equals("obsidianRod"))
/*     */     {
/*  42 */       addStickRecipe(stack);
/*     */     }
/*  44 */     else if (ore.equals("sandstoneRod"))
/*     */     {
/*  46 */       addStickRecipe(stack);
/*     */     }
/*  48 */     else if (ore.equals("paperRod"))
/*     */     {
/*  50 */       addStickRecipe(stack);
/*     */     }
/*  52 */     else if (ore.equals("mossyRod"))
/*     */     {
/*  54 */       addStickRecipe(stack);
/*     */     }
/*  56 */     else if (ore.equals("netherrackRod"))
/*     */     {
/*  58 */       addStickRecipe(stack);
/*     */     }
/*  60 */     else if (ore.equals("glowstoneRod"))
/*     */     {
/*  62 */       addStickRecipe(stack);
/*     */     }
/*  64 */     else if (ore.equals("lavaRod"))
/*     */     {
/*  66 */       addStickRecipe(stack);
/*     */     }
/*  68 */     else if (ore.equals("iceRod"))
/*     */     {
/*  70 */       addStickRecipe(stack);
/*     */     }
/*  72 */     else if (ore.equals("slimeRod"))
/*     */     {
/*  74 */       addStickRecipe(stack);
/*     */     }
/*  76 */     else if (ore.equals("cactusRod"))
/*     */     {
/*  78 */       addStickRecipe(stack);
/*     */     }
/*  80 */     else if (ore.equals("flintRod"))
/*     */     {
/*  82 */       addStickRecipe(stack);
/*     */     }
/*  84 */     else if (ore.equals("copperRod"))
/*     */     {
/*  86 */       addStickRecipe(stack);
/*     */     }
/*  88 */     else if (ore.equals("bronzeRod"))
/*     */     {
/*  90 */       addStickRecipe(stack);
/*     */     }
/*  92 */     else if (ore.equals("workedIronRod"))
/*     */     {
/*  94 */       addStickRecipe(stack);
/*     */     }
/*  96 */     else if (ore.equals("steelRod"))
/*     */     {
/*  98 */       addStickRecipe(stack);
/*     */     }
/* 100 */     else if (ore.equals("cobaltRod"))
/*     */     {
/* 102 */       addStickRecipe(stack);
/*     */     }
/* 104 */     else if (ore.equals("arditeRod"))
/*     */     {
/* 106 */       addStickRecipe(stack);
/*     */     }
/* 108 */     else if (ore.equals("manyullynRod"))
/*     */     {
/* 110 */       addStickRecipe(stack);
/*     */     }
/* 112 */     else if (ore.equals("uraniumRod"))
/*     */     {
/* 114 */       addStickRecipe(stack);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addStickRecipe(ItemStack stack)
/*     */   {
/* 122 */     GameRegistry.addRecipe(new ItemStack(Block.torchWood, 4), new Object[] { "c", "s", Character.valueOf('c'), new ItemStack(Item.coal, 1, -1), Character.valueOf('s'), stack });
/*     */ 
/* 126 */     GameRegistry.addRecipe(new ItemStack(Block.torchRedstoneActive, 1), new Object[] { "c", "s", Character.valueOf('c'), new ItemStack(Item.redstone, 1, 0), Character.valueOf('s'), stack });
/*     */ 
/* 130 */     GameRegistry.addRecipe(new ItemStack(Block.lever, 1), new Object[] { "s", "c", Character.valueOf('c'), new ItemStack(Block.cobblestone, 1, 0), Character.valueOf('s'), stack });
/*     */ 
/* 134 */     GameRegistry.addRecipe(new ItemStack(Block.rail, 16), new Object[] { "c c", "csc", "c c", Character.valueOf('c'), new ItemStack(Item.ingotIron, 1, 0), Character.valueOf('s'), stack });
/*     */ 
/* 138 */     GameRegistry.addRecipe(new ItemStack(Block.railPowered, 6), new Object[] { "c c", "csc", "crc", Character.valueOf('c'), new ItemStack(Item.ingotGold, 1, -1), Character.valueOf('s'), stack, Character.valueOf('r'), new ItemStack(Item.redstone, 1, 0) });
/*     */   }
/*     */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.OreDictHandler
 * JD-Core Version:    0.6.2
 */