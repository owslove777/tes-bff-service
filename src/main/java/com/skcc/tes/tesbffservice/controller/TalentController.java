package com.skcc.tes.tesbffservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skcc.tes.tesbffservice.vo.TalentCategoryDto;
import com.skcc.tes.tesbffservice.vo.TalentDetailDto;
import com.skcc.tes.tesbffservice.vo.TalentDto;
import com.skcc.tes.tesbffservice.vo.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS})
@RequiredArgsConstructor
@Slf4j
public class TalentController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${tes.url.talent}")
    private String talentServiceUrl;

    @Value("${tes.url.user}")
    private String userServiceUrl;

    @GetMapping("/talents/category")
    public List<TalentCategoryDto> getAllCategory() {
        List<TalentCategoryDto> list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/category"), List.class);
        return list;
    }

    @GetMapping("/talents")
    public List<TalentDetailDto> allMappings(){
        List list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents"), List.class);
        return addDetailInfoToList(list);
    }

    @GetMapping("/talents/{id}")
    public TalentDto findById(@PathVariable Long id) {
        TalentDto dto = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/"+id), TalentDto.class);
        return dto;
    }

    @GetMapping("/talents/detail/{id}")
    public TalentDetailDto findByIdWithOptions(@PathVariable Long id){
        TalentDetailDto dto = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/detail/"+id), TalentDetailDto.class);
        addDetailInfoToDto(dto);
        return dto;
    }

    @GetMapping("/talents/category/{id}")
    public List<TalentDetailDto> findByCategoryId(@PathVariable Long id,
                                            @RequestParam(required = false) String address){
        String reqUrl = String.format("%s%s", talentServiceUrl, "/talents/category/"+id);
//        Map<String, Object> payload = new HashMap<>();
        if (address != null && address.length() > 0) {
            reqUrl += "?address="+address;
        }
        List list = restTemplate.getForObject(reqUrl, List.class);
        return addDetailInfoToList(list);
    }

    @GetMapping("/talents/user/{id}")
    public List<TalentDetailDto> findByUserId(@PathVariable Long id){
        // 재능인 ID, Category Name
        List list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/user/"+id), List.class);
//        List<TalentDetailDto> talentList = convertList(list, TalentDetailDto.class);
//
//        Map<Long, TalentCategoryDto> categoryMap = new HashMap<>();
//        Map<Long, String> nameMap = new HashMap<>();
//        for(TalentDetailDto dto: talentList) {
//            long categoryId =  dto.getCategoryId();
//            long userId = dto.getUserId();
//            if (!categoryMap.containsKey(categoryId)) {
//                List categoryList = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/category/" + categoryId), List.class);
//                TalentCategoryDto category = convertList(categoryList, TalentCategoryDto.class).get(0);
//                categoryMap.put(categoryId, category);
//            }
//            dto.setCategoryName(categoryMap.get(categoryId).getCategoryName());
//
//            if (!nameMap.containsKey(userId)) {
//                UserDto user =  restTemplate.getForObject(String.format("%s%s", userServiceUrl, "/users/" + userId), UserDto.class);
//                nameMap.put(userId, user.getName());
//            }
//            dto.setUserName(nameMap.get(userId));
//        }


        return addDetailInfoToList(list);
    }

    private <T> List<T> convertList(List list, Class<T> clazz){
        List<T> ret = new ArrayList<>();
        list.forEach(s -> {
            T dto = objectMapper.convertValue(s, clazz);
            ret.add(dto);
        });
        return ret;
    }
    private List<TalentDetailDto> addDetailInfoToList(List list) {
        List<TalentDetailDto> talentList = convertList(list, TalentDetailDto.class);

        Map<Long, TalentCategoryDto> categoryMap = new HashMap<>();
        Map<Long, String> nameMap = new HashMap<>();
        for(TalentDetailDto dto: talentList) {
            addDetailInfoToDto(categoryMap, nameMap, dto);
        }
        return talentList;
    }

    private TalentDetailDto addDetailInfoToDto(Map<Long, TalentCategoryDto> categoryMap, Map<Long, String> nameMap, TalentDetailDto dto) {
        long categoryId =  dto.getCategoryId();
        long userId = dto.getUserId();
        if (!categoryMap.containsKey(categoryId)) {
            TalentCategoryDto category = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/category/" + categoryId), TalentCategoryDto.class);
            categoryMap.put(categoryId, category);
        }
        dto.setCategoryName(categoryMap.get(categoryId).getCategoryName());

        if (!nameMap.containsKey(userId)) {
            UserDto user =  restTemplate.getForObject(String.format("%s%s", userServiceUrl, "/users/" + userId), UserDto.class);
            nameMap.put(userId, user.getName());
        }
        dto.setUserName(nameMap.get(userId));
        return dto;
    }

    private TalentDetailDto addDetailInfoToDto(TalentDetailDto dto) {
        long categoryId =  dto.getCategoryId();
        long userId = dto.getUserId();
        TalentCategoryDto category = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/category/" + categoryId), TalentCategoryDto.class);
        dto.setCategoryName(category.getCategoryName());
        UserDto user =  restTemplate.getForObject(String.format("%s%s", userServiceUrl, "/users/" + userId), UserDto.class);
        dto.setUserName(user.getName());
        return dto;
    }
}
