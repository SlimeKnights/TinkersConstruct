/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import net.minecraft.item.ItemBlock;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class MagicSlabStoneItem extends ItemBlock
/*    */ {
/*  8 */   public static final String[] blockType = { "Stone", "Stone Slab", "Cobblestone", "stonebrick", "crackedstonebrick", "mossystonebrick", "brick", "mossy", "obsidian", "netherrack", "sandstone", "iron", "gold", "diamond", "endstone", "netherBrick" };
/*    */ 
/*    */   public MagicSlabStoneItem(int i)
/*    */   {
/* 17 */     super(i);
/* 18 */     setMaxDamage(0);
/* 19 */     setHasSubtypes(true);
/*    */   }
/*    */ 
/*    */   public int getMetadata(int md)
/*    */   {
/* 25 */     return md;
/*    */   }
/*    */ 
/*    */   public String getItemNameDisplayName(ItemStack itemstack)
/*    */   {
/* 30 */     return blockType[itemstack.getItemDamage()] + "Magic Slab";
/*    */   }

/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabStoneItem
 * JD-Core Version:    0.6.2
 */