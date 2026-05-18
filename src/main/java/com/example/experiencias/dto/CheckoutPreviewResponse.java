package com.example.experiencias.dto;

import java.util.List;

public record CheckoutPreviewResponse(
    int previewId,
    List<CheckoutItemPreview> items,
    double total
) {}
