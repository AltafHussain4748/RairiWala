package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Product {
    private Category category;
    private int product_id;
    private String product_name;
    private String product_image;
    private String product_type;
    private ProductDetails productDetails;

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public Category getCategory() {
        return category;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProductDetails(ProductDetails productDetails) {
        this.productDetails = productDetails;
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }
}
