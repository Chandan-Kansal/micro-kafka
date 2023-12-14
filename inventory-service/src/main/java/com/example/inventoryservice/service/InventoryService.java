package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public Boolean isInStock(List<String> skuCode,List<Integer> quantity){
        boolean flag = true;
        List<InventoryResponse> list = new ArrayList<>();
        for(int i=0;i<skuCode.size();i++){
            Optional<Inventory> optional = Optional.ofNullable(inventoryRepository.findBySkuCode(skuCode.get(i)));
            if(optional.isEmpty())
                flag = false;
            else{
                if(quantity.get(i)>optional.get().getQuantity()){
                    flag = false;
                }
            }
        }
        Inventory inventory = new Inventory();
        if(flag==true){
            for(int i=0;i<skuCode.size();i++){
                inventory.setId(inventoryRepository.findBySkuCode(skuCode.get(i)).getId());
                inventory.setSkuCode(inventoryRepository.findBySkuCode(skuCode.get(i)).getSkuCode());
                inventory.setQuantity(inventoryRepository.findBySkuCode(skuCode.get(i)).getQuantity()-quantity.get(i));
                inventoryRepository.saveAndFlush(inventory);
            }
        }
        return flag;

    }

    public List<Inventory> getAllProd() {
        return inventoryRepository.findAll();
    }
}
