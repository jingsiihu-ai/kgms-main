package com.hjc.kgms.enums;

public enum UserRole {
    PRINCIPAL(0, "园长"),
    PARENT(1, "家长"),
    TEACHER(2, "教师"),
    NURSE(3, "保育员");

    private final int roleId;
    private final String displayName;

    UserRole(int roleId, String displayName) {
        this.roleId = roleId;
        this.displayName = displayName;
    }

    public int getRoleId() { return roleId; }
    public String getDisplayName() { return displayName; }

    public static UserRole fromRoleId(Integer roleId) {
        if (roleId == null) return TEACHER;
        for (UserRole value : values()) {
            if (value.roleId == roleId) return value;
        }
        return TEACHER;
    }
}
