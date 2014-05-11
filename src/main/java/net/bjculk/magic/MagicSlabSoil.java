/*     */ package net.bjculk.magic;
/*     */ 
/*     */ /*     */ import java.util.ArrayList;

/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockGrass;
/*     */ import net.minecraft.block.BlockLeaves;
/*     */ import net.minecraft.block.BlockMycelium;
/*     */ import net.minecraft.block.StepSound;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
/*     */ import net.minecraft.world.ColorizerFoliage;
/*     */ import net.minecraft.world.ColorizerGrass;
/*     */ import net.minecraft.world.IBlockAccess;
/*     */ import net.minecraft.world.biome.BiomeGenBase;
/*     */ 
/*     */ public class MagicSlabSoil extends MagicSlabBase
/*     */ {
/*     */   public MagicSlabSoil(int i)
/*     */   {
/*  19 */     super(i, Material.ground);
/*     */   }
/*     */ 
/*     */   public int getRenderColor(int md)
/*     */   {
/*  24 */     if (md == 1)
/*  25 */       return getBlockColor();
/*  26 */     if (md == 15) {
/*  27 */       return getLeavesColor();
/*     */     }
/*  29 */     return 16777215;
/*     */   }
/*     */ 
/*     */   public int getBlockColor()
/*     */   {
/*  34 */     double d = 0.5D;
/*  35 */     double d1 = 1.0D;
/*  36 */     return ColorizerGrass.getGrassColor(d, d1);
/*     */   }
/*     */ 
/*     */   public int getLeavesColor()
/*     */   {
/*  41 */     double d = 0.5D;
/*  42 */     double d1 = 1.0D;
/*  43 */     return ColorizerFoliage.getFoliageColor(d, d1);
/*     */   }
/*     */ 
/*     */   public int colorMultiplier(IBlockAccess iblockaccess, int x, int y, int z)
/*     */   {
/*  48 */     int md = iblockaccess.getBlockMetadata(x, y, z);
/*  49 */     if (md == 1)
/*     */     {
/*  51 */       int var5 = 0;
/*  52 */       int var6 = 0;
/*  53 */       int var7 = 0;
/*     */ 
/*  55 */       for (int var8 = -1; var8 <= 1; var8++)
/*     */       {
/*  57 */         for (int var9 = -1; var9 <= 1; var9++)
/*     */         {
/*  59 */           int var10 = iblockaccess.getBiomeGenForCoords(x + var9, z + var8).getBiomeFoliageColor();
/*  60 */           var5 += ((var10 & 0xFF0000) >> 16);
/*  61 */           var6 += ((var10 & 0xFF00) >> 8);
/*  62 */           var7 += (var10 & 0xFF);
/*     */         }
/*     */       }
/*     */ 
/*  66 */       return (var5 / 9 & 0xFF) << 16 | (var6 / 9 & 0xFF) << 8 | var7 / 9 & 0xFF;
/*     */     }
/*     */ 
/*  69 */     if (md == 15)
/*     */     {
/*  71 */       int var5 = iblockaccess.getBlockMetadata(x, y, z);
/*     */ 
/*  73 */       if ((var5 & 0x3) == 1)
/*     */       {
/*  75 */         return ColorizerFoliage.getFoliageColorPine();
/*     */       }
/*  77 */       if ((var5 & 0x3) == 2)
/*     */       {
/*  79 */         return ColorizerFoliage.getFoliageColorBirch();
/*     */       }
/*     */ 
/*  83 */       int var6 = 0;
/*  84 */       int var7 = 0;
/*  85 */       int var8 = 0;
/*     */ 
/*  87 */       for (int var9 = -1; var9 <= 1; var9++)
/*     */       {
/*  89 */         for (int var10 = -1; var10 <= 1; var10++)
/*     */         {
/*  91 */           int var11 = iblockaccess.getBiomeGenForCoords(x + var10, z + var9).getBiomeFoliageColor();
/*  92 */           var6 += ((var11 & 0xFF0000) >> 16);
/*  93 */           var7 += ((var11 & 0xFF00) >> 8);
/*  94 */           var8 += (var11 & 0xFF);
/*     */         }
/*     */       }
/*     */ 
/*  98 */       return (var6 / 9 & 0xFF) << 16 | (var7 / 9 & 0xFF) << 8 | var8 / 9 & 0xFF;
/*     */     }
/*     */ 
/* 102 */     return 16777215;
/*     */   }
/*     */ 
/*     */   public int getLightValue(IBlockAccess iba, int x, int y, int z)
/*     */   {
/* 107 */     int md = iba.getBlockMetadata(x, y, z);
/* 108 */     if (md == 13) {
/* 109 */       return 15;
/*     */     }
/* 111 */     return 0;
/*     */   }
/*     */ 
/*     */   protected Block getStepSound(StepSound stepsound, int md)
/*     */   {
/* 117 */     this.stepSound = stepsound;
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */   public float getHardness(int md) {
/* 122 */     switch (md) { case 0:
/* 123 */       return Block.dirt.getBlockHardness(null, 0, 0, 0);
/*     */     case 1:
/* 124 */       return Block.grass.getBlockHardness(null, 0, 0, 0);
/*     */     case 2:
/* 125 */       return Block.mycelium.getBlockHardness(null, 0, 0, 0);
/*     */     case 3:
/* 126 */       return Block.sand.getBlockHardness(null, 0, 0, 0);
/*     */     case 4:
/* 127 */       return Block.gravel.getBlockHardness(null, 0, 0, 0);
/*     */     case 5:
/* 128 */       return Block.wood.getBlockHardness(null, 0, 0, 0);
/*     */     case 6:
/* 129 */       return Block.wood.getBlockHardness(null, 0, 0, 0);
/*     */     case 7:
/* 130 */       return Block.wood.getBlockHardness(null, 0, 0, 0);
/*     */     case 8:
/* 131 */       return Block.planks.getBlockHardness(null, 0, 0, 0);
/*     */     case 9:
/* 132 */       return Block.snow.getBlockHardness(null, 0, 0, 0);
/*     */     case 10:
/* 133 */       return Block.slowSand.getBlockHardness(null, 0, 0, 0);
/*     */     case 11:
/* 134 */       return Block.mushroomCapBrown.getBlockHardness(null, 0, 0, 0);
/*     */     case 12:
/* 135 */       return Block.mushroomCapRed.getBlockHardness(null, 0, 0, 0);
/*     */     case 13:
/* 136 */       return Block.glowStone.getBlockHardness(null, 0, 0, 0);
/*     */     case 14:
/* 137 */       return Block.glass.getBlockHardness(null, 0, 0, 0);
/*     */     case 15:
/* 138 */       return Block.leaves.getBlockHardness(null, 0, 0, 0); }
/* 139 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   public Icon getIcon(int side, int md)
/*     */   {
/* 145 */     switch (md) {
/*     */     case 0:
/* 147 */       return Block.dirt.getIcon(side, 0);
/*     */     case 1:
/* 148 */       return Block.grass.getIcon(side, 0);
/*     */     case 2:
/* 149 */       return Block.mycelium.getIcon(side, 0);
/*     */     case 3:
/* 150 */       return Block.sand.getIcon(side, 0);
/*     */     case 4:
/* 151 */       return Block.gravel.getIcon(side, 0);
/*     */     case 5:
/* 152 */       return Block.wood.getIcon(side, 0);
/*     */     case 6:
/* 153 */       return Block.wood.getIcon(side, 1);
/*     */     case 7:
/* 154 */       return Block.wood.getIcon(side, 2);
/*     */     case 8:
/* 155 */       return Block.planks.getIcon(side, 0);
/*     */     case 9:
/* 156 */       return Block.snow.getIcon(side, 0);
/*     */     case 10:
/* 157 */       return Block.slowSand.getIcon(side, 0);
/*     */     case 11:
/* 159 */       if (side == 0) {
/* 160 */         return Block.mushroomBrown.getIcon(side, 0);
/*     */       }
/* 162 */       return Block.mushroomCapBrown.getIcon(side, 0);
/*     */     case 12:
/* 166 */       if (side == 0) {
/* 167 */         return Block.mushroomRed.getIcon(side, 0);
/*     */       }
/* 169 */       return Block.mushroomCapRed.getIcon(side, 0);
/*     */     case 13:
/* 172 */       return Block.glowStone.getIcon(side, 0);
/*     */     case 14:
/* 173 */       return Block.glass.getIcon(side, 0);
/*     */     case 15:
/* 174 */       return Block.leaves.getIcon(side, 0);
			  default:
				return Block.bedrock.getIcon(side, 0);
/* 175 */     }
/*     */   }
/*     */ 
/*     */   public void addCreativeItems(ArrayList arraylist)
/*     */   {
/* 204 */     for (int iter = 0; iter < 16; iter++)
/*     */     {
/* 206 */       InfiBlocks.getContentInstance(); arraylist.add(new ItemStack(InfiBlockContent.magicSlabSoil, 1, 0));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabSoil
 * JD-Core Version:    0.6.2
 */