package com.iambstha.tl_rest_api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;

@MappedSuperclass
@Data
public class BaseEntity {

    @JsonIgnore
    @Column(name = "created_by",updatable = false)
    private Long createdBy;

    @JsonIgnore
    @Column(name = "created_ts", updatable = false)
    private Timestamp createdTs;

    @JsonIgnore
    @Column(name = "modified_by", insertable = false)
    private Long modifiedBy;

    @JsonIgnore
    @Column(name = "modified_ts", insertable = false)
    private Timestamp modifiedTs;

    public void setCreatedTs() {
        this.createdTs = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(createdBy, that.createdBy) && Objects.equals(createdTs, that.createdTs) && Objects.equals(modifiedBy, that.modifiedBy) && Objects.equals(modifiedTs, that.modifiedTs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdBy, createdTs, modifiedBy, modifiedTs);
    }
}
