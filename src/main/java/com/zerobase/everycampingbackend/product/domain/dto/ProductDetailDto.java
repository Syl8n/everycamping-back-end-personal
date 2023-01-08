package com.zerobase.everycampingbackend.product.domain.dto;

import com.zerobase.everycampingbackend.product.domain.entity.Product;
import com.zerobase.everycampingbackend.product.type.ProductCategory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDto {

    private Long id;
    private String sellerName;
    private ProductCategory category;
    private String name;
    private String description;
    private int stock;
    private int price;
    private String imagePath;
    private List<String> tags;
    private boolean onSale;
    private int reviewCount;
    private double avgScore;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductDetailDto from(Product product) {
        return ProductDetailDto.builder()
            .id(product.getId())
            .sellerName(product.getSeller().getNickName())
            .category(product.getCategory())
            .name(product.getName())
            .description(product.getDescription())
            .stock(product.getStock())
            .price(product.getPrice())
            .imagePath(product.getDetailImagePath())
            .tags(product.getTags())
            .onSale(product.isOnSale())
            .reviewCount(product.getReviewCount())
            .avgScore(product.getReviewCount() == 0 ? 0
                : product.getTotalScore() / (double) product.getReviewCount())
            .createdAt(product.getCreatedAt())
            .modifiedAt(product.getModifiedAt())
            .build();
    }
}
