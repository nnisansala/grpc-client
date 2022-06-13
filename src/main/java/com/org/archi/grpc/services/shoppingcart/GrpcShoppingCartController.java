// Persistent Systems
//
// All Rights Reserved.
//
// This document or any part thereof may not, without the written
// consent of AePONA Limited, be copied, reprinted or reproduced in
// any material form including but not limited to photocopying,
// transcribing, transmitting or storing it in any medium or
// translating it into any language, in any form or by any means,
// be it electronic, mechanical, xerographic, optical,
// magnetic or otherwise.
//

package com.org.archi.grpc.services.shoppingcart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.org.archi.grpc.services.shoppingcart.model.ShoppingCart;
import com.org.archi.grpc.services.shoppingcart.model.TotalBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class GrpcShoppingCartController {

    private static final Logger logger = LoggerFactory.getLogger(GrpcShoppingCartController.class.getName());

    @Autowired
    GrpcShoppingCartService grpcShoppingCartService;



    @PostMapping("/calculateBill")
    @ResponseBody
    public TotalBill sayHello(@RequestBody ShoppingCart shoppingCart) throws InterruptedException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(shoppingCart);
        logger.info("Request Received => {}", json);
        return grpcShoppingCartService.calculate(shoppingCart);
    }

}
