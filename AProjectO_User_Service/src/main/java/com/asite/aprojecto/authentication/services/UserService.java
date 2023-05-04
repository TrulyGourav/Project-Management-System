package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.constant.ExceptionHandlingConstants;
import com.asite.aprojecto.authentication.dto.ResponseUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.UserUpdateDetailsDTO;
import com.asite.aprojecto.authentication.models.UserModel;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import com.asite.aprojecto.authentication.util.MappingHelper;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    String UPLOAD_DIR = "/home/asite/Desktop/Bhavik/AProjectO_User_Service/src/main/java/com/asite/aprojecto/authentication/resources/";
    @PersistenceContext
    EntityManager entityManager;


//  public
    public ResponseUserDetailsDTO findUserById(Long uid){
        Optional<UserModel> user=userRepository.findById(uid);
        return user.map(MappingHelper::getResponseUserDetailsDtoFromUserModel).orElse(null);
    }

    public Optional<List<ResponseUserDetailsDTO>> listUsers(){
        return Optional.of(MappingHelper.getResponseUserDetailsDtoListFromUserModelList(userRepository.findAll()));
    }

    @Transactional
    public String deleteUserById(@NotNull Long uid) throws UsernameNotFoundException{
        UserModel userModel= (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (uid.equals(userModel.getUid())){
            return "Cannot Delete Self!";
        }
        Optional<UserModel> model=userRepository.findById(uid);
        if (model.isPresent()){
            model.get().setDeleted(true);
            userRepository.save(model.get());
            String jpql = "UPDATE UserRoleProjectModel u SET u.isDeleted = true WHERE u.uid = :uid";
            entityManager.createQuery(jpql)
                    .setParameter("uid", uid)
                    .executeUpdate();
            return "User Deleted";
        }else {
            throw new UsernameNotFoundException(ExceptionHandlingConstants.USER_DOES_NOT_EXISTS);
        }
    }

    public ResponseUserDetailsDTO findMe() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return MappingHelper.getResponseUserDetailsDtoFromUserModel((UserModel)authentication.getPrincipal());
    }

    @Transactional
    public ResponseUserDetailsDTO updateMe(String phoneNumber) throws UsernameNotFoundException{
        UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserModel> user=userRepository.findById(userModel.getUid());
        if (!user.isPresent()){
            throw new UsernameNotFoundException(ExceptionHandlingConstants.USER_NOT_FOUND);
        }
        user.get().setMobileNumber(phoneNumber);
        return MappingHelper.getResponseUserDetailsDtoFromUserModel(userRepository.save(user.get()));
    }

    @Transactional
    public UserUpdateDetailsDTO updateUser(UserUpdateDetailsDTO userUpdateDetailsDto) throws UsernameNotFoundException{
        Optional<UserModel> user=userRepository.findById(userUpdateDetailsDto.getUid());
        if (!user.isPresent()){
            throw new UsernameNotFoundException(ExceptionHandlingConstants.USER_NOT_FOUND);
        }
        return MappingHelper.getUserDetailsDto(userRepository.save(MappingHelper.getUserModelByUserUpdateDetailsDto(user.get(), userUpdateDetailsDto)));
    }

    private byte[] saveFile(MultipartFile profilePic, UserModel userModel) throws IOException {
        String extension = Objects.requireNonNull(profilePic.getOriginalFilename()).substring(profilePic.getOriginalFilename().lastIndexOf("."));
        String pathOfFile = String.valueOf(new ClassPathResource("static/Profile/"));
        Path attachmentPath = Paths.get(pathOfFile+"/"+userModel.getUid() + extension).toAbsolutePath().normalize();

        Files.createDirectories(attachmentPath.getParent());
        Files.copy(profilePic.getInputStream(), attachmentPath, StandardCopyOption.REPLACE_EXISTING);

//      String filepath = UPLOAD_DIR + userModel.getUid() + extension;
//      profilePic.transferTo(new File(filepath));

        userModel.setProfilePicturePath(attachmentPath.toString());
        userRepository.save(userModel);

//      Path filePath = Paths.get(filepath);
        return Files.readAllBytes(attachmentPath);
    }

    public byte[] addProfilePicture(MultipartFile image) throws IOException {
        UserModel userModel=(UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
         return saveFile(image,userModel);
        } catch (IOException e) {
            throw new IOException(ExceptionHandlingConstants.IMAGE_COULD_NOT_BE_SAVED);
        }
    }

    public byte[] getProfilePicture() throws IOException{
        UserModel userModel=(UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return getImage(userModel.getProfilePicturePath());
        } catch (IOException e) {
            throw new IOException(ExceptionHandlingConstants.IMAGE_COULD_NOT_BE_SAVED);
        }
    }

    private byte[] getImage(String profilePicturePath) throws IOException {
        try {
            return Files.readAllBytes(Paths.get(profilePicturePath));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public String getNameFromID(Long uid) {
        String name = (String) entityManager.createNativeQuery("Select first_name from user_udetails_tbl u SET u.first_name = "+uid).getSingleResult();
        return name;
    }
}
