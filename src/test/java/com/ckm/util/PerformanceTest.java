package com.ckm.util;

import com.ckm.util.pb.PBTest;
import com.google.protobuf.Descriptors;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceTest {
    byte[] testAllTypeData;
    Descriptors.Descriptor descriptor;
    int round;

    @Before
    public void init() {
        round = 10000;

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
    public void performanceTest() throws Exception {
        long start;
        long end;
        int rate;

        for (int r = 0; r <= 5; r ++) {
            rate = (int) Math.pow(10, r);
            start = System.currentTimeMillis();
            for (int i = 0; i < round * rate; i++) {
                PBTest.TestAllType.parseFrom(testAllTypeData).getXMessageList(1).getId();
            }
            end = System.currentTimeMillis();
            System.out.println("原生`parseFrom`加`get`方法，循环 " + (round * rate) + " 次耗时：" + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < round * r; i++) {
                PBContentParser.getFieldFromBytes(descriptor, testAllTypeData, "x_message_list.1.id");
            }
            end = System.currentTimeMillis();
            System.out.println("pb-parser方法，循环 " + (round * rate) + " 次耗时：" + (end - start));
        }
    }
}
