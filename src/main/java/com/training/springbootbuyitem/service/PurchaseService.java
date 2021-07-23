package com.training.springbootbuyitem.service;

import com.training.springbootbuyitem.entity.model.Item;
import com.training.springbootbuyitem.entity.model.Purchase;
import com.training.springbootbuyitem.entity.model.User;
import com.training.springbootbuyitem.entity.request.CreatePurchaseRequestDto;
import com.training.springbootbuyitem.entity.response.CreatePurchaseResponseDto;
import com.training.springbootbuyitem.enums.EnumEntity;
import com.training.springbootbuyitem.enums.EnumPurchaseState;
import com.training.springbootbuyitem.error.EntityNotFoundException;
import com.training.springbootbuyitem.error.NotEnoughStockException;
import com.training.springbootbuyitem.repository.PurchaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PurchaseService implements IPurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    public CreatePurchaseResponseDto startPurchaseTransaction(CreatePurchaseRequestDto purchaseDetails) {
        User user = userService.get((long) purchaseDetails.getUserId());
        Item item = itemService.get((long) purchaseDetails.getItemId());

        if (!itemService.hasStockToReserve(item.getItemUid(), purchaseDetails.getQuantity())) {
            throw new NotEnoughStockException(item.getName(), item.getItemUid());
        }

        itemService.block(item.getItemUid(), purchaseDetails.getQuantity());

        Purchase purchase = new Purchase(user, item, Timestamp.from(Instant.now()),
                BigInteger.valueOf(purchaseDetails.getQuantity()), BigInteger.valueOf(0),
                item.getPriceTag().multiply(BigDecimal.valueOf(purchaseDetails.getQuantity())));

        user.setPurchase(purchase);
        userService.update(user);
        save(purchase);
        return CreatePurchaseResponseDto.builder().id(purchase.getId())
                .price(purchase.getPrice())
                .date(purchase.getDate())
                .state(purchase.getState())
                .quantity(purchase.getQuantity())
                .quantityDispatched(purchase.getQuantityDispatched())
                .itemName(item.getName())
                .buyerName(user.getName())
                .build();
    }

    public void cancelPurchase(Long id) {
        Purchase purchase = get(id);
        setCanceledPurchase(purchase);
        save(purchase);
    }

    public void setFinalizedPurchase(Purchase purchase) {
        purchase.setState(EnumPurchaseState.FINALIZED.name());
    }

    public void setCanceledPurchase(Purchase purchase) {
        purchase.setState(EnumPurchaseState.CANCELLED.name());
    }

    public Purchase findOldestPurchaseForItem(Long id) {
        return purchaseRepository.findOldestPurchaseItem(id);
    }

    @Override
    public List<Purchase> list() {
        return null;
    }

    @Override
    public Purchase get(Long id) {
        return purchaseRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(EnumEntity.PURCHASE_TRANSACTION.name(), id));
    }

    @Override
    public CreatePurchaseResponseDto getWithItemAndUser(Long id) {
        Purchase purchase = get(id);
        return CreatePurchaseResponseDto.builder().id(purchase.getId())
                .price(purchase.getPrice())
                .date(purchase.getDate())
                .state(purchase.getState())
                .quantity(purchase.getQuantity())
                .quantityDispatched(purchase.getQuantityDispatched())
                .itemName(purchase.getItem().getName())
                .buyerName(purchase.getUser().getName())
                .build();
    }

    @Override
    public List<Purchase> get(List<Long> id) {
        return null;
    }

    @Override
    public List<Purchase> update(List<Purchase> id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Purchase update(Purchase entity) {
        return null;
    }

    @Override
    public Purchase save(Purchase entity) {
        return purchaseRepository.save(entity);
    }

}
