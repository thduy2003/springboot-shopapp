package com.example.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="social_accounts")

@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 50)
    private String provider_id;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "email", length = 150)
    private String email;
}
