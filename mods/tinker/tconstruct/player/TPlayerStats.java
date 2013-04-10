package mods.tinker.tconstruct.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;

public class TPlayerStats
{
	public WeakReference<EntityPlayer> player;
	public int level;
	public int health;
	public int hunger;
	public boolean beginnerManual;
	public boolean materialManual;
	public boolean smelteryManual;
	
	//public boolean prevOnGround;
	//public double prevMotionY;
}
