package com.ecommerce.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_authority")
@IdClass(UserAuthorityId.class)
public class UserAuthority implements Serializable{

    @Id
    @Column(name = "user_id")
    private long userId;
    @Id
    @Column(name = "authority_id")
    private long authorityId;

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAuthorityId() {
        return authorityId;
    }
    public void setAuthorityId(long authorityId) {
        this.authorityId = authorityId;
    }

    public UserAuthority(long userId, long authorityId) {
        this.userId = userId;
        this.authorityId = authorityId;
    }
    public UserAuthority(){ }
}
