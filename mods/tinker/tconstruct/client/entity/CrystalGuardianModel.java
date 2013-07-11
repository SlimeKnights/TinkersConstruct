package mods.tinker.tconstruct.client.entity;

import mods.tinker.tconstruct.entity.Automaton;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class CrystalGuardianModel extends ModelBase
{
    public ModelRenderer chest;
    public ModelRenderer leftarm;
    public ModelRenderer rightarm;
    public ModelRenderer leftleg;
    public ModelRenderer rightleg;
    public ModelRenderer neckplate;
    public ModelRenderer head;
    public ModelRenderer crystal;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeright;

    public CrystalGuardianModel()
    {
        textureWidth = 64;
        textureHeight = 64;

        chest = new ModelRenderer(this, 0, 0);
        chest.addBox(-8F, -8F, -5F, 16, 16, 10);
        chest.setRotationPoint(0F, 0F, 0F);
        setRotation(chest, 0F, 0F, 0F);
        leftarm = new ModelRenderer(this, 0, 26);
        leftarm.mirror = true;
        leftarm.addBox(0F, -2F, -2F, 4, 22, 4);
        leftarm.setRotationPoint(8F, -5F, 0F);
        setRotation(leftarm, 0F, 0F, 0F);
        rightarm = new ModelRenderer(this, 0, 26);
        rightarm.mirror = false;
        rightarm.addBox(-4F, -2F, -2F, 4, 22, 4);
        rightarm.setRotationPoint(-8F, -5F, 0F);
        setRotation(rightarm, 0F, 0F, 0F);
        leftleg = new ModelRenderer(this, 16, 26);
        leftleg.mirror = true;
        leftleg.addBox(-2F, 0F, -3F, 4, 16, 6);
        leftleg.setRotationPoint(4F, 8F, 0F);
        setRotation(leftleg, 0F, 0F, 0F);
        rightleg = new ModelRenderer(this, 16, 26);
        rightleg.mirror = false;
        rightleg.addBox(-2F, 0F, -3F, 4, 16, 6);
        rightleg.setRotationPoint(-4F, 8F, 0F);
        setRotation(rightleg, 0F, 0F, 0F);
        neckplate = new ModelRenderer(this, 8, 48);
        neckplate.addBox(-4F, -1F, -4F, 8, 1, 8);
        neckplate.setRotationPoint(0F, -8F, 0F);
        setRotation(neckplate, 0F, 0F, 0F);
        head = new ModelRenderer(this, 36, 26);
        head.addBox(-3F, -6F, -3F, 6, 6, 6);
        head.setRotationPoint(0F, -9F, 0F);
        setRotation(head, 0F, 0F, 0F);
        crystal = new ModelRenderer(this, 36, 38);
        crystal.addBox(-1F, -8F, -1F, 2, 2, 2);
        crystal.setRotationPoint(0F, -9F, 0F);
        setRotation(crystal, 0F, 0F, 0F);
        eyeleft = new ModelRenderer(this, 36, 42);
        eyeleft.addBox(1F, -4F, -4F, 2, 2, 1);
        eyeleft.setRotationPoint(0F, -9F, 0F);
        setRotation(eyeleft, 0F, 0F, 0F);
        eyeright = new ModelRenderer(this, 36, 42);
        eyeright.addBox(-3F, -4F, -4F, 2, 2, 1);
        eyeright.setRotationPoint(0F, -9F, 0F);
        setRotation(eyeright, 0F, 0F, 0F);
    }

    public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        chest.render(f5);
        leftarm.render(f5);
        rightarm.render(f5);
        leftleg.render(f5);
        rightleg.render(f5);
        neckplate.render(f5);
        head.render(f5);
        crystal.render(f5);
        eyeleft.render(f5);
        eyeright.render(f5);
    }

    private void setRotation (ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles (float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        //super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.rightleg.rotateAngleX = -1.5F * this.func_78172_a(par1, 13.0F) * par2;
        this.leftleg.rotateAngleX = 1.5F * this.func_78172_a(par1, 13.0F) * par2;
    }

    @Override
    public void setLivingAnimations (EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        Automaton entityirongolem = (Automaton) par1EntityLiving;
        int i = 0;//entityirongolem.getAttackTimer();

        this.rightarm.rotateAngleX = (-0.2F + 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
        this.leftarm.rotateAngleX = (-0.2F - 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
        /*this.rightarm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);
        this.leftarm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);*/
    }

    private float func_78172_a (float par1, float par2)
    {
        return (Math.abs(par1 % par2 - par2 * 0.5F) - par2 * 0.25F) / (par2 * 0.25F);
    }
}
