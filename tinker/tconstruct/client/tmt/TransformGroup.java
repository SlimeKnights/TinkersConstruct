package tinker.tconstruct.client.tmt;

import net.minecraft.util.Vec3;

public abstract class TransformGroup
{
	public abstract double getWeight();
	public abstract Vec3 doTransformation(PositionTransformVertex vertex);
}
