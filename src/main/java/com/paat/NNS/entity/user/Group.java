package com.paat.NNS.entity.user;

import com.paat.NNS.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "authority_group")
@Getter
@Setter
public class Group extends BaseEntity {
    private String name;
    // Account
    @ManyToMany
    private List<Authority> authorities;

    // Get transaction
    // Add transaction
}
