package tconstruct.preloader;

/*
 * ASMInterfaceRepair is derived from code graciously provided by AlgorithmX2 of Applied Energistics.
 * The code was adapted for this loader by Sunstrike. Any queries should be directed to him, not AlgorithmX2.
 */

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import tconstruct.preloader.helpers.PropertyManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ASMInterfaceRepair implements IClassTransformer  {

    public static Map<String,Boolean> APIInterfaces = new HashMap<String, Boolean>();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if ( name.startsWith( "tconstruct." ) && ! name.startsWith( "tconstruct.preloader" ) )
        {
            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);

            boolean changed = false;

            if ( this.verifyAPI(cn) )
                changed = true;

            if ( changed )
            {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                bytes = cw.toByteArray();
                log( "Altering definition of " + transformedName );
            }
        }

        return bytes;
    }

    private boolean isInternal( String path )
    {
        return path.startsWith( "java/" )
                || path.startsWith( "cpw/mods/fml/" )
                || path.startsWith( "tconstruct/" )
                || path.startsWith( "com/google/" )
                || path.startsWith( "net/minecraftforge/" )
                || path.startsWith( "net/minecraft/" );
    }

    private boolean isBlacklisted( String path )
    {
        return path.startsWith( "tconstruct/plugins/minefactoryreloaded/" );
    }

    private boolean isClassAvailable( String inf )
    {
        if ( APIInterfaces.containsKey( inf ) )
            return APIInterfaces.get( inf );

        boolean isAvailable = false;
        Class obj = null;

        try
        {
            obj = Class.forName( inf.replace( "/", "." )  );
            //obj = ReflectionHelper.getClass( getClass().getClassLoader(), inf.replace( "/", "." ) );
        }
        catch ( Throwable _ ) {}

        // java.io.InputStream is = this.getClass().getResourceAsStream( "/" + inf + ".class" );
        APIInterfaces.put( inf, isAvailable = ( obj != null) );
        return isAvailable;
    }

    class SigChecker extends SignatureVisitor {

        private ASMInterfaceRepair asmTransformer;
        public boolean isAvailable = true;

        public SigChecker(int api, ASMInterfaceRepair t) {
            super(api);
            asmTransformer = t;
        }

        @Override
        public void visitInnerClassType(String name) {
            visitTypeVariable(name);
        }

        @Override
        public void visitClassType(String name) {
            visitTypeVariable(name);
        }

        @Override
        public void visitTypeVariable(String className) {
            if ( isInternal( className ) || isBlacklisted( className ) ) return;
            isAvailable = isAvailable && asmTransformer.isClassAvailable( className );
            log( className + " is " + ( isAvailable ? "available" : "not available" ) );
        }

    }

    private boolean verifyAPI(ClassNode cn)
    {
        boolean changed = false;

        log( "Examining " + cn.name );

        Iterator<MethodNode> mn = cn.methods.iterator();
        while ( mn.hasNext() )
        {
            MethodNode meth = mn.next();

            SigChecker sc = new SigChecker(1,this);
            ( new SignatureReader(meth.desc) ).accept( sc );

            boolean isAvailable = sc.isAvailable;

            if ( ! isAvailable )
            {
                mn.remove();
                changed = true;
            }
        }

        Iterator<String> i = cn.interfaces.iterator();
        while ( i.hasNext() )
        {
            String inf = i.next();

            if ( isInternal( inf ) || isBlacklisted( inf ) )
                continue;

            boolean isAvailable = isClassAvailable( inf );
            log(  inf + " is " + ( isAvailable ? "available" : "not available" ) );

            if ( ! isAvailable )
            {
                i.remove();
                changed = true;
            }
        }

        return changed;
    }

    private void log(String string) {
        if (PropertyManager.asmInterfaceRepair_verboseLog) {
            TConstructLoaderContainer.logger.info( string );
        }
    }

}
