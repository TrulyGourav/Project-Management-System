package com.asite.apo.service;

import com.asite.apo.dao.IterationRepo;
import com.asite.apo.dto.CreateProjectItrDTO;
import com.asite.apo.dto.ProjectListDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.dao.ProjectRepo;
import com.asite.apo.dto.ProjectModelDTO;
import com.asite.apo.model.ProjectModel;
import com.asite.apo.model.TaskModel;
import com.asite.apo.util.MappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;


@Service
public class ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    IterationRepo iterationRepo;
    @Autowired
    IterationService iterationService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private WorkLogService workLogService;


    /**
     * This method gives the list of all projects that a user is a part of!!
     *
     * @param uid
     * @return
     */
    public ResponseEntity<?> getProjectList(Long uid) throws ParseException {
        LOGGER.debug("Getting Project List",uid);
        //check if user exists!!
        List listOfProjects = entityManager.createNativeQuery( "select project_id from user_lk_project_role_user_tbl where uid = " +uid).getResultList();

        List<ProjectListDTO> projectListDTOList = new ArrayList<>();
        List<ProjectModel> projectModelList = projectRepo.findAllWhereProjectIdIn(listOfProjects);
        projectListDTOList = projectModelToProjectListDTO(projectModelList, projectListDTOList);
        return new ResponseEntity<>(projectListDTOList,HttpStatus.OK);
    }

    /**
     * This method returns the entire detailing of the project
     *
     * @param projectId
     * @return
     */
    public HashMap getProjectDetails(Long projectId) throws ResourceNotFoundException, ParseException {

        LOGGER.debug("Getting Project Details by Project Id",projectId);

        Optional<ProjectModel> optionalProjectModel = projectRepo.findById(projectId);
        if (!optionalProjectModel.isPresent())
            throw new ResourceNotFoundException("Project Id not found");
        else
        {
            ProjectModel projectModel = optionalProjectModel.get();
            HashMap hashMap = new HashMap();
            hashMap.put("projectModel",projectModel);
            List<TaskModel> tasklist = projectModel.getTasks();
            Long openTaskCount, closedTaskCount;
            openTaskCount = tasklist.stream().filter(a -> a.getTaskStatus().equals("Open")).count();
            closedTaskCount = tasklist.stream().filter(a -> a.getTaskStatus().equals("Closed")).count();
            hashMap.put("openTaskCount",openTaskCount);
            hashMap.put("closedTaskCount",closedTaskCount);
            hashMap.put("projectStatus",workLogService.getStatusByProject(projectId));
            hashMap.put("projectReporterName", entityManager.createNativeQuery("Select first_name from user_udetails_tbl where uid = " + projectModel.getProjectReporter()).getFirstResult());
            LOGGER.debug("Found Project Details by Project Id",projectId);
            return hashMap;
        }
    }

    public List<ProjectListDTO> getAllProjects() throws ParseException {
        LOGGER.debug("Getting All Projects");
        List<ProjectListDTO> projectListDTOList = new ArrayList<>();
        List<ProjectModel> projectModelList = projectRepo.findAll();
        projectListDTOList = projectModelToProjectListDTO(projectModelList, projectListDTOList);
        return projectListDTOList;
    }

    public  List<ProjectListDTO> projectModelToProjectListDTO(List<ProjectModel> projectModelList,List<ProjectListDTO> projectListDTOList) throws ParseException {
        for (int i = 0 ; i < projectModelList.size() ; i++ )
        {
            ProjectListDTO projectListDTO = new ProjectListDTO();

            projectListDTO.setNumberOfTasks(projectModelList.get(i).getTasks().size());
            String sql= "select count(*) from user_lk_project_role_user_tbl where project_id = " + projectModelList.get(i).getProjectId();
            Query query = entityManager.createNativeQuery(sql);
            projectListDTO.setNumberOfUsers( ((BigInteger)query.getResultList().get(0)).intValue());

            projectListDTO.setProjectStatus(workLogService.getStatusByProject(projectModelList.get(i).getProjectId()));

            BeanUtils.copyProperties(projectModelList.get(i),projectListDTO);
            projectListDTOList.add(projectListDTO);
        }
        return projectListDTOList;
    }

    /**
     * This method validate the input from the user and then passes it to the repo to save it
     *
     * @param projectModelDTO
     * @return
     */
    @Transactional
    public ProjectModelDTO createProject(ProjectModelDTO projectModelDTO) throws Exception {
        LOGGER.debug("Creating new Project",projectModelDTO);

        if (projectModelDTO.getProjectId()!=null)
            throw new Exception("Id cannot be accepted while creating entity");
        if (projectModelDTO.getStatus()=="Open" || projectModelDTO.getStatus()=="Closed")
            throw new Exception("Cannot send status while creating Project");
        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(projectModelDTO.getStartDate()));
        Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(projectModelDTO.getDeadline()));
        if ( startDate.after(deadline))
            throw new Exception("Deadline must be after Start date");

        ProjectModel projectModel = MappingHelper.projectModelDTOToProjectModel(new ProjectModel(), projectModelDTO);
        projectModel.setStatus("Open");

        projectModel = projectRepo.save(projectModel);
        projectModelDTO.setProjectId(projectModel.getProjectId());

        CreateProjectItrDTO createProjectItrDTO = new CreateProjectItrDTO();
        createProjectItrDTO = MappingHelper.projectModelDTOToCreateProjectItrDTO(projectModelDTO, createProjectItrDTO);
        createProjectItrDTO = iterationService.createIteration(createProjectItrDTO, true);

        projectModel.setCurrentIterationId(createProjectItrDTO.getVersionId());
        projectModel = projectRepo.save(projectModel);

        projectModelDTO = MappingHelper.projectModelToProjectModelDTO(projectModel,projectModelDTO);
        LOGGER.debug("New Project Created",projectModelDTO);
        return projectModelDTO;
    }


    /**
     * This method updates the project details
     *
     * @param projectId
     * @param projectModelDTO
     * @return
     */
    @Transactional
    public ProjectModelDTO updateProject(long projectId, ProjectModelDTO projectModelDTO) throws Exception {
        LOGGER.debug("Updating Project",projectModelDTO);
        Optional<ProjectModel> optionalProjectModel =  projectRepo.findById(projectId);

        if (!optionalProjectModel.isPresent())
            throw new ResourceNotFoundException("Invalid Project !!");
        ProjectModel projectModel;
        projectModel = optionalProjectModel.get();
        if (projectModelDTO.getCurrentIterationId()!=null)
            throw new Exception("Current Iteration Id cannot be set !!");
        //check user's existence
//        else if ()

//        projectModel.setLastModifiedAt(LocalDateTime.now());
//        projectModel.setLastModifiedBy(projectModelDTO.getProjectReporter());

        projectModelDTO.setCurrentIterationId(projectModel.getCurrentIterationId());
        projectModel = MappingHelper.projectModelDTOToProjectModel(projectModel, projectModelDTO);

        if(projectModel.getEndDate()!=null)
        {
            if (projectModel.getEndDate().equals(LocalDate.now()))
            {
                projectModel.setStatus("Closed");
            }
            else
                throw new Exception("End date cannot be from future");
        }

        projectModel = projectRepo.save(projectModel);

        projectModelDTO = MappingHelper.projectModelToProjectModelDTO(projectModel, projectModelDTO);
        LOGGER.debug("Project Updated",projectModelDTO);
        return projectModelDTO;
//        return new ResponseEntity<>(projectModelDTO, HttpStatus.ACCEPTED);
    }


    /**
     * This method deletes the Project of given project id if it exists
     *
     * @param projectId
     * @return
     */
    @Transactional
    public ResponseEntity<?> deleteProject(Long projectId) {
        //delete tasks that have project id as its field.
        //delete subtasks that have respective task as a part of its field.
        LOGGER.debug("Deleting Existing Project",projectId);
        projectRepo.deleteById(projectId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
