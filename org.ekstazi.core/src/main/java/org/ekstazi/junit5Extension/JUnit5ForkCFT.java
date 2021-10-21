package org.ekstazi.junit5Extension;

import org.ekstazi.Config;
import org.ekstazi.Ekstazi;
import org.ekstazi.agent.Instr;
import org.ekstazi.asm.*;
import org.ekstazi.log.Log;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static org.ekstazi.junit5Extension.Junit5Helper.isTestClassTransformNeeded;

public class JUnit5ForkCFT implements ClassFileTransformer {

    public static class Junit5ForkClassVisitor extends ClassVisitor {
        public Junit5ForkClassVisitor (ClassVisitor cv) {
            super(Instr.ASM_API_VERSION, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return new Junit5ForkMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }

        private static class Junit5ForkMethodVisitor extends MethodVisitor {
            public Junit5ForkMethodVisitor (MethodVisitor mv) {
                super(Instr.ASM_API_VERSION, mv);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return null;
            }
        }
    }

    private static Boolean isDepdencyFileExist(String className) {
        String filePath = Config.ROOT_DIR_V + "/" + className.replace("/", ".") + ".clz";
        //Log.d2f("isDepdencyFileExist -> filePath = " + filePath);
        File file = new File(filePath);
        //Log.d2f("isDepdencyFileExist -> exist = " + file.exists());
        return file.exists();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (isTestClassTransformNeeded(className) && !Ekstazi.inst().isClassAffected(className.replace("/", ".")) && isDepdencyFileExist(className)) {
            //Log.d2f("Forked transform = " + className);
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            Junit5ForkClassVisitor visitor = new Junit5ForkClassVisitor(classWriter);
            classReader.accept(visitor, 0);
            //Log.write("/Users/alenwang/Documents/xlab/zookeeper/zookeeper-server/Shuai_debug.class", classWriter.toByteArray());
            return classWriter.toByteArray();
        }
        return null;
    }
}
