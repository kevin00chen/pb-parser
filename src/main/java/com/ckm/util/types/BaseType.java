package com.ckm.util.types;

public class BaseType {
    private static final String BINARY = "BINARY";

    private String keyType;
    private String elementType;
    private String className;
    private boolean isList = false;
    private boolean isMap = false;
    private boolean isBinary = false;

    public BaseType(String keyType, String elementType, boolean isList, boolean isMap) {
        this.keyType = keyType;
        this.elementType = elementType;
        this.isList = isList;
        this.isMap = isMap;
    }

    // list 和 基础类型
    public BaseType(String elementType, boolean isList) {
        this.elementType = elementType;
        this.isList = isList;
    }

    // map
    public BaseType(String keyType, String elementType) {
        this.keyType = keyType;
        this.elementType = elementType;
        this.isMap = true;
    }

    // binary
    public BaseType(String elementType, String className, boolean isBinary) {
        this.elementType = elementType;
        this.className = className;
        this.isBinary = isBinary;
    }

    public static BaseType newListType(String elementType) {
        return new BaseType(elementType, true);
    }

    public static BaseType newBaseType(String elementType) {
        return new BaseType(elementType, false);
    }

    public static BaseType newBinaryType(String className) {
        return new BaseType(BINARY, className, true);
    }

    public static BaseType newMapType(String keyType, String valueType) {
        return new BaseType(keyType, valueType);
    }

    public BaseType setList(boolean isList) {
        this.isList = isList;
        return this;
    }

    public BaseType setBinary(boolean isBinary) {
        this.isBinary = isBinary;
        return this;
    }

    public BaseType setMap(boolean isMap) {
        this.isMap = isMap;
        return this;
    }

    public BaseType setKeyType(String keyType) {
        this.keyType = keyType;
        return this;
    }

    public boolean isList() {
        return isList;
    }
    public boolean isMap() {
        return isMap;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public String getElementType() {
        return elementType;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getClassName() {
        return className;
    }
}
