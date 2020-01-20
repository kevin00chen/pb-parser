package com.ckm.util;

import com.ckm.util.pb.PBTest;
import com.ckm.util.types.BaseType;
import com.google.protobuf.Descriptors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StructParserTest {
    Descriptors.Descriptor descriptor;

    @Before
    public void init() {
        descriptor = PBTest.TestAllType.getDescriptor();
    }

    @Test
    public void typeTest() throws Exception {
        BaseType baseType;
        baseType = PBStructParser.parseReturnType(descriptor, "int32");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "int32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "int64");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "int64".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "uint32");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "uint32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "uint64");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "uint64".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "sint32");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "sint32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "sint64");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "sint64".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "bool");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "bool".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "fixed64");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "fixed64".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "sfixed64");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "sfixed64".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "double");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "double".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "string");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "fixed32");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "fixed32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "sfixed32");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "sfixed32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "float");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "float".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "baseMap");
        assertFalse(baseType.isBinary());
        assertTrue(baseType.isMap());
        assertFalse(baseType.isList());
        assertEquals(baseType.getKeyType(), "string".toUpperCase());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "baseMap.k1");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message");
        assertTrue(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertEquals(baseType.getClassName(), PBTest.XMessage.class.getName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "binary".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message.id");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message.name");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message.age");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "int32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_list");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertTrue(baseType.isList());
        assertEquals(baseType.getClassName(), PBTest.XMessage.class.getName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "binary".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_list.1");
        assertTrue(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertEquals(baseType.getClassName(), PBTest.XMessage.class.getName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "binary".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_list.1.id");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_list.1.name");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_list.1.age");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "int32".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_map");
        assertFalse(baseType.isBinary());
        assertTrue(baseType.isMap());
        assertFalse(baseType.isList());
        assertEquals(baseType.getClassName(), PBTest.XMessage.class.getName());
        assertEquals(baseType.getKeyType(), "string".toUpperCase());
        assertEquals(baseType.getElementType(), "binary".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_map.lucy");
        assertTrue(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertEquals(baseType.getClassName(), PBTest.XMessage.class.getName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "binary".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_map.lucy.id");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_map.lucy.name");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "string".toUpperCase());

        baseType = PBStructParser.parseReturnType(descriptor, "x_message_map.lucy.age");
        assertFalse(baseType.isBinary());
        assertFalse(baseType.isMap());
        assertFalse(baseType.isList());
        assertNull(baseType.getClassName());
        assertNull(baseType.getKeyType());
        assertEquals(baseType.getElementType(), "int32".toUpperCase());
    }
}
