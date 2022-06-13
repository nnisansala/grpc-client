package com.org.archi.grpc.services.shoppingcart.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalBill {

    Double priceForItems;

    Double tax;

    Double totalBill;
}
