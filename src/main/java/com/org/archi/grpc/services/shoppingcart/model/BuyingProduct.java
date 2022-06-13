package com.org.archi.grpc.services.shoppingcart.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyingProduct {

    String productCode;

    String productName;

    Double unitPrice;

    int noOfItems;
}
