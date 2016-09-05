package com.lingyun.entity;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    @Transient
    private double totalPrice;
    @Transient
    private int totalAmount;
    private List<ProductSelected> productSelectedList;
    public List<ProductSelected> getProductSelectedList() {
        return productSelectedList;
    }
    public void setProductSelectedList(List<ProductSelected> productSelectedList) {
        this.productSelectedList = productSelectedList;
    }
    public void merge(ProductSelected anotherProductSelected){
        List<ProductSelected> productSelectedList=this.getProductSelectedList();
        if (productSelectedList==null){
            productSelectedList=new ArrayList<ProductSelected>();
            productSelectedList.add(anotherProductSelected);
            this.setProductSelectedList(productSelectedList);
            return;
        }
        for (ProductSelected thisProductSelected : this.getProductSelectedList()){
            if (thisProductSelected.equals(anotherProductSelected)){
                int amount=thisProductSelected.getAmount()==null?0:thisProductSelected.getAmount();
                thisProductSelected.setAmount(amount+anotherProductSelected.getAmount());
                return;
            }
        }
        productSelectedList.add(anotherProductSelected);
//        setProductSelectedList(productSelectedList);
    }
    public void merge(Cart anotherCart){
        if (anotherCart==null) return;
        if (anotherCart.getProductSelectedList()==null) return;
        List<ProductSelected> thisProductSelectedList=this.getProductSelectedList();
        if (thisProductSelectedList==null){
            this.setProductSelectedList(anotherCart.getProductSelectedList());
            return;
        }
        outer:for (ProductSelected anotherProductSelected:anotherCart.getProductSelectedList()){
            inner:for (ProductSelected thisProductSelected : this.getProductSelectedList()){
                if (thisProductSelected.equals(anotherProductSelected)){
                    thisProductSelected.setAmount(thisProductSelected.getAmount()+anotherProductSelected.getAmount());
                    break outer;
                }
            }
            thisProductSelectedList.add(anotherProductSelected);
//            setProductSelectedList(thisProductSelectedList);
        }

    }

    public double getTotalPrice() {
        if (productSelectedList==null) return 0d;
        double totalPrice=0d;
        for (ProductSelected productSelected:productSelectedList){
            Assert.notNull(productSelected.getProductSeries());
            if (productSelected.getProductSeries().getCurrentPrice()==null)
                totalPrice+=0;
            else
                totalPrice+=productSelected.getAmount()*productSelected.getProductSeries().getCurrentPrice().getPrice();
        }
        return totalPrice;
    }

    public int getTotalAmount() {
        if (productSelectedList==null) return 0;
        int totalAmount=0;
        for (ProductSelected productSelected:productSelectedList){
            totalAmount+=productSelected.getAmount();
        }
        return totalAmount;
    }
}
