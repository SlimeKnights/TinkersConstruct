package mods.tinker.tconstruct.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class Skyla extends EntityPlayer
{

	public Skyla(World par1World)
	{
		super(par1World);
		username = "Skyla";
		texture = "/tinkertextures/mob/skyla.png";
	}

	@Override
	public void sendChatToPlayer (String var1) {}

	@Override
	public boolean canCommandSenderUseCommand (int var1, String var2)
	{
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates ()
	{
		return null;
	}
    
}
