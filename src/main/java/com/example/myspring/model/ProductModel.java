package com.example.myspring.model;

import lombok.Data;

@Data
public class ProductModel {
    int id;
    String photoUrl;
    String title;
    String description;
    int price;
    String storeUrl;
    String storeName;
}
