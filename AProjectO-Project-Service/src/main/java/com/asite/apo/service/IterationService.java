package com.asite.apo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.asite.apo.dao.ProjectRepo;
import com.asite.apo.dto.CreateProjectItrDTO;
import com.asite.apo.dto.GetByProjectDTO;
import com.asite.apo.dto.GetVersionDTO;
import com.asite.apo.dto.UpdateProjectItrDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.ProjectModel;
import com.asite.apo.util.MappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.asite.apo.dao.IterationRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
public class IterationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IterationService.class);

    @Autowired
    IterationRepo iterationRepo;

    @Autowired
    ProjectRepo projectRepo;

    /**
     * This method creates an iteration(version) of project
     * @return CreateProjectItrDTO
     */

    @Transactional
    public CreateProjectItrDTO createIteration(CreateProjectItrDTO createProjectItrDTO, boolean isFirstItr) throws Exception {

        LOGGER.debug("Creating Iterations",createProjectItrDTO,isFirstItr);

        if (isFirstItr) {
            ProjectIterationModel itrModel = MappingHelper.getProjectItrModelFromCreateProjectItrDTO(createProjectItrDTO);
            itrModel.setStartDate(LocalDate.parse(createProjectItrDTO.getStartDate()));
            ProjectModel projectModel = projectRepo.findById(createProjectItrDTO.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project does not exist"));
            itrModel.setProject(projectModel);
            iterationRepo.save(itrModel);
            ProjectIterationModel projectIterationModel = iterationRepo.save(itrModel);
            createProjectItrDTO.setVersionId(projectIterationModel.getVersionId());
            projectModel.setCurrentIterationId(projectIterationModel.getVersionId());

            return createProjectItrDTO;
        } else {
            if (createProjectItrDTO.getVersionId() != null)
                throw new Exception("Id cannot be accepted while creating entity");
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = today.format(dateTimeFormatter);
            System.out.println(formattedDate);
            if (formattedDate.equals(createProjectItrDTO.getStartDate())) {
                ProjectIterationModel itrModel = MappingHelper.getProjectItrModelFromCreateProjectItrDTO(createProjectItrDTO);
                itrModel.setStartDate(LocalDate.parse(createProjectItrDTO.getStartDate()));
                ProjectModel projectModel = projectRepo.findById(createProjectItrDTO.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project does not exist"));
                if (iterationRepo.findByVersionId(projectModel.getCurrentIterationId()) != null) {
                    iterationRepo.findByVersionId(projectModel.getCurrentIterationId()).setEndDate(LocalDate.parse(formattedDate));
                }
                itrModel.setProject(projectModel);
                ProjectIterationModel iterationModel = iterationRepo.save(itrModel);
                createProjectItrDTO.setVersionId(iterationModel.getVersionId());
                projectModel.setCurrentIterationId(iterationModel.getVersionId());
                LOGGER.debug("Iterations Created",createProjectItrDTO,isFirstItr);
                return createProjectItrDTO;
            } else {
                throw new Exception("Enter today's date in proper format (yyyy-MM-dd)");
            }
        }
    }

    /**
     * This method updates iteration of project
     * @return UpdateProjectItrDTO
     */
    @Transactional
    public UpdateProjectItrDTO updateIteration(UpdateProjectItrDTO updateProjectItrDTO) throws Exception {
        LOGGER.debug("Updating Iterations",updateProjectItrDTO);
        ProjectIterationModel toBeUpdatedIteration = iterationRepo.findById(updateProjectItrDTO.getVersionId())
                .orElseThrow(() -> new ResourceNotFoundException("Project version not found with ID : " + updateProjectItrDTO.getVersionId()));
        toBeUpdatedIteration = MappingHelper.getUpdatedModelFromUpdateProjectDTO(updateProjectItrDTO);
        iterationRepo.save(toBeUpdatedIteration);
        LOGGER.debug("Iterations updated",updateProjectItrDTO);
        return updateProjectItrDTO;
    }

    /**
     * This method deletes an iteration of project
     * @return String
     */
    @Transactional
    public String deleteIteration(Long versionId) throws Exception {
        LOGGER.debug("Deleting Iterations",versionId);
        ProjectIterationModel deletedIteration = iterationRepo.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Project version not found with ID: " + versionId));
        ProjectModel projectModel = projectRepo.findById(versionId).orElseThrow(() -> new ResourceNotFoundException("Project does not exist"));
        projectModel.setCurrentIterationId(versionId);
        iterationRepo.delete(deletedIteration);
        LOGGER.debug("Iterations Deleted",versionId);
        return "Project version deleted successfully";
    }

    /**
     * This method returns information about a particular iteration
     * @return GetVersionDTO
     */
    public GetVersionDTO getProjectIteration(Long versionId) {
        LOGGER.debug("Getting Project Iterations",versionId);
        ProjectIterationModel iteration = iterationRepo.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Project version not found with ID: " + versionId));
        LOGGER.debug("Found Project Iterations",versionId);
        return MappingHelper.getVersionDTOFromProjectItrModel(iteration);
    }

    /**
     * This method returns information about iterations of a particular project
     * @return List<GetByProjectDTO>
     */
    public List<GetByProjectDTO> getProjectIterationByProject(Long projectId) {
        LOGGER.debug("Getting Project Iterations by Project Id",projectId);
        ProjectModel projectModel = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        List<ProjectIterationModel> projectIterationModels = projectModel.getIterations();
        LOGGER.debug("Found Project Iterations by Project Id",projectId);
        return MappingHelper.getGetByProjectDTOsFromProjectModel(projectIterationModels);
    }
}
