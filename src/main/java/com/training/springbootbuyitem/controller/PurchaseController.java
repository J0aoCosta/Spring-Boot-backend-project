package com.training.springbootbuyitem.controller;

import com.training.springbootbuyitem.constant.ItemStorageConstant;
import com.training.springbootbuyitem.entity.request.CreatePurchaseRequestDto;
import com.training.springbootbuyitem.entity.response.CreatePurchaseResponseDto;
import com.training.springbootbuyitem.enums.EnumOperation;
import com.training.springbootbuyitem.service.IPurchaseService;
import com.training.springbootbuyitem.service.ItemService;
import com.training.springbootbuyitem.utils.annotation.ServiceOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RefreshScope
@RestController
@RequestMapping("purchase")
@CrossOrigin
@Slf4j
public class PurchaseController {

    @Autowired
    private IPurchaseService purchaseService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ModelMapper mapper;

    // TODO
    @GetMapping("/{id}")
    @ServiceOperation("getPurchase")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatePurchaseResponseDto> getPurchase(@PathVariable("id") Long id){
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.GetPurchase.name());
        return new ResponseEntity<>(mapper.map(purchaseService.getWithItemAndUser(id), CreatePurchaseResponseDto.class), HttpStatus.OK);
    }

    @PostMapping("")
    @ServiceOperation("createPurchase")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CreatePurchaseResponseDto> createPurchase(@RequestBody() @Valid CreatePurchaseRequestDto purchase) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.CreatePurchase.name());
        return new ResponseEntity<>(mapper.map(purchaseService.startPurchaseTransaction(purchase), CreatePurchaseResponseDto.class), HttpStatus.OK);
    }
//
//    @DeleteMapping("/confirm")
//    @ServiceOperation("confirmPurchase")
//    public ResponseEntity<HttpStatus> commitPurchase(@PathVariable("id") Long id) {
//        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.RestockItem.name());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @PostMapping("/{id}/cancel")
    @ServiceOperation("cancelPurchase")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<HttpStatus> cancelPurchase(@PathVariable("id") Long id) {
        purchaseService.cancelPurchase(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
