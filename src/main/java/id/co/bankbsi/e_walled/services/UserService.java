package id.co.bankbsi.e_walled.services;

import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.TransactionsResponse;
import id.co.bankbsi.e_walled.dto.response.UserResponse;
import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.InternalServerException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {
    @Value("${user.profile.image.directory}") // Directory to save images
    private String profileImageDir;

    @Autowired
    private UserRepository userRepository;


    public Response getProfile(Users user) {
        return UserResponse.success(user);
    }


    public UserResponse updateUserProfile(Users user, String name, String email, String mobilePhone, MultipartFile imageFile) throws IOException {

        // Update non-image fields
        if (name != null && !name.isEmpty()) {
            user.setFullName(name);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (mobilePhone != null && !mobilePhone.isEmpty()) {
            user.setMobilePhone(mobilePhone);
        }

        if (imageFile != null && !imageFile.isEmpty()) {

            String uploadDir = System.getProperty("user.dir") + "/assets/images/users/";
            Files.createDirectories(Paths.get(uploadDir)); // Make sure the directory exists

            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));

            String filename = user.getId() + fileExtension;
            Path filePath = Paths.get(uploadDir, filename);

            imageFile.transferTo(filePath.toFile());

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String fileUrl = baseUrl + "/assets/images/users/" + filename;

            user.setImageUrl(fileUrl);
            user = userRepository.save(user);
        }
        return UserResponse.success(user);
    }
}
