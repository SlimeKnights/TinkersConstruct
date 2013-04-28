package mods.tinker.tconstruct.library.util;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.ForgeDirection;

public interface IFacingLogic
{
	public byte getRenderDirection();
	public ForgeDirection getForgeDirection();
	public void setDirection(float yaw, float pitch, EntityLiving player);
}