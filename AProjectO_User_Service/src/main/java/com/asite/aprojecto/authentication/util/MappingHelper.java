package com.asite.aprojecto.authentication.util;

import com.asite.aprojecto.authentication.dto.ResponseUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.RoleDTO;
import com.asite.aprojecto.authentication.dto.UserUpdateDetailsDTO;
import com.asite.aprojecto.authentication.models.RoleModel;
import com.asite.aprojecto.authentication.models.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingHelper {
    private static final ModelMapper modelMapper = new ModelMapper();

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        return Stream.of(pds)
                .map(PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    public static UserUpdateDetailsDTO getUserDetailsDto(UserModel userModel)
    {
        UserUpdateDetailsDTO userUpdateDetailsDto = new UserUpdateDetailsDTO();
        BeanUtils.copyProperties(userModel, userUpdateDetailsDto);
        return userUpdateDetailsDto;
    }

    public static UserModel getUserModelByUserUpdateDetailsDto(UserModel userModel, UserUpdateDetailsDTO userUpdateDetailsDto)
    {
        BeanUtils.copyProperties(userUpdateDetailsDto,userModel,getNullPropertyNames(userUpdateDetailsDto));
        return userModel;
    }

    public static ResponseUserDetailsDTO getResponseUserDetailsDtoFromUserModel(UserModel userModel)
    {
        ResponseUserDetailsDTO dto = new ResponseUserDetailsDTO();
        BeanUtils.copyProperties(userModel,dto);
        System.out.println("Country - "+dto.getCountry());
        return dto;
    }

    public static List<ResponseUserDetailsDTO> getResponseUserDetailsDtoListFromUserModelList(List<UserModel> userModels) {
        List<ResponseUserDetailsDTO> dtoList = new ArrayList<>();
        for (UserModel userModel : userModels) {
            ResponseUserDetailsDTO dto = new ResponseUserDetailsDTO();
            BeanUtils.copyProperties(userModel, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public static RoleDTO getRoleDtoFromRoleModel(RoleModel roleModel)
    {
        return modelMapper.map(roleModel, RoleDTO.class);
    }

    public static List<RoleDTO> getRoleDtoListFromRoleModelList(List<RoleModel> roleModels) {
        return roleModels.stream()
                .map(roleModel -> modelMapper.map(roleModel, RoleDTO.class))
                .collect(Collectors.toList());
    }

    public static RoleModel getRoleModelFromRoleDto(RoleDTO roleDTO) {
        RoleModel roleModel = new RoleModel();
        String roleName=roleDTO.getRoleName().toUpperCase();
        roleDTO.setRoleName(roleName);
        BeanUtils.copyProperties(roleDTO,roleModel,getNullPropertyNames(roleDTO));
        return roleModel;
    }

    public static RoleModel addRoleModelPropertiesFromRoleDto(RoleDTO roleDTO, RoleModel roleModel) {
        BeanUtils.copyProperties(roleDTO,roleModel,getNullPropertyNames(roleDTO));
        return roleModel;
    }
}
