/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import net.minecraft.item.ItemBlock;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class MagicSlabSoilItem extends ItemBlock
/*    */ {
/*  8 */   public static final String[] blockType = { "dirt", "grass", "mycelium", "sand", "gravel", "oak", "pine", "birch", "planks", "snow", "soulsand", "brownMushroom", "redMushroom", "glowstone", "glass", "oakLeaves" };
/*    */ 
/*    */   public MagicSlabSoilItem(int i)
/*    */   {
/* 16 */     super(i);
/* 17 */     setMaxDamage(0);
/* 18 */     setHasSubtypes(true);
/*    */   }
/*    */ 
/*    */   public int getMetadata(int md)
/*    */   {
/* 24 */     return md;
/*    */   }
/*    */ 
/*    */   public String getItemNameIS(ItemStack itemstack)
/*    */   {
/* 29 */     return blockType[itemstack.getItemDamage()] + "MagicSlab";
/*    */   }
/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabSoilItem
 * JD-Core Version:    0.6.2
 */