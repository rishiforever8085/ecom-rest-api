package com.ecommerce.order;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import com.ecommerce.service.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    Validator orderValidator;
    @Autowired
    private EcommerceService ecommerceService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(orderValidator);
    }

    @Track
    @GetMapping
    public ResponseEntity index() {
        List<Order> out = ecommerceService.getOrders();
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") long id) {
        Order out = ecommerceService.getOrder(id);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Order order) {
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }
        Order out = ecommerceService.saveOrder(order);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }
}
