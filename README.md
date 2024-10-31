　　最好用的ProtoBuf嵌套解析。

　　当使用ProtoBuf结构的数据时，底层会序列化成一个`byte[]`，如果想取指定某个字段，在`parseFrom`然后`get`的方法之外，还可以用本方案。

## 一、特性

　　本项目支持从`byte[]`中获取指定字段，或者对于嵌套Message，可以支持路径递归。同时也支持在`Map`或者`List`中根据`key`和`index`获取元素
并且如果元素为Message类型，仍然可以递归调用。

如下所示：

```
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
```

　　该方案同时提供了Struct和Content的解析，所以可以用于Hive UDF或者Flink UDF中，可以用来动态设置返回类型。

## 二、性能

　　对同一个数据循环`10,000 ~ 1,000,000,000`次进行测试。
结果如下，数据量越大pb-parser方案效果显著。

|  次数 |  原生方法耗时(ms) | pb-parser耗时(ms) |
| ---- | ---- | ---- |
| 10,000 | 89 | 0 |
| 100,000 | 278 | 57 |
| 1,000,000 | 2211 | 24 |
| 10,000,000 | 21812 | 35 |
| 100,000,000 | 177358 | 47 |
| 10,00,000,000 | 1770571 | 60 |

　　原生方案的运行时间随着运行次数直线上升，pb-parser方案耗时稳定，在大数据量的情况下效果显著。

　　观察运行时JVM情况发现，该测试案例运行时频繁YGC，每秒`3~4`次。
