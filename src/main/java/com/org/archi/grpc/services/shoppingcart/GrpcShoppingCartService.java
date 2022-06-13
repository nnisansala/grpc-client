package com.org.archi.grpc.services.shoppingcart;

import com.org.archi.billing.grpc.services.BillingDetails;
import com.org.archi.billing.grpc.services.BillingServiceGrpc;
import com.org.archi.billing.grpc.services.Product;
import com.org.archi.grpc.services.shoppingcart.model.BuyingProduct;
import com.org.archi.grpc.services.shoppingcart.model.ShoppingCart;
import com.org.archi.grpc.services.shoppingcart.model.TotalBill;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(GrpcShoppingCartService.class.getName());

    @Autowired
    private Environment env;

    public TotalBill calculate(ShoppingCart shoppingCart) throws InterruptedException {

        TotalBill totalBill = new TotalBill();

        BillingServiceGrpc.BillingServiceStub
                billingServiceStub =
                BillingServiceGrpc
                        .newStub(getChannel(env.getProperty("grpc.server.address")));

        logger.info("=========== Start Processing (Client Side Streaming)==============");
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<BillingDetails> responseObserver = new StreamObserver<BillingDetails>() {

            @Override
            public void onNext(BillingDetails billingDetails) {
                totalBill.setTotalBill(billingDetails.getTotalAmount());
                totalBill.setTax(billingDetails.getTax());
                totalBill.setPriceForItems(billingDetails.getBillingAmount());
                logger.info("RESPONSE, Billing Amount : {}, Tax: {}, Total Amount : {}",
                        billingDetails.getBillingAmount(),
                        billingDetails.getTax(),
                        billingDetails.getTotalAmount());
            }

            @Override
            public void onCompleted() {
                logger.info("Finished clientSideStreamingGetStatisticsOfStocks");
                finishLatch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                logger.warn("Stock Statistics Failed: {}", Status.fromThrowable(t));
                finishLatch.countDown();
            }
        };

        List<BuyingProduct> buyingProductList = shoppingCart.getProductList();

        List<Product> productList = new ArrayList<>();
        for (final BuyingProduct buyingProduct : buyingProductList) {
            Product p1 = Product.newBuilder()
                    .setProductCode(buyingProduct.getProductCode())
                    .setNoOfItems(buyingProduct.getNoOfItems())
                    .setUnitPrice(buyingProduct.getUnitPrice())
                    .build();
            productList.add(p1);
        }

        StreamObserver<Product> requestObserver = billingServiceStub.calculate(responseObserver);
        try {

            for (Product product :  productList) {
                logger.info("REQUEST: {}, {}, {}", product.getProductCode(), product.getUnitPrice(), product.getNoOfItems());
                requestObserver.onNext(product);
                if (finishLatch.getCount() == 0) {
                    return totalBill;
                }
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            logger.warn("can not finish within 1 minutes");
        }
        return totalBill;
    }


    private ManagedChannel getChannel(String address) {
        return ManagedChannelBuilder.forAddress(address.split(":")[0], Integer.parseInt(address.split(":")[1]))
                .usePlaintext()
                .build();
    }



}
