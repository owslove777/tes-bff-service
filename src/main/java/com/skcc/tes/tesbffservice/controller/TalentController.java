package com.skcc.tes.tesbffservice.controller;

import com.skcc.tes.tesbffservice.vo.TalentCategoryDto;
import com.skcc.tes.tesbffservice.vo.TalentDetailDto;
import com.skcc.tes.tesbffservice.vo.TalentDto;
import com.skcc.tes.tesbffservice.vo.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS})
@RequiredArgsConstructor
@Slf4j
public class TalentController {

    private final RestTemplate restTemplate;

    @Value("${tes.url.talent}")
    private String talentServiceUrl;

    @Value("${tes.url.user}")
    private String userServiceUrl;

    @GetMapping("/talent/category")
    public List<TalentCategoryDto> getAllCategory() {
        List<TalentCategoryDto> list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/category"), List.class);
        return list;
    }

    @GetMapping("/talents")
    public List<TalentDto> allMappings(){
        List<TalentDto> list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents"), List.class);
        return list;
    }

    @GetMapping("/talents/{id}")
    public TalentDto findById(@PathVariable Long id) {
        TalentDto dto = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/"+id), TalentDto.class);
        return dto;
    }

    @GetMapping("/talents/detail/{id}")
    public TalentDto findByIdWithOptions(@PathVariable Long id){
        TalentDto dto = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/detail/"+id), TalentDto.class);
        return dto;
    }

    @GetMapping("/talents/category/{id}")
    public List<TalentDto> findByCategoryId(@PathVariable Long id,
                                            @RequestParam(required = false) String address){
        Map<String, Object> payload = new HashMap<>();
        if (address != null) {
            payload.put("address", address);
        }
        List<TalentDto> list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/category/"+id), List.class, payload);
        return list;
    }

    @GetMapping("/talents/user/{id}")
    public List<Map<String, Object>> findByUserId(@PathVariable Long id){
        // 재능인 ID, Category Name
        List<Map<String, Object>> list = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/user/"+id), List.class);

        Map<Long, Map<String, Object>> categoryMap = new HashMap<>();
        Map<Long, String> nameMap = new HashMap<>();
        for(Map dto: list) {
            long categoryId =  (long)dto.get("categoryId");
            long userId = (long)dto.get("userId");
            if (!categoryMap.containsKey(categoryId)) {
                Map<String, Object> category = restTemplate.getForObject(String.format("%s%s", talentServiceUrl, "/talents/category/" + categoryId), Map.class);
                categoryMap.put(categoryId, category);
            }
            dto.put("categoryName", categoryMap.get(categoryId).get("categoryName").toString());

            if (!nameMap.containsKey(userId)) {
                Map<String, Object> user =  restTemplate.getForObject(String.format("%s%s", userServiceUrl, "/user/" + userId), Map.class);
                nameMap.put(userId, user.get("name").toString());
            }
            dto.put("userName", nameMap.get(userId));
        }

        return list;
    }
}
