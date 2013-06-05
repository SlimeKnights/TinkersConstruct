package mods.tinker.tconstruct.skill;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class Jump extends Skill
{
	@Override
	public String getTextureFile (int guiscale)
	{
		/*if (guiscale == 2)
			return "/mods/tinker/textures/skill/Jump32x.png";
		if (guiscale == 3)
			return "/mods/tinker/textures/skill/Jump48x.png";
		
		return "/mods/tinker/textures/skill/Jump16x.png";*/
		return "/mods/tinker/textures/skill/Jump48x.png";
	}

	@Override
	public String getSkillName ()
	{
		return "Jump";
	}

	@Override
	public void activate (Entity entity, World world)
	{
		if (entity.onGround)
			entity.motionY = 0.8f;
	}

}
