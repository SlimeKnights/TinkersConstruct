package tinker.tconstruct.client.tmt;

import java.util.ArrayList;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class TexturedPolygon extends TexturedQuad
{
	public TexturedPolygon(PositionTextureVertex apositionTexturevertex[])
    {
		super(apositionTexturevertex);
		invertNormal = false;
		normals = new float[0];
		iNormals = new ArrayList<Vec3>();
    }
	
	public void setInvertNormal(boolean isSet)
	{
		invertNormal = isSet;
	}
	
	public void setNormals(float x, float y, float z)
	{
		normals = new float[] {x, y, z};
	}
	
	public void setNormals(ArrayList<Vec3> vec)
	{
		iNormals = vec;
	}
	
	public void draw(Tessellator tessellator, float f)
    {
        
        if(nVertices == 3)
        	tessellator.startDrawing(GL11.GL_TRIANGLES);
        else if (nVertices == 4)
        	tessellator.startDrawingQuads();
        else
        	tessellator.startDrawing(GL11.GL_POLYGON);
        
        if(iNormals.size() == 0)
        {
	        if(normals.length == 3)
	        {
	        	if(invertNormal)
	        	{
	        		tessellator.setNormal(-normals[0], -normals[1], -normals[2]);
	        	} else
	        	{
	        		tessellator.setNormal(normals[0], normals[1], normals[2]);
	        	}
	        } else
	        if(vertexPositions.length >= 3)
	        {
		        Vec3 Vec3 = vertexPositions[1].vector3D.subtract(vertexPositions[0].vector3D);
		        Vec3 Vec31 = vertexPositions[1].vector3D.subtract(vertexPositions[2].vector3D);
		        Vec3 Vec32 = Vec31.crossProduct(Vec3).normalize();
		
		        if(invertNormal)
		        {
		            tessellator.setNormal(-(float)Vec32.xCoord, -(float)Vec32.yCoord, -(float)Vec32.zCoord);
		        } else
		        {
		            tessellator.setNormal((float)Vec32.xCoord, (float)Vec32.yCoord, (float)Vec32.zCoord);
		        }
	        }
	        else
	        {
	        	return;
	        }
        }
        for(int i = 0; i < nVertices; i++)
        {
            PositionTextureVertex positionTexturevertex = vertexPositions[i];
            if(positionTexturevertex instanceof PositionTransformVertex)
            	((PositionTransformVertex)positionTexturevertex).setTransformation();
            if(i < iNormals.size())
            {
            	if(invertNormal)
            	{
            		tessellator.setNormal(-(float)iNormals.get(i).xCoord, -(float)iNormals.get(i).yCoord, -(float)iNormals.get(i).zCoord);
            	}
            	else
            	{
            		tessellator.setNormal((float)iNormals.get(i).xCoord, (float)iNormals.get(i).yCoord, (float)iNormals.get(i).zCoord);
            	}
            }
            tessellator.addVertexWithUV((float)positionTexturevertex.vector3D.xCoord * f, (float)positionTexturevertex.vector3D.yCoord * f, (float)positionTexturevertex.vector3D.zCoord * f, positionTexturevertex.texturePositionX, positionTexturevertex.texturePositionY);
        }

        tessellator.draw();
    }

    private boolean invertNormal;
    private float[] normals;
    private ArrayList<Vec3> iNormals;
}
