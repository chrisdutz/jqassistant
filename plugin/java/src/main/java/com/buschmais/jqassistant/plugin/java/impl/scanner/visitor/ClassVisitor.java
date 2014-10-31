package com.buschmais.jqassistant.plugin.java.impl.scanner.visitor;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import com.buschmais.jqassistant.plugin.java.api.model.*;
import com.buschmais.jqassistant.plugin.java.api.scanner.SignatureHelper;

public class ClassVisitor extends org.objectweb.asm.ClassVisitor {

    private VisitorHelper.CachedType<ClassFileDescriptor> cachedType;
    private VisitorHelper visitorHelper;

    public ClassVisitor(VisitorHelper visitorHelper) {
        super(Opcodes.ASM5);
        this.visitorHelper = visitorHelper;
    }

    /**
     * Return the type descriptor created by visiting the class.
     * 
     * @return The type descriptor.
     */
    public ClassFileDescriptor getTypeDescriptor() {
        return cachedType.getTypeDescriptor();
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        Class<? extends ClassFileDescriptor> javaType = getJavaType(access);
        cachedType = visitorHelper.getCachedType(SignatureHelper.getObjectType(name), javaType);
        if (hasFlag(access, Opcodes.ACC_ABSTRACT) && !hasFlag(access, Opcodes.ACC_INTERFACE)) {
            cachedType.getTypeDescriptor().setAbstract(Boolean.TRUE);
        }
        setModifiers(access, cachedType.getTypeDescriptor());
        if (signature == null) {
            if (superName != null) {
                cachedType.getTypeDescriptor().setSuperClass(visitorHelper.getType(SignatureHelper.getObjectType(superName)).getTypeDescriptor());
            }
            for (int i = 0; interfaces != null && i < interfaces.length; i++) {
                cachedType.getTypeDescriptor().getInterfaces().add(visitorHelper.getType(SignatureHelper.getObjectType(interfaces[i])).getTypeDescriptor());
            }
        } else {
            new SignatureReader(signature).accept(new ClassSignatureVisitor(cachedType.getTypeDescriptor(), visitorHelper));
        }
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        final FieldDescriptor fieldDescriptor = visitorHelper.getFieldDescriptor(cachedType, SignatureHelper.getFieldSignature(name, desc));
        fieldDescriptor.setName(name);
        fieldDescriptor.setVolatile(hasFlag(access, Opcodes.ACC_VOLATILE));
        fieldDescriptor.setTransient(hasFlag(access, Opcodes.ACC_TRANSIENT));
        setModifiers(access, fieldDescriptor);
        if (signature == null) {
            TypeDescriptor type = visitorHelper.getType(SignatureHelper.getType((desc))).getTypeDescriptor();
            fieldDescriptor.setType(type);
        } else {
            new SignatureReader(signature).accept(new AbstractTypeSignatureVisitor<FieldDescriptor>(fieldDescriptor, visitorHelper) {
                @Override
                public SignatureVisitor visitArrayType() {
                    return new DependentTypeSignatureVisitor(fieldDescriptor, visitorHelper);
                }

                @Override
                public SignatureVisitor visitTypeArgument(char wildcard) {
                    return new DependentTypeSignatureVisitor(fieldDescriptor, visitorHelper);
                }

                @Override
                public SignatureVisitor visitSuperclass() {
                    return this;
                }

                @Override
                public void visitEnd(TypeDescriptor resolvedTypeDescriptor) {
                    fieldDescriptor.setType(resolvedTypeDescriptor);
                }
            });
        }
        if (value instanceof org.objectweb.asm.Type) {
            visitorHelper.addDependency(cachedType.getTypeDescriptor(), fieldDescriptor, SignatureHelper.getType((org.objectweb.asm.Type) value));
        }
        return new FieldVisitor(fieldDescriptor, visitorHelper);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodDescriptor methodDescriptor = visitorHelper.getMethodDescriptor(cachedType, SignatureHelper.getMethodSignature(name, desc));
        methodDescriptor.setName(name);
        setModifiers(access, methodDescriptor);
        if (hasFlag(access, Opcodes.ACC_ABSTRACT)) {
            methodDescriptor.setAbstract(Boolean.TRUE);
        }
        if (hasFlag(access, Opcodes.ACC_NATIVE)) {
            methodDescriptor.setNative(Boolean.TRUE);
        }
        if (signature == null) {
            String returnType = SignatureHelper.getType(org.objectweb.asm.Type.getReturnType(desc));
            methodDescriptor.setReturns(visitorHelper.getType(returnType).getTypeDescriptor());
            org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(desc);
            for (int i = 0; i < types.length; i++) {
                ParameterDescriptor parameterDescriptor = visitorHelper.getParameterDescriptor(methodDescriptor, i);
                // ParameterDescriptor parameterDescriptor =
                // methodDescriptor.createParameter(i);
                String parameterType = SignatureHelper.getType(types[i]);
                parameterDescriptor.setType(visitorHelper.getType(parameterType).getTypeDescriptor());
            }
        } else {
            new SignatureReader(signature).accept(new MethodSignatureVisitor(methodDescriptor, visitorHelper));
        }
        for (int i = 0; exceptions != null && i < exceptions.length; i++) {
            TypeDescriptor exception = visitorHelper.getType(SignatureHelper.getObjectType(exceptions[i])).getTypeDescriptor();
            methodDescriptor.getDeclaredThrowables().add(exception);
        }
        return new MethodVisitor(cachedType.getTypeDescriptor(), methodDescriptor, visitorHelper);
    }

    private void setModifiers(final int access, AccessModifierDescriptor descriptor) {
        VisibilityModifier visibility = getVisibility(access);
        descriptor.setVisibility(visibility.getValue());
        if (hasFlag(access, Opcodes.ACC_SYNTHETIC)) {
            descriptor.setSynthetic(Boolean.TRUE);
        }
        if (hasFlag(access, Opcodes.ACC_FINAL)) {
            descriptor.setFinal(Boolean.TRUE);
        }
        if (hasFlag(access, Opcodes.ACC_STATIC)) {
            descriptor.setStatic(Boolean.TRUE);
        }
    }

    @Override
    public void visitSource(final String source, final String debug) {
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        addInnerClass(cachedType.getTypeDescriptor(), visitorHelper.getType(SignatureHelper.getObjectType(name)).getTypeDescriptor());
    }

    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
        addInnerClass(visitorHelper.getType(SignatureHelper.getObjectType(owner)).getTypeDescriptor(), cachedType.getTypeDescriptor());
    }

    // ---------------------------------------------

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        AnnotationValueDescriptor annotationDescriptor = visitorHelper.addAnnotation(cachedType.getTypeDescriptor(), SignatureHelper.getType(desc));
        return new AnnotationVisitor(annotationDescriptor, visitorHelper);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
    }

    @Override
    public void visitEnd() {
    }

    /**
     * Checks whether the value contains the flag.
     * 
     * @param value
     *            the value
     * @param flag
     *            the flag
     * @return <code>true</code> if (value & flag) == flag, otherwise
     *         <code>false</code>.
     */
    private boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }

    /**
     * Returns the AccessModifier for the flag pattern.
     * 
     * @param flags
     *            the flags
     * @return the AccessModifier
     */
    private VisibilityModifier getVisibility(int flags) {
        if (hasFlag(flags, Opcodes.ACC_PRIVATE)) {
            return VisibilityModifier.PRIVATE;
        } else if (hasFlag(flags, Opcodes.ACC_PROTECTED)) {
            return VisibilityModifier.PROTECTED;
        } else if (hasFlag(flags, Opcodes.ACC_PUBLIC)) {
            return VisibilityModifier.PUBLIC;
        } else {
            return VisibilityModifier.DEFAULT;
        }
    }

    /**
     * Determine the types label to be applied to a class node.
     * 
     * @param flags
     *            The access flags.
     * @return The types label.
     */
    private Class<? extends ClassFileDescriptor> getJavaType(int flags) {
        if (hasFlag(flags, Opcodes.ACC_ANNOTATION)) {
            return AnnotationTypeDescriptor.class;
        } else if (hasFlag(flags, Opcodes.ACC_ENUM)) {
            return EnumTypeDescriptor.class;
        } else if (hasFlag(flags, Opcodes.ACC_INTERFACE)) {
            return InterfaceTypeDescriptor.class;
        }
        return ClassTypeDescriptor.class;
    }

    /**
     * Adds an inner class relation.
     * 
     * @param outerClass
     *            The outer class.
     * @param innerClass
     *            The inner class.
     */
    private void addInnerClass(TypeDescriptor outerClass, TypeDescriptor innerClass) {
        if (!innerClass.equals(outerClass)) {
            outerClass.getDeclaredInnerClasses().add(innerClass);
        }
    }
}