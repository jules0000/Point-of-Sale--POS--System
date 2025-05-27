package com.pos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the system.
 */
public class Sale extends BaseModel {
    public enum PaymentMethod {
        CASH,
        CARD,
        OTHER
    }

    private String saleNumber;
    private LocalDateTime saleDate;
    private Long userId;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private String notes;
    private List<SaleItem> items;

    public Sale() {
        super();
        this.saleDate = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.changeAmount = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
        this.changeAmount = amountPaid.subtract(totalAmount);
    }

    public BigDecimal getChange() {
        return changeAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    /**
     * Adds an item to the sale and recalculates totals.
     * @param item The sale item to add
     */
    public void addItem(SaleItem item) {
        items.add(item);
        recalculateTotals();
    }

    /**
     * Removes an item from the sale and recalculates totals.
     * @param item The sale item to remove
     */
    public void removeItem(SaleItem item) {
        items.remove(item);
        recalculateTotals();
    }

    /**
     * Recalculates the sale totals based on items.
     */
    public void recalculateTotals() {
        this.subtotal = items.stream()
                .map(SaleItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = this.subtotal
                .subtract(this.discountAmount)
                .add(this.taxAmount);
    }

    public BigDecimal getDiscount() {
        return discountAmount;
    }

    public void setDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public BigDecimal getTotal() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return String.format("Sale{id=%d, saleNumber='%s', saleDate=%s, customerName='%s', " +
                        "subtotal=%s, discountAmount=%s, taxAmount=%s, totalAmount=%s, paymentMethod=%s}",
                getId(), saleNumber, saleDate, customerName,
                subtotal, discountAmount, taxAmount, totalAmount, paymentMethod);
    }
} 