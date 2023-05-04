package com.asite.aprojecto.authentication.controllers;

import com.asite.aprojecto.authentication.annotations.FileType;
import com.asite.aprojecto.authentication.constant.PermissionConstant;
import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.dto.ResponseUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.UserUpdateDetailsDTO;
import com.asite.aprojecto.authentication.exceptions.IncorrectFileFormatException;
import com.asite.aprojecto.authentication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(UserServiceURI.USER_API)
@Validated
public class UserController {
    @Autowired
    UserService userService;
    @PutMapping(UserServiceURI.UPDATE_ME)
    public ResponseEntity<?> updateMe(@Valid @NotNull @Size(min = 10, max = 10) String mobileNumber){
        try {
            ResponseUserDetailsDTO responseUserDetailsDTO=userService.updateMe(mobileNumber);
            return ResponseEntity.ok().body(responseUserDetailsDTO);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(UserServiceURI.GET_ME_NAME)
    public ResponseEntity<String> getName(@Valid @NotNull @Size(min = 1) Long uid){
        try {
            String name = userService.getNameFromID(uid);
            if (name==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            return ResponseEntity.ok().body(name);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



//    @GetMapping(UserServiceURI.USER_NAME_BY_USERID + "/{userId}")
//    public ResponseEntity<?> getNameByUserID(@PathVariable("userId") Long userId )
//    {
//        String name = userService.getUserNameByUserId(userId);
//        return new ResponseEntity<>(name, HttpStatus.OK);
//    }

    @PreAuthorize(PermissionConstant.MANAGE_USER)
    @PutMapping(UserServiceURI.UPDATE_USER)
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDetailsDTO userUpdateDetailsDTO){
        try {
            UserUpdateDetailsDTO user=userService.updateUser(userUpdateDetailsDTO);
            return ResponseEntity.ok().body(user);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_USER)
    @GetMapping("/{uid}")
    public ResponseEntity<?> getUser(@Valid @NotNull @Min(1) @PathVariable Long uid){
        Optional<ResponseUserDetailsDTO> user = Optional.ofNullable(userService.findUserById(uid));
        if (user.isPresent()){
            return ResponseEntity.ok(user.get());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    @GetMapping(UserServiceURI.USER_ME)
    public ResponseEntity<?> getMe(){
        Optional<ResponseUserDetailsDTO> user = Optional.ofNullable(userService.findMe());
        if (user.isPresent()){
            return ResponseEntity.ok(user.get());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    //Todo Make Soft delete
    @PreAuthorize(PermissionConstant.MANAGE_USER)
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteUser(@Valid @NotNull @Min(1) @PathVariable Long uid){
        try {
            String deletedStr = userService.deleteUserById(uid);
            return ResponseEntity.ok(deletedStr);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_USER)
    @GetMapping(UserServiceURI.USER_ALL)
    public ResponseEntity<?> getAllUsers(){
        Optional<List<ResponseUserDetailsDTO>> user = userService.listUsers();
        if (user.isPresent()){
            return ResponseEntity.ok(user.get());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Users not found!");
        }
    }

    @GetMapping(UserServiceURI.GET_PROFILE_PICTURE)
    public ResponseEntity<?> getProfilePicture(){
        try {
            byte[] file=userService.getProfilePicture();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(file);
        }  catch (IOException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e);
        }
    }

    @PostMapping(UserServiceURI.POST_PROFILE_PICTURE)
    public ResponseEntity<?> addProfilePicture(@Valid @NotNull @FileType({"image/jpeg", "image/png"}) MultipartFile image){
        try {
            byte[] file=userService.addProfilePicture(image);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(file);
        } catch (IncorrectFileFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e);
        }
    }
}
