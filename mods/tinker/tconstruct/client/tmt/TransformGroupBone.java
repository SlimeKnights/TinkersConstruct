package mods.tinker.tconstruct.client.tmt;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * The PositionTransformGroup class adds a class which allows for vertex transformations.
 * @author GaryCXJk
 *
 */
public class TransformGroupBone extends TransformGroup
{
    public TransformGroupBone(Bone bone, double wght)
    {
        baseVector = bone.getPosition();
        baseAngles = bone.getAbsoluteAngle();
        attachedBone = bone;
        weight = wght;
    }

    public Angle3D getBaseAngles ()
    {
        return baseAngles.copy();
    }

    public Angle3D getTransformAngle ()
    {
        Angle3D returnAngle = attachedBone.getAbsoluteAngle().copy();
        returnAngle.angleX -= baseAngles.angleX;
        returnAngle.angleY -= baseAngles.angleY;
        returnAngle.angleZ -= baseAngles.angleZ;
        return returnAngle;
    }

    public Vec3 getBaseVector ()
    {
        return Vec3.createVectorHelper(baseVector.xCoord, baseVector.yCoord, baseVector.zCoord);
    }

    public Vec3 getTransformVector ()
    {
        return baseVector.subtract(attachedBone.getPosition());
    }

    public Vec3 getCurrentVector ()
    {
        return attachedBone.getPosition();
    }

    public double getWeight ()
    {
        return weight;
    }

    public void attachBone (Bone bone)
    {
        baseVector = bone.getPosition();
        baseAngles = bone.getAbsoluteAngle();
        attachedBone = bone;
    }

    public Vec3 doTransformation (PositionTransformVertex vertex)
    {
        Vec3 vector = Vec3.createVectorHelper(vertex.neutralVector.xCoord, vertex.neutralVector.yCoord, vertex.neutralVector.zCoord);
        vector = getBaseVector().subtract(vector);
        Angle3D angle = getTransformAngle();
        setVectorRotations(vector, angle.angleX, angle.angleY, angle.angleZ);

        return vector;
    }

    protected void setVectorRotations (Vec3 vector, float xRot, float yRot, float zRot)
    {
        float x = xRot;
        float y = yRot;
        float z = zRot;
        float xC = MathHelper.cos(x);
        float xS = MathHelper.sin(x);
        float yC = MathHelper.cos(y);
        float yS = MathHelper.sin(y);
        float zC = MathHelper.cos(z);
        float zS = MathHelper.sin(z);

        double xVec = vector.xCoord;
        double yVec = vector.yCoord;
        double zVec = vector.zCoord;

        // rotation around x
        double xy = xC * yVec - xS * zVec;
        double xz = xC * zVec + xS * yVec;
        // rotation around y
        double yz = yC * xz - yS * xVec;
        double yx = yC * xVec + yS * xz;
        // rotation around z
        double zx = zC * yx - zS * xy;
        double zy = zC * xy + zS * yx;

        xVec = zx;
        yVec = zy;
        zVec = yz;

        vector.xCoord = xVec;
        vector.yCoord = yVec;
        vector.zCoord = zVec;
    }

    protected Angle3D baseAngles;
    protected Vec3 baseVector;
    protected Bone attachedBone;
    protected double weight;
}
