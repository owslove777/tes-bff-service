package com.skcc.tes.tesbffservice.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id; // 사용자 고유 ID

    private String email;
    private String name;    // 사용자 닉네임
    private String status;
    private String imageUrl;
    private String address; // 주소
    private String userType;
}