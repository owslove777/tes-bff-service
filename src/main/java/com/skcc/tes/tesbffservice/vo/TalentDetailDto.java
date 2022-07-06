package com.skcc.tes.tesbffservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentDetailDto {

	Long id;
	Long categoryId;
	Long userId;
	String userName;
	String address;
	String title;
	String description;
	String categoryName;

	List<TalentItemDto> options;

}
