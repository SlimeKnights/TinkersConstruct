/*    */ package net.bjculk.magic;
/*    */ 
/*    */ /*    */ import java.util.ArrayList;

/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.BlockHalfSlab;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
/*    */ 
/*    */ public class MagicSlabStone extends MagicSlabBase
/*    */ {
/*    */   public MagicSlabStone(int i)
/*    */   {
/* 15 */     super(i, Material.rock);
/*    */   }
/*    */ 
/*    */   public float getHardness(int md) {
/* 19 */     switch (md) { case 0:
/* 20 */       return Block.stone.getBlockHardness(null, 0, 0, 0);
/*    */     case 1:
/* 21 */       return Block.stoneSingleSlab.getBlockHardness(null, 0, 0, 0);
/*    */     case 2:
/* 22 */       return Block.cobblestone.getBlockHardness(null, 0, 0, 0);
/*    */     case 3:
/* 23 */       return Block.stoneBrick.getBlockHardness(null, 0, 0, 0);
/*    */     case 4:
/* 24 */       return Block.stoneBrick.getBlockHardness(null, 0, 0, 0);
/*    */     case 5:
/* 25 */       return Block.stoneBrick.getBlockHardness(null, 0, 0, 0);
/*    */     case 6:
/* 26 */       return Block.brick.getBlockHardness(null, 0, 0, 0);
/*    */     case 7:
/* 27 */       return Block.cobblestoneMossy.getBlockHardness(null, 0, 0, 0);
/*    */     case 8:
/* 28 */       return Block.obsidian.getBlockHardness(null, 0, 0, 0);
/*    */     case 9:
/* 29 */       return Block.netherrack.getBlockHardness(null, 0, 0, 0);
/*    */     case 10:
/* 30 */       return Block.sandStone.getBlockHardness(null, 0, 0, 0);
/*    */     case 11:
/* 31 */       return Block.blockIron.getBlockHardness(null, 0, 0, 0);
/*    */     case 12:
/* 32 */       return Block.blockGold.getBlockHardness(null, 0, 0, 0);
/*    */     case 13:
/* 33 */       return Block.blockDiamond.getBlockHardness(null, 0, 0, 0);
/*    */     case 14:
/* 34 */       return Block.whiteStone.getBlockHardness(null, 0, 0, 0);
/*    */     case 15:
/* 35 */       return Block.netherBrick.getBlockHardness(null, 0, 0, 0); }
/* 36 */     return 0.0F;
/*    */   }
/*    */ 
/*    */   public Icon getIcon(int side, int md)
/*    */   {
/* 42 */     switch (md) {
/*    */     case 0:
/* 44 */       return Block.stone.getIcon(side, 0);
/*    */     case 1:
/* 45 */       return Block.stoneSingleSlab.getIcon(side, 0);
/*    */     case 2:
/* 46 */       return Block.cobblestone.getIcon(side, 0);
/*    */     case 3:
/* 47 */       return Block.stoneBrick.getIcon(side, 0);
/*    */     case 4:
/* 48 */       return Block.stoneBrick.getIcon(side, 1);
/*    */     case 5:
/* 49 */       return Block.stoneBrick.getIcon(side, 2);
/*    */     case 6:
/* 50 */       return Block.brick.getIcon(side, 0);
/*    */     case 7:
/* 51 */       return Block.cobblestoneMossy.getIcon(side, 0);
/*    */     case 8:
/* 52 */       return Block.obsidian.getIcon(side, 0);
/*    */     case 9:
/* 53 */       return Block.netherrack.getIcon(side, 0);
/*    */     case 10:
/* 54 */       return Block.sandStone.getIcon(side, 0);
/*    */     case 11:
/* 55 */       return Block.blockIron.getIcon(side, 0);
/*    */     case 12:
/* 56 */       return Block.blockGold.getIcon(side, 0);
/*    */     case 13:
/* 57 */       return Block.blockDiamond.getIcon(side, 0);
/*    */     case 14:
/* 58 */       return Block.whiteStone.getIcon(side, 0);
/*    */     case 15:
/* 59 */       return Block.netherBrick.getIcon(side, 0);
/* 60 */     }
return Block.bedrock.getIcon(side, md);
/*    */   }
/*    */ 
/*    */   public void addCreativeItems(ArrayList arraylist)
/*    */   {
/* 66 */     for (int iter = 0; iter < 16; iter++)
/*    */     {
/* 68 */       InfiBlocks.getContentInstance(); arraylist.add(new ItemStack(InfiBlockContent.magicSlabStone, 1, 0));
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabStone
 * JD-Core Version:    0.6.2
 */