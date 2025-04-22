package id.co.bankbsi.e_walled.controllers;

//import id.co.bankbsi.e_walled.services.HtmlToPdfService;

import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.HtmlToPdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class StatementController {
    @Autowired
    private final HtmlToPdfService htmlToPdfService;

    @GetMapping("/generate-wallet-statement")
    public ResponseEntity<InputStreamResource> generateAllStatement(
            @AuthenticationPrincipal Users user,
            @RequestParam Integer month,
            @RequestParam Integer year) throws Exception {


        ByteArrayOutputStream pdfOutputStream = htmlToPdfService.generateWalletStatement(user, "", month, year);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @GetMapping("/generate-wallet-statement/{wallet}")
    public ResponseEntity<InputStreamResource> generateStatement(
            @AuthenticationPrincipal Users user,
            @PathVariable String wallet,
            @RequestParam Integer month,
            @RequestParam Integer year) throws Exception {


        ByteArrayOutputStream pdfOutputStream = htmlToPdfService.generateWalletStatement(user, wallet, month, year);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }

}
