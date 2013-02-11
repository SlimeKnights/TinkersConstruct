package tinker.tconstruct.client.tmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * The Bone class makes it possible to create skeletons, which should help you out in
 * animating your mobs a little bit more easy. However, since you won't work with a
 * graphical interface, creating bones will be different from what you are probably
 * used to.
 * <br /><br />
 * First, you will need to instantiate every Bone in the constructor of your model
 * file. The default orientation, when all angles are set to zero, will be in the
 * vector (0, 0, length), meaning it will always point backwards on a regular model.
 * You can also set what its parent node is. If a Bone does not have a parent node,
 * it is assumed it is the root node. Each Bone can only have one parent, but several
 * children. Also, all children will inherit the offset position of the root node.
 * <br /><br />
 * The neutral position basically defines in what direction the Bone normally faces
 * when in rest. This will not affect the rotation of any model currently attached
 * to it or the rotation of the child nodes, but will affect the position of the
 * child nodes when recalculating their positions. The length always defines how far
 * each child Bone will be placed, since child Bones are always placed at the end of
 * their parent Bone.
 * <br /><br />
 * Once you're ready to render, you can call the prepareDraw method. You only need
 * to apply it to one Bone, since it will always search for the root node to execute
 * the code there. It will then automatically rotate every child Bone and places
 * them at the right position. Finally, use the setAnglesToModels method to rotate
 * each model and place them at the correct spot. Note that if you also apply custom
 * rotation for the individual models that you should apply that after you've run
 * setAnglesToModels, since this will override the settings the model originally had.
 * The best way to solve this is to make a separate method to rotate the Bones.
 * <br /><br />
 * The following would be an example of a biped with a skeleton. It takes ModelBiped
 * as an example and extends it with a skeleton. First, we have the part that goes
 * in the constructor.
 * <pre>
 * // First, the origin will be placed. This is where the rest is attached to.
 * skeletonOrigin = new Bone(0, 0, 0, 0);
 * 
 * // Next, the entire skeleton is built up.
 * skeletonHead = new Bone(-3.141593F / 2, 0, 0, 0, skeletonOrigin);
 * skeletonBody = new Bone(3.141593F / 2, 0, 0, 12, skeletonOrigin);
 * skeletonShoulderRight = new Bone(0, -3.141593F / 2, 0, 5, skeletonOrigin);
 * skeletonShoulderLeft = new Bone(0, 3.141593F / 2, 0, 5, skeletonOrigin);
 * skeletonArmRight = new Bone(3.141593F / 2, 0, 0, 12, skeletonShoulderRight);
 * skeletonArmLeft = new Bone(3.141593F / 2, 0, 0, 12, skeletonShoulderLeft);
 * skeletonPelvisRight = new Bone(0, -3.141593F / 2, 0, 2, skeletonBody);
 * skeletonPelvisLeft = new Bone(0, 3.141593F / 2, 0, 2, skeletonBody);
 * skeletonLegRight = new Bone(3.141593F / 2, 0, 0, 12, skeletonPelvisRight);
 * skeletonLegLeft = new Bone(3.141593F / 2, 0, 0, 12, skeletonPelvisLeft);
 * 
 * // Finally, all models will be attached to the skeletons.
 * skeletonHead.addModel(bipedHead);
 * skeletonHead.addModel(bipedHeadwear);
 * skeletonBody.addModel(bipedBody);
 * skeletonArmRight.addModel(bipedRightArm);
 * skeletonArmLeft.addModel(bipedLeftArm);
 * skeletonLegRight.addModel(bipedRightLeg);
 * skeletonLegLeft.addModel(bipedRightLeg);
 * </pre>
 * <br /><br />
 * After that, you could replace anything in the setRotationAngles method with
 * the following code. It's not a complete code, but you'll get the basics.
 * <br /><br />
 * <pre>
 * skeletonHead.relativeAngles.angleY = f3 / 57.29578F;
 * skeletonHead.relativeAngles.angleX = f4 / 57.29578F;
 * skeletonArmRight.relativeAngles.angleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 2.0F * f1 * 0.5F;
 * skeletonArmRight.relativeAngles.angleZ = 0.0F;
 * skeletonArmLeft.relativeAngles.angleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
 * skeletonArmLeft.relativeAngles.angleZ = 0.0F;
 * skeletonLegRight.relativeAngles.angleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
 * skeletonLegRight.relativeAngles.angleY = 0.0F;
 * skeletonLegLeft.relativeAngles.angleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
 * skeletonLegLeft.relativeAngles.angleY = 0.0F;
 * </pre>
 * <br /><br />
 * Finally, in the render method, you could use the following code.
 * <br /><br />
 * <pre>
 * setRotationAngles(f, f1, f2, f3, f4, f5);
 * skeletonOrigin.prepareDraw();
 * skeletonOrigin.setAnglesToModels();
 * </pre>
 * <br /><br />
 * This should generate the same animation of the regular biped. Don't forget to add
 * the individual render methods for each model though, as it won't automatically
 * render them.
 * <br /><br />
 * @author GaryCXJk
 *
 */
public class Bone
{
	/**
	 * Constructor to create a bone.
	 * @param x the x-rotation of the bone
	 * @param y the y-rotation of the bone
	 * @param z the z-rotation of the bone
	 * @param l the length of the bone
	 */
	public Bone(float x, float y, float z, float l)
	{
		neutralAngles = new Angle3D(x, y, z);
		relativeAngles = new Angle3D(0, 0, 0);
		absoluteAngles = new Angle3D(0, 0, 0);
		positionVector = Vec3.createVectorHelper(0, 0, 0);
		length = l;
		childNodes = new ArrayList<Bone>();
		models = new ArrayList<ModelRenderer>();
		modelBaseRot = new HashMap<ModelRenderer, Angle3D>();
		parentNode = null;
		offsetX = 0;
		offsetY = 0;
		offsetZ = 0;
		positionVector = Vec3.createVectorHelper(0, 0, 0);
	}
	
	/**
	 * Constructor to create a bone.
	 * @param xOrig the x-offset of the origin
	 * @param yOrig the y-offset of the origin
	 * @param zOrig the z-offset of the origin
	 * @param xRot the x-rotation of the bone
	 * @param yRot the y-rotation of the bone
	 * @param zRot the z-rotation of the bone
	 * @param l the length of the bone
	 */
	public Bone(float xOrig, float yOrig, float zOrig, float xRot, float yRot, float zRot, float l)
	{
		this(xRot, yRot, zRot, l);
		positionVector = setOffset(xOrig, yOrig, zOrig);
	}
	
	/**
	 * Constructor to create a bone. This attaches the bone to a parent bone, and will
	 * calculate its current position relative to the origin.
	 * @param x the x-rotation of the bone
	 * @param y the y-rotation of the bone
	 * @param z the z-rotation of the bone
	 * @param l the length of the bone
	 * @param parent the parent Bone node this Bone is attached to
	 */
	public Bone(float x, float y, float z, float l, Bone parent)
	{
		this(x, y, z, l);
		attachBone(parent);
	}
	
	/**
	 * Detaches the bone from its parent.
	 */
	public void detachBone()
	{
		parentNode.childNodes.remove(this);
		parentNode = null;
	}
	
	/**
	 * Attaches the bone to a parent. If the parent is already set, detaches the bone
	 * from the previous parent.
	 * @param parent the parent Bone node this Bone is attached to
	 */
	public void attachBone(Bone parent)
	{
		if(parentNode != null)
			detachBone();
		parentNode = parent;
		parent.addChildBone(this);
		offsetX = parent.offsetX;
		offsetY = parent.offsetY;
		offsetZ = parent.offsetZ;
		resetOffset();
	}
	
	/**
	 * Sets the current offset of the parent root Bone. Note that this will
	 * always set the parent root Bone, not the current Bone, as its offset
	 * is determined by the offset, rotation and length of its parent.
	 * @param x the x-position
	 * @param y the y-position
	 * @param z the z-position
	 * @return a Vec3 with the new coordinates of the current bone
	 */
	public Vec3 setOffset(float x, float y, float z)
	{
		if(parentNode != null)
		{
			Vec3 vector = parentNode.setOffset(x, y, z);
			offsetX = (float)vector.xCoord;
			offsetY = (float)vector.yCoord;
			offsetZ = (float)vector.zCoord;
			return vector;
		}
		offsetX = x;
		offsetY = y;
		offsetZ = z;
		resetOffset(true);
		return Vec3.createVectorHelper(x, y, z);
	}
	
	/**
	 * Resets the offset.
	 */
	public void resetOffset()
	{
		resetOffset(false);
	}
	
	/**
	 * Resets the offset.
	 * @param doRecursive
	 */
	public void resetOffset(boolean doRecursive)
	{
		if(parentNode != null)
		{
			positionVector = Vec3.createVectorHelper(0, 0, parentNode.length);
			parentNode.setVectorRotations(positionVector);
			positionVector.xCoord += parentNode.positionVector.xCoord;
			positionVector.yCoord += parentNode.positionVector.yCoord;
			positionVector.zCoord += parentNode.positionVector.zCoord;			
		}
		if(doRecursive && !childNodes.isEmpty())
		{
			for(int index = 0; index < childNodes.size(); index++)
			{
				childNodes.get(index).resetOffset(doRecursive);
			}
		}
	}
	
	/**
	 * Sets the current neutral rotation of the bone. This is the same rotation as in
	 * the constructor.
	 * @param x the x-rotation of the bone
	 * @param y the y-rotation of the bone
	 * @param z the z-rotation of the bone
	 */
	public void setNeutralRotation(float x, float y, float z)
	{
		neutralAngles.angleX = x;
		neutralAngles.angleY = y;
		neutralAngles.angleZ = z;
	}
	
	/**
	 * Gets the root parent bone.
	 * @return the root parent Bone.
	 */
	public Bone getRootParent()
	{
		if(parentNode == null)
			return this;
		else
			return parentNode.getRootParent();
	}
	
	/**
	 * Attaches a model to the bone. Its base rotation will be set to the neutral
	 * rotation of the model.
	 * @param model the model to attach
	 */
	public void addModel(ModelRenderer model)
	{
		addModel(model, false);
	}
	
	/**
	 * Attaches a model to the bone. If inherit is true, it sets the base rotation
	 * to the neutral rotation of the Bone, otherwise it's set to the neutral
	 * rotation of the model.
	 * @param model the model to attach
	 * @param inherit whether the model should inherit the Bone's base rotations
	 */
	public void addModel(ModelRenderer model, boolean inherit)
	{
		addModel(model, 0F, 0F, 0F, inherit);
	}
	
	/**
	 * Attaches a model to the bone. If inherit is true, it sets the base rotation
	 * to the neutral rotation of the Bone, otherwise it's set to the neutral
	 * rotation of the model. When isUpright is set, the model will be rotated
	 * (-PI / 2, 0, 0).
	 * @param model the model to attach
	 * @param inherit whether the model should inherit the Bone's base rotations
	 * @param isUpright whether the model is modeled in the upright position
	 */	
	public void addModel(ModelRenderer model, boolean inherit, boolean isUpright)
	{
		addModel(model, 0F, 0F, 0F, inherit, isUpright);
	}
	
	/**
	 * Attaches a model to the bone with a given base rotation.
	 * @param model the model to attach
	 * @param x the base x-rotation
	 * @param y the base y-rotation
	 * @param z the base z-rotation
	 */
	public void addModel(ModelRenderer model, float x, float y, float z)
	{
		addModel(model, x, y, z, false);
	}
	
	/**
	 * Attaches a model to the bone with a given base rotation. When inherit is
	 * true, it will add the Bone's neutral rotation to the given angles.
	 * @param model the model to attach
	 * @param x the base x-rotation
	 * @param y the base y-rotation
	 * @param z the base z-rotation
	 * @param inherit whether the model should inherit the Bone's base rotations
	 */	
	public void addModel(ModelRenderer model, float x, float y, float z, boolean inherit)
	{
		addModel(model, x, y, z, inherit, false);
	}
	
	/**
	 * Attaches a model to the bone with a given base rotation. When inherit is
	 * true, it will add the Bone's neutral rotation to the given angles.
	 * When isUpright is set, the model will be rotated (-PI / 2, 0, 0).
	 * @param model the model to attach
	 * @param x the base x-rotation
	 * @param y the base y-rotation
	 * @param z the base z-rotation
	 * @param inherit whether the model should inherit the Bone's base rotations
	 * @param isUpright whether the model is modeled in the upright position
	 */		
	public void addModel(ModelRenderer model, float x, float y, float z, boolean inherit, boolean isUpright)
	{
		if(inherit)
		{
			x += neutralAngles.angleX + (isUpright ? (float)Math.PI / 2 : 0);
			y += neutralAngles.angleY;
			z += neutralAngles.angleZ;
		}
		models.add(model);
		modelBaseRot.put(model, new Angle3D(x, y, z));
	}
	
	/**
	 * Removes the given model from the Bone. Always detach the model before adding
	 * it to another Bone. The best thing however is to just keep the model to one
	 * bone.
	 * @param model the model to remove from the bone
	 */
	public void removeModel(ModelRenderer model)
	{
		models.remove(model);
		modelBaseRot.remove(model);
	}
	
	/**
	 * Gets the current absolute angles. The absolute angle is calculated by getting
	 * the sum of all parent Bones' relative angles plus the current relative angle.
	 * This must be called after using the prepareDraw method.
	 * @return an Angle3D object which holds the current angles of the current node.
	 */
	public Angle3D getAbsoluteAngle()
	{
		return new Angle3D(absoluteAngles.angleX, absoluteAngles.angleY, absoluteAngles.angleZ);
	}
	
	/**
	 * Gets the current position of the bone. You should call this after all rotations
	 * and positions are applied, e.g. after prepareDraw has been called.
	 * @return a vector containing the current position relative to the origin.
	 */
	public Vec3 getPosition()
	{
		return Vec3.createVectorHelper(positionVector.xCoord, positionVector.yCoord, positionVector.zCoord);
	}
	
	protected void addChildBone(Bone bone)
	{
		childNodes.add(bone);
	}
	
	/**
	 * Prepares the bones for rendering. This will automatically take the root Bone
	 * if it isn't.
	 */
	public void prepareDraw()
	{
		if(parentNode != null)
			parentNode.prepareDraw();
		else
		{
			setAbsoluteRotations();
			setVectors();
		}
	}
	
	/**
	 * Sets the current rotation of the Bone, not calculating any parent bones in.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotations(float x, float y, float z)
	{
		relativeAngles.angleX = x;
		relativeAngles.angleY = y;
		relativeAngles.angleZ = z;
	}
	
	protected void setAbsoluteRotations()
	{
		absoluteAngles.angleX = relativeAngles.angleX;
		absoluteAngles.angleY = relativeAngles.angleY;
		absoluteAngles.angleZ = relativeAngles.angleZ;
		for(int i = 0; i < childNodes.size(); i++)
		{
			childNodes.get(i).setAbsoluteRotations(absoluteAngles.angleX, absoluteAngles.angleY, absoluteAngles.angleZ);
		}
	}
	
	protected void setAbsoluteRotations(float x, float y, float z)
	{
		absoluteAngles.angleX = relativeAngles.angleX + x;
		absoluteAngles.angleY = relativeAngles.angleY + y;
		absoluteAngles.angleZ = relativeAngles.angleZ + z;
		for(int i = 0; i < childNodes.size(); i++)
		{
			childNodes.get(i).setAbsoluteRotations(absoluteAngles.angleX, absoluteAngles.angleY, absoluteAngles.angleZ);
		}
		
	}
	
	protected void setVectorRotations(Vec3 vector)
	{
		float x = neutralAngles.angleX + absoluteAngles.angleX;
		float y = neutralAngles.angleY + absoluteAngles.angleY;
		float z = neutralAngles.angleZ + absoluteAngles.angleZ;
		setVectorRotations(vector, x, y, z);
	}
	
	protected void setVectorRotations(Vec3 vector, float xRot, float yRot, float zRot)
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
		double xy = xC*yVec - xS*zVec;
		double xz = xC*zVec + xS*yVec;
		// rotation around y
		double yz = yC*xz - yS*xVec;
		double yx = yC*xVec + yS*xz;
		// rotation around z
		double zx = zC*yx - zS*xy;
		double zy = zC*xy + zS*yx;
		
		xVec = zx;
		yVec = zy;
		zVec = yz;
		
        vector.xCoord = xVec;
        vector.yCoord = yVec;
        vector.zCoord = zVec;
	}

	protected void addVector(Vec3 destVec, Vec3 srcVec)
	{
		destVec.xCoord += srcVec.xCoord;
		destVec.yCoord += srcVec.yCoord;
		destVec.zCoord += srcVec.zCoord;		
	}

	protected void setVectors()
	{
		Vec3 tempVec = Vec3.createVectorHelper(0, 0, length);
		positionVector = Vec3.createVectorHelper(offsetX, offsetY, offsetZ);
		addVector(tempVec, positionVector);
		setVectorRotations(tempVec);
		for(int i = 0; i < childNodes.size(); i++)
		{
			childNodes.get(i).setVectors(tempVec);
		}
	}
	
	protected void setVectors(Vec3 vector)
	{
		positionVector = vector;
		Vec3 tempVec = Vec3.createVectorHelper(0, 0, length);
		setVectorRotations(tempVec);
		addVector(tempVec, vector);
		for(int i = 0; i < childNodes.size(); i++)
		{
			childNodes.get(i).setVectors(tempVec);
		}
		
	}
	
	/**
	 * Sets the current angles of the Bone to the models attached to it.
	 */
	public void setAnglesToModels()
	{
		for(int i = 0; i < models.size(); i++)
		{
			ModelRenderer currentModel = models.get(i);
			Angle3D baseAngles = modelBaseRot.get(currentModel);
			currentModel.rotateAngleX = baseAngles.angleX + absoluteAngles.angleX;
			currentModel.rotateAngleY = baseAngles.angleY + absoluteAngles.angleY;
			currentModel.rotateAngleZ = baseAngles.angleZ + absoluteAngles.angleZ;
			currentModel.rotationPointX = (float)positionVector.xCoord;
			currentModel.rotationPointY = (float)positionVector.yCoord;
			currentModel.rotationPointZ = (float)positionVector.zCoord;
		}
		
		for(int i = 0; i < childNodes.size(); i++)
		{
			childNodes.get(i).setAnglesToModels();
		}
	}
		
	protected Angle3D neutralAngles;
	public Angle3D relativeAngles;
	protected Angle3D absoluteAngles;
	private Vec3 positionVector;
	private float length;
	private Bone parentNode;
	protected ArrayList<Bone> childNodes;
	private ArrayList<ModelRenderer> models;
	private Map<ModelRenderer, Angle3D> modelBaseRot;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
}
