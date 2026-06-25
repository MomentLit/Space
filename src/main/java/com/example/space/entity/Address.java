package com.example.space.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sido;

    @Column(nullable = false)
    private String sigungu;

    @Column(name = "eup_myeon_dong")
    private String eupMyeonDong;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "jibun_address")
    private String jibunAddress;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Address(
            String sido,
            String sigungu,
            String eupMyeonDong,
            String roadAddress,
            String jibunAddress,
            String detailAddress,
            String postalCode,
            BigDecimal lat,
            BigDecimal lng
    ) {
        this.sido = sido;
        this.sigungu = sigungu;
        this.eupMyeonDong = eupMyeonDong;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.lat = lat;
        this.lng = lng;
    }

    public static Address create(
            String sido,
            String sigungu,
            String eupMyeonDong,
            String roadAddress,
            String jibunAddress,
            String detailAddress,
            String postalCode,
            BigDecimal lat,
            BigDecimal lng
    ) {
        return Address.builder()
                .sido(sido)
                .sigungu(sigungu)
                .eupMyeonDong(eupMyeonDong)
                .roadAddress(roadAddress)
                .jibunAddress(jibunAddress)
                .detailAddress(detailAddress)
                .postalCode(postalCode)
                .lat(lat)
                .lng(lng)
                .build();
    }

    public void update(
            String sido,
            String sigungu,
            String eupMyeonDong,
            String roadAddress,
            String jibunAddress,
            String detailAddress,
            String postalCode,
            BigDecimal lat,
            BigDecimal lng
    ) {
        if (sido != null) {
            this.sido = sido;
        }
        if (sigungu != null) {
            this.sigungu = sigungu;
        }
        if (eupMyeonDong != null) {
            this.eupMyeonDong = eupMyeonDong;
        }
        if (roadAddress != null) {
            this.roadAddress = roadAddress;
        }
        if (jibunAddress != null) {
            this.jibunAddress = jibunAddress;
        }
        if (detailAddress != null) {
            this.detailAddress = detailAddress;
        }
        if (postalCode != null) {
            this.postalCode = postalCode;
        }
        if (lat != null) {
            this.lat = lat;
        }
        if (lng != null) {
            this.lng = lng;
        }
    }
}
