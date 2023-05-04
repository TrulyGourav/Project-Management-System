package com.asite.aprojecto.authentication.dbseeder;

import com.asite.aprojecto.authentication.enums.Permissions;
import com.asite.aprojecto.authentication.enums.Roles;
import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.models.RoleModel;
import com.asite.aprojecto.authentication.models.UserModel;
import com.asite.aprojecto.authentication.repositories.PermissionRepository;
import com.asite.aprojecto.authentication.repositories.RoleRepository;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class Seeder {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PermissionRepository permissionRepository;

    @EventListener
    public void seedData(ApplicationReadyEvent event) {
        UserModel admin= UserModel.builder().
                        email("adminAProjectO@asite.com").
                        firstName("Prakash")
                        .lastName("Udhav")
                        .mobileNumber("7047595")
                        .designationTitle("Admin")
                        .role(Roles.ADMIN.name())
                        .profilePicturePath("/path")
                        .password(passwordEncoder.encode("Admin@123"))
                        .build();

        UserModel admin2= UserModel.builder().
                email("ppritam@asite.com").
                firstName("Pritam")
                .lastName("Prakash")
                .mobileNumber("7069096437")
                .designationTitle("Admin")
                .role(Roles.ADMIN.name())
                .profilePicturePath("/path")
                .password(passwordEncoder.encode("Admin@123"))
                .build();

        UserModel admin3= UserModel.builder().
                email("sramavat@asite.com").
                firstName("Shubham")
                .lastName("Ramavat")
                .mobileNumber("7069096437")
                .designationTitle("Admin")
                .role(Roles.ADMIN.name())
                .profilePicturePath("/path")
                .password(passwordEncoder.encode("Admin@123"))
                .build();


        PermissionModel manageRoles = PermissionModel.builder().permission_name(Permissions.MANAGE_ROLES.name()).build();
        PermissionModel manageUsers = PermissionModel.builder().permission_name(Permissions.MANAGE_USERS.name()).build();
        PermissionModel manageProjects = PermissionModel.builder().permission_name(Permissions.MANAGE_PROJECTS.name()).build();
        PermissionModel manageTasks = PermissionModel.builder().permission_name(Permissions.MANAGE_TASKS.name()).build();
        PermissionModel manageSubTasks = PermissionModel.builder().permission_name(Permissions.MANAGE_SUB_TASKS.name()).build();
        PermissionModel canViewReports = PermissionModel.builder().permission_name(Permissions.CAN_VIEW_REPORTS.name()).build();
        PermissionModel canCreateProject = PermissionModel.builder().permission_name(Permissions.CAN_CREATE_PROJECT.name()).build();
        PermissionModel canViewAllProject = PermissionModel.builder().permission_name(Permissions.CAN_VIEW_ALL_PROJECT.name()).build();

        RoleModel roleAdmin = RoleModel.builder().roleName(Roles.ADMIN.name()).permissions(new HashSet<>(Arrays.asList(manageProjects,manageRoles,manageUsers,manageSubTasks,manageTasks,canCreateProject,canViewAllProject,canViewReports))).build();
        RoleModel roleProjectAdmin = RoleModel.builder().roleName(Roles.PROJECT_MANAGER.name()).permissions(new HashSet<>(Arrays.asList(manageProjects,manageSubTasks,manageTasks))).build();
        RoleModel roleUser = RoleModel.builder().roleName(Roles.USER.name()).build();

        Optional<UserModel> optionalAdmin=Optional.ofNullable(userRepository.findByEmail(admin.getEmail()));
        Optional<UserModel> optionalAdmin2=Optional.ofNullable(userRepository.findByEmail(admin2.getEmail()));
        Optional<UserModel> optionalAdmin3=Optional.ofNullable(userRepository.findByEmail(admin3.getEmail()));

        if (!optionalAdmin.isPresent()) {
            userRepository.save(admin);
        }
        if (!optionalAdmin2.isPresent()) {
            userRepository.save(admin2);
        }
        if (!optionalAdmin3.isPresent()) {
            userRepository.save(admin3);
        }

        List<PermissionModel> permissionsToSave = Arrays.asList(manageTasks,manageProjects,manageRoles,manageUsers,manageSubTasks,canViewReports,canCreateProject,canViewAllProject);
        List<RoleModel> rolesToSave = Arrays.asList(roleUser,roleAdmin,roleProjectAdmin);

        if (permissionRepository.count() < 8 || roleRepository.count() < 3){
            permissionRepository.saveAll(permissionsToSave);
            roleRepository.saveAll(rolesToSave);
        }
    }
}
