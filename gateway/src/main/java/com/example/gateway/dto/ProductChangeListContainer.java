package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductChangeListContainer {
    private ArrayList<ProductChangeDTO> list;
    private boolean increase;
}
