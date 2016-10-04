/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
/*    */ import cpw.mods.fml.common.Mod.Init;
/*    */ import cpw.mods.fml.common.Mod.Instance;
/*    */ import cpw.mods.fml.common.Mod.PostInit;
/*    */ import cpw.mods.fml.common.Mod.PreInit;
/*    */ import cpw.mods.fml.common.SidedProxy;
/*    */ import cpw.mods.fml.common.event.FMLInitializationEvent;
/*    */ import cpw.mods.fml.common.event.FMLPostInitializationEvent;
/*    */ import cpw.mods.fml.common.event.FMLPreInitializationEvent;
/*    */ import cpw.mods.fml.common.network.NetworkMod;
/*    */ import cpw.mods.fml.common.network.NetworkRegistry;
/*    */ import net.minecraft.creativetab.CreativeTabs;
/*    */ 
/*    */ @Mod(modid="InfiBlocks", name="InfiBlocks", version="1.4.6_2013.1.5")
/*    */ @NetworkMod(serverSideRequired=false, clientSideRequired=true)
/*    */ public class InfiBlocks
/*    */ {
/*    */ 
/*    */   @SidedProxy(clientSide="inficraft.infiblocks.client.InfiBlocksClientProxy", serverSide="inficraft.infiblocks.InfiBlocksCommonProxy")
/*    */   public static InfiBlocksCommonProxy proxy;
/*    */ 
/*    */   @Mod.Instance("InfiBlocks")
/*    */   public static InfiBlocks instance;
/* 32 */   public static CreativeTabs infiBlockTab = new TabInfiBlocks("InfiBlocks");
/*    */   private static InfiBlockContent contentInstance;
/* 69 */   public static String bricksImage = "/infitextures/bricks.png";
/* 70 */   public static String blocksImage = "/infitextures/infiblocks.png";
/* 71 */   public static String techImage = "/infitextures/tech.png";
/*    */   public static String modid = "InfiBlocks";
/*    */   public static InfiBlockContent getContentInstance()
/*    */   {
/* 38 */     return contentInstance;
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void preInit(FMLPreInitializationEvent evt)
/*    */   {
/* 44 */     PHInfiBlocks.initProps();
/* 45 */     contentInstance = new InfiBlockContent();
/* 46 */     contentInstance.init();
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void load(FMLInitializationEvent evt)
/*    */   {
/* 52 */     proxy.registerRenderer();
/* 53 */     proxy.addNames();
/* 54 */     InfiBlockRecipes.recipeStorm();
/* 55 */     InfiBlockRecipes.magicSlabFrenzy();
/* 56 */     InfiBlockRecipes.furnaceBlaze();
/* 57 */     InfiBlockRecipes.initDetails();
/*    */ 
/* 59 */     NetworkRegistry.instance().registerGuiHandler(instance, proxy);
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void postInit(FMLPostInitializationEvent evt)
/*    */   {
/* 65 */     contentInstance.resolveModConflicts();
/*    */   }
/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.InfiBlocks
 * JD-Core Version:    0.6.2
 */