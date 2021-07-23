package com.training.springbootbuyitem.service;

import com.training.springbootbuyitem.entity.model.Purchase;
import com.training.springbootbuyitem.entity.request.CreatePurchaseRequestDto;
import com.training.springbootbuyitem.entity.response.CreatePurchaseResponseDto;

public interface IPurchaseService extends ICrudService<Purchase>{
    public void cancelPurchase(Long id);

    public CreatePurchaseResponseDto startPurchaseTransaction(CreatePurchaseRequestDto purchaseDetails);

    public CreatePurchaseResponseDto getWithItemAndUser(Long id);
}
