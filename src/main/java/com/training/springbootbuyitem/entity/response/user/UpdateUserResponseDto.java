package com.training.springbootbuyitem.entity.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponseDto extends CreateUserResponseDto {
    private String name;
}
