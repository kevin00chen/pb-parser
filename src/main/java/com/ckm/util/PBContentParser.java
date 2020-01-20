package com.ckm.util;

import com.google.protobuf.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Map不支持嵌套类型的key或value
 */
public class PBContentParser {
    private static Map<String, String> functionNames = new HashMap<String, String>() {{
        put("INT32", "readInt32");
        put("INT64", "readInt64");
        put("UINT32", "readUInt32");
        put("UINT64", "readUInt64");
        put("SINT32", "readSInt32");
        put("SINT64", "readSInt64");
        put("BOOL", "readBool");
        put("ENUM", "readEnum");
        put("FIXED64", "readFixed64");
        put("SFIXED64", "readSFixed64");
        put("DOUBLE", "readDouble");
        put("STRING", "readString");
        put("MESSAGE", "readMessage");
        put("BYTES", "readBytes");
        put("FIXED32", "readFixed32");
        put("SFIXED32", "readSFixed32");
        put("FLOAT", "readFloat");
    }};

    public static Object getFieldFromBytes(Descriptors.Descriptor descriptors, byte[] data, String pathStr) throws Exception {
        return getFieldFromBytes(descriptors, data, pathStr, 0);
    }

    public static Object getFieldFromBytes(Descriptors.Descriptor descriptors, byte[] data, String[] pathStr) throws Exception {
        return getFieldFromBytes(descriptors, data, pathStr, 0);
    }

    public static Object getFieldFromBytes(Descriptors.Descriptor descriptors, byte[] data, String[] paths, int index) throws Exception {
        Object result = null;
        int depth = paths.length;

        String path = paths[index];
        int tagNum = descriptors.findFieldByName(path).getNumber();
        // bool类型为false时，byte[]数据中没有记录，不初始化的话返回为null
        if (descriptors.findFieldByNumber(tagNum).getType() == Descriptors.FieldDescriptor.Type.BOOL) {
            result = false;
        }
        descriptors.findFieldByName(path).getType().name();

        CodedInputStream input = CodedInputStream.newInstance(data, 0, data.length);
        int currentRead = 0; // 当前读取位置
        int currentTagNum = 0;

        while (currentTagNum != tagNum && currentRead < data.length) {
            int currentTag = input.readTag();

            int readTagLength = input.getTotalBytesRead();
            currentRead += readTagLength;
            int tmpNum = currentTag >> 3; // 获取proto中的字段编号
            currentTagNum = tmpNum;

            Descriptors.FieldDescriptor fieldDescriptor = descriptors.findFieldByNumber(tmpNum);

            if (tmpNum < tagNum) {
                switch (fieldDescriptor.getType()) {
                    case INT32:
                    case INT64:
                    case UINT32:
                    case UINT64:
                    case SINT32:
                    case SINT64:
                    case BOOL:
                    case ENUM:
                        currentRead += getVarintLength(data, currentRead);
                        input = CodedInputStream.newInstance(data, currentRead, data.length);
                        break;
                    case FIXED64:
                    case SFIXED64:
                    case DOUBLE:
                        currentRead += 8;
                        input = CodedInputStream.newInstance(data, currentRead, data.length);
                        break;
                    case STRING:
                    case MESSAGE:
                    case BYTES:
                        int dataLength = input.readRawVarint32();
                        currentRead += dataLength + input.getTotalBytesRead() - readTagLength;
                        input = CodedInputStream.newInstance(data, currentRead, data.length);
                        break;
                    case FIXED32:
                    case SFIXED32:
                    case FLOAT:
                        currentRead += 4;
                        input = CodedInputStream.newInstance(data, currentRead, data.length);
                        break;
                    default:
                        break;
                }
            } else if (tmpNum == tagNum) {
                switch (fieldDescriptor.getType()) {
                    case INT32:
                    case INT64:
                    case UINT32:
                    case UINT64:
                    case SINT32:
                    case SINT64:
                    case BOOL:
                    case FIXED64:
                    case SFIXED64:
                    case DOUBLE:
                    case STRING:
                    case BYTES:
                    case FIXED32:
                    case SFIXED32:
                    case FLOAT:
                    case ENUM:
                        result = getValueByFunctionName(fieldDescriptor, paths, index, depth, input, currentTag);
                        break;
                    case MESSAGE:
                        if (index < depth - 1 || fieldDescriptor.isMapField() || fieldDescriptor.isRepeated()) { // 对嵌套类型取子字段递归调用，或者请求的为Map类型时需要对map进行解析
                            if (fieldDescriptor.isMapField()) { // 只支持Key为基础类型的Map, value为复杂类型的，返回对应的byte[]
                                String valueTypeName = fieldDescriptor.getMessageType().getFields().get(1).getType().name();
                                if (index < depth - 2 && !valueTypeName.equals("MESSAGE")) { // 不支持基础value类型map的递归取值
                                    throw new Exception("暂不支持map的递归取值");
                                } else { // 根据map的key取值
                                    Object currentKey;
                                    Object expectKey = null;

                                    if (index < depth - 1) {
                                        expectKey = paths[index + 1];
                                    }

                                    WireFormat.FieldType keyType = getWireFormatFieldType(fieldDescriptor.getMessageType().getFields().get(0).getType());
                                    WireFormat.FieldType valueType = getWireFormatFieldType(fieldDescriptor.getMessageType().getFields().get(1).getType());

                                    Object defaultKey = fieldDescriptor.getMessageType().getFields().get(0).getDefaultValue();
                                    Object defaultValue;

                                    if (!valueType.name().equals("BYTES")) {
                                        defaultValue = fieldDescriptor.getMessageType().getFields().get(1).getDefaultValue();
                                    } else {
                                        defaultValue = ByteString.EMPTY;
                                    }

                                    com.google.protobuf.MapEntry<Object, Object> defaultEntry =
                                            com.google.protobuf.MapEntry
                                                    .<Object, Object>newDefaultInstance(
                                                            fieldDescriptor.getMessageType(),
                                                            keyType,
                                                            defaultKey,
                                                            valueType,
                                                            defaultValue);

                                    Object mapEntry = null;
                                    int nextTag = currentTag;

                                    while(nextTag == currentTag && (expectKey == null || (expectKey != null && mapEntry == null))) { // 当下一个元素仍然是该map中的元素时
                                        mapEntry = input.readMessage(
                                                defaultEntry.getParserForType(), ExtensionRegistryLite.getEmptyRegistry());
                                        try {
                                            nextTag = input.readTag();
                                        } catch (ArrayIndexOutOfBoundsException e) { // 如果读取到byte[]最后，则清零
                                            currentTag = 0;
                                        }

                                        currentKey = ((MapEntry) mapEntry).getKey();
                                        if (!currentKey.equals(expectKey) && expectKey != null) {
                                            mapEntry = null;
                                        } else {
                                            if (expectKey != null) {
                                                if (mapEntry instanceof MapEntry) {
                                                    result = ((MapEntry) mapEntry).getValue();
                                                }
                                            } else {
                                                if (result == null) {
                                                    result = new HashMap<Object, Object>();
                                                }
                                                Object mapKey = ((MapEntry) mapEntry).getKey();
                                                Object mapValue = ((MapEntry) mapEntry).getValue();
                                                ((HashMap) result).put(mapKey, mapValue);
                                            }
                                        }
                                    }

                                    if (index < depth - 2) {
                                        System.out.println();
                                        String[] subPath = Arrays.copyOfRange(paths, index + 2, depth);
                                        result = getFieldFromBytes(fieldDescriptor.getMessageType().getFields().get(1).getMessageType(), ((ByteString) result).toByteArray(), subPath, 0);
                                    }
                                }
                            } else if (fieldDescriptor.isRepeated()) { // 如果是复杂类型的list
                                byte[] currentValue = input.readByteArray();
                                int nextTag = input.readTag();

                                while(currentTag == nextTag) { // 如果出现连续tag，则当前字段为list
                                    if (result == null) {
                                        result = new ArrayList<Object>();
                                        ((ArrayList) result).add(currentValue);
                                    } else {
                                        ((ArrayList) result).add(input.readByteArray());
                                        try {
                                            nextTag = input.readTag();
                                        } catch (ArrayIndexOutOfBoundsException e) { // 如果读取到byte[]最后，则清零
                                            currentTag = 0;
                                        }
                                    }
                                }
                                index ++;

                                if (index == depth - 1) { //
                                    String nextPath = paths[index];
                                    if (result instanceof ArrayList) {
                                        result = ((ArrayList) result).get(Integer.valueOf(nextPath));
                                    }
                                } else if (index < depth - 1) {
                                    String nextPath = paths[index];
                                    if (result instanceof ArrayList) {
                                        result = ((ArrayList) result).get(Integer.valueOf(nextPath));
                                        String[] subPath = Arrays.copyOfRange(paths, index + 1, depth);
                                        result = getFieldFromBytes(fieldDescriptor.getMessageType(), (byte[]) result, subPath);
                                    }
                                }
                            } else {
                                result = getFieldFromBytes(fieldDescriptor.getMessageType(), input.readByteArray(), paths, ++ index); // 递归调用时从下一个field开始
                            }
                        } else { // 只取到嵌套类型，返回byte[]
                            result = input.readByteArray();
                        }
                        break;

                    default:
                        break;
                }
            } else {
                break;
            }
        }
        return result;
    }


    /**
     * 根据字段名从byte[]中直接获取指定部分的数据，可以使用链式字段名
     * 对map结构，可以直接链式访问key得到对应的value值，比如a.b.m.key
     * 对list结构，可以直接链式访问指定位置的元素，比如a.b.0
     */
    public static Object getFieldFromBytes(Descriptors.Descriptor descriptors, byte[] data, String pathStr, int index) throws Exception {
        String[] paths = pathStr.split("\\.");
        return getFieldFromBytes(descriptors, data, paths, 0);
    }

    private static WireFormat.FieldType getWireFormatFieldType(Descriptors.FieldDescriptor.Type type) {
        switch (type) {
            case DOUBLE:
                return WireFormat.FieldType.DOUBLE;
            case FLOAT:
                return WireFormat.FieldType.FLOAT;
            case INT64:
                return WireFormat.FieldType.INT64;
            case UINT64:
                return WireFormat.FieldType.UINT64;
            case INT32:
                return WireFormat.FieldType.INT32;
            case FIXED64:
                return WireFormat.FieldType.FIXED64;
            case FIXED32:
                return WireFormat.FieldType.FIXED32;
            case BOOL:
                return WireFormat.FieldType.BOOL;
            case STRING:
                return WireFormat.FieldType.STRING;
            case GROUP:
                return WireFormat.FieldType.GROUP;
            case MESSAGE:
                return WireFormat.FieldType.BYTES;
            case BYTES:
                return WireFormat.FieldType.BYTES;
            case UINT32:
                return WireFormat.FieldType.UINT32;
            case ENUM:
                return WireFormat.FieldType.ENUM;
            case SFIXED32:
                return WireFormat.FieldType.SFIXED32;
            case SFIXED64:
                return WireFormat.FieldType.SFIXED64;
            case SINT32:
                return WireFormat.FieldType.SINT32;
            case SINT64:
                return WireFormat.FieldType.SINT64;
            default:
                return WireFormat.FieldType.STRING;
        }
    }

    private static String getEnumValueByTag(Descriptors.FieldDescriptor fieldDescriptor, int tagNum) {
        String result = null;
        List<Descriptors.EnumValueDescriptor> values = fieldDescriptor.getEnumType().getValues();

        for (Descriptors.EnumValueDescriptor value : values) {
            if (value.getNumber() == tagNum) {
                result = value.getName();
            }
        }
        return result;
    }

    private static Object getValueByFunctionName(Descriptors.FieldDescriptor fieldDescriptor, String[] paths, int index, int depth, CodedInputStream input, int currentTag) throws Exception {
        String typeName = fieldDescriptor.getType().name();
        int nextTag;
        Object result = null;
        if (index < depth - 2) { // 基础类型字段，最多再往下取一级，下一级可以是数字(可能取List中的元素)，也可以是字符串(取map中key对应的value)
            throw new Exception();
        } else {
            Method method  = input.getClass().getMethod(functionNames.get(typeName), null);
            method.setAccessible(true);
            Object currentValue = method.invoke(input, null);

            if ("ENUM".equals(fieldDescriptor.getType().name())) { // 根据enum中tag值过滤
                fieldDescriptor.getEnumType().getValues();
                currentValue = getEnumValueByTag(fieldDescriptor, (Integer) currentValue);
            }

            if (fieldDescriptor.isRepeated()) {
                nextTag = input.readTag();
                while(currentTag == nextTag) { // 如果出现连续tag，则当前字段为list
                    if (result == null) {
                        result = new ArrayList<Object>();
                        ((ArrayList) result).add(currentValue);
                    } else {
                        ((ArrayList) result).add(method.invoke(input, null));
                        try {
                            nextTag = input.readTag();
                        } catch (ArrayIndexOutOfBoundsException e) { // 如果读取到byte[]最后，则清零
                            currentTag = 0;
                        }
                    }
                }
                index ++;

                if (index == depth - 1) {
                    String nextPath = paths[index];
                    if (result instanceof ArrayList) {
                        result = ((ArrayList) result).get(Integer.valueOf(nextPath));
                    }
                }
            } else {
                result = currentValue;
            }
        }
        return result;
    }

    public static int getVarintLength(byte[] bytes, int index) {
        int result = 1;
        byte b;
        for (int i = index; i < bytes.length; i ++) {
            b = bytes[i];
            b = (byte) (b & 0x80);
            if (b >> 7 == -1) {
                result ++;
            } else {
                break;
            }
        }
        return result;
    }
}