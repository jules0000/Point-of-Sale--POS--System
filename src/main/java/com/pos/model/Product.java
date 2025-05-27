package com.pos.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a product in the system
 */
public class Product extends BaseModel {
    private String id;
    private String name;
    private String description;
    private String barcode;
    private String category;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int currentStock;
    private int lowStockThreshold;
    private int quantity;
    private int reorderLevel;
    private boolean active;
    private String imagePath;
    private byte[] image;

    public Product() {
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BigDecimal getProfit() {
        return sellingPrice.subtract(costPrice);
    }

    public BigDecimal getProfitMargin() {
        if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfit().divide(costPrice, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public boolean isLowOnStock() {
        return isLowStock();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", barcode='" + barcode + '\'' +
                ", category='" + category + '\'' +
                ", costPrice=" + costPrice +
                ", sellingPrice=" + sellingPrice +
                ", currentStock=" + currentStock +
                ", lowStockThreshold=" + lowStockThreshold +
                ", quantity=" + quantity +
                ", reorderLevel=" + reorderLevel +
                ", active=" + active +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
} 