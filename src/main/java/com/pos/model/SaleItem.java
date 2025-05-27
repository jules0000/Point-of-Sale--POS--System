package com.pos.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an item in a sale transaction.
 */
public class SaleItem extends BaseModel {
    private Sale sale;
    private Product product;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;

    public SaleItem() {
        super();
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.unitPrice = product.getSellingPrice();
            calculateTotal();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
        calculateTotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        calculateTotal();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateTotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Calculates the total amount for this item based on quantity, unit price, and discount.
     */
    private void calculateTotal() {
        if (unitPrice != null && quantity > 0) {
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            this.totalAmount = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return String.format("SaleItem{id=%d, product=%s, quantity=%d, unitPrice=%s, discountAmount=%s, totalAmount=%s}",
                getId(), product != null ? product.getName() : "null", quantity, unitPrice, discountAmount, totalAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItem saleItem = (SaleItem) o;
        return id == saleItem.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 