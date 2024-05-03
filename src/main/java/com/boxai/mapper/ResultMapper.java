package com.boxai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxai.model.domain.Result;

/**
* @author Hzh
* @description 针对表【result(数据信息表)】的数据库操作Mapper
* @createDate 2024-04-23 18:41:23
* @Entity com.boxai.model.domain.Result
*/
public interface ResultMapper extends BaseMapper<Result> {

//    更新生成名称
    int updateGenName(Long id, String genName);
    // 更新代码注释
    int updateCodeComment(Long id, String codeComment);
//    更新代码简介
    int updateCodeProfile(Long id, String codeProfile);
//    更新codeAPI
    int updateCodeAPI(Long id, String codeAPI);
//    更新codeRun
    int updateCodeRun(Long id, String codeRun);
//    更新codeSuggestion
    int updateCodeSuggestion(Long id, String codeSuggestion);
//    更新codeNormStr
    int updateCodeNormStr(Long id, String codeNormStr);
//    更新codeCataloguePath
    int updateCodeCataloguePath(Long id, String codeCataloguePath);
}




