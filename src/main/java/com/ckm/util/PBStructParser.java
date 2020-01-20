package com.ckm.util;

import com.google.protobuf.Descriptors;
import com.ckm.util.types.BaseType;

import java.util.List;

public class PBStructParser {
    public static BaseType parseReturnType(Descriptors.Descriptor descriptors, String pathStr) throws Exception {
        return parseReturnType(descriptors, pathStr, 0);
    }

    public static BaseType parseReturnType(Descriptors.Descriptor descriptors, String pathStr, int index) throws Exception {
        String[] paths = pathStr.replace("'", "").replace("\"", "").split("\\.");
        BaseType resultType;
        int depth = paths.length;

        String path = paths[index];
        Descriptors.FieldDescriptor fieldDescriptor = descriptors.findFieldByName(path);

        if (fieldDescriptor.isMapField()) { // 如果当前字段为Map
            List<Descriptors.FieldDescriptor> fields = fieldDescriptor.getMessageType().getFields();
            String keyType = fields.get(0).getType().name();
            String valueType = fields.get(1).getType().name();

            if (index < depth - 2 && !valueType.equals("MESSAGE")) { // 对List只支持向下一级
                throw new Exception();
            } else if (index == depth - 1) { // 取Map的返回结果
                if (valueType.equals("MESSAGE")) {
                    resultType = BaseType.newBinaryType(getClassName(fields.get(1)));
                    resultType.setBinary(false)
                            .setMap(true)
                            .setKeyType(keyType);
                } else {
                    resultType = BaseType.newMapType(keyType, valueType);
                }
            } else { // 向下一级
                switch (fields.get(1).getType()) {
                    case MESSAGE:
                        resultType = getElementType(fields.get(1), index + 1, depth, pathStr);
                        break;
                    default:
                        resultType = BaseType.newBaseType(fields.get(1).getType().name());
                        break;
                }
            }
        } else if (fieldDescriptor.isRepeated()) { // 如果当前字段为List
            if (index == depth - 1) { // 取List的返回结果
                switch (fieldDescriptor.getType()) {
                    case MESSAGE:
                        resultType = getElementType(fieldDescriptor, index, depth, pathStr);
                        resultType.setBinary(false);
                        resultType.setList(true);
                        break;
                    default:
                        resultType = BaseType.newListType(fieldDescriptor.getType().name());
                        break;
                }
            } else { // 向下一级
                try {
                    Integer.parseInt(paths[index + 1]); // List下一级必须为数字
                    resultType = getElementType(fieldDescriptor, index + 1, depth, pathStr);
                } catch (Exception e) {
                    throw e;
                }
            }
        } else {
            resultType = getElementType(fieldDescriptor, index, depth, pathStr);
        }
        return resultType;
    }

    private static BaseType getElementType(Descriptors.FieldDescriptor fieldDescriptor, int index, int depth, String pathStr) throws Exception {
        BaseType resultType = null;
        switch (fieldDescriptor.getType()) {
            case INT32:
            case INT64:
            case UINT32:
            case UINT64:
            case SINT32:
            case SINT64:
            case BOOL:
            case ENUM:
            case FIXED64:
            case SFIXED64:
            case DOUBLE:
            case STRING:
            case BYTES:
            case FIXED32:
            case SFIXED32:
            case FLOAT:
                if (index < depth - 1 && !fieldDescriptor.isRepeated()) { // 对非List的基础类型取下一级，直接报错
                    throw new Exception();
                } else {
                    resultType = BaseType.newBaseType(fieldDescriptor.getType().name());
                }
                break;
            case MESSAGE:
                if (index < depth - 1) {
                    resultType = parseReturnType(fieldDescriptor.getMessageType(), pathStr, ++index); // 递归调用时从下一个field开始
                    break;
                } else {
                    String className = getClassName(fieldDescriptor);

                    resultType = BaseType.newBinaryType(className);
                    break;
                }
        }
        return resultType;
    }

    private static String getClassName(Descriptors.FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getMessageType().getFile().getOptions().getJavaPackage() +
                "." + fieldDescriptor.getMessageType().getFile().getOptions().getJavaOuterClassname() +
                "$" + fieldDescriptor.getMessageType().getName();
    }
}
