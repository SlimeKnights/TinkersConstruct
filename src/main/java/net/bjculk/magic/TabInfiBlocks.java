/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.creativetab.CreativeTabs;
/*    */ 
/*    */ public class TabInfiBlocks extends CreativeTabs
/*    */ {
/*    */   public TabInfiBlocks(String label)
/*    */   {
/* 11 */     super(label);
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public int getTabIconItemIndex()
/*    */   {
/* 18 */     InfiBlocks.getContentInstance(); return Block.anvil.blockID;
/*    */   }
/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.TabInfiBlocks
 * JD-Core Version:    0.6.2
 */