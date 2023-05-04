package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.dto.ProjectUserDTO;
import com.asite.aprojecto.authentication.dto.UserRoleDTO;
import com.asite.aprojecto.authentication.exceptions.ResourceNotFoundException;
import com.asite.aprojecto.authentication.exceptions.UserAlreadyExistsException;
import com.asite.aprojecto.authentication.models.UserRoleProjectModel;
import com.asite.aprojecto.authentication.repositories.ProjectUserRoleRepository;
import com.asite.aprojecto.authentication.repositories.RoleRepository;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProjectService {
    @Autowired
    private ProjectUserRoleRepository projectUserRoleRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public boolean addUsersToProject(Long projectId, ProjectUserDTO projectUserDTO) throws Exception {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM project_tx_pdetails_tbl WHERE project_id = :projectId");
        query.setParameter("projectId", projectId);

        int count = ((Number) query.getSingleResult()).intValue();
        if (count < 1) {
            throw new ResourceNotFoundException("Project Not found");
        }

        List<UserRoleDTO> userRoleDTOs = projectUserDTO.getUsers();
        List<UserRoleProjectModel> projectUserRoles = new ArrayList<>();
        Set<Long> existingUserIds = new HashSet<>();

        for (UserRoleDTO userRoleDTO : userRoleDTOs) {
            UserRoleProjectModel projectUserRole = new UserRoleProjectModel();
            projectUserRole.setProjectId(projectId);

            if (existingUserIds.contains(userRoleDTO.getUserId())){
                throw new UserAlreadyExistsException("User Exists in Project");
            }
            else if (projectUserRoleRepository.findByUidAndProjectId(userRoleDTO.getUserId(),projectId).size()>0) {
                // If the user already exists in the project, update their role ID
                List<UserRoleProjectModel> userRoles = projectUserRoleRepository.findByUidAndProjectId(userRoleDTO.getUserId(), projectId);
                if (userRoles.size() > 1) {
                    throw new Exception("Multiple user roles found for user ID " + userRoleDTO.getUserId() + " in project " + projectId);
                }
                UserRoleProjectModel userRole = userRoles.get(0);
                if (checkForRole(userRoleDTO)){
                    userRole.setRoleId(userRoleDTO.getRoleId());
                    userRole.setDeleted(false);
                    projectUserRole = userRole;
                }
            }else if (userRepository.existsById(userRoleDTO.getUserId())){
                projectUserRole.setUid(userRoleDTO.getUserId());
                if (checkForRole(userRoleDTO)){
                    projectUserRole.setRoleId(userRoleDTO.getRoleId());
                }
                existingUserIds.add(userRoleDTO.getUserId());
            }else {
                throw new ResourceNotFoundException("User does not exist, ID: "+userRoleDTO.getUserId());
            }

            projectUserRoles.add(projectUserRole);
        }

        Optional<List<UserRoleProjectModel>> userRoleProjectModels=Optional.of(projectUserRoleRepository.saveAll(projectUserRoles));
        return userRoleProjectModels.get().size() == projectUserRoles.size();
    }
    private boolean checkForRole(UserRoleDTO userRoleDTO) throws Exception {
        if (userRoleDTO.getRoleId()==2){
            throw new Exception("Cannot set Admin role to Project:");
        }else if (roleRepository.existsById(userRoleDTO.getRoleId())){
            return true;
        }else {
            throw new ResourceNotFoundException("Role does not exist, ID: "+userRoleDTO.getRoleId());
        }
    }

    public JSONArray getUsersToProject(Long projectId) {
        List<Object[]> results = entityManager.createQuery(
                        "SELECT u.uid, u.first_Name, r.roleName, r.roleId " +
                                "FROM UserModel u " +
                                "JOIN UserRoleProjectModel ur ON u.uid = ur.uid " +
                                "JOIN RoleModel r ON ur.roleId = r.roleId " +
                                "WHERE ur.projectId = :projectId AND u.isDeleted = false AND ur.isDeleted = false AND r.roleName != 'ADMIN'",
                        Object[].class)
                .setParameter("projectId", projectId)
                .getResultList();

        JSONArray jsonArray = new JSONArray();
        for (Object[] result : results) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid", result[0]);
            jsonObject.put("username", result[1]);
            jsonObject.put("roleName", result[2]);
            jsonObject.put("roleId", result[3]);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Transactional
    public String softDeleteUserRoleProject(Long projectId, Long uid) {
        String jpql = "UPDATE UserRoleProjectModel u SET u.isDeleted = true WHERE u.projectId = :projectId AND u.uid = :uid";
        entityManager.createQuery(jpql)
                .setParameter("projectId", projectId)
                .setParameter("uid", uid)
                .executeUpdate();
        return "Deleted";
    }
}
