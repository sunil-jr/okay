package com.paat.NNS.entity.user;

import com.paat.NNS.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authority")
@Getter
@Setter
public class Authority extends BaseEntity {

    @Column(unique = true)
    private String name;
}
