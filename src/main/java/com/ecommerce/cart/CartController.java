package com.ecommerce.cart;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import com.ecommerce.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    Validator orderValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(orderValidator);
    }

    @Track
    @PostMapping("/")
    public ResponseEntity<ResponseWithStatus> create() {
        String out = cartService.createNewCart();
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping("/{id}")
    public ResponseEntity addProduct(@PathVariable("id") String cartId, @RequestBody CartItem cartItem) {
        cartService.addProduct(cartId, cartItem);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }

    @Track
    @GetMapping("/{id}")
    public ResponseEntity getCartItems(@PathVariable("id") String cartId) {
        Set<CartItem> out = cartService.getItems(cartId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @DeleteMapping("{id}/{product_id}")
    public ResponseEntity<ResponseWithStatus> removeItem(@PathVariable("id") String cartId, @PathVariable("product_id") String productId) {
        cartService.removeProduct(cartId, productId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }

    @Track
    @PostMapping("{id}/quantity")
    public ResponseEntity<ResponseWithStatus> setProductQuantity(@PathVariable("id") String cartId, @RequestBody CartItem cartItem) {
        String productId = Long.toString(cartItem.getProductId());
        cartService.setProductQuantity(cartId, productId, cartItem.getQuantity());
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }

    @Track
    @PostMapping("{id}/order")
    public ResponseEntity<ResponseWithStatus> createOrder(@PathVariable("id") String cartId, @RequestBody @Valid Order order) {
        if (order == null) {
            System.out.println("Order not in POST");
            return null;
        }
        Order out = cartService.createOrder(cartId, order);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }
}
