package com.pos.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base model class with common fields for all entities
 */
public abstract class BaseModel {
    protected String id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected String createdBy;
    protected String updatedBy;
    protected boolean active;

    public BaseModel() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Updates the audit fields before saving.
     * @param username The username of the user performing the operation
     */
    public void prepareForUpdate(String username) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = username;
    }

    /**
     * Initializes the audit fields for a new record.
     * @param username The username of the user performing the operation
     */
    public void prepareForCreate(String username) {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = username;
        this.updatedBy = username;
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return id != null && id.equals(baseModel.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, createdAt=%s, updatedAt=%s, createdBy='%s', updatedBy='%s', active=%s}",
                getClass().getSimpleName(), id, createdAt, updatedAt, createdBy, updatedBy, active);
    }
} 