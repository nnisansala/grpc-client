package com.org.archi.grpc.services.shoppingcart.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShoppingCart {
    List<BuyingProduct> productList;
}
