package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.glassfish.jaxb.runtime.v2.bytecode.ClassTailor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.junit.Test;

public class ForeignAccessorTest {

    /**
     * Foreign accessor shouldn't throw ClassCastException, just skip optimization.
     */
    @Test
    public void foreignAccessor() throws NoSuchFieldException{
        String newClassName = EntityWithForeignAccessor.class.getName().replace('.','/') + "$JaxbAccessorF_author";
        Class<?> foreignAccessor = AccessorInjector.prepare(EntityWithForeignAccessor.class,
                ClassTailor.toVMClassName(ForeignAccessorTest.FieldAccessor_Ref.class),
                newClassName);
        assertNotNull(foreignAccessor);

        Accessor<Object, Object> accessor = OptimizedAccessorFactory.get(EntityWithForeignAccessor.class.getDeclaredField("author"));
        assertNull(accessor);
    }
    
    @Test
    public void knownAccessor() throws NoSuchFieldException {
        Accessor<Object, Object> accessor = OptimizedAccessorFactory.get(EntityWithKnownAccessor.class.getDeclaredField("author"));
        assertNotNull(accessor);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class EntityWithKnownAccessor {
        String author;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class EntityWithForeignAccessor {
        String author;
    }

    /**
     * Test template doesn't extend accessor intentionally.
     */
    public static class FieldAccessor_Ref /*extends Accessor*/ {
        public FieldAccessor_Ref() {
           // super(Ref.class);
        }

        public Object get(Object bean) {
            return ((Bean)bean).f_ref;
        }

        public void set(Object bean, Object value) {
            ((Bean)bean).f_ref = (Ref)value;
        }
    }
}
