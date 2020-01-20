package com.ckm.util;

import com.ckm.util.pb.PBTest;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DataParserTest {
    byte[] testAllTypeData;
    Descriptors.Descriptor descriptor;

    @Before
    public void init() {
        Map<String, String> baseMap = new HashMap();
        baseMap.put("k1", "v1");
        baseMap.put("k2", "v2");

        List<PBTest.XMessage> xMessageList = new ArrayList<PBTest.XMessage>();
        xMessageList.add(PBTest.XMessage.newBuilder().setAge(18).setId("4306").setName("tom").build());
        xMessageList.add(PBTest.XMessage.newBuilder().setAge(28).setId("4307").setName("lucy").build());
        xMessageList.add(PBTest.XMessage.newBuilder().setAge(38).setId("4308").setName("jim").build());

        Map<String, PBTest.XMessage> xMessageMap = new HashMap<String, PBTest.XMessage>();
        xMessageMap.put("tom", PBTest.XMessage.newBuilder().setAge(18).setId("4306").setName("tom").build());
        xMessageMap.put("lucy", PBTest.XMessage.newBuilder().setAge(28).setId("4307").setName("lucy").build());
        xMessageMap.put("jim", PBTest.XMessage.newBuilder().setAge(38).setId("4308").setName("jim").build());

        testAllTypeData = PBTest.TestAllType.newBuilder()
                .setInt32(132)
                .setInt64(164)
                .setUint32(232)
                .setUint64(264)
                .setSint32(332)
                .setSint64(364)
                .setFixed32(432)
                .setFixed64(464)
                .setSfixed32(532)
                .setSfixed64(564)
                .setBool(true)
                .setDouble(1.1)
                .setFloat(2.1f)
                .setString("str-1")
                .putAllBaseMap(baseMap)
                .setXMessage(PBTest.XMessage.newBuilder().setAge(18).setId("4306").setName("tom").build())
                .addAllXMessageList(xMessageList)
                .putAllXMessageMap(xMessageMap)
                .build()
                .toByteArray();
        descriptor = PBTest.TestAllType.getDescriptor();
    }

    @Test
    public void dataParseTest() throws Exception {
        Object result;

        // Message Map test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_map");
        assertTrue(result instanceof Map);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_map.jim");
        assertTrue(result instanceof ByteString);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_map.jim.id");
        assertTrue(result instanceof String);
        assertEquals(result, "4308");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_map.jim.name");
        assertTrue(result instanceof String);
        assertEquals(result, "jim");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_map.jim.age");
        assertTrue(result instanceof Integer);
        assertEquals(result, 38);

        // Message List test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list");
        assertTrue(result instanceof List);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list.1");
        assertTrue(result instanceof byte[]);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list.1.id");
        assertTrue(result instanceof String);
        assertEquals(result, "4307");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list.1.name");
        assertTrue(result instanceof String);
        assertEquals(result, "lucy");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list.1.age");
        assertTrue(result instanceof Integer);
        assertEquals(result, 28);

        // Message test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message");
        assertTrue(result instanceof byte[]);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message.id");
        assertTrue(result instanceof String);
        assertEquals(result, "4306");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message.name");
        assertTrue(result instanceof String);
        assertEquals(result, "tom");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message.age");
        assertTrue(result instanceof Integer);
        assertEquals(result, 18);

        // Base Map test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "baseMap");
        assertTrue(result instanceof Map);
        assertEquals(((Map) result).get("k1"), "v1");
        assertEquals(((Map) result).get("k2"), "v2");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "baseMap.k1");
        assertTrue(result instanceof String);
        assertEquals(result, "v1");

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "baseMap.k2");
        assertTrue(result instanceof String);
        assertEquals(result, "v2");

        // Boolean test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "bool");
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);

        // String test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "string");
        assertTrue(result instanceof String);
        assertEquals(result, "str-1");

        // Double & Float test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "double");
        assertTrue(result instanceof Double);
        assertEquals(result, 1.1);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "float");
        assertTrue(result instanceof Float);
        assertEquals(result, 2.1f);

        // Integer & Long test
        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "int32");
        assertTrue(result instanceof Integer);
        assertEquals(result, 132);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "int64");
        assertTrue(result instanceof Long);
        assertEquals(result, 164L);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "uint32");
        assertTrue(result instanceof Integer);
        assertEquals(result, 232);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "uint64");
        assertTrue(result instanceof Long);
        assertEquals(result, 264L);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "sint32");
        assertTrue(result instanceof Integer);
        assertEquals(result, 332);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "sint64");
        assertTrue(result instanceof Long);
        assertEquals(result, 364L);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "fixed32");
        assertTrue(result instanceof Integer);
        assertEquals(result, 432);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "fixed64");
        assertTrue(result instanceof Long);
        assertEquals(result, 464L);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "sfixed32");
        assertTrue(result instanceof Integer);
        assertEquals(result, 532);

        result = PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "sfixed64");
        assertTrue(result instanceof Long);
        assertEquals(result, 564L);

    }
}
