/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import cpw.mods.fml.common.FMLCommonHandler;
/*    */ import cpw.mods.fml.common.network.IGuiHandler;

/*    */ import java.io.File;

/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.network.packet.Packet;
/*    */ import net.minecraft.server.MinecraftServer;
/*    */ import net.minecraft.server.management.ServerConfigurationManager;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class InfiBlocksCommonProxy
/*    */   implements IGuiHandler
/*    */ {
/* 55 */   public static int craftingGuiID = 1;
/* 56 */   public static int furnaceGuiID = 2;
/*    */ 
/*    */   public void registerRenderer()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void addNames()
/*    */   {
/*    */   }
/*    */ 
/*    */   public File getMinecraftDir()
/*    */   {
/* 29 */     return new File(".");
/*    */   }
/*    */ 
/*    */   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
/*    */   {
/* 52 */     return null;
/*    */   }
/*    */ 
/*    */   public void sendCustomPacket(Packet packet)
/*    */   {
/* 64 */     FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendPacketToAllPlayers(packet);
/*    */   }
/*    */ 
/*    */   public void sendCustomPacketToPlayersInRange(double X, double Y, double Z, Packet packet, double range)
/*    */   {
/* 69 */     FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(X, Y, Z, range, 0, packet);
/*    */   }
/*    */
@Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world,
		int x, int y, int z) {
	// TODO Auto-generated method stub
	return null;
} }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.InfiBlocksCommonProxy
 * JD-Core Version:    0.6.2
 */