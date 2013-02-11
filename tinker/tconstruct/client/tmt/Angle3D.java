package tinker.tconstruct.client.tmt;

/**
 * This class handles angles. Basically all it does it store angles. You can
 * directly alter the angles.
 * @author GaryCXJk
 *
 */
public class Angle3D
{
	/**
	 * The constructor to create a new Angle3D.
	 * @param x the x-rotation
	 * @param y the y-rotation
	 * @param z the z-rotation
	 */
	public Angle3D(float x, float y, float z)
	{
		angleX = x;
		angleY = y;
		angleZ = z;
	}
	
	/**
	 * Adds the given angles to the current angles.
	 * @param x the x-rotation
	 * @param y the y-rotation
	 * @param z the z-rotation
	 */
	public void addAngles(float x, float y, float z)
	{
		angleX+= x;
		angleY+= y;
		angleZ+= z;
	}
	
	/**
	 * Adds the angles of another Angle3D to the current angles.
	 * @param angles the Angle3D
	 */
	public void addAngles(Angle3D angles)
	{
		angleX+= angles.angleX;
		angleY+= angles.angleY;
		angleZ+= angles.angleZ;
	}
	
	/**
	 * Multiplies the angles with the given angles.
	 * @param x the x-rotation
	 * @param y the y-rotation
	 * @param z the z-rotation
	 */
	public void multiplyAngles(float x, float y, float z)
	{
		angleX*= x;
		angleY*= y;
		angleZ*= z;
	}
	
	/**
	 * Multiplies the angles with a given Angle3D.
	 * @param angles the Angle3D
	 */
	public void multiplyAngles(Angle3D angles)
	{
		angleX*= angles.angleX;
		angleY*= angles.angleY;
		angleZ*= angles.angleZ;
	}
	
	/**
	 * Gets the center angle between two angles.
	 * @param angles1 the first Angle3D
	 * @param angles2 the second Angle3D
	 * @return the center Angle3D
	 */
	public static Angle3D getCenter(Angle3D angles1, Angle3D angles2)
	{
		Angle3D angles = new Angle3D(0, 0, 0);
		angles.addAngles(angles1);
		angles.addAngles(angles2);
		angles.multiplyAngles(0.5F, 0.5F, 0.5F);
		return angles;
	}
	
	/**
	 * Copies the current Angle3D over to a new Angle3D instance.
	 * @return a copy of the Angle3D instance
	 */
	public Angle3D copy()
	{
		return new Angle3D(angleX, angleY, angleZ);
	}
	
	public float angleX;
	public float angleY;
	public float angleZ;
}
