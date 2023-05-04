package com.asite.aprojecto.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "token_tbl")
public class TokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    public Long tokenId;

    @Column(unique = true)
    public String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserModel user;

    @Column(name = "device_id")
    public String deviceId;

}
