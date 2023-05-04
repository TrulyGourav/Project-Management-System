package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.constant.ExceptionHandlingConstants;
import com.asite.aprojecto.authentication.dto.RoleDTO;
import com.asite.aprojecto.authentication.exceptions.RoleAlreadyExistsException;
import com.asite.aprojecto.authentication.models.RoleModel;
import com.asite.aprojecto.authentication.repositories.RoleRepository;
import com.asite.aprojecto.authentication.util.MappingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Optional<List<RoleDTO>> getAllRole(){
        List<RoleDTO> roleDTOS=MappingHelper.getRoleDtoListFromRoleModelList(roleRepository.findAll());
        return Optional.of(roleDTOS);
    }

    public RoleDTO addRole(RoleDTO roleDTO) throws RoleAlreadyExistsException{
        if (roleRepository.findByRoleName(roleDTO.getRoleName())!=null){
            throw new RoleAlreadyExistsException(ExceptionHandlingConstants.ROLE_EXISTS);
        }
        return MappingHelper.getRoleDtoFromRoleModel(roleRepository.save(MappingHelper.getRoleModelFromRoleDto(roleDTO)));
    }

    public RoleDTO getRole(Long role_id) throws NoSuchElementException{
        Optional<RoleModel> roleModel = roleRepository.findById(role_id);
        if (!roleModel.isPresent()){
            throw new NoSuchElementException(ExceptionHandlingConstants.ROLE_NOT_PRESENT);
        }
        return MappingHelper.getRoleDtoFromRoleModel(roleModel.get());
    }

    public Optional<RoleDTO> getPermissions(String role_name) {
        return Optional.of(MappingHelper.getRoleDtoFromRoleModel(roleRepository.findByRoleName(role_name)));
    }

    public RoleDTO updateRole(RoleDTO roleDTO) throws RoleNotFoundException {
        System.out.println(roleDTO.getRoleId());
        System.out.println(roleDTO.getRoleName());
        Optional<RoleModel> role=roleRepository.findById(roleDTO.getRoleId());
        if (role.isPresent()){
            return MappingHelper.getRoleDtoFromRoleModel(roleRepository.save(MappingHelper.
                    addRoleModelPropertiesFromRoleDto(roleDTO,role.get())));
        }else {
            throw new RoleNotFoundException(ExceptionHandlingConstants.ROLE_NOT_FOUND);
        }
    }
}
